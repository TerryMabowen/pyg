package cn.itcast.core.service.impl;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.entity.Data;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.service.FindOrderService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FindOrderServiceImpl implements FindOrderService {
    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    GoodsDao goodsDao;


    //订单查询
    @Override
    public PageResult search(Integer page, Integer rows, String userName, Order order) {
        PageHelper.startPage(page, rows);
        //创建查询条件
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        //根据状态
        if (order.getStatus() != null && !"".equals(order.getStatus())) {
            criteria.andStatusEqualTo(order.getStatus());
        }
        //根据订单来源
        if (order.getSourceType() != null && !"".equals(order.getSourceType())) {
            criteria.andSourceTypeEqualTo(order.getSourceType());
        }
        //根据收货人
        if (order.getReceiver() != null && !"".equals(order.getReceiver())) {
            criteria.andReceiverEqualTo(order.getReceiver());
        }

        //根据商家ID查
        criteria.andSellerIdEqualTo(userName);
        Page<Order> orders = (Page<Order>) orderDao.selectByExample(orderQuery);

        return new PageResult(orders.getTotal(), orders.getResult());
    }


    //订单发货
    @Override
    public void updateStatus(Long[] ids) {
        if (ids != null && ids.length > 0) {
            Order order = new Order();
            for (Long id : ids) {

                order.setOrderId(id);
                //设置状态 4是已发货
                order.setStatus("4");
                orderDao.updateByPrimaryKeySelective(order);
            }
        }
    }


    //订单统计
    @Override
    public List<OrderItem> selectOrderItemList(Long id) {
        //根据订单ID 查询
        OrderItemQuery orderItemQuery = new OrderItemQuery();
        OrderItemQuery.Criteria criteria = orderItemQuery.createCriteria().andOrderIdEqualTo(id);
        List<OrderItem> orderItems = orderItemDao.selectByExample(orderItemQuery);
        return orderItems;
    }


    //订单折线图  销售额
    @Override
    public Data selectPayment(String userName) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //设置初始查询时间
        String day = "2019-03-02";
        Date parse = null;
        try {
            //将初始时间转换时间类型
            parse = simpleDateFormat.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        //创建时间集合
        List<String> dateList = new ArrayList<>();
        //创建销售额 集合
        List<String> payList = new ArrayList<>();

        String format = null;
        //
        for (int i = 0; i < 7; i++) {
            //时间递增 加一天
            calendar.add(calendar.DATE, 1);

            format = simpleDateFormat.format(calendar.getTime());

            //根据 7天时间 和商家ID查询
            String strings = orderDao.selectPayment("%" + format + "%", userName);
            //把时间加时间集合里去
            dateList.add(format);
            //把销售额加销售额集合里去
            payList.add(strings);
        }
        //封装对象
        Data data = new Data();
        data.setDateList(dateList);
        data.setPayList(payList);
        return data;

    }


}