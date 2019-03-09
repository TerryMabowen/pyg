package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import org.springframework.stereotype.Repository;


public interface OrderService {
    //提交订单
     void add(Order order);
    //支付成功后,修改支付日志表和订单表的支付状态
     void updateStatusToPayLogAndOrder(String out_trade_no);
}
