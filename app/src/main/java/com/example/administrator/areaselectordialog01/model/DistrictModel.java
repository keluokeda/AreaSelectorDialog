package com.example.administrator.areaselectordialog01.model;

public class DistrictModel {
    private String name;

    private String id;

    public DistrictModel() {
        super();
    }

    public DistrictModel(String name, String id) {
        this.name = name;

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


}
