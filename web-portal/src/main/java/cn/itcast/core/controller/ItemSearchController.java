package cn.itcast.core.controller;

import cn.itcast.core.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map searchMap){
       // String keywords = (String) searchMap.get("keywords");
       // System.out.println("========keywords======="+keywords);
        Map<String, Object> itemMaps = itemSearchService.search(searchMap);
        return itemMaps;
    }
}
