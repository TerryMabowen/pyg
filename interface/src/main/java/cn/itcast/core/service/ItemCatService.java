package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Repository
public interface ItemCatService {
    //查询所有
    List<ItemCat> queryAll();

    //分页查询
    //PageResult findPage(Integer page, Integer rows);
    //新增分类
    void add(ItemCat itemCat);

    //查询一条
    ItemCat findOne(Long id);

    //更新品牌
    void update(ItemCat itemCat);

    //批量删除
    void delete(Long[] ids);

    //批量提交申请
    void updateStat(Long[] ids);

    //多条件查询不带分页
    PageResult  search(Integer page, Integer rows, ItemCat itemCat);

    //根据上级ID查询商品分类列表
    List<ItemCat> findByParentId(Long parentId);
}
