package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.seller.Seller;

import java.util.List;

public interface OrderListService {
    //查询订单所有
    List<Seller> findAll();
    //分页查询
    PageResult findPage(Integer page, Integer rows);
//    //多条件分页查询
//    PageResult search(Integer page, Integer rows, Order order);
    //根据商品ID查询所有
    List<OrderItem> getOrderList(String sellerId);
}
