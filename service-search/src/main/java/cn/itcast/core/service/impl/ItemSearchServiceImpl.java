package cn.itcast.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.service.ItemSearchService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemDao itemDao;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //1. 根据关键字分页查询高亮显示
        Map<String, Object> map = highLightSearch(searchMap);
        //2. 根据关键字分组查询分类集合
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //获取页面上用户点击的分类
        String categoryName = (String) searchMap.get("category");
        //3. 根据分类名称到redis中查询对应的模板id, 根据模板id查询对应的品牌集合和规格集合
        if (categoryName == null || "".equals(categoryName)) {
            //如果消费者没有在页面上点击分类, 那么默认根据分类集合中的第一个分类查询对应的品牌集合和规格集合
            if (categoryList != null && categoryList.size() > 0) {
                categoryName = categoryList.get(0);
                Map<String, Object> brandAndSpecList = findBrandsAndSpecsByCategoryName(categoryName);
                //将一个map集合放入到另一个map集合,可以对其转成set遍历一个一个放入另一个map集合,
                // jdk考虑到这一情况,map有个方法,map.putAll(map2)可以将一个map集合放入另一个map集合中
                if (brandAndSpecList != null) {
                    map.putAll(brandAndSpecList);
                }
            }
        } else {
            //如果消费者在页面上点击分类, 则根据这个分类名称查询对应的品牌和规格集合
            Map<String, Object> brandAndSpecList = findBrandsAndSpecsByCategoryName(categoryName);
            if (brandAndSpecList != null) {
                map.putAll(brandAndSpecList);
            }
        }

        return map;
    }

    //将运营商通过审核的商品集合添加到solr库中
    @Override
    public void importList(Long goodsId, String status) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        if (goodsId != null) {
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo(status);
        }
        List<Item> itemList = itemDao.selectByExample(query);
        //解析规格
        if (itemList != null && itemList.size() > 0) {
            for (Item item : itemList) {
                String specJsonStr = item.getSpec();
                Map<String, String> maps = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(maps);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }
    }

    //将商家删除的商品数据从solr库中删除
    @Override
    public void deleteList(Long goodsId) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").is(goodsId);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }

    //关键字高亮查询
    private Map<String, Object> highLightSearch(Map searchMap) {
        //获取关键字
        String keywords = (String) searchMap.get("keywords");
        //获取当前页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        //获取每页显示条数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        //获取用户选中的分类名称
        String category = (String) searchMap.get("category");
        //获取用户选中的品牌名称
        String brand = (String) searchMap.get("brand");
        //获取用户选中的规格名称
        Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
        //获取用户选中的价格区间字符串
        String priceStr = (String) searchMap.get("price");
        //获取用户选中的排序的字段(价格?销量?评论....)
        String sortField = (String) searchMap.get("sortField");
        //获取用户选中的排序方式(升序ASC?降序DESC)
        String sortType = (String) searchMap.get("sort");

        //多关键字之间间去空格
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replace(" ", "");
        }

        Map<String, Object> map = new HashMap<>();
        //创建高亮查询条件对象
        HighlightQuery query = new SimpleHighlightQuery();
        //创建高亮选项对象
        HighlightOptions options = new HighlightOptions();
        //设置高亮显示的域
        options.addField("item_title");
        //设置选项前缀标签
        options.setSimplePrefix("<em style='color:red'>");
        //设置选项后缀标签
        options.setSimplePostfix("</em>");

        query.setHighlightOptions(options);

        //分页设置
        if (pageNo != null || pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize != null || pageSize <= 0) {
            pageSize = 40;
        }
        Integer startIn = (pageNo - 1) * pageSize;  //分页查询开始索引  相当于 sql语句中limit 0,10 中的0(从第几个数据开始查询)
        query.setOffset(startIn);
        query.setRows(pageSize);

        //设置过滤条件
        //根据分类过滤
        if (category != null && !"".equals(category)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建过滤条件对象
            Criteria item_category = new Criteria("item_category").is(category);
            //将条件对象放入过滤对象中
            filterQuery.addCriteria(item_category);
            //将过滤对象放入查询对象中
            query.addFilterQuery(filterQuery);
        }
        // 根据品牌过滤
        if (brand != null && !"".equals(brand)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建过滤条件对象
            Criteria item_brand = new Criteria("item_brand").is(brand);
            //将条件对象放入过滤对象中
            filterQuery.addCriteria(item_brand);
            //将过滤对象放入查询对象中
            query.addFilterQuery(filterQuery);
        }
        //根据规格过滤
        if (specMap != null) {
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //创建过滤条件对象
                Criteria item_spec = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                //将条件对象放入过滤对象中
                filterQuery.addCriteria(item_spec);
                //将过滤对象放入查询对象中
                query.addFilterQuery(filterQuery);
            }
        }
        //根据价格区间过滤
        if (priceStr != null && !"".equals(priceStr)) {
            String[] split = priceStr.split("-");
            String leftPriceStr = split[0];
            String rightPriceStr = split[1];
            if ("0".equals(leftPriceStr)) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //创建过滤条件对象
                Criteria item_price = new Criteria("item_price").lessThanEqual(rightPriceStr);
                //将条件对象放入过滤对象中
                filterQuery.addCriteria(item_price);
                //将过滤对象放入查询对象中
                query.addFilterQuery(filterQuery);
            } else if ("*".equals(rightPriceStr)) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //创建过滤条件对象
                Criteria item_price = new Criteria("item_price").greaterThanEqual(leftPriceStr);
                //将条件对象放入过滤对象中
                filterQuery.addCriteria(item_price);
                //将过滤对象放入查询对象中
                query.addFilterQuery(filterQuery);
            } else if (!"0".equals(leftPriceStr) && !"*".equals(rightPriceStr)) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //创建过滤条件对象
                Criteria item_price_less = new Criteria("item_price").lessThanEqual(rightPriceStr);
                Criteria item_price_greater = new Criteria("item_price").greaterThanEqual(leftPriceStr);
                //将条件对象放入过滤对象中
                filterQuery.addCriteria(item_price_less);
                filterQuery.addCriteria(item_price_greater);
                //将过滤对象放入查询对象中
                query.addFilterQuery(filterQuery);
            }
        }
        //根据排序字段进行排序
        if (sortField != null && !"".equals(sortField)) {
            if ("ASC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if ("DESC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }


        //is(String keywords) 调用该方法后是通过solr配置的分词器将关键字切分词,然后一个一个查询
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);


        HighlightPage<Item> itemPages = solrTemplate.queryForHighlightPage(query, Item.class);
        //获取高亮显示item集合
        List<Item> itemList = new ArrayList<>();
        if (itemPages != null) {
            List<HighlightEntry<Item>> highlighted = itemPages.getHighlighted();
            if (highlighted != null && highlighted.size() > 0) {
                for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                    //获取原装实例类对象(没有高亮显示,原版的)
                    Item item = itemHighlightEntry.getEntity();
                    //获取高亮显示实例类对象
                    List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
                    if (highlights != null && highlights.size() > 0) {
                        //如果有结果,则将item中的标题设为高亮显示,否则原样显示
                        List<String> snipplets = highlights.get(0).getSnipplets();
                        if (snipplets != null && snipplets.size() > 0) {
                            item.setTitle(snipplets.get(0));
                        }
                    }
                    itemList.add(item);
                }
            }
        }
        //高亮显示的数据集合
        map.put("rows", itemList);
        //搜索的结果总数量
        map.put("total", itemPages.getTotalElements());
        //总页数
        map.put("totalPages", itemPages.getTotalPages());

        return map;
    }

    //根据关键字分组(分类去重)查询出分类集合
    private List<String> searchCategoryList(Map searchMap) {
        List<String> categoryNameList = new ArrayList<>();
        //获取关键字
        String keywords = (String) searchMap.get("keywords");

        //多关键字之间间去空格
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replace(" ", "");
        }

        //创建查询条件对象
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);

        //设置分组选项
        GroupOptions groupOptions = new GroupOptions();
        //设置分组的域
        groupOptions.addGroupByField("item_category");

        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<Item> itemGroupPage = solrTemplate.queryForGroupPage(query, Item.class);
        //得到分组的结果集
        GroupResult<Item> groupResult = itemGroupPage.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();

        // long totalElements = groupEntries.getTotalElements();
        //  System.out.println(totalElements);
        //得到分组结果集
        List<GroupEntry<Item>> content = groupEntries.getContent();

        if (content != null && content.size() > 0) {
            //遍历集合
            for (GroupEntry<Item> itemGroupEntry : content) {
                //获取分组结果的分类名称
                String categoryName = itemGroupEntry.getGroupValue();
                //  System.out.println("=======categoryName======="+categoryName);
                //将分类名称放入集合
                categoryNameList.add(categoryName);
            }
        }
