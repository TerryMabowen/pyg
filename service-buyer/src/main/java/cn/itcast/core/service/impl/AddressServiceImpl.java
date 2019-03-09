package cn.itcast.core.service.impl;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import cn.itcast.core.pojo.entity.UserInfo;
import cn.itcast.core.pojo.user.User;
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

    @Override
    public void update(String userName,UserInfo userInfo) {
        Address addre = new Address();
        addre.setUserId(userName);
        addre.setProvinceId(userInfo.getProvinceId());
        addre.setCityId(userInfo.getCityId());
        addre.setTownId(userInfo.getTownId());
        AddressQuery addressQuery = new AddressQuery();
        AddressQuery.Criteria criteria = addressQuery.createCriteria();
        if(addre.getUserId() != null && !"".equals(addre.getUserId())) {
            criteria.andUserIdEqualTo(addre.getUserId());
        }
        addressDao.updateByExampleSelective(addre,addressQuery);
    }

}
