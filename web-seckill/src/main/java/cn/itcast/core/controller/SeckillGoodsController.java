package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询秒杀商品列表
     *
     * @return 秒杀商品集合
     */
    @RequestMapping("/findList")
    public List<SeckillGoods> findList() {
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.findList();
        return seckillGoodsList;
    }
}
