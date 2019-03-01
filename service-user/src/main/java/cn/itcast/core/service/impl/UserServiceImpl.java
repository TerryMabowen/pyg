package cn.itcast.core.service.impl;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ActiveMQQueue smsDestination;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private UserDao userDao;

    @Value("${smsSign}")
    private String smsName;

    @Value("${templateId}")
    private String templateCode;

    //向消息服务器发送验证码,手机号等信息,通过消息服务器向短信发送微服务
    //itcast_sms发送信息,其接收后向阿里大于发送数据,通过阿里云通讯给用户发送验证码
    @Override
    public void sendCode(final String phone) {
        //1. 生成一个小于等于6位的随机数作为验证码
        //Math.random()生成的是0到1的随机数,double类型
        final long code = (long) (Math.random() * 1000000);
        //2. 将手机号作为key, 验证码作为value存入redis
        redisTemplate.boundValueOps(phone).set(String.valueOf(code),10, TimeUnit.MINUTES);
        //3. 将手机号, 验证码, 模板编号, 签名等数据封装成map格式的消息发送给消息服务器
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //向消息服务器发送map信息
                MapMessage mapMessage = new ActiveMQMapMessage();
                mapMessage.setString("phone",phone);
                mapMessage.setString("smsSign",smsName);
                mapMessage.setString("templateId",templateCode);
//                Map<String,String> codeMap = new HashMap<>();
//                codeMap.put("code",String.valueOf(code));
                mapMessage.setString("code", String.valueOf(code));
                mapMessage.setString("min","10");
                return mapMessage;
            }
        });
    }

    //校验验证码
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        //1. 判断手机号或者是验证码为空返回false
        if(phone == null || "".equals(phone) || smsCode == null || "".equals(smsCode)  ){
            return false;
        }
        //2. 通过手机号到redis中获取验证码
        String redisSmsCode = (String) redisTemplate.boundValueOps(phone).get();
        //3. 判断redis中获取的验证码如果为空则返回false
        if(redisSmsCode == null || "".equals(redisSmsCode)){
            return false;
        }
        //4. 校验页面输入的验证码和redis中获取的验证码如果一致返回true
        if(redisSmsCode.equals(smsCode)){
            return true;
        }
        return false;
    }

    //验证通过后向数据库添加用户
    @Override
    public void add(User user) {
       userDao.insertSelective(user);
    }
}
