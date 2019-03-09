package cn.itcast.core.service.impl;


import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.DownloadService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public List<Order> downloadOrderList() {
        return orderDao.selectByExample(null);
    }

    @Override
    public List<OrderItem> downlodownloadOrderItemList() {
        return orderItemDao.selectByExample(null);
    }
}
