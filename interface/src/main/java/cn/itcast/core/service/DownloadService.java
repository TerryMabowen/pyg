package cn.itcast.core.service;
import cn.itcast.core.pojo.order.Order;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface DownloadService {

    List<Order> downloadOrderList();

}
