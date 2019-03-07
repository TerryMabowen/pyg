package cn.itcast.core.service;

import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface PayService {
    //调用微信支付sdk统一下单接口生成支付链接
    public Map<String, String> createNative(String out_trade_no, String total_fee);

    //通过订单号调用微信支付查询订单接口查询支付状态
    public Map<String, String> queryPayStatus(String out_trade_no);

    //通过用户名到Redis中查询支付日志对象
    PayLog findPayLogByUsername(String username);

    //通过用户名到redis中查询秒杀订单
    public SeckillOrder searchOrderFromRedisByUsername(String username);

    //关闭微信订单
    public Map<String, String> closePay(String out_trade_no);
}
