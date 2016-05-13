package com.example.administrator.area_select_dialog;

import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.administrator.area_select_dialog.adapter.ArrayWheelAdapter;
import com.example.administrator.area_select_dialog.interfaces.OnWheelChangedListener;
import com.example.administrator.area_select_dialog.model.AreaBean;
import com.example.administrator.area_select_dialog.model.CityModel;
import com.example.administrator.area_select_dialog.model.DistrictModel;
import com.example.administrator.area_select_dialog.model.ProvinceModel;
import com.example.administrator.area_select_dialog.view.WheelView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 区域选择对话框
 */

@SuppressWarnings("unused")
public class AreaSelectDialog extends Dialog implements OnWheelChangedListener, View.OnClickListener {

    /**
     * 初始化三级联动的数据
     *
     * @param inputStream 包含数据的流
     * @return 成功返回true
     */
    public static boolean initAreaData(InputStream inputStream) {
        boolean isSuccess = false;
        try {
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(inputStream, handler);
            inputStream.close();

            List<ProvinceModel> provinceList = handler.getDataList();
            sProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                sProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();

                String[] cityNames = new String[cityList.size()];
                sProvinceIdDatasMap.put(provinceList.get(i).getName(), provinceList.get(i).getId());
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    sCityIdDatasMap.put(sProvinceDatas[i] + cityList.get(j).getName(), cityList.get(j).getId());
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getId());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        //  mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        sDistrictIdDatasMap.put(sProvinceDatas[i] + cityList.get(j).getName() + districtList.get(k).getName(), districtList.get(k).getId());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    sDistrictMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                sCityDataMap.put(provinceList.get(i).getName(), cityNames);
            }
            isSuccess = true;
        } catch (Throwable t) {
                t.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 所有省
     */
    protected static String[] sProvinceDatas;
    /**
     * key - 省 value - 市[]
     */
    protected static Map<String, String[]> sCityDataMap = new HashMap<>();
    /**
     * key - 市 values - 区[]
     */
    protected static Map<String, String[]> sDistrictMap = new HashMap<>();

    /**
     * 省名称 省id
     */
    protected static Map<String, String> sProvinceIdDatasMap = new HashMap<>();
    /**
     * 省名称+市名称 市id
     */
    protected static Map<String, String> sCityIdDatasMap = new HashMap<>();
    /**
     * 省名称+市名称+区名称 区id
     */
    protected static Map<String, String> sDistrictIdDatasMap = new HashMap<>();


    /**
     * 当前省的名称，ID
     */
    protected String mCurrentProviceName, mCurrentProviceId;
    /**
     * 当前市的名称，ID
     */
    protected String mCurrentCityName, mCurrentCityId;
    /**
     * 当前区的名称，ID
     */
    protected String mCurrentDistrictName = "", mCurrentDistrictId = "";


    private Button btn_cancel;
    private Button btn_sure;

    private AreaBean areaBean;
    private Context mContext;
    private WheelView wv_province, wv_city, wv_district;

    /**
     * 点击确认的回调接口
     */
    public interface AreaSelectLintener {
        void refreshArea(AreaBean bean);
    }

    private AreaSelectLintener areaSelectLintener;


    public AreaSelectDialog(Context context, AreaSelectLintener areaSelectLintener) {
        super(context, R.style.Dialog_FS);
        this.mContext = context;
        this.areaSelectLintener = areaSelectLintener;
        areaBean = null;

    }

    /**
     * 显示区域滚动视图，并为视图指定默认选中的数据
     *
     * @param context            上下文
     * @param areaSelectLintener 点击确认执行的回调
     * @param areaBean           默认显示的省市区
     */
    public AreaSelectDialog(Context context, AreaSelectLintener areaSelectLintener, AreaBean areaBean) {
        this(context, areaSelectLintener);
        this.areaBean = areaBean;

    }

    private void setDefaultSeltctorItem() {
        if (areaBean == null) {
            wv_province.setCurrentItem(0);
            return;
        }
        mCurrentProviceName = areaBean.getProvice();
        mCurrentProviceId = areaBean.getP_id();
        mCurrentCityName = areaBean.getCity();
        mCurrentCityId = areaBean.getC_id();
        mCurrentDistrictName = areaBean.getDistrict();
        mCurrentDistrictId = areaBean.getD_id();

        for (int i = 0; i < sProvinceDatas.length; i++) {
            if (sProvinceDatas[i].equals(mCurrentProviceName)) {
                wv_province.setCurrentItem(i);
                break;
            }
        }
        String[] cityNameArrays = sCityDataMap.get(mCurrentProviceName);
        for (int j = 0; j < cityNameArrays.length; j++) {
            if (cityNameArrays[j].equals(areaBean.getCity())) {
                wv_city.setCurrentItem(j);
                break;
            }
        }

        String[] districtNameArrays = sDistrictMap.get(mCurrentCityName);
        for (int k = 0; k < districtNameArrays.length; k++) {
            if (districtNameArrays[k].equals(areaBean.getDistrict())) {
                wv_district.setCurrentItem(k);
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_area_select);
        initView();
        initListener();
        initData();
        setDefaultSeltctorItem();
    }


    private void initView() {
        wv_province = (WheelView) findViewById(R.id.wv_dialog_province);
        wv_city = (WheelView) findViewById(R.id.wv_dialog_city);
        wv_district = (WheelView) findViewById(R.id.wv_dialog_district);

        btn_cancel = (Button) findViewById(R.id.btn_dialog_cancel);
        btn_sure = (Button) findViewById(R.id.btn_dialog_sure);
    }

    private void initListener() {
        wv_province.addChangingListener(this);
        wv_city.addChangingListener(this);
        wv_district.addChangingListener(this);

        btn_cancel.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
    }

    private void initData() {
        wv_province.setViewAdapter(new ArrayWheelAdapter<>(mContext, sProvinceDatas));
        // 设置可见条目数量
//        wv_province.setVisibleItems(5);
//        wv_city.setVisibleItems(5);
//        wv_district.setVisibleItems(5);

        updateCities();
        updateAreas();
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = wv_province.getCurrentItem();
        mCurrentProviceName = sProvinceDatas[pCurrent];
        mCurrentProviceId = sProvinceIdDatasMap.get(mCurrentProviceName);
        String[] cities = sCityDataMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        wv_city.setViewAdapter(new ArrayWheelAdapter<>(mContext, cities));
        wv_city.setCurrentItem(0);
        updateAreas();


    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = wv_city.getCurrentItem();
        mCurrentCityName = sCityDataMap.get(mCurrentProviceName)[pCurrent];
        mCurrentCityId = sCityIdDatasMap.get(mCurrentProviceName + mCurrentCityName);
        String[] areas = sDistrictMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        wv_district.setViewAdapter(new ArrayWheelAdapter<>(mContext, areas));
        wv_district.setCurrentItem(0);

        mCurrentDistrictName = areas[0];
        mCurrentDistrictId = sDistrictIdDatasMap.get(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wv_province) {
            updateCities();
        } else if (wheel == wv_city) {
            updateAreas();
        } else if (wheel == wv_district) {
            mCurrentDistrictName = sDistrictMap.get(mCurrentCityName)[newValue];
            mCurrentDistrictId = sDistrictIdDatasMap.get(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_dialog_cancel) {
            super.dismiss();
        } else if (v.getId() == R.id.btn_dialog_sure) {
            areaSelectLintener.refreshArea(new AreaBean(mCurrentProviceName, mCurrentProviceId, mCurrentCityName, mCurrentCityId, mCurrentDistrictName, mCurrentDistrictId));
            dismiss();

        }
    }

}
