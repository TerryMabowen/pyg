package cn.itcast.core.service.impl;


import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.service.Seckill_goodsService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.util.Date;


@Service
@Transactional
public class Seckill_goodsServiceImpl implements Seckill_goodsService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;

    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;

    @Override
    public void add(SeckillGoods seckillGood) {
        //设置未申请状态
        seckillGood.setStatus("3");
        seckillGood.setCreateTime(new Date());
        //添加商品信息
        seckillGoodsDao.insertSelective(seckillGood);
    }


    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);
        SeckillGoodsQuery query = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = query.createCriteria();

        if (seckillGoods != null) {
            if (seckillGoods.getStatus() != null) {
                //状态是通过复选框选择的,只需判断是否为空即可
                criteria.andStatusEqualTo(seckillGoods.getStatus());
            }
            if (seckillGoods.getTitle() != null && !"".equals(seckillGoods.getTitle().trim())) {
                //前后去空格
                criteria.andTitleEqualTo("%" + seckillGoods.getTitle().trim() + "%");
            }
        }
        Page<SeckillGoods> seckillGoodsList = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(query);
        return new PageResult(seckillGoodsList.getTotal(), seckillGoodsList.getResult());
    }


    @Override
    public SeckillGoods findOne(Long id) {
        SeckillGoods seckillGoods = seckillGoodsDao.selectByPrimaryKey(id);
        return seckillGoods;
    }

    @Override
    public void update(SeckillGoods seckillGoods) {
        seckillGoods.setCheckTime(new Date());
        seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);

    }


    //删除(逻辑删除,并不是真的在数据库中删除,只是在页面上不显示)
    @Override
    public void delete(Long[] ids) {

        if (ids != null && ids.length > 0) {
            for (final Long id : ids) {
                seckillGoodsDao.deleteByPrimaryKey(id);
                //将商品id作为消息发送到消息服务器下架队列
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        //id必须final修饰
                        TextMessage message = session.createTextMessage(String.valueOf(id));
                        return message;
                    }
                });
            }
        }
    }


    //批量撤回申请
    @Override
    public void updateStat(Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (final Long id : ids) {
                //1. 更新秒杀表的商品状态
                SeckillGoods seckillGoods = seckillGoodsDao.selectByPrimaryKey(id);
                seckillGoods.setStatus("3");
                seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);


                if ("1".equals("")) {
                    //3. 将商品id作为消息发送到消息服务器上架队列上
                    //因为接收方为两个服务器(search和page)所以使用订阅发布模式
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            //id必须final修饰
                            TextMessage message = session.createTextMessage(String.valueOf(id));
                            return message;
                        }
                    });
                }
            }
        }
    }

}
