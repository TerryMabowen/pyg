package cn.itcast.core.controller;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.UserInfo;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.AddressService;
import cn.itcast.core.service.UserService;
import cn.itcast.core.util.Constants;
import cn.itcast.core.util.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private AddressService addressService;



    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        try {
        //1. 校验电话号码的正确性(合法性)
        if(phone == null || "".equals(phone)){
            return new Result(false,"请正确填写手机号!");
        }
        if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
            return new Result(false,"手机号格式不正确!");
        }
        //2. 如果手机号正确发送验证码

            userService.sendCode(phone);
            return new Result(true,"验证码发送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"验证码发送失败!");
        }
    }

    @RequestMapping("/add")
    public Result add(@RequestBody User user,String smscode){
        try {
        //1. 判断验证码是否正确
        boolean check = userService.checkSmsCode(user.getPhone(), smscode);

        if(!check){
            return new Result(false,"手机号或者验证码错误!");
        }
        //2. 保存用户对象
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //默认为pc端注册
        user.setSourceType("1");
        //使用状态默认为正常
        user.setStatus("Y");
            userService.add(user);
            return new Result(true,"注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败!");
        }
    }
    @RequestMapping("/updateUser")
    public Result updateUser(@RequestBody UserInfo userInfo){
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.update(userName,userInfo);
            addressService.update(userName,userInfo);
            return new Result(true,"注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败!");
        }
    }

    @RequestMapping("/showAddress")
    public List<Address> showAddress(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Address> addList =  addressService.findAddressByUsername(userName);
        return addList;
    }

    @RequestMapping("/showUserInfo")
    public UserInfo showUserInfo(){
        UserInfo userInfo = null;
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userName != null && !"".equals(userName) && !"anonymousUser".equals(userName)){
            userInfo = userService.findUserInfoByUsername(userName);
        }
        return userInfo;
    }

    @RequestMapping("/addAddress")
    public Result addAddress(@RequestBody Address address){
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            address.setUserId(userName);
            userService.addAddress(address);
            return new Result(true,"保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败!");
        }
    }

    @RequestMapping("/backShowAddress")
    public Address updateAddress(Long id){
        Address address = userService.backShowAddress(id);
        return address;
    }

    @RequestMapping("/update")
    public Result updateAddress(@RequestBody Address address){
        try {
            userService.updateAddress(address);
            return new Result(true,"保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败!");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long id){
        try {
            userService.delete(id);
            return new Result(true,"保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败!");
        }
    }

    @RequestMapping("/collect")
    public List<Item> collect(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Item> itemList = userService.findItemFromRedis(userName);
        return itemList;
    }
}
