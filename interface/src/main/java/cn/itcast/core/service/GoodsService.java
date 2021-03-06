package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.Item;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface GoodsService {
    //添加商品
    void add(GoodsEntity goodsEntity);

    //分页加条件查询
    PageResult search(Integer page, Integer rows, Goods goods);

    //点修改回显数据
    GoodsEntity findOne(Long id);

    //修改
    void update(GoodsEntity goodsEntity);

    //删除(逻辑删除,并不是真的在数据库中删除,只是在页面上不显示)
    void delete(Long[] ids);

    //上架
    void upShelf(Long[] ids);

    //下架
    void downShelf(Long[] ids);

    //上架功能查询商品列表
    public List<Goods> findGoodsForUpShelf(String sellerId);

    //下架功能查询商品列表
    public List<Goods> findGoodsForDownShelf(String sellerId);


    //批量修改状态
    void updateStatus(Long[] ids, String status);



}
