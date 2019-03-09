package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.seckill.SeckillOrder;

import java.util.List;

public interface SeckillOrderService {

    public void submitOrder(Long seckillId, String username);

    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);

    public void deleteOrderFromRedis(String userId, Long orderId);

    public List<SeckillOrder> findSeckillOrderList(String username);

    public void cancelOrder(Long orderId);

    //查询订单所有
    List<SeckillOrder> findAll();
    //分页查询
    PageResult findPage(Integer page, Integer rows);
    //多条件分页查询
    PageResult search(Integer page,Integer rows,SeckillOrder seckillOrder);
}
