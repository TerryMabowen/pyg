package cn.itcast.core.controller;


import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.DownloadService;
import cn.itcast.core.util.ExcelUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/download")
public class DownloadController {

    @Reference
    private DownloadService downloadService;

    @RequestMapping("/downloadOrderList")
    public void downloadOrderList(HttpServletResponse response) {
        List<Order> orderList = downloadService.downloadOrderList();
        if (orderList != null) {
            String sheetName = "商品统计表";
            String titleName = "售出商品数据统计表";
            String fileName ="Order"+ String.valueOf(System.currentTimeMillis());
            //Id,金额,支付类型,支付状态,创建时间,用户名,收货地址,收货人,卖家名
            int[] columnWidth = {30, 20, 10,10, 30, 10, 40, 10, 10};
            String[] columnName = {"id", "金额", "支付类型","支付状态","创建时间","用户名","收货地址","收货人","商家名称"};
            int columnNumber = 9;
            String[][] dataList = new String[orderList.size()][columnName.length];
            for (int i = 0; i < orderList.size(); i++) {
                Order order = orderList.get(i);
                dataList[i][0] = String.valueOf(order.getOrderId());
                dataList[i][1] = String.valueOf(order.getPayment());
                dataList[i][2] = String.valueOf(order.getPaymentType());
                dataList[i][3] = String.valueOf(order.getStatus());
                dataList[i][4] = String.valueOf(order.getCreateTime());
                dataList[i][5] = String.valueOf(order.getUserId());
                dataList[i][6] = String.valueOf(order.getReceiverAreaName());
                dataList[i][7] = String.valueOf(order.getReceiver());
                dataList[i][8] = String.valueOf(order.getSellerId());
            }
            /*String[][] dataList = {{"001", "2015-01-01", "IT"},
                    {"002", "2015-01-02", "市场部"}, {"003", "2015-01-03", "测试"}};*/

            try {
                ExcelUtil.ExportWithResponse(sheetName, titleName, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @RequestMapping("/downloadOrderItemList")
    public void downloadOrderItemList(HttpServletResponse response) {
        List<OrderItem> orderList = downloadService.downlodownloadOrderItemList();
        if (orderList != null) {
            String sheetName = "商品统计表";
            String titleName = "商品数据统计表";
            String fileName = "OrderItem"+String.valueOf(System.currentTimeMillis());
            //Id,金额,支付类型,支付状态,创建时间,用户名,收货地址,收货人,卖家名
            int[] columnWidth = {25, 20, 35,30, 40, 10, 10, 40, 10};
            String[] columnName = {"id", "SKU", "SPU","订单id","商品标题","单价","数量","图片","商家名称"};
            int columnNumber = 9;
            String[][] dataList = new String[orderList.size()][columnName.length];
            for (int i = 0; i < orderList.size(); i++) {
                OrderItem orderItem = orderList.get(i);
                dataList[i][0] = String.valueOf(orderItem.getId());
                dataList[i][1] = String.valueOf(orderItem.getItemId());
                dataList[i][2] = String.valueOf(orderItem.getGoodsId());
                dataList[i][3] = String.valueOf(orderItem.getOrderId());
                dataList[i][4] = String.valueOf(orderItem.getTitle());
                dataList[i][5] = String.valueOf(orderItem.getPrice());
                dataList[i][6] = String.valueOf(orderItem.getNum());
                dataList[i][7] = String.valueOf(orderItem.getPicPath());
                dataList[i][8] = String.valueOf(orderItem.getSellerId());
            }
            try {
                ExcelUtil.ExportWithResponse(sheetName, titleName, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}