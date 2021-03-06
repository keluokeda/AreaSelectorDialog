package com.example.administrator.area_select_dialog.model;

import java.util.List;

public class CityModel {
    private String name;
    private List<DistrictModel> districtList;
    private String id;

    public CityModel() {
        super();
    }


    public CityModel(String name, List<DistrictModel> districtList, String id) {
        this.name = name;
        this.districtList = districtList;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DistrictModel> getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List<DistrictModel> districtList) {
        this.districtList = districtList;
    }



}
