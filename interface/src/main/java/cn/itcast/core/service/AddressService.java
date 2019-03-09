package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AddressService {
    //通过用户名获取用户的默认住址
    List<Address> findAddressByUsername(String username);
}
