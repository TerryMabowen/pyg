package cn.itcast.core.dao.good;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface BrandDao {
    int countByExample(BrandQuery example);

    int deleteByExample(BrandQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Brand record);

    int insertSelective(Brand record);

    List<Brand> selectByExample(BrandQuery example);

    Brand selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Brand record, @Param("example") BrandQuery example);

    int updateByExample(@Param("record") Brand record, @Param("example") BrandQuery example);

    int updateByPrimaryKeySelective(Brand record);

    int updateByPrimaryKey(Brand record);

    //获取品牌列表数据格式是封装的map对象
    List<Map> selectOptionList();

    //根据excel表传入的name查询到对应的信息 以Map形式返回
    Map<String,String> selectOptionListByName(@Param("name") String name);
}