package cn.itcast.core.service;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.entity.PageResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentService {
    //查询所有
    List<Content> queryAll();
    //新增品牌
    void add(Content content);
    //查询一条
    Content findOne(Long id);
    //更新品牌
    void update(Content content);
    //批量删除
    void delete(Long[] ids);
    //多条件分页查询
    PageResult search(Integer page, Integer rows, Content content);
    //根据广告类型id查询广告
    List<Content> findByCategoryId(Long categoryId);
    //从Redis查询
    List<Content> findByCategoryIdFromRedis(Long categoryId);

    List<Content> findNameById(String floor);



}
