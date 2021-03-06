package cn.itcast.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.cart.BuyerCart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<BuyerCart> addGoodsToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2. 判断商品是否存在不存在, 抛异常
        if(item == null ){
            throw new RuntimeException("商品不存在!");
        }
        //3. 判断商品状态是否为1已审核, 状态不对抛异常
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("商品未审核通过!");
        }
        //4.获取商家ID
        String sellerId = item.getSellerId();
        //5.根据商家ID查询购物车列表中是否存在该商家的购物车
        BuyerCart buyerCart = findCartBySellerId(cartList, sellerId);
        //6.判断如果购物车列表中不存在该商家的购物车
        if(buyerCart == null){
            //6.a.1 新建购物车对象
            buyerCart = new BuyerCart();
            //创建购物项集合
            List<OrderItem> orderItemList = new ArrayList<>();
            //创建购物项对象
            OrderItem orderItem = createOrderItem(item, num);
            //购物项放入购物项集合中
            orderItemList.add(orderItem);
            //购物项集合放入购物车中
            buyerCart.setOrderItemList(orderItemList);
            //设置本购物车所属卖家的id
            buyerCart.setSellerId(sellerId);
            //设置本购物车所属卖家的名称
            buyerCart.setSellerName(item.getSeller());
            //6.a.2 将新建的购物车对象添加到购物车列表
            cartList.add(buyerCart);
        }else {
            //6.b.1如果购物车列表中存在该商家的购物车 (查询购物车明细列表中是否存在该商品)
            List<OrderItem> orderItemList = buyerCart.getOrderItemList();
            //查询购物车明细列表中是否存在该商品
            OrderItem orderItem = findOrderItemByItemId(orderItemList, itemId);
            //6.b.2判断购物车明细是否为空
            if(orderItem == null){
                //6.b.3为空，新增购物车明细
                orderItem = createOrderItem(item, num);
                //将新创建的购物项对象放入购物项集合中
                orderItemList.add(orderItem);
            }else {
                //6.b.4不为空，在原购物车明细上添加数量，更改金额
                //购买量 = 原来对象的购买量 + 现在的购买量
                orderItem.setNum(orderItem.getNum() + num);
                //总价 = 最新的购买量 X 单价(BigDecimal中没有+ - * / 运算符,需要调用方法实现计算功能)
                orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
                //6.b.5如果购物车明细中数量操作后小于等于0，则移除
                if(orderItem.getNum() <= 0){
                    orderItemList.remove(orderItem);
                }
                //6.b.6如果购物车中购物车明细列表为空,则移除
                if(orderItemList.size() <= 0 ){
                    cartList.remove(buyerCart);
                }
            }
        }
        //7. 返回购物车列表对象
        return cartList;
    }

    @Override
    public void setCartListToRedis(String userName, List<BuyerCart> cartList) {
          redisTemplate.boundHashOps(Constants.REDIS_CARTLIST).put(userName,cartList);
    }

    @Override
    public List<BuyerCart> getCartListFromRedis(String userName) {
        List<BuyerCart> cartList = (List<BuyerCart>)redisTemplate.boundHashOps(Constants.REDIS_CARTLIST).get(userName);
        if(cartList == null){
            cartList = new ArrayList<BuyerCart>();
        }
        return cartList;
    }

    @Override
    public List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList) {
        //1. 遍历cookie购物车集合
        if (cookieCartList != null) {
            for (BuyerCart cart : cookieCartList) {
                //2. 遍历购物车中的购物项集合
                if (cart.getOrderItemList() != null) {
                    for (OrderItem orderItem : cart.getOrderItemList()) {
                        //向redis中的购物车集合中加入, 指定的商品
                        redisCartList = addGoodsToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
                    }
                }
            }
        }
        return redisCartList;
    }

    //创建购物项对象(根据商品库存和购买数量)
    private OrderItem createOrderItem(Item item, Integer num) {
        if(num <= 0){
            throw new RuntimeException("请至少选择一个商品");
        }
        OrderItem orderItem = new OrderItem();
        //购买数量
        orderItem.setNum(num);
        //商品id
        orderItem.setGoodsId(item.getGoodsId());
        //库存id
        orderItem.setItemId(item.getId());
        //商品示例图片
        orderItem.setPicPath(item.getImage());
        //商品单价
        orderItem.setPrice(item.getPrice());
        //商品卖家id
        orderItem.setSellerId(item.getSellerId());
        //商品标题
        orderItem.setTitle(item.getTitle());
        //总价 = 单价乘以购买数量
        //BigDecimal中没有+ - * / 运算符,需要调用方法实现计算功能
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }

    //根据商品库存id到购物项集合中查询购物项对象
    private OrderItem findOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    //根据商家id到购物车列表中查询购物车选项
    private BuyerCart findCartBySellerId(List<BuyerCart> cartList,String sellerId){
        if(cartList != null){
            for (BuyerCart buyerCart : cartList) {
                if(buyerCart.getSellerId().equals(sellerId)){
                      return buyerCart;
                }
            }
        }
        return null;
    }
}
