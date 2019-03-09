package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Data;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.FindOrderService;
import com.alibaba.dubbo.config.annotation.Reference;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/*
    商家后台订单管理
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Reference
    FindOrderService findOrderService;

    //订单查询 分页
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Order order){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        PageResult search = findOrderService.search(page, rows, userName,order);
        return search;
    }



    //订单发货 修改状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids){
        try {
            findOrderService.updateStatus(ids);
            return new Result(true,"发货成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"发货失败");
        }
    }


    //订单统计  查询订单详情集合
    @RequestMapping("/selectOrderItemList")
    public List<OrderItem> selectOrderItemList(Long id){

        List<OrderItem> orderItems = findOrderService.selectOrderItemList(id);
        return orderItems;
    }



    //订单折线图
    @RequestMapping("/getJsonData")
    public Data getJsonData() {

        //获取商家ID  根据商家ID 查询
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Data data = findOrderService.selectPayment(name);
        return data;
    }
}