//        else {
//            System.out.println("*****************************");
//        }
        return categoryNameList;
    }

    //根据分类名称, 查询对应的模板id, 根据模板id查询对应的品牌集合和规格集合数据返回
    private Map<String, Object> findBrandsAndSpecsByCategoryName(String categoryName) {
        Map<String, Object> map = new HashMap<>();
        if (categoryName != null) {
            //1. 根据分类名称到redis中查询对应的模板id
            Long templateId = (Long) redisTemplate.boundHashOps(Constants.REDIS_CATEGORY_LIST).get(categoryName);
            if (templateId != null) {
                //2. 根据模板id到redis中获取对应的品牌集合数据
                List<Map> brandList = (List<Map>) redisTemplate.boundHashOps(Constants.REDIS_BRAND_LIST).get(templateId);
                //3. 根据模板id到redis中获取对应的规格集合数据
                List<Map> specList = (List<Map>) redisTemplate.boundHashOps(Constants.REDIS_SPEC_LIST).get(templateId);
                //4. 将获取到的品牌集合和规格集合数据封装到Map中返回
                map.put("brandList", brandList);
                map.put("specList", specList);
            }
        }
        return map;
    }

//    @Override  //关键字搜索,不加过滤条件
//    public Map<String, Object> search(Map searchMap) {
//        String keywords = (String) searchMap.get("keywords");
//        Integer pageNo = (Integer) searchMap.get("pageNo");
//        Integer pageSize = (Integer) searchMap.get("pageSize");
//        Map<String,Object> map = new HashMap<>();
//        Query query = new SimpleQuery();
//
//        Criteria criteria = new Criteria("item_keywords").is(keywords);
//        query.addCriteria(criteria);
//        Integer startIn = (pageNo - 1) * pageSize;
//        query.setOffset(startIn);
//        query.setRows(pageSize);
//
//        ScoredPage<Item> itemPages = solrTemplate.queryForPage(query, Item.class);
//        map.put("rows",itemPages.getContent());
//        map.put("total",itemPages.getTotalElements());
//        map.put("totalPages",itemPages.getTotalPages());
//        return map;
//    }
    //wqewqeqweqweqwasdasdasdasdasdsaasdasdasdsaasdasdas
}
