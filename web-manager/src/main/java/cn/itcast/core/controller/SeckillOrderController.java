package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {
   @Reference
    private SeckillOrderService seckillOrderService;

   @RequestMapping("/findAll")
    public List<SeckillOrder> findAll(){
       List<SeckillOrder> orderList = seckillOrderService.findAll();
       return orderList;
   }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows){
        PageResult pageResult = seckillOrderService.findPage(page, rows);
        return pageResult;
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody SeckillOrder seckillOrder){
        PageResult pageResult = seckillOrderService.search(page, rows, seckillOrder);
        return pageResult;
    }
}
