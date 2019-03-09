package cn.itcast.core.pojo.entity;

import java.io.Serializable;

public class Statistics_user implements Serializable {
    private Long userTotal; // 用户总数
    private String proportion; //男占比
    private Long pc;
    private Long h5;
    private Long android;
    private Long ios;
    private Long wechat;

    public Long getUserTotal() {
        return userTotal;
    }

    public void setUserTotal(Long userTotal) {
        this.userTotal = userTotal;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public Long getPc() {
        return pc;
    }

    public void setPc(Long pc) {
        this.pc = pc;
    }

    public Long getH5() {
        return h5;
    }

    public void setH5(Long h5) {
        this.h5 = h5;
    }

    public Long getAndroid() {
        return android;
    }

    public void setAndroid(Long android) {
        this.android = android;
    }

    public Long getIos() {
        return ios;
    }

    public void setIos(Long ios) {
        this.ios = ios;
    }

    public Long getWechat() {
        return wechat;
    }

    public void setWechat(Long wechat) {
        this.wechat = wechat;
    }

    @Override
    public String toString() {
        return "Statistics_user{" +
                "userTotal=" + userTotal +
                ", proportion=" + proportion +
                ", pc=" + pc +
                ", h5=" + h5 +
                ", android=" + android +
                ", ios=" + ios +
                ", wechat=" + wechat +
                '}';
    }
}
