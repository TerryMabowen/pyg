package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showName")
    public Map name() {
        String name = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        //String name = "baidu";
        Map map = new HashMap();
        map.put("userName", name);
        return map;
    }
}