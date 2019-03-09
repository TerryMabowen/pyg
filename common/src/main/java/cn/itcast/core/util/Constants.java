package cn.itcast.core.util;

public interface Constants {
    //设置Redis中广告集合的主键
    public final static String REDIS_CONTENT_LIST ="contentList";
    //设置Redis中分类集合的主键
    public final static String REDIS_CATEGORY_LIST = "categoryList";
    //设置Redis中品牌集合的主键
    public final static String REDIS_BRAND_LIST = "brandList";
    //设置Redis中规格集合的主键
    public final static String REDIS_SPEC_LIST = "specList";
    //设置Redis中用户已登录后购物车集合的主键
    public final static String REDIS_CARTLIST = "login_cartList";
   //设置Redis中订单支付日志集合的主键
    public final static String REDIS_PAYLOG = "payLog";
   //设置cookie中用户未登录后购物车集合的主键
    public final static String COOKIE_CARTLIST = "logout_cartList";
    //设置Redis中用户已登录后收藏集合的主键
    public final static String REDIS_COLLECTIONLIST = "login_collectionList";

}
