package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import org.springframework.stereotype.Repository;

@Repository
public interface Seckill_orderService {


    //分页加条件查询
    PageResult search(Integer page, Integer rows, SeckillOrder seckillOrder);

    //点修改回显数据
    SeckillOrder findOne(Long id);

    //修改
    void update(SeckillOrder seckillOrder);

    //取消订单
    void delete(Long id);

    //批量修改状态
    void updateStat(Long[] ids);

}
