package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;

//    @Reference
//    private ItemSearchService itemSearchService;

    @RequestMapping("/add")
    public Result add(@RequestBody GoodsEntity goodsEntity) {
        //获取当前登录的用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goodsEntity.getGoods().setSellerId(userName);
        //String userName = "baidu";
        try {
            goodsService.add(goodsEntity);
            return new Result(true, "添加商品成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加商品失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods) {
        //获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        PageResult pageResult = goodsService.search(page, rows, goods);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id) {
        GoodsEntity one = goodsService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goodsEntity.getGoods().setSellerId(userName);
        try {
            goodsService.update(goodsEntity);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
//            if(ids != null && ids.length > 0) {
//                for (Long id : ids) {
//                    itemSearchService.deleteList(id);
//                }
//            }
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/findGoodsForUpShelf")
    public List<Goods> findGoodsForUpShelf() {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Goods> goodsList = goodsService.findGoodsForUpShelf(sellerId);
        return goodsList;
    }

    @RequestMapping("/findGoodsForDownShelf")
    public List<Goods> findGoodsForDownShelf() {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Goods> goodsList = goodsService.findGoodsForDownShelf(sellerId);
        return goodsList;
    }

    @RequestMapping("/upShelf")
    public Result upShelf(Long[] ids) {
        try {
            goodsService.upShelf(ids);
            return new Result(true, "上架成功!");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上架失败!");
        }
    }

    @RequestMapping("/downShelf")
    public Result downShelf(Long[] ids) {
        try {
            goodsService.downShelf(ids);
            return new Result(true, "下架成功!");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "下架失败!");
        }
    }

    @RequestMapping("/audit_status")
    public Result updateStat(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);
//            if(ids != null && ids.length > 0) {
//                for (Long id : ids) {
//                    itemSearchService.deleteList(id);
//                }
//            }
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }
}
