package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.PayService;
import cn.itcast.core.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        //获取当前登陆用户的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //通过用户名到Redis中查询订单信息
        SeckillOrder seckillOrder = payService.searchOrderFromRedisByUsername(username);
        //调用微信支付统一下单接口生成支付链接
        if (seckillOrder != null) {
            //Map map = payService.createNative(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
            //Map<String, String> map = payService.createNative(seckillOrder.getId()+"", "1");
            long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);//金额（分）
            Map<String, String> map = payService.createNative(seckillOrder.getId() + "", fen + "");
            return map;
        } else {
            return new HashMap<>();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int flag = 0;
        //1. 死循环, 不停的查
        while (true) {
            //2. 根据支付单号调用微信查询订单接口
            Map<String, String> map = payService.queryPayStatus(out_trade_no);
            //3. 判断返回值如果为空, 证明支付单号是作废的, 返回二维码超时信息
            if (map == null) {
                result = new Result(false, "支付超时!");
                break;
            }
            //4. 如果查询支付成功(查看微信支付平台返回的信息中trade_state的值是什么,是SUCCESS表示支付成功)
            if ("SUCCESS".equals(map.get("trade_state"))) {
                //5. 将redis中的订单保存至数据库,并清除缓存中的订单
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                result = new Result(true, "支付成功!");
                break;
            }
            //6. 每次查询睡3秒, 防止不停的查询, 服务器压力过大
            try {
                Thread.sleep(3000);
                flag++;
                if (flag > 100) {
                    result = new Result(false, "支付超时!");
                    Map<String, String> payresult = payService.closePay(out_trade_no);
                    if (!"SUCCESS".equals(payresult.get("result_code"))) {//如果返回结果是正常关闭
                        if ("ORDERPAID".equals(payresult.get("err_code"))) {
                            result = new Result(true, "支付成功");
                            seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                        }
                    }
                    if (result.isSuccess() == false) {
                        //System.out.println("超时，取消订单");
                        //2.调用删除
                        seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                    }
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                result = new Result(false, "支付错误!");
            }
        }
        return result;
    }
}
