package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.OrderListService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/orderList")
public class OrderListController {
   @Reference
    public OrderListService orderListService;
   @RequestMapping("/findAll")
    public List<Seller> findAll(){
       List<Seller> orderList = orderListService.findAll();
       return orderList;
   }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows){
        PageResult pageResult = orderListService.findPage(page, rows);
        return pageResult;
    }
    @RequestMapping("/getOrderList")
    public List<OrderItem> getOrderList(String sellerId){
        List<OrderItem> getOrderList = orderListService.getOrderList(sellerId);
        for (OrderItem orderItem : getOrderList) {
            System.out.println(orderItem);
        }
        return getOrderList;
    }

}
