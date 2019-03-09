package cn.itcast.core.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import cn.itcast.core.service.UploadService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class UploadServiceImpl implements UploadService {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private SpecificationDao specDao;

    @Autowired
    private SpecificationOptionDao spec_optionDao;

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Override
    public void uploadExcel2Brand(List<String[]> excelList) throws IOException {
        if (excelList != null) {
            Brand brand = new Brand();
            for (String[] brands : excelList) {
                brand.setName(brands[1]);
                brand.setFirstChar(brands[2]);
                brandDao.insert(brand);
            }
        }
    }

    @Override
    public void uploadExcel2Spec(List<String[]> excelList) {
        if (excelList != null) {
            Specification spec = new Specification();
            SpecificationOption spec_option = new SpecificationOption();
            for (String[] specs : excelList) {
                spec.setSpecName(specs[1]);
                specDao.insert(spec);
                int i = 3;
                int j = 1;
                while (true) {
                    if (i <= specs.length) {
                        System.out.println(i);
                        spec_option.setOptionName(specs[i - 1]);
                        spec_option.setSpecId(spec.getId());
                        spec_option.setOrders(j);
                        spec_optionDao.insert(spec_option);
                        i++;
                        j++;
                        continue;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void uploadExcel2Template(List<String[]> excelList) {
        if (excelList != null) {
            TypeTemplate template = new TypeTemplate();
            String strSpecIds = null;
            for (String[] types : excelList) {
                template.setName(types[1]);
                if (types[2] != null) {
                    String str = types[2].toString();
                    String[] specNames = str.split(",");
                    String jsonStr = "[";
                    for (int i = 0; i < specNames.length; i++) {
                        if (i != (specNames.length - 1)) {
                            Map<String, String> map = specDao.selectOptionListBySpecName(specNames[i]);
                            String jsonString = JSON.toJSONString(map);
                            jsonStr += jsonString + ",";
                        } else {
                            Map<String, String> map = specDao.selectOptionListBySpecName(specNames[i]);
                            String jsonString = JSON.toJSONString(map);
                            jsonStr += jsonString + "]";
                        }
                    }
                    template.setSpecIds(jsonStr);
                }
                if (types[3] != null) {
                    String str = types[3].toString();
                    String[] brandNames = str.split(",");
                    String jsonStr = "[";
                    for (int i = 0; i < brandNames.length; i++) {
                        if (i != (brandNames.length - 1)) {
                            Map<String, String> map = brandDao.selectOptionListByName(brandNames[i]);
                            String jsonString = JSON.toJSONString(map);
                            jsonStr += jsonString + ",";
                        } else {
                            Map<String, String> map = brandDao.selectOptionListByName(brandNames[i]);
                            String jsonString = JSON.toJSONString(map);
                            jsonStr += jsonString + "]";
                        }
                    }
                    template.setBrandIds(jsonStr);
                }

                if (types[4] != null) {
                    String str = types[4].toString();
                    String[] cai = str.split(",");
                    String jsonStr = "[";
                    String text = "{\"text\":";
                    for (int i = 0; i < cai.length; i++) {
                        if (i != cai.length - 1) {
                            jsonStr += text + "\"" + cai[i] + "\"" + "},";
                        } else {
                            jsonStr += text + "\"" + cai[i] + "\"" + "}]";
                        }
                    }
                    template.setCustomAttributeItems(jsonStr);
                } else {
                    String str = "[]";
                    template.setCustomAttributeItems(str);
                }
            }
            typeTemplateDao.insertSelective(template);
        }
    }

    @Override
    public void uploadExcel2ItemCat(List<String[]> excelList) {
        if (excelList != null) {
                ItemCat itemCat = new ItemCat();
            for (String[] cats : excelList) {
                String parentName = cats[1];
                if (parentName != null || !"".equals(parentName)) {
                    ItemCatQuery query = new ItemCatQuery();
                    ItemCatQuery.Criteria criteria = query.createCriteria();
                    criteria.andNameEqualTo(parentName);
                    List<ItemCat> itemCats = itemCatDao.selectByExample(query);
                    if (itemCats != null) {
                        for (ItemCat cat : itemCats) {
                            itemCat.setParentId(cat.getId());
                        }
                    }
                }
                itemCat.setName(cats[2]);
                String typeName = cats[3];
                if (typeName != null) {
                    TypeTemplateQuery query = new TypeTemplateQuery();
                    TypeTemplateQuery.Criteria criteria = query.createCriteria();
                    criteria.andNameEqualTo(typeName);
                    List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(query);
                    if (typeTemplates != null || !"".equals(typeName)) {
                        for (TypeTemplate template : typeTemplates) {
                            itemCat.setTypeId(template.getId());
                        }
                    }
                }
            }
            itemCatDao.insertSelective(itemCat);
        }
    }
}
