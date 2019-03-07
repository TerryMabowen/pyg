package cn.itcast.core.controller;


import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.DownloadService;
import cn.itcast.core.util.ExcelUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/download")
public class DownloadController {

    @Reference
    private DownloadService downloadService;

    @RequestMapping("/downloadOrderList")
    public void downloadBrand(HttpServletRequest request, HttpServletResponse response) {
        List<Order> orderList = downloadService.downloadOrderList();
        if (orderList != null) {
            String sheetName = "商品统计表";
            String titleName = "商品数据统计表";
            String fileName = String.valueOf(System.currentTimeMillis());
            int columnNumber = 8;
            //Id,金额,支付类型,支付状态,创建时间,用户名,收货地址,收货人,卖家名
            int[] columnWidth = {30, 20, 10,10, 30, 10, 40, 10, 10};
            String[][] dataList = {{"001", "2015-01-01", "IT"},
                    {"002", "2015-01-02", "市场部"}, {"003", "2015-01-03", "测试"}};
            String[] columnName = {"单号", "申请时间", "申请部门"};
            try {
                ExcelUtil.ExportWithResponse(sheetName, titleName, fileName, columnNumber, columnWidth, columnName, dataList, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
