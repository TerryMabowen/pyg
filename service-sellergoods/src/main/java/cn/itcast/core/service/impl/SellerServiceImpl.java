package cn.itcast.core.service.impl;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;

    //查询所有
    @Override
    public List<Seller> queryAll() {
        return sellerDao.selectByExample(null);
    }
    //分页查询
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<Seller> sellerPage = (Page<Seller>)sellerDao.selectByExample(null);
        PageResult pageResult = new PageResult(sellerPage.getTotal(),sellerPage.getResult());
        return pageResult;
    }
    //新增品牌
    @Override
    public void add(Seller seller) {
        seller.setStatus("0");
        seller.setCreateTime(new Date());
        sellerDao.insertSelective(seller);
    }
    //查询一条
    @Override
    public Seller findOne(String id) {
        Seller seller = sellerDao.selectByPrimaryKey(id);
        return seller;
    }
    //更新品牌
    @Override
    public void update(Seller seller) {
        sellerDao.updateByPrimaryKeySelective(seller);
    }
    //批量删除
    @Override
    public void delete(String[] ids) {
        for (String sellerId : ids) {
            sellerDao.deleteByPrimaryKey(sellerId);
        }
    }
    //多条件分页查询

    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        SellerQuery query = new SellerQuery();
        SellerQuery.Criteria criteria = query.createCriteria();
        if(seller != null){
            if(seller.getName() != null && !"".equals(seller.getName())){
                criteria.andNameLike("%"+seller.getName()+"%");
            }
            if(seller.getNickName() != null && !"".equals(seller.getNickName())){
                criteria.andNickNameLike("%"+seller.getNickName()+"%");
            }
            if(seller.getStatus() != null && !"".equals(seller.getStatus())) {
                criteria.andStatusEqualTo("0");
            }
        }
        PageHelper.startPage(page, rows);
        Page<Seller> sellerPage = (Page<Seller>) sellerDao.selectByExample(query);
        return new PageResult(sellerPage.getTotal(),sellerPage.getResult());
    }


    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

}
