package cn.itcast.core.dao.order;


import cn.itcast.core.pojo.entity.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext*.xml"})
public class OrderDaoTest {
@Autowired
OrderDao orderDao;
    @Test
    public void selectPayment() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String day = "2019-03-05";
        Date parse = simpleDateFormat.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);


        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        String format = null;

        for (int i = 0; i < 7; i++) {
            calendar.add(calendar.DATE, 1);

            format = simpleDateFormat.format(calendar.getTime());
          String  hm = orderDao.selectPayment("%" + format + "%", "hm");
          list1.add(hm);
            list.add(format);
        }

        System.out.println(list);
        System.out.println(list1);

    }
}
