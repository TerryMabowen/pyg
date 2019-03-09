package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Spec;
import cn.itcast.core.pojo.specification.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface SpecificationService {
    //查询所有
    List<Specification> queryAll();
    //分页查询
    PageResult findPage(Integer page, Integer rows);
    //新增品牌
    void add(Spec spec);
    //查询一条
    Spec findOne(Long id);
    //更新品牌
    void update(Spec spec);
    //批量删除
    void delete(Long[] ids);
    //多条件分页查询
    PageResult search(Integer page, Integer rows, Specification specification);
    //获取规格列表数据格式是封装的map对象
    List<Map> selectOptionList();

    //修改状态
    void updateStatus(Long[] ids, String status);
}
