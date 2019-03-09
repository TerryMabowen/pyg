package cn.itcast.core.pojo.entity;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentCategory;

import java.io.Serializable;
import java.util.List;

public class ContentDesc implements Serializable {

    private ContentCategory contentCategory;

    private List<Content> contentList;

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public ContentCategory getContentCategory() {

        return contentCategory;
    }

    public void setContentCategory(ContentCategory contentCategory) {
        this.contentCategory = contentCategory;
    }
}
