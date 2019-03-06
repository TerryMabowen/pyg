package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.OrderDesc;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 查询用户所有订单,并分页显示
     *
     */
    @RequestMapping("/findOrdersByUsername")
    public OrderDesc search(String status){
        //1 获取当前登录用户的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.调用service传递参数,获取订单数据集合
        OrderDesc orderDesc = orderService.findOrderDescByUsername(username,status);
        //3.返回结果集
        return orderDesc;
    }
}
