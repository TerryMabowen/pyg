package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/add")
    @CrossOrigin(origins = "http://localhost:8090" ,allowCredentials = "true") //注解解决跨域访问问题
    public Result add(@RequestBody Order order){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(username);
            if(!"anonymousUser".equals(username)) {
                orderService.add(order);
            }
            return new Result(true,"提交订单成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交订单失败!");
        }
    }
}
