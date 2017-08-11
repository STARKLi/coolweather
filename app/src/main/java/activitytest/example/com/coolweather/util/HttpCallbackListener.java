package activitytest.example.com.coolweather.util;

/**
 * Created by STARK on 2017/8/11.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
