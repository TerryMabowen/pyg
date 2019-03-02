package cn.itcast.core.controller;

import cn.itcast.core.pojo.cart.BuyerCart;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.CartService;
import cn.itcast.core.util.Constants;
import cn.itcast.core.util.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8087" ,allowCredentials = "true") //注解解决跨域访问问题
    public Result addGoodsToCartList(Long itemId, Integer num){
        try {
            //1. 获取当前登录用户名称
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //2. 获取购物车列表
            List<BuyerCart> cartList = findCartList();
            //3. 将当前商品加入到购物车列表
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //4. 判断当前用户是否登录, 未登录用户名为"anonymousUser"
            if("anonymousUser".equals(username)){
                //4.a.如果未登录, 则将购物车列表存入cookie中
                String cartListJsonStr = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response, Constants.COOKIE_CARTLIST,cartListJsonStr,60*60*24*30,"utf-8");
            }else{
                //4.b.如果已登录, 则将购物车列表存入redis中
                cartService.setCartListToRedis(username,cartList);
            }
            //下面为设置响应头,解决跨域访问接收不到响应的问题(SpringMVC提供了注解版,更简单)
            // javax.servlet.http.HttpServletResponse response
            // response.setHeader("Access-Control-Allow-Origin", "http://localhost:8087");
            // response.setHeader("Access-Control-Allow-Credentials", "true");
            return new Result(true,"购物车添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"购物车添加失败!");
        }
    }

    @RequestMapping("/findCartList")
    public List<BuyerCart> findCartList(){
        //1. 获取当前登录用户名称
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 从cookie中获取购物车列表json格式字符串
        String cookieValue = CookieUtil.getCookieValue(request, Constants.COOKIE_CARTLIST, "utf-8");
        //3. 如果购物车列表json串为空则返回"[]"
        if(cookieValue == null || "".equals(cookieValue)){
            cookieValue = "[]";
        }
        //4. 将购物车列表json转换为对象
        List<BuyerCart> cookieCartList = JSON.parseArray(cookieValue, BuyerCart.class);
        //5. 判断用户是否登录, 未登录用户为"anonymousUser"
        if("anonymousUser".equals(username)){
            //5.a. 未登录, 返回cookie中的购物车列表对象0..+ 
            return cookieCartList;
        }else {
            //5.b.1.已登录, 从redis中获取购物车列表对象
            List<BuyerCart> redisCartList = cartService.getCartListFromRedis(username);
            //5.b.2.判断cookie中是否存在购物车列表
            if(cookieCartList != null && cookieCartList.size() > 0){
                //如果cookie中存在购物车列表则和redis中的购物车列表合并成一个对象
                redisCartList = cartService.mergeCookieCartListToRedisCartList(cookieCartList, redisCartList);
                //删除cookie中购物车列表
                CookieUtil.deleteCookie(request,response,Constants.COOKIE_CARTLIST);
                //将合并后的购物车列表存入redis中
                cartService.setCartListToRedis(username,redisCartList);
            }
            //5.b.3.返回购物车列表对象
            return redisCartList;
        }
    }
}
