package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    public List<SeckillGoods> findList();

    public SeckillGoods findOneFromRedis(Long id);


    //分页加条件查询
    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    //批量修改状态
    void updateStatus(Long[] ids,String status);

}
