package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.Spec;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    //查询所有
    @RequestMapping("/findAll")
    public List<Specification> findAll() {
        List<Specification> specificationList = specificationService.queryAll();
        return specificationList;
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows) {
        PageResult pageResult = specificationService.findPage(page, rows);
        return pageResult;
    }

    //新增品牌
    @RequestMapping("/add")
    public Result add(@RequestBody Spec spec) {
        try {
            specificationService.add(spec);
            return new Result(true, "新建成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "新建失败!");
        }
    }

    //查询一条
    @RequestMapping("/findOne")
    public Spec findOne(Long id) {
        return specificationService.findOne(id);
    }

    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody Spec spec) {
        try {
            specificationService.update(spec);
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
            specificationService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    //批量提交申请
    @RequestMapping("/updateStat")
    public Result updateStat(Long[] ids) {
        try {
            specificationService.updateStat(ids);
            return new Result(true, "提交成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "提交失败!");
        }
    }

    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification) {
        PageResult pageResult = specificationService.search(page, rows, specification);
        return pageResult;
    }

    //查询规格列表
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        List<Map> maps = specificationService.selectOptionList();
        return maps;
    }
}
