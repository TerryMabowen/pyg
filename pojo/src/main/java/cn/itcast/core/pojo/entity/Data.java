package cn.itcast.core.pojo.entity;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
    //销售时间
    private List<String> dateList;
    //销售额
    private List<String> payList;

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public List<String> getPayList() {
        return payList;
    }

    public void setPayList(List<String> payList) {
        this.payList = payList;
    }

    @Override
    public String toString() {
        return "Data{" +
                "dateList=" + dateList +
                ", payList=" + payList +
                '}';
    }
}
