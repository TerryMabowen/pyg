package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface BrandService {
    //查询所有
    List<Brand> queryAll();
    //分页查询
    PageResult findPage(Integer page,Integer rows);
    //新增品牌
    void add(Brand brand);
    //查询一条
    Brand findOne(Long id);
    //更新品牌
    void update(Brand brand);
    //批量删除
    void delete(Long[] ids);
    //多条件分页查询
    PageResult search(Integer page,Integer rows,Brand brand);
    //获取品牌列表数据格式是封装的map对象
    List<Map> selectOptionList();

    //修改状态
    void updateStatus(Long[] ids, String status);
}
