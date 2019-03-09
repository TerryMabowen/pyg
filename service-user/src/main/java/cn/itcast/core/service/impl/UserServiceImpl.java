package cn.itcast.core.service.impl;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import cn.itcast.core.pojo.entity.UserInfo;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Statistics_user;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import cn.itcast.core.service.UserService;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private ActiveMQQueue smsDestination;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AddressDao addressDao;

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
        redisTemplate.boundValueOps(phone).set(String.valueOf(code), 10, TimeUnit.MINUTES);
        //3. 将手机号, 验证码, 模板编号, 签名等数据封装成map格式的消息发送给消息服务器
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //向消息服务器发送map信息
                MapMessage mapMessage = new ActiveMQMapMessage();
                mapMessage.setString("phone", phone);
                mapMessage.setString("smsSign", smsName);
                mapMessage.setString("templateId", templateCode);
//                Map<String,String> codeMap = new HashMap<>();
//                codeMap.put("code",String.valueOf(code));
                mapMessage.setString("code", String.valueOf(code));
                mapMessage.setString("min", "10");
                return mapMessage;
            }
        });
    }

    //校验验证码
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        //1. 判断手机号或者是验证码为空返回false
        if (phone == null || "".equals(phone) || smsCode == null || "".equals(smsCode)) {
            return false;
        }
        //2. 通过手机号到redis中获取验证码
        String redisSmsCode = (String) redisTemplate.boundValueOps(phone).get();
        //3. 判断redis中获取的验证码如果为空则返回false
        if (redisSmsCode == null || "".equals(redisSmsCode)) {
            return false;
        }
        //4. 校验页面输入的验证码和redis中获取的验证码如果一致返回true
        if (redisSmsCode.equals(smsCode)) {
            return true;
        }
        return false;
    }

    //验证通过后向数据库添加用户
    @Override
    public void add(User user) {
        userDao.insertSelective(user);
    }

    @Override
    public PageResult search(Integer page, Integer rows, User user) {
        UserQuery query = new UserQuery();
        UserQuery.Criteria criteria = query.createCriteria();
        if (user != null) {
            if (user.getUsername() != null && !"".equals(user.getUsername())) {
                criteria.andUsernameLike("%" + user.getName() + "%");
            }
            if (user.getPhone() != null && user.getPhone().length() > 0) {
                criteria.andPhoneEqualTo(user.getPhone());
            }
        }
        PageHelper.startPage(page, rows);
        Page<User> brandPage = (Page<User>) userDao.selectByExample(query);
        return new PageResult(brandPage.getTotal(), brandPage.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null) {
            User user = new User();
            if (status != null || !"".equals(status)) {
                //冻结用户
                if ("2".equals(status)) {
                    for (Long id : ids) {
                        user.setId(id);
                        user.setStatus(status);
                        userDao.updateByPrimaryKeySelective(user);
                    }
                }
                //激活用户
                if ("1".equals(status)) {
                    for (Long id : ids) {
                        user.setId(id);
                        user.setStatus(status);
                        userDao.updateByPrimaryKeySelective(user);
                    }
                }
            }
        }
    }

    @Override
    public List<Statistics_user> findAll() {
        ArrayList<Statistics_user> statistics_userArrayList = new ArrayList<>();
        List<User> userList = userDao.selectByExample(null);
        Statistics_user statistics_user = new Statistics_user();
        int man = 0;
        long pc = 0;
        long h5 = 0;
        long android = 0;
        long ios = 0;
        long wechat = 0;
        statistics_user.setUserTotal((long) userList.size());

        for (User user : userList) {
            String sex = user.getSex();
            if ("1".equals(sex) || "".equals(sex)) {
                man++;
            }
            String sourceType = user.getSourceType();
            if ("1".equals(sourceType)) {
                pc++;
            }
            if ("2".equals(sourceType)) {
                h5++;
            }
            if ("3".equals(sourceType)) {
                android++;
            }
            if ("4".equals(sourceType)) {
                ios++;
            }
            if ("5".equals(sourceType)) {
                wechat++;

            }
        }
        statistics_user.setProportion(new BigDecimal((((double)man/(double)statistics_user.getUserTotal())*100)).setScale(2,BigDecimal.ROUND_UP)+"%");
        statistics_user.setPc(pc);
        statistics_user.setH5(h5);
        statistics_user.setAndroid(android);
        statistics_user.setIos(ios);
        statistics_user.setWechat(wechat);
        statistics_userArrayList.add(statistics_user);
        return statistics_userArrayList;
    }

    @Override
    public UserInfo findUserInfoByUsername(String userName) {
        UserInfo userInfo = new UserInfo();
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();
        criteria.andUsernameEqualTo(userName);
        List<User> users = userDao.selectByExample(userQuery);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if(users != null) {
            for (User user : users) {
                String birthday = formatter.format(user.getBirthday());
                String[] split = birthday.split("-");
                userInfo.setYear(split[0]);
                userInfo.setMonth(split[1]);
                userInfo.setDay(split[2]);
                userInfo.setHeadPic(user.getHeadPic());
                userInfo.setJob(user.getJob());
                userInfo.setNickName(user.getNickName());
                userInfo.setSex(user.getSex());
            }
        }
        AddressQuery addressQuery = new AddressQuery();
        AddressQuery.Criteria criteria1 = addressQuery.createCriteria();
        criteria1.andUserIdEqualTo(userName);
        List<Address> addresses = addressDao.selectByExample(addressQuery);
        if(addresses != null) {
            for (Address address : addresses) {
               userInfo.setProvinceId(address.getProvinceId());
               userInfo.setCityId(address.getCityId());
               userInfo.setTownId(address.getTownId());
            }
        }
        return userInfo;
    }

    @Override
    public void update(String userName,UserInfo userInfo)throws Exception {
        User user = new User();
        user.setUsername(userName);
        user.setUpdated(new Date());
        user.setNickName(userInfo.getNickName());
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = userInfo.getYear() + "-" + userInfo.getMonth() + "-" + userInfo.getDay();
        Date parse = formatter.parse(date);
        user.setBirthday(parse);
        user.setSex(userInfo.getSex());
        user.setHeadPic(userInfo.getHeadPic());
        user.setJob(userInfo.getJob());
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();
        if(user.getUsername() != null && !"".equals(user.getUsername())) {
            criteria.andUsernameEqualTo(user.getUsername());
        }
        userDao.updateByExampleSelective(user,userQuery);
    }

    @Override
    public void addAddress(Address address) {
        addressDao.insertSelective(address);
    }

    @Override
    public Address backShowAddress(Long id) {
        Address address = addressDao.selectByPrimaryKey(id);
        return address;
    }

    @Override
    public void updateAddress(Address address) {
        addressDao.updateByPrimaryKeySelective(address);
    }

    @Override
    public void delete(Long id){
        addressDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<Item> findItemFromRedis (String userName){
        List<Item> itemList = (List<Item>) redisTemplate.boundHashOps(Constants.REDIS_COLLECTIONLIST).get(userName);
        return itemList;
    }

}
