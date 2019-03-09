package cn.itcast.core.service;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import org.springframework.stereotype.Repository;


import java.util.List;


public interface ContentCategoryService {
    //查询所有
    List<ContentCategory> queryAll();
    //新增品牌
    void add(ContentCategory contentCategory);
    //查询一条
    ContentCategory findOne(Long id);
    //更新品牌
    void update(ContentCategory contentCategory);
    //批量删除
    void delete(Long[] ids);
    //多条件分页查询
    PageResult search(Integer page, Integer rows, ContentCategory contentCategory);

    ContentCategory findAllById(String floor);


}
