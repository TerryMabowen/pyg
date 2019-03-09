package cn.itcast.core.pojo.entity;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;

public class Seckill_Goods {
    private SeckillGoods seckillGoods;
    private Goods goods;

    public SeckillGoods getSeckillGoods() {
        return seckillGoods;
    }

    public void setSeckillGoods(SeckillGoods seckillGoods) {
        this.seckillGoods = seckillGoods;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    @Override
    public String toString() {
        return "Seckill_Goods{" +
                "seckillGoods=" + seckillGoods +
                ", goods=" + goods +
                '}';
    }
}
