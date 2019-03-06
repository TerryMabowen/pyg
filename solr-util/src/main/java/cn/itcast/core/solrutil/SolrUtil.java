package cn.itcast.core.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.item.ItemQuery.Criteria;

@Component
public class SolrUtil {

	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	
	public void importItemData() {
		ItemQuery query = new ItemQuery();
		Criteria criteria = query.createCriteria();
		criteria.andStatusEqualTo("1");//已审核
		List<Item> items = itemDao.selectByExample(query);
		//System.out.println("商品列表");
		for (Item item : items) {
			//System.out.println(item.getTitle());
			//从数据库中提取规格json字符串转换为map
			Map<String, String> specMap = JSON.parseObject(item.getSpec(),Map.class);
			item.setSpecMap(specMap);
		}
		//System.out.println("结束");
		solrTemplate.saveBeans(items);
		solrTemplate.commit();
	}
	//main方法
	public static void main(String[] args) {
		ApplicationContext context =  new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil)context.getBean("solrUtil");
		solrUtil.importItemData();
	}
}
