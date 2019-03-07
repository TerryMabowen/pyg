package cn.itcast.core.service;

public interface SeckillOrderService {

    public void submitOrder(Long seckillId, String username);

    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);

    public void deleteOrderFromRedis(String userId, Long orderId);
}
