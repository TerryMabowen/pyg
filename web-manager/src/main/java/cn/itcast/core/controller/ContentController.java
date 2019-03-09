package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    //查询所有
    @RequestMapping("/findAll")
    public List<Content> findAll() {
        List<Content> contentList = contentService.queryAll();
        return contentList;
    }

    //新增品牌
    @RequestMapping("/add")
    public Result add(@RequestBody Content content) {
        try {
            contentService.add(content);
            return new Result(true, "新建成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "新建失败!");
        }
    }

    //查询一条
    @RequestMapping("/findOne")
    public Content findOne(Long id) {
        return contentService.findOne(id);
    }

    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody Content content) {
        try {
            contentService.update(content);
            return new Result(true, "更新成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败!");
        }
    }

    //批量删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            contentService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Content content) {
        PageResult pageResult = contentService.search(page, rows, content);
        return pageResult;
    }
}
