package com.example.administrator.areaselectordialog01.model;

/**
 * Created by Administrator on 2016/2/24 0024.
 */
public class AreaBean {
    private String provice;
    private String city;
    private String district;
    private String p_id;
    private String c_id;
    private String d_id;

    public AreaBean(String provice, String p_id, String city, String c_id, String district, String d_id) {
        this.provice = provice;
        this.city = city;
        this.district = district;
        this.p_id = p_id;
        this.c_id = c_id;
        this.d_id = d_id;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
    }


    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
