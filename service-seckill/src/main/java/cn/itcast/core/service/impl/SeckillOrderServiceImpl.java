package cn.itcast.core.service.impl;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SeckillOrderService;
import cn.itcast.core.util.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private SeckillOrderDao seckillOrderDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SellerDao sellerDao;

    /**
     * 点击立即抢购,提交秒杀订单,预存到redis中
     *
     * @param seckillId 秒杀商品id
     * @param username  当前登录的用户名
     */
    @Override
    public void submitOrder(Long seckillId, String username) {
        //从缓存中查询秒杀商品
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if (seckillGoods == null) {
            throw new RuntimeException("商品不存在!");
        }
        if (seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("商品已被抢购一空!");
        }
        //扣减redis中的商品库存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //重新存回redis
        redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);
        //如果已经被秒光,则将数据同步到数据库,并将该商品从缓存中移除
        if (seckillGoods.getStockCount() == 0) {
            seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
        }
        //创建预订单,保存至redis
        SeckillOrder seckillOrder = new SeckillOrder();
        long id = idWorker.nextId();
        seckillOrder.setId(id);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setUserId(username);//设置用户ID
        seckillOrder.setStatus("0");//状态
        redisTemplate.boundHashOps("seckillOrder").put(username, seckillOrder);
    }

    /**
     * 支付成功后,将redis中的订单保存至数据库,并清除缓存中的订单
     *
     * @param userId        当前登陆的用户名
     * @param orderId       秒杀订单id
     * @param transactionId 订单的交易流水号,支付成功后由微信返回
     */
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        //根据用户ID查询日志
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        //如果与传递过来的订单号不符
        if (seckillOrder.getId().longValue() != orderId.longValue()) {
            throw new RuntimeException("订单不相符");
        }
        seckillOrder.setTransactionId(transactionId);//交易流水号
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//状态
        seckillOrderDao.insert(seckillOrder);//保存到数据库
        redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除
    }

    /**
     * 订单失效,清除缓存并恢复库存
     *
     * @param userId
     * @param orderId
     */
    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //根据用户ID查询日志
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder != null &&
                seckillOrder.getId().longValue() == orderId.longValue()) {
            redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除缓存中的订单
            //恢复库存
            //1.从缓存中提取秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
            if (seckillGoods != null) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);//存入缓存
            }
        }
    }

    @Override
    public List<SeckillOrder> findSeckillOrderList(String username) {
        //从数据库查已支付订单
        SeckillOrderQuery query = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(username);
        criteria.andStatusEqualTo("1");
        List<SeckillOrder> listFromDb = seckillOrderDao.selectByExample(query);

        if (listFromDb != null) {
            for (SeckillOrder seckillOrder : listFromDb) {
                //查询订单对应的商品,保存到订单中
                SeckillGoods seckillGoods = seckillGoodsDao.selectByPrimaryKey(seckillOrder.getSeckillId());
                seckillOrder.setSeckillGoods(seckillGoods);
                Seller seller = sellerDao.selectByPrimaryKey(seckillOrder.getSellerId());
                seckillOrder.setSellerNickName(seller.getNickName());
            }
        }
        return listFromDb;
    }

    @Override
    public void cancelOrder(Long orderId) {
        //修改订单表状态
        SeckillOrder seckillOrder = seckillOrderDao.selectByPrimaryKey(orderId);
        seckillOrder.setStatus("6");
        seckillOrderDao.updateByPrimaryKeySelective(seckillOrder);
        //恢复缓存中对应的商品库存
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
        if (seckillGoods != null) {
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
        }
    }
}
