package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    //查询所有
    List<TypeTemplate> queryAll();

   /* //分页查询
    PageResult findPage(Integer page, Integer rows);*/

    //新增规格
    void add(TypeTemplate typeTemplate);

    //查询一条
    TypeTemplate findOne(Long id);

    //更新品牌
    void update(TypeTemplate typeTemplate);

    //批量删除
    void delete(Long[] ids);

    //批量提交申请
    void updateStat(Long[] ids);
    //多条件分页查询
    PageResult search(TypeTemplate typeTemplate, int page, int rows);

    // 返回规格列表
    List<Map> findSpecList(Long id);

    //根据模板id查询规格选项集合
    List<Map> findSpecListByTypeId(Long templateId);

    //审批
    void updateStatus(Long[] ids, String status);
}
