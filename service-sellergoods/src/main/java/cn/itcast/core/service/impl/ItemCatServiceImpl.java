package cn.itcast.core.service.impl;


import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.ItemCatService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import javafx.scene.input.InputMethodTextRun;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;

    //查询所有
    @Override
    public List<ItemCat> queryAll() {
        return itemCatDao.selectByExample(null);
    }

    /*//分页查询
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<ItemCat> itemCatPage = (Page<ItemCat>)itemCatDao.selectByExample(null);
        PageResult pageResult = new PageResult(itemCatPage.getTotal(),itemCatPage.getResult());
        return pageResult;
    }*/
    //新增分类
    @Override
    public void add(ItemCat itemCat) {
        //itemCat.setStat("0");
        itemCat.setStat(Constants.YI_SHEN_HE);
        itemCatDao.insertSelective(itemCat);
    }

    //查询一条
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    //更新品牌
    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    //批量删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            itemCatDao.deleteByPrimaryKey(id);
        }
    }


    //批量提交申请
    @Override
    public void updateStat(Long[] ids) {
        for (Long id : ids) {
            //先提交规格对象
            ItemCat itemCat = itemCatDao.selectByPrimaryKey(id);
            itemCat.setStat("1");
            itemCatDao.updateByPrimaryKeySelective(itemCat);
        }
    }

    //多条件查询不带分页
    @Override
    public PageResult search(Integer page, Integer rows, ItemCat itemCat) {
        PageHelper.startPage(page, rows);
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        if (itemCat != null) {
            if (itemCat.getName() != null && !"".equals(itemCat.getName())) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }
            if(itemCat.getStat()!=null){
                criteria.andStatEqualTo(itemCat.getStat());
            }
            if (itemCat.getStat() != null && !"".equals(itemCat.getStat())) {
                criteria.andStatEqualTo(itemCat.getStat());
            }
        }
        PageHelper.startPage(page, rows);
        Page<ItemCat> itemCatPage = (Page<ItemCat>) itemCatDao.selectByExample(query);
        return new PageResult(itemCatPage.getTotal(),itemCatPage.getResult());
    }



//根据上级ID查询商品分类列表
    //select * from tb_item_cat where parent_id = ?

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        /**
         * 查询分类所有数据, 缓存到redis中, 分类名称作为key, 模板id作为value
         */
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if (itemCats != null && itemCats.size() > 0) {
            for (ItemCat itemCat : itemCats) {
                //分类名称作为key, 模板id作为value存到Redis中
                redisTemplate.boundHashOps(Constants.REDIS_CATEGORY_LIST).put(itemCat.getName(), itemCat.getTypeId());
            }
        }

        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCatList = itemCatDao.selectByExample(query);
        return itemCatList;

    }
    @Override
    public void updateStatus(Long []  ids, String status) {
        if(ids!=null){
            for (Long id : ids) {
                ItemCat itemCat = new ItemCat();
                itemCat.setId(id);
                itemCat.setStat(status);
                itemCatDao.updateByPrimaryKeySelective(itemCat);
            }
        }
    }

    @Override
    public List<ItemCat> selectByParentId(Long parentId) {
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCatList1 = itemCatDao.selectByExample(query);
        for (ItemCat itemCat1 : itemCatList1) {
            criteria.andParentIdEqualTo(itemCat1.getParentId());
            List<ItemCat> itemCatList2 = itemCatDao.selectByExample(query);
            for (ItemCat itemCat2 : itemCatList2) {
                criteria.andParentIdEqualTo(itemCat2.getParentId());
                List<ItemCat> itemCatList3 = itemCatDao.selectByExample(query);
                itemCat2.setItemCatList(itemCatList3);
            }
            itemCat1.setItemCatList(itemCatList2);
        }
        return itemCatList1;
    }

}


