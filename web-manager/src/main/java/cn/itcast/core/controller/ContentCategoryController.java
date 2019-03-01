package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    //查询所有
    @RequestMapping("/findAll")
    public List<ContentCategory> findAll() {
        List<ContentCategory> contentCategoryList = contentCategoryService.queryAll();
        return contentCategoryList;
    }

    //新增品牌
    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.add(contentCategory);
            return new Result(true, "新建成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "新建失败!");
        }
    }

    //查询一条
    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id) {
        return contentCategoryService.findOne(id);
    }

    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.update(contentCategory);
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
            contentCategoryService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody ContentCategory contentCategory) {
        PageResult pageResult = contentCategoryService.search(page, rows, contentCategory);
        return pageResult;
    }

}
