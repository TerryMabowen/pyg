package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.ContentDesc;
import cn.itcast.core.service.ContentCategoryService;
import cn.itcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    @Reference
    private ContentService contentService;

    @RequestMapping("/queryShow")
    public ContentDesc queryShow(String floor){
        ContentCategory contentCategory = contentCategoryService.findAllById(floor);
        List<Content> contents = contentService.findNameById(floor);

        ContentDesc contentDesc = new ContentDesc();
        contentDesc.setContentCategory(contentCategory);
        contentDesc.setContentList(contents);
        return contentDesc;

    }


}
