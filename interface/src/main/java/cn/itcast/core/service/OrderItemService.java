package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.order.Order;

import java.util.List;

public interface OrderItemService {
    //查询订单所有
    List<Order> findAll();
    //分页查询
    PageResult findPage(Integer page, Integer rows);
    //多条件分页查询
    PageResult search(Integer page,Integer rows,Order order);
}
