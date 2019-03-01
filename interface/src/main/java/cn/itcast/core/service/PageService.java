package cn.itcast.core.service;

import org.springframework.stereotype.Repository;

import java.util.Map;
@Repository
public interface PageService {
    //利用freemarker生成商品详情页
    void createGoodsHtml(Long goodsId,Map<String,Object> rootMap) throws Exception;
    //根据商品id查询商品,商品详情,库存,商品三级分类数据
    Map<String ,Object> findGoodsById(Long goodsId);
}