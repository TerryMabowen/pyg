package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.Data;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;

import java.util.List;

public interface FindOrderService {
    public PageResult search(Integer page, Integer rows, String userName, Order order);


    public  void updateStatus(Long[] ids);

    public List<OrderItem> selectOrderItemList(Long id);

    Data selectPayment( String userName);
}
