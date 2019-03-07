package cn.itcast.core.service;



import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.List;


public interface UploadService {
    //导入品牌表
    public void uploadExcel2Brand(List<String[]> excelList) throws IOException;

    //导入规格表
    public void uploadExcel2Spec(List<String[]> excelList);

    //导入模版
    public void uploadExcel2Template(List<String[]> excelList);

    //导入分类
    public void uploadExcel2ItemCat(List<String[]> excelList);
}
