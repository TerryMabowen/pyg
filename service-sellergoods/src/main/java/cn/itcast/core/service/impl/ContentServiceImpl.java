package cn.itcast.core.service.impl;


import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.service.ContentService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    //查询所有
    @Override
    public List<Content> queryAll() {
        return contentDao.selectByExample(null);
    }

    //新增品牌
    @Override
    public void add(Content content) {
        redisTemplate.boundHashOps(Constants.REDIS_CONTENT_LIST).delete(content.getCategoryId());
        contentDao.insertSelective(content);
    }
    //查询一条
    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }
    //更新品牌
    @Override
    public void update(Content content) {
        //1. 根据页面传入的广告对象的主键id, 到数据库中查询对应的广告对象
        Content oldContent = findOne(content.getId());
        //2. 根据数据库中查询到的广告对象中的分类id, 删除redis中对应的广告集合数据
        redisTemplate.boundHashOps(Constants.REDIS_CONTENT_LIST).delete(oldContent.getCategoryId());
        //3. 根据页面传入的新的广告对象中的分类id, 删除redis中对应的广告集合数据
        redisTemplate.boundHashOps(Constants.REDIS_CONTENT_LIST).delete(content.getCategoryId());
        contentDao.updateByPrimaryKeySelective(content);
    }
    //批量删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //1. 根据广告id到数据库中查询到广告对象
            Content content = findOne(id);
            //2. 根据广告对象中的分类id删除redis中的对应的广告集合数据
            redisTemplate.boundHashOps(Constants.REDIS_CONTENT_LIST).delete(content.getCategoryId());
            contentDao.deleteByPrimaryKey(id);
        }
    }
    //多条件分页查询
    @Override
    public PageResult search(Integer page, Integer rows, Content content) {
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if(content != null){
            if(content.getTitle()!= null && !"".equals(content.getTitle())){
                criteria.andTitleLike("%"+content.getTitle()+"%");
            }
        }
        PageHelper.startPage(page, rows);
        Page<Content> contentPage = (Page<Content>) contentDao.selectByExample(query);
        return new PageResult(contentPage.getTotal(),contentPage.getResult());
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        //开启状态
        criteria.andStatusEqualTo("1");
        //排序
        query.setOrderByClause("sort_order");
        List<Content> contentList = contentDao.selectByExample(query);
        return contentList;
    }
    //从Redis中查询
    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        List<Content> contentList = (List<Content>)redisTemplate.boundHashOps(Constants.REDIS_CONTENT_LIST).get(categoryId);
        if(contentList != null && contentList.size() > 0){
            //如果Redis中有数据,直接返回结果
            return contentList;
        }else {
            //如果没有,去数据库中查询
            contentList = findByCategoryId(categoryId);
            //并保存一份到Redis中,这样下回就能直接在Redis中找到
            redisTemplate.boundHashOps(Constants.REDIS_CONTENT_LIST).put(categoryId,contentList);
            return contentList;
        }
    }

    @Override
    public List<Content> findNameById(String floor) {
        long l = Long.parseLong(floor);
        ContentQuery contentQuery = new ContentQuery();
        ContentQuery.Criteria criteria = contentQuery.createCriteria();
        criteria.andCategoryIdEqualTo(l);
        List<Content> contentList = contentDao.selectByExample(contentQuery);
        return contentList;
    }


}
