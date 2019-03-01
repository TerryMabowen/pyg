package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;

import cn.itcast.core.pojo.seller.Seller;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerService {
   //查询所有
   List<Seller> queryAll();
   //分页查询
   PageResult findPage(Integer page,Integer rows);
   //新增品牌
   void add(Seller seller);
   //查询一条
   Seller findOne(String id);
   //更新品牌
   void update(Seller seller);
   //批量删除
   void delete(String[] ids);
   //多条件分页查询
   PageResult search(Integer page,Integer rows,Seller seller);
   //根据主键更新商家状态(未审核,已审核或未通过审核)
   void updateStatus(String sellerId,String status);
   //根据用户名查询用户信息(登录),findOne方法即可,主键就是用户名
   //Seller findByUserName(String username);
}
