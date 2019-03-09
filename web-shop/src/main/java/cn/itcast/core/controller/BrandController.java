package cn.itcast.core.controller;


import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    BrandService brandService;


    @RequestMapping("/search")
            public PageResult search(Integer page,Integer rows,@RequestBody Brand brand){
        PageResult search = brandService.search(page, rows, brand);
        return search;
    }



    @RequestMapping("/update")
    public Result update(Long id){
        try {
            brandService.updateStat(id);
            return new Result(true,"申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"申请失败");
        }

    }

}
