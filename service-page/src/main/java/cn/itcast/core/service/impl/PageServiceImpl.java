package cn.itcast.core.service.impl;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.service.PageService;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService,ServletContextAware {

    private ServletContext servletContext;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void createGoodsHtml(Long goodsId,Map<String,Object> rootMap) throws Exception{
        //1. 获取freemarker初始化对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        //2. 通过初始化对象获取模板对象, 并且指定模板的名称
        Template template = configuration.getTemplate("item.ftl");
        //3. 定义静态页面的名称   页面名称 = 商品id + .html
        String path = goodsId + ".html";
        //4. 通过页面相对路径转化成绝对路径(保存生成后的页面的位置),
        // 例如: goodsId.html 转换为D:\intellijWorkSpace2\pyg_parent\service_page\target\service_page\goodsId.html
        String realPath = getRealPath(path);
        //5. 定义输出流,并指定字符集
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)),"utf-8");
        //6. 生成静态化页面
        template.process(rootMap,out);
        //7. 关闭流
        out.close();
    }

    @Override
    public Map<String, Object> findGoodsById(Long goodsId) {
        Map<String, Object> map = new HashMap<>();
        //1. 根据商品id获取商品对象
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //2. 根据商品id获取商品详情对象
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
        //3. 根据商品id获取库存集合对象
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        //状态为已审核状态
        criteria.andStatusEqualTo("1");
        //指定SPU(tb_goods)id
        criteria.andGoodsIdEqualTo(goodsId);
        //设置按照状态降序,保证第一个为默认
        //query.setOrderByClause("is_default desc");
        List<Item> itemList = itemDao.selectByExample(query);
        //4. 根据商品对象的分类id, 获取分类名称
        if(goods != null){
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
           map.put("itemCat1",itemCat1);
           map.put("itemCat2",itemCat2);
           map.put("itemCat3",itemCat3);
        }
        //5. 将以上获取的对象封装到Map中返回
        map.put("goods",goods);
        map.put("goodsDesc",goodsDesc);
        map.put("itemList",itemList);
        return map;
    }

    //通过实现ServletContextAware接口初始化ServletContext对象
    @Override
    public void setServletContext(ServletContext servletContext) {
         this.servletContext = servletContext;
    }
    //将相对路径转成绝对路径
    private String getRealPath(String path){
        String realPath = servletContext.getRealPath(path);
        return realPath;
    }
}
