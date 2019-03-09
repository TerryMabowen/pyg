package cn.itcast.core.pojo.entity;

import java.io.Serializable;
import java.util.List;

public class DataM implements Serializable {
    private List<Object> brandNameList;//品牌名集合
    private List<Object> totalMoneyList;//销售额集合
    private List<Object> totalNumList;

    public List<Object> getBrandNameList() {
        return brandNameList;
    }

    public void setBrandNameList(List<Object> brandNameList) {
        this.brandNameList = brandNameList;
    }

    public List<Object> getTotalMoneyList() {
        return totalMoneyList;
    }

    public void setTotalMoneyList(List<Object> totalMoneyList) {
        this.totalMoneyList = totalMoneyList;
    }

    public List<Object> getTotalNumList() {
        return totalNumList;
    }

    public void setTotalNumList(List<Object> totalNumList) {
        this.totalNumList = totalNumList;
    }
}
