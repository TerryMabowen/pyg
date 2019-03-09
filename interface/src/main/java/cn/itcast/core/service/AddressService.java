package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.entity.UserInfo;
import cn.itcast.core.pojo.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AddressService {
    //通过用户名获取用户的默认住址
    List<Address> findAddressByUsername(String username);

    void update(String userName,UserInfo userInfo);

}
