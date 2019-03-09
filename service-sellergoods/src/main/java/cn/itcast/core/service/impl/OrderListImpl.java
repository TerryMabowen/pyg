package cn.itcast.core.service.impl;

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.OrderListService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class OrderListImpl implements OrderListService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private OrderItemDao orderItemDao;
    @Override
    public List<Seller> findAll() {
        return sellerDao.selectByExample(null);
    }

    @Override
    public List<OrderItem> getOrderList(String sellerId) {
        OrderItemQuery query = new OrderItemQuery();
        OrderItemQuery.Criteria queryCriteria = query.createCriteria();
        queryCriteria.andSellerIdEqualTo(sellerId);
        List<OrderItem> getOrderList = orderItemDao.selectByExample(query);
        return getOrderList;
    }

    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<Order> orderPage = (Page<Order>)orderDao.selectByExample(null);
        PageResult pageResult = new PageResult(orderPage.getTotal(),orderPage.getResult());
        return pageResult;
    }
//
//    @Override
//    public PageResult search(Integer page, Integer rows, Order order) {
//        return null;
//    }
}