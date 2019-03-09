package cn.itcast.core.service.impl;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询秒杀商品列表
     *
     * @return 秒杀商品集合
     */
    @Override
    public List<SeckillGoods> findList() {
        //首先从redis中获取秒杀商品集合
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        //如果redis中没有数据,则从数据库中查并且缓存到redis中
        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {
            SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
            SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();
            //审核通过
            criteria.andStatusEqualTo("1");
            //剩余库存大于0
            criteria.andStockCountGreaterThan(0);
            //开始时间小于等于当前时间
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            //结束时间大于当前时间
            criteria.andEndTimeGreaterThan(new Date());
            seckillGoodsList = seckillGoodsDao.selectByExample(seckillGoodsQuery);
            //将商品列表装入缓存
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
                }
            }
        }
        return seckillGoodsList;
    }

    /**
     * 根据id从redis中查询商品
     *
     * @param id 秒杀商品id
     * @return 秒杀商品
     */
    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
        return seckillGoods;
    }

    /**
     * 每分钟查询数据库，将符合条件并且缓存中不存在的秒杀商品存入缓存
     */
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods() {
        //查询缓存中在售商品的id集合
        List seckillGoodsIds = new ArrayList<>(redisTemplate.boundHashOps("seckillGoods").keys());
        SeckillGoodsQuery query = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThan(new Date());
        //排除缓存中已有的商品
        criteria.andIdNotIn(seckillGoodsIds);
        //从数据库中查符合条件的秒杀商品
        List<SeckillGoods> seckillGoodsList = seckillGoodsDao.selectByExample(query);
        if (seckillGoodsList != null) {
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                //存入缓存
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            }
        }
    }

    /**
     * 移除缓存中过期的秒杀商品
     */
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods() {
        //查询缓存中商品集合
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        if (seckillGoodsList != null) {
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                if (seckillGoods.getEndTime().getTime() < new Date().getTime()) {
                    seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
                    redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
                }
            }
        }
    }

    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();
        if(seckillGoods!=null){
            if(seckillGoods.getTitle()!=null&&seckillGoods.getTitle().length()>0){
                criteria.andTitleLike(seckillGoods.getTitle());
            }
            if(seckillGoods.getStatus()!=null){
                criteria.andStatusEqualTo(seckillGoods.getStatus());
            }
        }
        PageHelper.startPage(page,rows);
        Page<SeckillGoods> seckillGoodsPage = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
        return new PageResult(seckillGoodsPage.getTotal(),seckillGoodsPage.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if(ids!=null&&ids.length>0){
            for (Long id : ids) {
                SeckillGoods seckillGoods = new SeckillGoods();
                seckillGoods.setGoodsId(id);
                seckillGoods.setStatus(status);
                seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
            }
        }
    }

}
