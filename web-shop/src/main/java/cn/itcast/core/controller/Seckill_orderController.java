package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.Seckill_Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.Seckill_goodsService;
import cn.itcast.core.service.Seckill_orderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill_order")
public class Seckill_orderController {

    @Reference
    private Seckill_orderService seckill_orderService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SeckillOrder seckillOrder) {
        //获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillOrder.setSellerId(userName);
        PageResult pageResult = seckill_orderService.search(page, rows, seckillOrder);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public SeckillOrder findOne(Long id) {
        SeckillOrder one = seckill_orderService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody SeckillOrder seckillOrder) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillOrder.setSellerId(userName);
        try {
            seckill_orderService.update(seckillOrder);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    //取消订单
    @RequestMapping("/delete")
    public Result delete(Long id) {
        try {
            seckill_orderService.delete(id);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    //确认发货
    @RequestMapping("/status")
    public Result updateStat(Long[] ids) {
        try {
            seckill_orderService.updateStat(ids);
            return new Result(true, "已发货！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "未发货！");
        }
    }
}
