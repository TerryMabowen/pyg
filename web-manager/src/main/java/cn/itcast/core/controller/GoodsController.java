package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.ItemSearchService;
import cn.itcast.core.service.PageService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

//    @Reference
////    private ItemSearchService itemSearchService;

    @Reference
    private PageService pageService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        PageResult pageResult = goodsService.search(page, rows, goods);
        return pageResult;
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            if(ids != null){
            //1. 先更新数据库商品的状态
            goodsService.updateStatus(ids, status);
            //2. 根据状态为1的商品添加到solr索引库中(更新数据)
//            if("1".equals(status)){
//                //审核通过商品集合
//                List<Item> itemList = goodsService.findByGoodsId(ids);
//                //添加到solr库中
//                if(itemList != null && itemList.size() > 0) {
//                    itemSearchService.importList(itemList);
//                }
//                    for (Long id : ids) {
//                        //3. 根据商品id获取商品数据, 商品详情数据, 库存集合数据等然后根据模板生成静态化页面
//                        //获取模板中需要的数据
//                        Map<String, Object> rootMap = pageService.findGoodsById(id);
//                        //生成静态化页面
//                        pageService.createGoodsHtml(id,rootMap);
//                    }
//                }
            }
            return new Result(true,"批量修改状态成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"批量修改状态失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            goodsService.delete(ids);
//            if(ids != null && ids.length > 0) {
//                for (Long id : ids) {
//                    itemSearchService.deleteList(id);
//                }
//            }
            return new Result(true,"逻辑删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"逻辑删除失败!");
        }
    }
}
