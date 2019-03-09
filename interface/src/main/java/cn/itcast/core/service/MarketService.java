package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.DataM;

public interface MarketService {
    //销售图,根据时间显示每天的和销售额,
    DataM showSalesMoney();

    //销售图,根据时间显示每天的和销售量,
    DataM showSalesQuantity();
}
