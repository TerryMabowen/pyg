package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillOrder;

import java.util.List;

public interface SeckillOrderService {

    public void submitOrder(Long seckillId, String username);

    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);

    public void deleteOrderFromRedis(String userId, Long orderId);

    public List<SeckillOrder> findSeckillOrderList(String username);

    public void cancelOrder(Long orderId);

}
