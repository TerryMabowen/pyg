package cn.itcast.core.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.entity.DataM;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.service.MarketService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class MarketServiceImpl implements MarketService {
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private BrandDao brandDao;
    // 响应回前端每品牌,销售额,
    @Override
    public DataM showSalesMoney() {
        //创建map
        Map<String, Object> map = new HashMap<>();
        List<OrderItem> orderItemList = orderItemDao.selectByExample(null);
        for (OrderItem orderItem : orderItemList) {
            //获取品牌名称
            Long goodsId = orderItem.getGoodsId();
            Goods goods = goodsDao.selectByPrimaryKey(goodsId);
            Long brandId = goods.getBrandId();
            Brand brand = brandDao.selectByPrimaryKey(brandId);
            String brandName = brand.getName();
            double totalMoney = 0;
            if (goodsId != null) {
                OrderItemQuery query = new OrderItemQuery();
                query.createCriteria().andGoodsIdEqualTo(goodsId);
                List<OrderItem> orderItems = orderItemDao.selectByExample(query);
                for (OrderItem item : orderItems) {
                    //获取总金额
                    double totalFee = item.getTotalFee().doubleValue();
                    totalMoney += totalFee;
                }
                map.put(brandName, String.valueOf(totalMoney));
            }
        }
        // 获取品牌名
        Set<String> keys = map.keySet();
        // 获取总销量
        Collection<Object> values =  map.values();

        // 创建返回值
        DataM data = new DataM();
        data.setBrandNameList(new ArrayList<Object>(keys));
        data.setTotalMoneyList(new ArrayList<Object>(values));
        return data;
//		return map;
    }

    //响应回前端每品牌,销售量
    @Override
    public DataM showSalesQuantity() {
        //创建map
        Map<String, Object> map = new HashMap<>();
        List<OrderItem> orderItemList = orderItemDao.selectByExample(null);
        for (OrderItem orderItem : orderItemList) {
            //获取品牌名称
            Long goodsId = orderItem.getGoodsId();
            Goods goods = goodsDao.selectByPrimaryKey(goodsId);
            Long brandId = goods.getBrandId();
            Brand brand = brandDao.selectByPrimaryKey(brandId);
            String brandName = brand.getName();
            int totalNum = 0;
            if (goodsId != null) {
                OrderItemQuery query = new OrderItemQuery();
                query.createCriteria().andGoodsIdEqualTo(goodsId);
                List<OrderItem> orderItems = orderItemDao.selectByExample(query);
                for (OrderItem item : orderItems) {
                    //获取总销量
                    int num = item.getNum();
                    totalNum += num;
                }
                map.put(brandName, String.valueOf(totalNum));
            }
        }

        // 获取品牌名
        Set<String> keys = map.keySet();
        // 获取总销量
        Collection<Object> values =  map.values();

        // 创建返回值
        DataM data = new DataM();
        data.setBrandNameList(new ArrayList<Object>(keys));
        data.setTotalNumList(new ArrayList<Object>(values));
        return data;
//		return map;
    }


}
