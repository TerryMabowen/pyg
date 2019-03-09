package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.DataM;
import cn.itcast.core.service.MarketService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/market")
public class MarketController {
    @Reference
    private MarketService marketService;
    //销售饼状图和销售折线图 销售额,每天的
    @RequestMapping("/showSalesMoney")
    public DataM showSalesMoney() {
        return marketService.showSalesMoney();
    }
    //销售饼状图和销售折线图 销售量,每天的
    @RequestMapping("/showSalesQuantity")
    public DataM showSalesQuantity() {
        return marketService.showSalesQuantity();
    }
}
