package cn.itcast.core.service.impl;


import cn.itcast.core.dao.ad.ContentCategoryDao;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.ad.ContentCategoryQuery;
import cn.itcast.core.pojo.entity.PageResult;

import cn.itcast.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private ContentCategoryDao contentCategoryDao;
    //查询所有
    @Override
    public List<ContentCategory> queryAll() {
        return contentCategoryDao.selectByExample(null);
    }

    //新增品牌
    @Override
    public void add(ContentCategory contentCategory) {
        contentCategoryDao.insertSelective(contentCategory);
    }
    //查询一条
    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryDao.selectByPrimaryKey(id);
    }
    //更新品牌
    @Override
    public void update(ContentCategory contentCategory) {
        contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
    }
    //批量删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            contentCategoryDao.deleteByPrimaryKey(id);
        }
    }
    //多条件分页查询
    @Override
    public PageResult search(Integer page, Integer rows, ContentCategory contentCategory) {
        ContentCategoryQuery query = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = query.createCriteria();
        if(contentCategory != null){
            if(contentCategory.getName() != null && !"".equals(contentCategory.getName())){
                criteria.andNameLike("%"+contentCategory.getName()+"%");
            }
        }
        PageHelper.startPage(page, rows);
        Page<ContentCategory> contentCategoryPage = (Page<ContentCategory>) contentCategoryDao.selectByExample(query);
        return new PageResult(contentCategoryPage.getTotal(),contentCategoryPage.getResult());
    }

    @Override
    public ContentCategory findAllById(String floor) {
        long h = Long.parseLong(floor);
        ContentCategory contentCategory = contentCategoryDao.selectByPrimaryKey(h);
        return contentCategory;
    }


}
