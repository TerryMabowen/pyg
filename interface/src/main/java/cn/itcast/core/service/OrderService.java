package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.entity.OrderDesc;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

@Repository
public interface OrderService {
    //提交订单
     void add(Order order);
    //支付成功后,修改支付日志表和订单表的支付状态
     void updateStatusToPayLogAndOrder(String out_trade_no);



     //查询用户的所有订单数据,并分页
     List<OrderDesc> findOrderDescByUsername(String userName,String status);

}
