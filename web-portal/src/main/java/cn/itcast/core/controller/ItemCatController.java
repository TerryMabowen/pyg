package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/selectByParentId")
    public List<ItemCat> selectByParentId(Long parentId){
        List<ItemCat> itemCatList = itemCatService.selectByParentId(parentId);
        return itemCatList;
    }


}
