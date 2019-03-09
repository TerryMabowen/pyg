package cn.itcast.core.service.impl;


import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Spec;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.service.SpecificationService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    //查询所有
    @Override
    public List<Specification> queryAll() {
        return specificationDao.selectByExample(null);
    }

    //分页查询
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Page<Specification> specificationPage = (Page<Specification>) specificationDao.selectByExample(null);
        PageResult pageResult = new PageResult(specificationPage.getTotal(), specificationPage.getResult());
        return pageResult;
    }

    //新增品牌
    @Override
    public void add(Spec spec) {
        Specification specification = spec.getSpecification();
        specification.setStat(Constants.YI_SHEN_HE);
        //先保存规格对象
        //设置未申请状态
        specification.setStat("0");
        specificationDao.insert(specification);
        //保存规格选项对象
        List<SpecificationOption> specificationOptionList = spec.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setStat("0");
            specificationOption.setSpecId(specification.getId());
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    //查询一条
    @Override
    public Spec findOne(Long id) {
        Spec spec = new Spec();
        spec.setSpecification(specificationDao.selectByPrimaryKey(id));
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        spec.setSpecificationOptionList(specificationOptionDao.selectByExample(query));
        return spec;
    }

    //更新品牌
    @Override
    public void update(Spec spec) {
        //先保存规格对象
        Specification specification = spec.getSpecification();
        specificationDao.updateByPrimaryKeySelective(specification);
        //删除掉当前规格下关联的规格选项
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());
        specificationOptionDao.deleteByExample(query);

        //保存规格选项对象
        List<SpecificationOption> specificationOptionList = spec.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setSpecId(specification.getId());
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    //批量删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除掉传入的规格的id，将规格对象删除
            specificationDao.deleteByPrimaryKey(id);
            //并且删除掉当前规格下的规格选项
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(query);
        }
    }

    //批量提交申请
    @Override
    public void updateStat(Long[] ids) {
        for (Long id : ids) {
            //先提交规格对象
            Specification specification = specificationDao.selectByPrimaryKey(id);
            specification.setStat("1");
            specificationDao.updateByPrimaryKeySelective(specification);

            //并且删除掉当前规格下的规格选项
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(id);
            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
            for (SpecificationOption specificationOption : specificationOptions) {
                specificationOption.setStat("1");
                specificationOptionDao.updateByPrimaryKey(specificationOption);
            }
        }
    }

    //多条件分页查询
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if (specification != null) {
            if (specification.getSpecName() != null && !"".equals(specification.getSpecName())) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
            if (specification.getStat() != null && !"".equals(specification.getStat())) {
                criteria.andStatEqualTo(specification.getStat());
            }
            if(specification.getStat()!=null){
                criteria.andStatEqualTo(specification.getStat());
            }
        }
        PageHelper.startPage(page, rows);
        Page<Specification> specificationPage = (Page<Specification>) specificationDao.selectByExample(query);
        return new PageResult(specificationPage.getTotal(), specificationPage.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        List<Map> maps = specificationDao.selectOptionList();
        return maps;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if(ids!=null&&ids.length>0){
            for (Long id : ids) {
                Specification specification = new Specification();
                specification.setId(id);
                specification.setStat(status);
                specificationDao.updateByPrimaryKeySelective(specification);
            }
        }
    }
}
