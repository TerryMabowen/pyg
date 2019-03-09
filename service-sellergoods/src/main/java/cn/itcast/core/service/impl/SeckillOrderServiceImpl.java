package cn.itcast.core.service.impl;

import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import cn.itcast.core.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService ,Serializable{
    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<SeckillOrder> orderPage = (Page<SeckillOrder>)seckillOrderDao.selectByExample(null);
        PageResult pageResult = new PageResult(orderPage.getTotal(),orderPage.getResult());
        return pageResult;
    }

    //多条件分页查询
    @Override
    public PageResult search(Integer page, Integer rows, SeckillOrder seckillOrder) {
        PageHelper.startPage(page,rows);
        SeckillOrderQuery query = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = query.createCriteria();
        if(seckillOrder != null){
            if(seckillOrder.getStatus() != null && !"".equals(seckillOrder.getStatus())){
                //状态是通过复选框选择的,只需判断是否为空即可
                criteria.andStatusEqualTo(seckillOrder.getStatus());
            }
        }
        Page<SeckillOrder> goodList = (Page<SeckillOrder>) seckillOrderDao.selectByExample(query);
        return new PageResult(goodList.getTotal(),goodList.getResult());
    }
}
