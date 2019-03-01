package cn.itcast.core.listener;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.PageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
import java.util.Map;


public class PageListener implements MessageListener {

    @Autowired
    private PageService pageService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage)message;
        try {
            //接收发送的消息:商品id
            String goodsId = activeMQTextMessage.getText();
            try {
            //根据商品id查询状态为已审核的商品集合
            Map<String, Object> rootMap = pageService.findGoodsById(Long.parseLong(goodsId));
            //根据模板生成静态化页面
                pageService.createGoodsHtml(Long.parseLong(goodsId),rootMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
