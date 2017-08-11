package activitytest.example.com.coolweather.model;

/**
 * Created by STARK on 2017/8/10.
 */

public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id=id;
    }
    public String getProvinceName() {
        return provinceName;
    }
    public String getProvinceCode() {
        return provinceCode;
    }
    public void setProvinceName(String provinceName) {
        this.provinceName=provinceName;
    }
    public  void setProvinceCode(String provinceCode) {
        this.provinceCode=provinceCode;
    }
}
