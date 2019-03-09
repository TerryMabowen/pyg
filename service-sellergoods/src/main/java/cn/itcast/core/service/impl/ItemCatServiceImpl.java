package cn.itcast.core.service.impl;


import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.service.ItemCatService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import javafx.scene.input.InputMethodTextRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    //分页查询
    /*@Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<ItemCat> itemCatPage = (Page<ItemCat>)itemCatDao.selectByExample(null);
        PageResult pageResult = new PageResult(itemCatPage.getTotal(),itemCatPage.getResult());
        return pageResult;
    }*/
    //新增品牌
    @Override
    public void add(ItemCat itemCat) {
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

    //多条件分页查询
   /* @Override
    public PageResult search(Integer page, Integer rows, ItemCat itemCat) {
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        if(itemCat != null){
            if(itemCat.getName() != null && !"".equals(itemCat.getName())){
                criteria.andNameLike("%"+itemCat.getName()+"%");
            }
        }
        PageHelper.startPage(page, rows);
        Page<ItemCat> itemCatPage = (Page<ItemCat>) itemCatDao.selectByExample(query);
        return new PageResult(itemCatPage.getTotal(),itemCatPage.getResult());
    }*/
    //根据上级ID查询商品分类列表
    //select * from tb_item_cat where parent_id = ?
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        /**
         * 查询分类所有数据, 缓存到redis中, 分类名称作为key, 模板id作为value
         */
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if(itemCats != null && itemCats.size() > 0){
            for (ItemCat itemCat : itemCats) {
                //分类名称作为key, 模板id作为value存到Redis中
                redisTemplate.boundHashOps(Constants.REDIS_CATEGORY_LIST).put(itemCat.getName(),itemCat.getTypeId());
            }
        }


        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCatList = itemCatDao.selectByExample(query);
        return itemCatList;
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
