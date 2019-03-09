package cn.itcast.core.controller;


import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;
    //查询所有
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        List<ItemCat> itemCatList = itemCatService.queryAll();
        return itemCatList;
    }
    //分页查询
    /*@RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer rows){
        PageResult pageResult = itemCatService.findPage(page, rows);
        return pageResult;
    }*/
    //新增品牌
    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat){
        try {
            itemCatService.add(itemCat);
            return new Result(true,"新建成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新建失败!");
        }
    }
    //查询一条
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }
    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat){
        try {
            itemCatService.update(itemCat);
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
            itemCatService.delete(ids);
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }
    //多条件分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody ItemCat itemCat){
        PageResult pageResult = itemCatService.search(page, rows, itemCat);
        return pageResult;
    }
    //根据上级ID查询商品分类列表
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        List<ItemCat> itemCatList = itemCatService.findByParentId(parentId);
        return itemCatList;
    }

    //修改状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long [] ids ,String status ){
        try {
            if(ids!=null&&ids.length>0){
                    itemCatService.updateStatus(ids,status);
            }
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"修改失败");
        }
    }
}
