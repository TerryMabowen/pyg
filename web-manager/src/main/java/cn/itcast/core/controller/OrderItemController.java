package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderItemService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {
    @Reference
    private OrderItemService orderItemService;
    //查询订单所有
    @RequestMapping("/findAll")
    public List<Order> orderItemList(){
        List<Order> orderList = orderItemService.findAll();
        return orderList;
    }
    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows){
        PageResult pageResult = orderItemService.findPage(page, rows);
        return pageResult;
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody Order order){
        PageResult pageResult = orderItemService.search(page, rows, order);
        return pageResult;
    }
}
