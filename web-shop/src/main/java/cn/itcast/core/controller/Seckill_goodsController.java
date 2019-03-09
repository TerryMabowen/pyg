package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.Seckill_Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.Seckill_goodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill_goods")
public class Seckill_goodsController {
    @Reference
    private GoodsService goodsService;

    @Reference
    private Seckill_goodsService seckill_goodsService;

    @RequestMapping("/add")
    public Result add(@RequestBody Seckill_Goods seckill_goods) {
        //获取当前登录的用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        seckill_goods.getSeckillGoods().setSellerId(userName);
        System.out.println(seckill_goods);

        seckill_goods.getSeckillGoods().setSellerId(seckill_goods.getGoods().getSellerId());
        seckill_goods.getSeckillGoods().setGoodsId(seckill_goods.getGoods().getId());
        seckill_goods.getSeckillGoods().setPrice(seckill_goods.getGoods().getPrice());

        try {
            seckill_goodsService.add(seckill_goods.getSeckillGoods());
            return new Result(true, "秒杀商品申请提交成功!请耐心等待审核...");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "秒杀商品申请提交失败!请按照要求重新录入，谢谢！");
        }
    }

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SeckillGoods seckillGoods) {
        //获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(userName);
        PageResult pageResult = seckill_goodsService.search(page, rows, seckillGoods);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public SeckillGoods findOne(Long id) {
        SeckillGoods one = seckill_goodsService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody SeckillGoods seckillGoods) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(userName);
        try {
            seckill_goodsService.update(seckillGoods);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckill_goodsService.delete(ids);

            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/audit_status")
    public Result updateStat(Long[] ids) {
        try {
            seckill_goodsService.updateStat(ids);

            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }
}
