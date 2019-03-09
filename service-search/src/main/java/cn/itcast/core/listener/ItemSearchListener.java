package cn.itcast.core.listener;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;


public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try {
            //接收发送的消息:商品id
            String goodsId = activeMQTextMessage.getText();
            //通过商品id查询状态为已审核的商品集合
            //将已审核的商品集合保存到solr索引库中
            itemSearchService.importList(Long.parseLong(goodsId), "1");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
