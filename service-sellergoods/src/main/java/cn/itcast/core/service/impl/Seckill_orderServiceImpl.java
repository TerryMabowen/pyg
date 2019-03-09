package cn.itcast.core.service.impl;


import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.entity.PageResult;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;

import cn.itcast.core.service.Seckill_orderService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class Seckill_orderServiceImpl implements Seckill_orderService {

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Override
    public PageResult search(Integer page, Integer rows, SeckillOrder seckillOrder) {
        PageHelper.startPage(page, rows);
        SeckillOrderQuery query = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = query.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getStatus() != null) {
                //状态是通过复选框选择的,只需判断是否为空即可
                criteria.andStatusEqualTo(seckillOrder.getStatus());
            }
            if (seckillOrder.getSeckillId() != null && !"".equals(seckillOrder.getSeckillId())) {
                //前后去空格
                criteria.andSeckillIdNotEqualTo(seckillOrder.getSeckillId());
            }
        }
        Page<SeckillOrder> SeckillOrderList = (Page<SeckillOrder>) seckillOrderDao.selectByExample(query);
        return new PageResult(SeckillOrderList.getTotal(), SeckillOrderList.getResult());
    }


    @Override
    public SeckillOrder findOne(Long id) {
        SeckillOrder seckillOrder = seckillOrderDao.selectByPrimaryKey(id);
        return seckillOrder;
    }


    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderDao.updateByPrimaryKeySelective(seckillOrder);

    }


    //取消订单
    @Override
    public void delete(Long id) {
        SeckillOrder seckillOrder = seckillOrderDao.selectByPrimaryKey(id);
        seckillOrder.setStatus("4");
        seckillOrderDao.updateByPrimaryKeySelective(seckillOrder);


    }


    //批量已发货
    @Override
    public void updateStat(Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (final Long id : ids) {
                //1. 更新秒杀表的商品状态
                SeckillOrder seckillOrder = seckillOrderDao.selectByPrimaryKey(id);
                seckillOrder.setStatus("2");
                seckillOrderDao.updateByPrimaryKeySelective(seckillOrder);
            }
        }
    }

}
