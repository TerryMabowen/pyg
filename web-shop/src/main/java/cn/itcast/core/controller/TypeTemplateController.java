package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;
    //查询所有
    @RequestMapping("/findAll")
    public List<TypeTemplate> findAll(){
        List<TypeTemplate> typeTemplateList = typeTemplateService.queryAll();
        return typeTemplateList;
    }
    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer rows){
        PageResult pageResult = typeTemplateService.findPage(page, rows);
        return pageResult;
    }
    //新增品牌
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true,"新建成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新建失败!");
        }
    }
    //查询一条
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }
    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true,"更新成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败!");
        }
    }
    //批量删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody TypeTemplate typeTemplate){
        PageResult pageResult = typeTemplateService.search(page, rows, typeTemplate);
        return pageResult;
    }

    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
        return typeTemplateService.findSpecList(id);
    }
}
