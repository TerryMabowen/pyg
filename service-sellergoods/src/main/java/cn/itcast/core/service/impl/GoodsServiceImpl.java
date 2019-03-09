package cn.itcast.core.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.*;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("topicPageAndSolrDestination")
    private ActiveMQTopic topicPageAndSolrDestination;

    //上架发布订阅
    @Autowired
    @Qualifier("topicPageAndSolrDestination2")
    private ActiveMQTopic topicPageAndSolrDestination2;

    @Autowired
    @Qualifier("queueSolrDeleteDestination")
    private ActiveMQQueue queueSolrDeleteDestination;
    @Autowired
    OrderDao orderDao;

    //下架点对点
    @Autowired
    @Qualifier("queueSolrDeleteDestination2")
    private ActiveMQQueue queueSolrDeleteDestination2;

    @Override
    public void add(GoodsEntity goodsEntity) {
        //设置未申请状态
        goodsEntity.getGoods().setAuditStatus("0");
        //添加商品基本信息
        goodsDao.insertSelective(goodsEntity.getGoods());
        //设置ID
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        //添加商品扩展数据
        goodsDescDao.insertSelective(goodsEntity.getGoodsDesc());
        //添加库存集合对象
        List<Item> itemList = goodsEntity.getItemList();
        if ("1".equals(goodsEntity.getGoods().getIsEnableSpec())) {
            //在添加商品的时候是否启用规格为勾选状态, 有规格, 有库存对象
            if (itemList != null) {
                for (Item item : itemList) {
                    //初始化库存对象的属性值
                    setItemValues(goodsEntity, item);
                    //库存标题, 通过 商品名称 + 规格=库存标题
                    //商品名称
                    String title = goodsEntity.getGoods().getGoodsName();
                    //获取规格json字符串
                    String specJsonStr = item.getSpec();
                    //解析成map, {"机身内存":"16G","网络":"联通3G"}
                    Map<String, String> specMap = JSON.parseObject(specJsonStr, Map.class);
                    //获取规格value的集合
                    Collection<String> values = specMap.values();
                    for (String value : values) {
                        title += " " + value;
                    }
                    //初始化库存标题
                    item.setTitle(title);
                    //添加库存
                    itemDao.insertSelective(item);
                }
            }
        } else {
            //在添加商品的时候是否启用规格为未勾选状态, 没有规格, 没有库存对象
            //初始化库存对象的属性值
            Item item = new Item();
            //初始化库存对象的属性值
            setItemValues(goodsEntity, item);
            //初始化商品名为库存标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            //初始化价格
            item.setPrice(new BigDecimal("0"));
            //初始化规格
            item.setSpec("{}");
            //初始化库存量
            item.setNum(0);
            //初始化是否默认
            item.setIsDefault("1");
            //添加库存
            itemDao.insertSelective(item);
        }
    }

    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        PageHelper.startPage(page, rows);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        //指定条件为未逻辑删除记录
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {
                //状态是通过复选框选择的,只需判断是否为空即可
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName().trim())) {
                //前后去空格
                criteria.andGoodsNameLike("%" + goods.getGoodsName().trim() + "%");
            }
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0 && !"admin".equals(goods.getSellerId()) && !"wc".equals(goods.getSellerId())) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        Page<Goods> goodList = (Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(goodList.getTotal(), goodList.getResult());
    }

    @Override
    public GoodsEntity findOne(Long id) {
        GoodsEntity goodsEntity = new GoodsEntity();
        Goods goods = goodsDao.selectByPrimaryKey(id);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);
        goodsEntity.setGoods(goods);
        goodsEntity.setGoodsDesc(goodsDesc);
        goodsEntity.setItemList(items);
        return goodsEntity;
    }

    @Override
    public void update(GoodsEntity goodsEntity) {
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        goodsDescDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        //库存数据先删除,再添加
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(query);
        //删除后保存修改的数据
        //首先判断是否启用了规格
        if ("1".equals(goodsEntity.getGoods().getIsEnableSpec())) {
            //启用
            List<Item> itemList = goodsEntity.getItemList();
            if (itemList != null) {
                for (Item item : itemList) {
                    //初始化库存对象的属性值
                    setItemValues(goodsEntity, item);
                    //库存标题, 通过 商品名称 + 规格=库存标题
                    //商品名称
                    String title = goodsEntity.getGoods().getGoodsName();
                    //获取规格json字符串
                    String specJsonStr = item.getSpec();
                    //解析成map, {"机身内存":"16G","网络":"联通3G"}
                    Map<String, String> specMap = JSON.parseObject(specJsonStr, Map.class);
                    //获取规格value的集合
                    Collection<String> values = specMap.values();
                    for (String value : values) {
                        title += " " + value;
                    }
                    //初始化库存标题
                    item.setTitle(title);
                    //添加库存
                    itemDao.insertSelective(item);
                }
            }
        } else {
            //不启用
            //初始化库存对象的属性值
            Item item = new Item();
            //初始化库存对象的属性值
            setItemValues(goodsEntity, item);
            //初始化商品名为库存标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            //初始化价格
            item.setPrice(new BigDecimal(0));
            //初始化规格
            item.setSpec("{}");
            //初始化库存量
            item.setNum(0);
            //初始化是否默认
            item.setIsDefault("1");
            //添加库存
            itemDao.insertSelective(item);
        }
    }

    //删除(逻辑删除,并不是真的在数据库中删除,只是在页面上不显示)
    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");
        if (ids != null && ids.length > 0) {
            for (final Long id : ids) {
                //根据商品id逻辑删除对应的商品信息
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                //将商品id作为消息发送到消息服务器下架队列
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        //id必须final修饰
                        TextMessage message = session.createTextMessage(String.valueOf(id));
                        return message;
                    }
                });
            }
        }
    }

    //上架
    @Override
    public void upShelf(Long[] ids) {
        if (ids != null) {
            for (final Long id : ids) {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setIsMarketable("1");
                goodsDao.updateByPrimaryKeySelective(goods);
                jmsTemplate.send(topicPageAndSolrDestination2, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
        }
    }

    //下架
    @Override
    public void downShelf(Long[] ids) {
        if (ids != null) {
            for (final Long id : ids) {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setIsMarketable("0");
                goodsDao.updateByPrimaryKeySelective(goods);
                jmsTemplate.send(queueSolrDeleteDestination2, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
        }
    }

    //上架功能查询商品列表
    @Override
    public List<Goods> findGoodsForUpShelf(String sellerId) {
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        criteria.andAuditStatusEqualTo("1");
        criteria.andSellerIdEqualTo(sellerId);
        criteria.andIsMarketableEqualTo("0");

        GoodsQuery.Criteria criteria1 = goodsQuery.createCriteria();
        criteria1.andAuditStatusEqualTo("1");
        criteria1.andSellerIdEqualTo(sellerId);
        criteria1.andIsMarketableIsNull();
        goodsQuery.or(criteria1);

        List<Goods> goodsList = goodsDao.selectByExample(goodsQuery);
        return goodsList;
    }

    //下架功能查询商品列表
    @Override
    public List<Goods> findGoodsForDownShelf(String sellerId) {
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        criteria.andIsMarketableEqualTo("1");
        criteria.andSellerIdEqualTo(sellerId);
        List<Goods> goodsList = goodsDao.selectByExample(goodsQuery);
        return goodsList;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            Item item = new Item();
            for (final Long id : ids) {
                //1. 更新商品表的状态
                goods.setId(id);
                goods.setAuditStatus(status);
                goodsDao.updateByPrimaryKeySelective(goods);
                //2. 更新库存表的商品状态
                item.setStatus(status);
                ItemQuery query = new ItemQuery();
                ItemQuery.Criteria criteria = query.createCriteria();
                criteria.andGoodsIdEqualTo(id);
                itemDao.updateByExampleSelective(item, query);
                if ("1".equals(status)) {
                    //3. 将商品id作为消息发送到消息服务器上架队列上
                    //因为接收方为两个服务器(search和page)所以使用订阅发布模式
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            //id必须final修饰
                            TextMessage message = session.createTextMessage(String.valueOf(id));
                            return message;
                        }
                    });
                }
            }
        }
    }



    //初始化库存对象的值
    private Item setItemValues(GoodsEntity goodsEntity, Item item) {
        //品牌名称
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //商品id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //分类id
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        //分类名称
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //卖家id
        item.setSellerId(goodsEntity.getGoods().getSellerId());
        //卖家名称
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(new Date());
        //示例图片
        String imgJsonStr = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(imgJsonStr, Map.class);
        if (maps != null) {
            item.setImage((String) maps.get(0).get("url"));
        }
        //库存状态,默认为0 ,未审核状态
        item.setStatus("0");
        return item;
    }




}
