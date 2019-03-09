package cn.itcast.core.service.impl;

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.service.OrderItemService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class OrderItemImpl implements OrderItemService {
    @Autowired
    private OrderDao orderDao;
    @Override
    public List<Order> findAll() {
        return orderDao.selectByExample(null);
    }
    //分页查询
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<Order> orderPage = (Page<Order>)orderDao.selectByExample(null);
        PageResult pageResult = new PageResult(orderPage.getTotal(),orderPage.getResult());
        return pageResult;
    }


    //多条件分页查询
    @Override
    public PageResult search(Integer page, Integer rows, Order order) {
        PageHelper.startPage(page,rows);
        OrderQuery query = new OrderQuery();
        OrderQuery.Criteria criteria = query.createCriteria();
        if(order != null){
            if(order.getStatus() != null && !"".equals(order.getStatus())){
                //状态是通过复选框选择的,只需判断是否为空即可
                criteria.andStatusEqualTo(order.getStatus());
            }
        }
        Page<Order> goodList = (Page<Order>) orderDao.selectByExample(query);
        return new PageResult(goodList.getTotal(),goodList.getResult());
    }

 }