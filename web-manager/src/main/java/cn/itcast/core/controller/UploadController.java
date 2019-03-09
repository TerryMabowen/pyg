package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.UploadService;
import cn.itcast.core.util.ExcelUtil;
import cn.itcast.core.util.FastDFSClient;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String file_server_url;


    @Reference
    private UploadService uploadService;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) throws Exception {
        try {
            //1、取文件的完整名称
            String filename = file.getOriginalFilename();
            //2、创建一个 FastDFS 的客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //3、执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(), filename, file.getSize());
            //4、拼接返回的 url 和 ip 地址，拼装成完整的 url
            String url = file_server_url + path;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "文件上传失败!");
        }
    }

    /**
     * 批量导入excel数据到数据库
     *
     * @param file 上传的文件
     * @return
     * @throws IOException
     */
    @RequestMapping("/uploadExcel2Brand")
    public Result uploadExcel2Brand(@RequestParam MultipartFile file) throws Exception {
        //如果名字的后缀为这两个格式的话 才执行

        try {
            if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
                //只能在Controller调用  因为workbook没有实现序列化接口
                Workbook workbook = ExcelUtil.getWorkBook(file);
                //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
                List<String[]> excelList = ExcelUtil.readExcelGetList(workbook);
                uploadService.uploadExcel2Brand(excelList);

                return new Result(true, "导入成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "导入失败!");
        }
        return new Result(false, "上传的文件格式不正确!");
    }

    @RequestMapping("/uploadExcel2Spec")
    public Result uploadExcel2Spec(@RequestParam MultipartFile file) {
        try {
            if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
                //只能在Controller调用  因为workbook没有实现序列化接口
                Workbook workbook = ExcelUtil.getWorkBook(file);
                //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
                List<String[]> excelList = ExcelUtil.readExcelGetList(workbook);
                uploadService.uploadExcel2Spec(excelList);

                return new Result(true, "导入成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "导入失败!");
        }
        return new Result(false, "上传的文件格式不正确!");
    }

    @RequestMapping("/uploadExcel2Template")
    public Result uploadExcel2Template(@RequestParam MultipartFile file) {
        try {
            if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
                //只能在Controller调用  因为workbook没有实现序列化接口
                Workbook workbook = ExcelUtil.getWorkBook(file);
                //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
                List<String[]> excelList = ExcelUtil.readExcelGetList(workbook);
                uploadService.uploadExcel2Template(excelList);
                return new Result(true, "导入成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "导入失败!");
        }
        return new Result(false, "上传的文件格式不正确!");
    }

    @RequestMapping("/uploadExcel2ItemCat")
    public Result uploadExcel2ItemCat(@RequestParam MultipartFile file) {
        try {
            if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
                //只能在Controller调用  因为workbook没有实现序列化接口
                Workbook workbook = ExcelUtil.getWorkBook(file);
                //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
                List<String[]> excelList = ExcelUtil.readExcelGetList(workbook);
                uploadService.uploadExcel2ItemCat(excelList);
                return new Result(true, "导入成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "导入失败!");
        }
        return new Result(false, "上传的文件格式不正确!");
    }

}
