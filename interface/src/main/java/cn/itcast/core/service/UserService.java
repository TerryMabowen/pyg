package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Statistics_user;
import cn.itcast.core.pojo.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserService {
     void sendCode(String phone);

     boolean checkSmsCode(String phone, String smsCode);

     void add(User user);

     //多条件分页查询
     PageResult search(Integer page, Integer rows, User user);

     //修改用户的状态
     void updateStatus(Long[] ids, String status);

     //查询所有用户
     List<Statistics_user> findAll();
}
