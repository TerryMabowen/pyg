package cn.itcast.core.service.impl;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

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
}
