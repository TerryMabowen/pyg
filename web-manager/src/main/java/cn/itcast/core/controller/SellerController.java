package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;
    //查询所有
    @RequestMapping("/findAll")
    public List<Seller> findAll(){
        List<Seller> sellerList = sellerService.queryAll();
        return sellerList;
    }
    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer rows){
        PageResult pageResult = sellerService.findPage(page, rows);
        return pageResult;
    }
    //新增品牌
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){
        try {
            sellerService.add(seller);
            return new Result(true,"新建成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新建失败!");
        }
    }
    //查询一条
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        Seller seller = sellerService.findOne(id);
        return seller;
    }
    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody Seller seller){
        try {
            sellerService.update(seller);
            return new Result(true,"更新成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败!");
        }
    }
    //批量删除
    @RequestMapping("/delete")
    public Result delete(String[] ids){
        try {
            sellerService.delete(ids);
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody Seller seller){
        PageResult pageResult = sellerService.search(page, rows, seller);
        return pageResult;
    }
    //更新审核状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true,"更新审核状态成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新审核状态失败!");
        }
    }
}
