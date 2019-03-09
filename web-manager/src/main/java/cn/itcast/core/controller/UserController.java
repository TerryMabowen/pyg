package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.Statistics_user;
import cn.itcast.core.pojo.entity.ViewSet;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import cn.itcast.core.service.ViewService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private ViewService viewService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody User user){
        PageResult pageResult = userService.search(page, rows, user);
        return pageResult;
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        if (ids != null) {
            if (status != null || !"".equals(status)) {
                if ("2".equals(status)) {
                    try {
                        userService.updateStatus(ids,status);
                        return new Result(true, "已冻结用户!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new Result(false, "冻结失败!");
                    }
                }
                if ("1".equals(status)) {
                    try {
                        userService.updateStatus(ids,status);
                        return new Result(true, "审核成功!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new Result(false, "审核失败!");
                    }
                }
            }
        }
        return new Result(false, "非法操作!");
    }

    @RequestMapping("/findAll")
    public List<Statistics_user> findAll() {
        List<Statistics_user> statistics_users = userService.findAll();
        return statistics_users;
    }

    @RequestMapping("/view2User")
    public int[] view2User() {
        return viewService.view2User();
    }
}
