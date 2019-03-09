package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.entity.UserInfo;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserService {
     void sendCode(String phone);

     boolean checkSmsCode(String phone, String smsCode);

     void add(User user);

     UserInfo findUserInfoByUsername(String userName);

     void update(String userName,UserInfo userInfo)throws Exception;

     void addAddress(Address address);

     Address backShowAddress(Long id);

     void updateAddress(Address address);

     void delete(Long id);

     List<Item> findItemFromRedis (String userName);
}
