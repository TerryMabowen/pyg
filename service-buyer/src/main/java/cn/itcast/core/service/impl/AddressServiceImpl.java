package cn.itcast.core.service.impl;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import cn.itcast.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;

    @Override
    public List<Address> findAddressByUsername(String username) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(username);
        List<Address> addressList = addressDao.selectByExample(query);
        return addressList;
    }
}
