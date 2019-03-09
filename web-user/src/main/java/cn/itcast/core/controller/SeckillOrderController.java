package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/findSeckillOrderList")
    public List<SeckillOrder> findSeckillOrderList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SeckillOrder> seckillOrderList = seckillOrderService.findSeckillOrderList(username);
        return seckillOrderList;
    }

    @RequestMapping("/cancelOrder")
    public Result cancelOrder(Long orderId) {
        try {
            seckillOrderService.cancelOrder(orderId);
            return new Result(true, "取消订单成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "取消订单失败!");
        }
    }
}
