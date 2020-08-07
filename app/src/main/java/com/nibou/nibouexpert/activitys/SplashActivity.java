package com.nibou.nibouexpert.activitys;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.models.ProfileModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class SplashActivity extends BaseActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        initView();

        if (AppUtil.isInternetAvailable(this) && LocalPrefences.getInstance().isUserLogin(this) && isTimeZoneChanged()) {
            updateProfileNetworkCall();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void initView() {
        handler = new Handler();
    }

    private Runnable runnable = () -> checkDeviceLanguage();

    private void checkDeviceLanguage() {
        if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE) == null) {
            if (Locale.getDefault().getLanguage().equals(AppConstant.ENGLISH)) {
                Configuration conf = getResources().getConfiguration();
                conf.setLocale(new Locale(AppConstant.ENGLISH));
                getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ENGLISH);
                moveToNextScreen();
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.TURKISH)) {
                Configuration conf = getResources().getConfiguration();
                conf.setLocale(new Locale(AppConstant.TURKISH));
                getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.TURKISH);
                moveToNextScreen();
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.ARABIC)) {
                Configuration conf = getResources().getConfiguration();
                conf.setLocale(new Locale(AppConstant.ARABIC));
                getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ARABIC);
                moveToNextScreen();
            } else {
                Intent intent = new Intent(SplashActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE).equals(Locale.getDefault().getLanguage())) {
            //no need to show language screen
            Configuration conf = getResources().getConfiguration();
            conf.setLocale(new Locale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE)));
            getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
            moveToNextScreen();
        } else {
            if (Locale.getDefault().getLanguage().equals(AppConstant.ENGLISH)) {
                Configuration conf = getResources().getConfiguration();
                conf.setLocale(new Locale(AppConstant.ENGLISH));
                getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ENGLISH);
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.TURKISH)) {
                Configuration conf = getResources().getConfiguration();
                conf.setLocale(new Locale(AppConstant.TURKISH));
                getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.TURKISH);
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.ARABIC)) {
                Configuration conf = getResources().getConfiguration();
                conf.setLocale(new Locale(AppConstant.ARABIC));
                getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ARABIC);
            } else {
                Configuration conf = getResources().getConfiguration();
                conf.setLocale(new Locale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE)));
                getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
            }
            moveToNextScreen();
        }
    }

    private void moveToNextScreen() {
        if (LocalPrefences.getInstance().isUserLogin(this)) {
            Intent intent = new Intent(SplashActivity.this, HomeChatActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private boolean isTimeZoneChanged() {
        if (LocalPrefences.getInstance().getString(this, AppConstant.TIMEZONE) != null && TimeZone.getDefault().getID().equals(LocalPrefences.getInstance().getString(this, AppConstant.TIMEZONE))) {
            return false;
        } else {
            return true;
        }
    }

    private void updateProfileNetworkCall() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("timezone", TimeZone.getDefault().getID());
        ApiHandler.requestService(this, ApiClient.getClient().create(ApiEndPoint.class).updateMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(this).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    LocalPrefences.getInstance().putString(SplashActivity.this, AppConstant.TIMEZONE, TimeZone.getDefault().getID());
                }
            }

            @Override
            public void failed() {
            }
        });
    }
}

