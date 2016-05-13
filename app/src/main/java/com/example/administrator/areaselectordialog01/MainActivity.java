package com.example.administrator.areaselectordialog01;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.area_select_dialog.AreaSelectDialog;
import com.example.administrator.area_select_dialog.model.AreaBean;

public class MainActivity extends Activity {
    private AreaBean mAreaBean;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView= (TextView) findViewById(R.id.tv);

        try {
            AreaSelectDialog.initAreaData(getAssets().open("province_data_01.xml"),"province","city","district");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void show(View view){
        new AreaSelectDialog(MainActivity.this, new AreaSelectDialog.AreaSelectLintener() {
            @Override
            public void refreshArea(AreaBean bean) {
                mAreaBean=bean;
                String content=bean.getProvice()+":"+bean.getP_id()+"\n"+bean.getCity()+":"+bean.getC_id()+"\n"+bean.getDistrict()+":"+bean.getD_id();
                mTextView.setText(content);
            }
        },mAreaBean).show();
    }
}
