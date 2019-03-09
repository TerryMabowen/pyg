package cn.itcast.core.service.impl;


import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;

import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import cn.itcast.core.service.TypeTemplateService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Autowired
    private RedisTemplate redisTemplate;

    //查询所有
    @Override
    public List<TypeTemplate> queryAll() {
        return typeTemplateDao.selectByExample(null);
    }


    //新增规格
    @Override
    public void add(TypeTemplate typeTemplate) {
        //typeTemplate.setStat("0");
        typeTemplate.setStat(Constants.YI_SHEN_HE);
        typeTemplateDao.insertSelective(typeTemplate);
    }

    //查询一条
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    //更新品牌
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    //批量删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }

    //批量提交审核
    @Override
    public void updateStat(Long[] ids) {
        for (Long id : ids) {
            TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
            typeTemplate.setStat("1");
            typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
        }
    }

    //多条件分页查询
    @Override
    public PageResult search(TypeTemplate typeTemplate, int page, int rows) {
        /**
         * 查询模板所有数据, 以模板id作为key, 对应的品牌集合作为value存入Redis中
         * 查询模板所有数据, 以模板id作为key, 对应的规格集合作为value存入Redis中
         */
        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if (typeTemplateList != null && typeTemplateList.size() > 0) {
            for (TypeTemplate template : typeTemplateList) {
                //以模板id作为key, 对应的品牌集合作为value存入Redis中
                String brandIdsJsonStr = template.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIdsJsonStr, Map.class);
                if (brandList != null) {
                    redisTemplate.boundHashOps(Constants.REDIS_BRAND_LIST).put(template.getId(), brandList);
                }
                //以模板id作为key, 对应的规格集合作为value存入Redis中
                List<Map> specList = findSpecListByTypeId(template.getId());
                if (specList != null) {
                    redisTemplate.boundHashOps(Constants.REDIS_SPEC_LIST).put(template.getId(), specList);
                }

            }
        }
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && !"".equals(typeTemplate.getName())) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
//            if(typeTemplate.getStat()!=null){
//                criteria.andStatEqualTo(typeTemplate.getStat());
//            }
            if (typeTemplate.getStat() != null && !"".equals(typeTemplate.getStat())) {
                criteria.andStatEqualTo(typeTemplate.getStat());
            }
        }
        PageHelper.startPage(page, rows);
        Page<TypeTemplate> typeTemplatePage = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);
        return new PageResult(typeTemplatePage.getTotal(), typeTemplatePage.getResult());
    }

    @Override
    public List<Map> findSpecList(Long id) {
        //查询模板
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        for (Map map : list) {
            //查询规格选项列表
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
            map.put("options", specificationOptionList);
        }
        return list;
    }

    @Override
    public List<Map> findSpecListByTypeId(Long templateId) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(templateId);
        List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        if (specList != null) {
            for (Map map : specList) {
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(Long.parseLong(String.valueOf(map.get("id"))));
                List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                map.put("options", options);
            }
        }
        return specList;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if(ids!=null&&ids.length>0){
            for (Long id : ids) {
                TypeTemplate typeTemplate = new TypeTemplate();
                typeTemplate.setId(id);
                typeTemplate.setStat(status);
                typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
            }
        }
    }
}
