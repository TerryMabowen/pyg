package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.omg.SendingContext.RunTime;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;
    //查询所有
    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        List<Brand> brandList = brandService.queryAll();
        return brandList;
    }
    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer rows){
        PageResult pageResult = brandService.findPage(page, rows);
        return pageResult;
    }
    //新增品牌
    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand){
        try {
            brandService.add(brand);
            return new Result(true,"新建成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新建失败!");
        }
    }
    //查询一条
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }
    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
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
            brandService.delete(ids);
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody Brand brand){
        PageResult pageResult = brandService.search(page, rows, brand);
        return pageResult;
    }
    //查询品牌列表
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        List<Map> maps = brandService.selectOptionList();
        return maps;
    }
}
