package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import org.springframework.stereotype.Repository;

@Repository
public interface Seckill_goodsService {
    //添加商品
    void add(SeckillGoods seckillGood);

    //分页加条件查询
    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    //点修改回显数据
    SeckillGoods findOne(Long id);

    //修改
    void update(SeckillGoods seckillGoods);

    //删除(逻辑删除,并不是真的在数据库中删除,只是在页面上不显示)
    void delete(Long[] ids);

    //批量修改状态
    void updateStat(Long[] ids);

}
