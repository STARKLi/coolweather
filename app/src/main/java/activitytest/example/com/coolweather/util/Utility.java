package activitytest.example.com.coolweather.util;

import android.text.TextUtils;

import activitytest.example.com.coolweather.db.CoolWeatherDB;
import activitytest.example.com.coolweather.model.City;
import activitytest.example.com.coolweather.model.County;
import activitytest.example.com.coolweather.model.Province;

/**
 * Created by STARK on 2017/8/11.
 */

public class Utility {
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces=response.split(",");
            if (allProvinces!=null&&allProvinces.length>0) {
                for (String p : allProvinces) {
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId ) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities=response.split(",");
            if (allCities!=null&&allCities.length>0) {
                for (String c : allCities) {
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCities(city);
                }
                return true;
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties=response.split(",");
            if (allCounties!=null&&allCounties.length>0) {
                for (String c : allCounties) {
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
