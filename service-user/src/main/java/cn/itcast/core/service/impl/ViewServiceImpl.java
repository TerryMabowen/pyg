package cn.itcast.core.service.impl;


import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.ViewService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ViewServiceImpl implements ViewService {

    @Autowired
    private UserDao userDao;

    @Override
    public int[] view2User() {
        //TODO
        //查询数据 展示客户来源  用户统计页面
        List<User> userList = userDao.selectByDay();
        int total = userDao.selectCount();
        int old = total - userList.size();
        int[] ints = {userList.size(), old};
        return ints;
    }
}
