package cn.itcast.core.service;

import cn.itcast.core.pojo.cart.BuyerCart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CartService {
    //添加商品到购物车(/addGoodsToCartList)
     List<BuyerCart> addGoodsToCartList(List<BuyerCart> cartList,Long itemId,Integer num);
     //将购物车集合根据用户名存入redis中
     void setCartListToRedis(String userName, List<BuyerCart> cartList);
     //根据用户名, 获取这个人redis中的购物车集合
     List<BuyerCart> getCartListFromRedis(String userName);
     //将cookie中的购物车集合合并到redis的购物车集合中, 并返回合并后的购物车集合
     List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList);
}
