package activitytest.example.com.coolweather.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import activitytest.example.com.coolweather.R;
import activitytest.example.com.coolweather.db.CoolWeatherDB;
import activitytest.example.com.coolweather.model.City;
import activitytest.example.com.coolweather.model.County;
import activitytest.example.com.coolweather.model.Province;
import activitytest.example.com.coolweather.util.HttpCallbackListener;
import activitytest.example.com.coolweather.util.HttpUtil;
import activitytest.example.com.coolweather.util.Utility;

public class ChooseArea extends AppCompatActivity {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private int currentLevel;
    private List<String> dataList=new ArrayList<>();
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private CoolWeatherDB coolWeatherDB;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private TextView titleText;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        titleText=(TextView)findViewById(R.id.title_text);
        listView=(ListView)findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(arrayAdapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE) {
                    selectedProvince=provinceList.get(position);
                    queryCity();
                }else if (currentLevel==LEVEL_CITY) {
                    selectedCity=cityList.get(position);
                    queryCounty();
                }
            }
        });
        queryProvince();
    }
    private void queryProvince() {
        provinceList=coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null,"province");
        }
    }

    private void queryCity() {
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
        } else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
        arrayAdapter.notifyDataSetChanged();
        listView.setSelection(0);
        titleText.setText(selectedProvince.getProvinceName());
        currentLevel=LEVEL_CITY;
    }
    private void queryCounty() {
        countyList=coolWeatherDB.loadCounty(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
        } else {
            queryFromServer(selectedCity.getCityCode(),"city");
        }
        arrayAdapter.notifyDataSetChanged();
        titleText.setText(selectedCity.getCityName());
        listView.setSelection(0);
        currentLevel=LEVEL_COUNTY;
    }
    private void queryFromServer(final String code,final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address= "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if ("province".equals(type)) {
                    result= Utility.handleProvinceResponse(coolWeatherDB,response);
                }else if ("city".equals(type)) {
                    result=Utility.handleCityResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if ("county".equals(type)) {
                    result=Utility.handleCountyResponse(coolWeatherDB,response,selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            }else if ("city".equals(type)) {
                                queryCity();
                            }else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseArea.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void showProgressDialog() {
        if (progressDialog==null) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog() {
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY) {
            queryCity();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvince();
        } else {
            finish();
        }
    }
}
