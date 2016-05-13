package com.example.administrator.area_select_dialog;


import com.example.administrator.area_select_dialog.model.CityModel;
import com.example.administrator.area_select_dialog.model.DistrictModel;
import com.example.administrator.area_select_dialog.model.ProvinceModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class XmlParserHandler extends DefaultHandler {
    private String firstElementName;//主元素名称
    private String secondElementName;//二级元素名称
    private String thirdElementName;//三级元素名称

    public XmlParserHandler(String firstElementName, String secondElementName, String thirdElementName) {
        this.firstElementName = firstElementName;
        this.secondElementName = secondElementName;
        this.thirdElementName = thirdElementName;
    }

    /**
     * 存储所有的解析对象
     */
    private List<ProvinceModel> provinceList = new ArrayList<>();



    public List<ProvinceModel> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
        // 当读到第一个开始标签的时候，会触发这个方法
    }

    ProvinceModel provinceModel = new ProvinceModel();
    CityModel cityModel = new CityModel();
    DistrictModel districtModel = new DistrictModel();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // 当遇到开始标记的时候，调用这个方法
        if (qName.equals(firstElementName)) {
            provinceModel = new ProvinceModel();
            provinceModel.setName(attributes.getValue(0));
            provinceModel.setId(attributes.getValue(1));

            //  Log.e("当前省的ID", attributes.getValue(1));
            provinceModel.setCityList(new ArrayList<CityModel>());
        } else if (qName.equals(secondElementName)) {
            cityModel = new CityModel();
            cityModel.setName(attributes.getValue(0));
            cityModel.setId(attributes.getValue(1));
            //  Log.e("当前市的ID", attributes.getValue(1));
            cityModel.setDistrictList(new ArrayList<DistrictModel>());
        } else if (qName.equals(thirdElementName)) {
            districtModel = new DistrictModel();
            districtModel.setName(attributes.getValue(0));
            districtModel.setId(attributes.getValue(1));
            // Log.e("当前区的ID", attributes.getValue(1));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // 遇到结束标记的时候，会调用这个方法
        if (qName.equals(thirdElementName)) {
            cityModel.getDistrictList().add(districtModel);
        } else if (qName.equals(secondElementName)) {
            provinceModel.getCityList().add(cityModel);
        } else if (qName.equals(firstElementName)) {
            provinceList.add(provinceModel);
        }
    }


}
