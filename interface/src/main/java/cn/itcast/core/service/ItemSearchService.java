package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    //根据关键字(多关键字),分类,品牌,规格,价格区间,排序搜索商品
    Map<String ,Object> search(Map searchMap);
    //将运营商通过审核的商品集合添加到solr库中
    void importList(Long goodsId,String status);
    //将商家删除的商品数据从solr库中删除
    void deleteList(Long goodsId);
}
