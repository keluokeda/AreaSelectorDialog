package com.example.administrator.areaselectordialog01;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.administrator.areaselectordialog01.adapter.ArrayWheelAdapter;
import com.example.administrator.areaselectordialog01.interfaces.OnWheelChangedListener;
import com.example.administrator.areaselectordialog01.model.AreaBean;
import com.example.administrator.areaselectordialog01.model.CityModel;
import com.example.administrator.areaselectordialog01.model.DistrictModel;
import com.example.administrator.areaselectordialog01.model.ProvinceModel;
import com.example.administrator.areaselectordialog01.view.WheelView;

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
    private Context mContext;
    private WheelView wv_province, wv_city, wv_district;

    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 省 values - id
     */
    protected Map<String, String> mProvinceIdDatasMap = new HashMap<String, String>();
    /**
     * key - 市 values - id
     */
    protected Map<String, String> mCityIdDatasMap = new HashMap<String, String>();
    /**
     * key - 区 values - id
     */
    protected Map<String, String> mDistrictIdDatasMap = new HashMap<String, String>();

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

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode = "";
    private Button btn_cancel;
    private Button btn_sure;

    private AreaBean areaBean;

    public interface AreaSelectLintener {
        public void refreshArea(AreaBean bean);
    }

    private AreaSelectLintener areaSelectLintener;
    private int type = 0;

    public AreaSelectDialog(Context context, AreaSelectLintener areaSelectLintener, int type) {
        super(context, R.style.Dialog_FS);
        this.mContext = context;
        this.areaSelectLintener = areaSelectLintener;
        this.type = type;
    }

    public AreaSelectDialog(Context context, AreaSelectLintener areaSelectLintener) {
        super(context, R.style.Dialog_FS);
        this.mContext = context;
        this.areaSelectLintener = areaSelectLintener;
        areaBean=null;

    }

    /**
     * 显示区域滚动视图，并为视图指定默认选中的数据
     * @param context 上下文
     * @param areaSelectLintener 点击确认执行的回调
     * @param areaBean 默认显示的省市区
     */
    public AreaSelectDialog(Context context, AreaSelectLintener areaSelectLintener, AreaBean areaBean){
        this(context, areaSelectLintener);
        this.areaBean=areaBean;

    }

    private void setDefaultSeltctorItem(){
        if(areaBean==null){
            return;
        }
        mCurrentProviceName=areaBean.getProvice();
        mCurrentProviceId=areaBean.getP_id();
        mCurrentCityName=areaBean.getCity();
        mCurrentCityId=areaBean.getC_id();
        mCurrentDistrictName=areaBean.getDistrict();
        mCurrentDistrictId=areaBean.getD_id();

        for(int i=0;i<mProvinceDatas.length;i++){
            if(mProvinceDatas[i].equals(mCurrentProviceName)){
                wv_province.setCurrentItem(i);
                break;
            }
        }
        String[] cityNameArrays=mCitisDatasMap.get(mCurrentProviceName);
        for(int j=0;j<cityNameArrays.length;j++){
            if(cityNameArrays[j].equals(areaBean.getCity())){
                wv_city.setCurrentItem(j);
                break;
            }
        }

        String[] districtNameArrays=mDistrictDatasMap.get(mCurrentCityName);
        for(int k=0;k<districtNameArrays.length;k++){
            if(districtNameArrays[k].equals(areaBean.getDistrict())){
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
        initProvinceDatas();
        wv_province.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
        // 设置可见条目数量
        wv_province.setVisibleItems(7);
        wv_city.setVisibleItems(7);
        wv_district.setVisibleItems(7);

        updateCities();
        updateAreas();
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = wv_province.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        mCurrentProviceId = mProvinceIdDatasMap.get(mCurrentProviceName);
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        //  String[] citiesId = mCitisDatasMap.get(mCurrentProviceId);
        if (cities == null) {
            cities = new String[]{""};
        }
        wv_city.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
        wv_city.setCurrentItem(0);
        updateAreas();


    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = wv_city.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        mCurrentCityId = mCityIdDatasMap.get(mCurrentCityName);
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        wv_district.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
        wv_district.setCurrentItem(0);

        mCurrentDistrictName = areas[0];
        mCurrentDistrictId = mDistrictIdDatasMap.get(mCurrentDistrictName);
    }

    /**
     * 解析省市区的XML数据
     */

    private void initProvinceDatas() {

        List<ProvinceModel> provinceList = null;
        AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("province_data_01.xml");
            if (type == 1) {
                input = asset.open("province_data_gd.xml");
            }

            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                mCurrentProviceId = provinceList.get(0).getId();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    mCurrentCityId = cityList.get(0).getId();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentDistrictId = districtList.get(0).getId();


                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                mProvinceIdDatasMap.put(provinceList.get(i).getName(), provinceList.get(i).getId());
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    mCityIdDatasMap.put(cityList.get(j).getName(), cityList.get(j).getId());
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getId());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        //  mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        mDistrictIdDatasMap.put(districtList.get(k).getName(), districtList.get(k).getId());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }

    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wv_province) {
            updateCities();
        } else if (wheel == wv_city) {
            updateAreas();
        } else if (wheel == wv_district) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentDistrictId = mDistrictIdDatasMap.get(mCurrentDistrictName);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_dialog_cancel:
                super.dismiss();
                break;
            case R.id.btn_dialog_sure:
                /*Toast.makeText(mContext, "当前选中:" + mCurrentProviceName + "," + mCurrentCityName + ","
                        + mCurrentDistrictName + "," + mCurrentZipCode, Toast.LENGTH_SHORT).show();*/
                areaSelectLintener.refreshArea(new AreaBean(mCurrentProviceName, mCurrentProviceId, mCurrentCityName, mCurrentCityId, mCurrentDistrictName, mCurrentDistrictId));


                //Log.e("当前选中城市", mCurrentProviceName + "," + mCurrentCityName + "," + mCurrentDistrictName);
              //  Log.e("当前选中ID", mCurrentProviceId + "," + mCurrentCityId + "," + mCurrentDistrictId);


                dismiss();
                break;

        }

    }


}
