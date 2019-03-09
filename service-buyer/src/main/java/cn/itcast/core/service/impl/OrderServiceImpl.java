package cn.itcast.core.service.impl;

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.cart.BuyerCart;
import cn.itcast.core.pojo.entity.OrderDesc;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.util.Constants;
import cn.itcast.core.util.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void add(Order order) {
        //1. 根据用户名到redis中获取购物车集合
        List<BuyerCart> cartList = (List<BuyerCart>) redisTemplate.boundHashOps(Constants.REDIS_CARTLIST).get(order.getUserId());
        //创建订单ID列表
        List<String> orderIdList = new ArrayList<>();
        //初始化总金额 （元）
        double total_money = 0;
        //2. 遍历购物车集合
        if(cartList != null){
            for (BuyerCart buyerCart : cartList) {
                //使用分布式id生成器, 生成订单id, 保证唯一
                long orderId = idWorker.nextId();
                Order order1 = new Order();
                order1.setOrderId(orderId);
                //TODO 一堆set方法, 设置订单对象属性
                //用户名
                order1.setUserId(order.getUserId());
                //支付类型
                order1.setPaymentType(order.getPaymentType());
                //状态：未付款
                order1.setStatus("1");
                //订单创建日期
                order1.setCreateTime(new Date());
                //订单更新日期
                order1.setUpdateTime(new Date());
                //收货人地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                //收货人手机号
                order1.setReceiverMobile(order.getReceiverMobile());
                //收货人
                order1.setReceiver(order.getReceiver());
                //订单来源
                order1.setSourceType(order.getSourceType());
                //商家ID
                order1.setSellerId(buyerCart.getSellerId());
                //初始化金额
                double money = 0;
                //循环购物车明细
                if(buyerCart.getOrderItemList() != null){
                    //3. 遍历购物车中的购物项集合
                    for (OrderItem orderItem : buyerCart.getOrderItemList()) {
                        //使用分布式id生成器, 生成订单详情id
                        long orderItemId = idWorker.nextId();
                        orderItem.setId(orderItemId);
                        //TODO 一堆set方法, 设置orderItem对象属性
                        //订单ID
                        orderItem.setOrderId(orderId);
                        //商家id
                        orderItem.setSellerId(buyerCart.getSellerId());
                        //金额累加
                        money += orderItem.getTotalFee().doubleValue();
                        //保存订单详情
                        orderItemDao.insertSelective(orderItem);
                    }
                }
                //订单金额
                order1.setPayment(new BigDecimal(money));
                //保存订单
                orderDao.insertSelective(order1);
                //4. 计算总价格(累加到总金额)
                total_money += money;
                //添加到订单列表
                orderIdList.add(orderId + "");
            }
        }
        //保存支付日志
        if("1".equals(order.getPaymentType())){
            //如果是微信支付
            PayLog payLog = new PayLog();
            //支付订单号
            String payLogId = idWorker.nextId()+"";
            payLog.setOutTradeNo(payLogId);
            //创建时间
            payLog.setCreateTime(new Date());
            //订单号列表，逗号分隔
            String orderIds = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(orderIds);
            //支付类型
            payLog.setPayType("1");
            //总金额(分)
            payLog.setTotalFee((long)(total_money * 100));
            //支付状态
            payLog.setTradeState("0");
            //用户ID
            payLog.setUserId(order.getUserId());
            //插入到支付日志表
            payLogDao.insertSelective(payLog);
            //将支付日志根据用户名缓存到redis中一份
            redisTemplate.boundHashOps(Constants.REDIS_PAYLOG).put(order.getUserId(),payLog);
        }
        //保存完订单, 那么redis中的购物车列表就没有用了, 可以删除掉
            redisTemplate.boundHashOps(Constants.REDIS_CARTLIST).delete(order.getUserId());
    }

    @Override
    public void updateStatusToPayLogAndOrder(String out_trade_no) {
        //获取该支付单号对应的支付日志对象
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);
        //设置支付日志对象的支付状态trade_state为已支付
        payLog.setTradeState("1");
        //更新数据库支付日志信息
        payLogDao.updateByPrimaryKeySelective(payLog);
        //获取该支付日志的订单号集合字符串
        String orderListStr = payLog.getOrderList();
        //将该字符串用 , 号切割得到数组,遍历数组
        String[] orderIds = orderListStr.split(",");
        if(orderIds != null){
            for (String orderId : orderIds) {
                //根据订单号集合获取订单对象
                long oid = Long.parseLong(orderId);
                Order order = orderDao.selectByPrimaryKey(oid);
                //设置订单状态status为已支付
                order.setStatus("2");
                //更新数据库中订单信息
                orderDao.updateByPrimaryKeySelective(order);
                //删除Redis中支付日志数据
                redisTemplate.boundHashOps(Constants.REDIS_PAYLOG).delete(order.getUserId());
            }
        }
    }

   @Override
    public List<OrderDesc> findOrderDescByUsername(String userName,String status){
        List<OrderDesc> orderDescList = new ArrayList<>();
       OrderDesc orderDesc = new OrderDesc();
       OrderQuery orderQuery = new OrderQuery();
       OrderQuery.Criteria criteria = orderQuery.createCriteria();
       criteria.andUserIdEqualTo(userName);
       if(status != null && !"".equals(status) && !"undefined".equals(status)){
           criteria.andStatusEqualTo(status);
       }
       List<Order> orderList = orderDao.selectByExample(orderQuery);
       if(orderList != null && orderList.size() > 0){
           orderDesc.setOrderList(orderList);
           for (Order order : orderList) {
               Long orderId = order.getOrderId();
               orderDesc.setOrderId(orderId);
               orderDesc.setOrderIdStr(orderId+"");
               orderDesc.setPaymentType(order.getPaymentType());
               orderDesc.setCreateTime(order.getCreateTime());
               orderDesc.setUpdateTime(order.getUpdateTime());
               orderDesc.setPostFee(order.getPostFee());
               orderDesc.setStatus(order.getStatus());
               orderDesc.setBuyerNick(order.getBuyerNick());
               orderDesc.setReceiverAreaName(order.getReceiverAreaName());
               orderDesc.setReceiverMobile(order.getReceiverMobile());
               orderDesc.setReceiver(order.getReceiver());

               OrderItemQuery orderItemQuery = new OrderItemQuery();
               OrderItemQuery.Criteria criteria1 = orderItemQuery.createCriteria();
               criteria1.andOrderIdEqualTo(orderId);
               List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
               if(orderItemList != null){
                   orderDesc.setOrderItemList(orderItemList);
               }
               String sellerId = order.getSellerId();
               Seller seller = sellerDao.selectByPrimaryKey(sellerId);
               orderDesc.setSellerName(seller.getName());
           }
           orderDescList.add(orderDesc);
       }
       return orderDescList;
    }


}
