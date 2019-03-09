package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.PayService;
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
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        //获取当前登陆用户的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //通过用户名到Redis中查询订单信息
        PayLog payLog = payService.findPayLogByUsername(username);
        //调用微信支付统一下单接口生成支付链接
        if (payLog != null) {
            //Map map = payService.createNative(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
            Map<String, String> map = payService.createNative(payLog.getOutTradeNo(), "1");
            return map;
        } else {
            return new HashMap<>();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        int flag = 1;
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
                //5. 修改支付日志表和订单表支付状态为支付成功, 删除redis中缓存的待支付日志对象
                orderService.updateStatusToPayLogAndOrder(out_trade_no);
                result = new Result(true, "支付成功!");
                break;
            }
            //6. 每次查询睡3秒, 防止不停的查询, 服务器压力过大
            try {
                Thread.sleep(3000);
                flag++;
                if (flag >= 200) {
                    result = new Result(false, "支付超时!");
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
