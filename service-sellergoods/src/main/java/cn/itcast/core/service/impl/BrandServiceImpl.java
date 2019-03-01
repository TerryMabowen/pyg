package cn.itcast.core.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;
    //查询所有
    @Override
    public List<Brand> queryAll() {
        return brandDao.selectByExample(null);
    }
    //分页查询
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<Brand> brandPage = (Page<Brand>)brandDao.selectByExample(null);
        PageResult pageResult = new PageResult(brandPage.getTotal(),brandPage.getResult());
        return pageResult;
    }
    //新增品牌
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }
    //查询一条
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }
    //更新品牌
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }
    //批量删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }
    //多条件分页查询
    @Override
    public PageResult search(Integer page, Integer rows, Brand brand) {
        BrandQuery query = new BrandQuery();
        BrandQuery.Criteria criteria = query.createCriteria();
        if(brand != null){
            if(brand.getName() != null && !"".equals(brand.getName())){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar() != null && brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        PageHelper.startPage(page, rows);
        Page<Brand> brandPage = (Page<Brand>) brandDao.selectByExample(query);
        return new PageResult(brandPage.getTotal(),brandPage.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        List<Map> maps = brandDao.selectOptionList();
        return maps;
    }
}
