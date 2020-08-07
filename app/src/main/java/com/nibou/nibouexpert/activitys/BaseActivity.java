package com.nibou.nibouexpert.activitys;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.models.AppSessionModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppForegroundStateManager;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.List;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ENGLISH);
        if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE) != null) {
            Configuration conf = getResources().getConfiguration();
            conf.setLocale(new Locale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE)));
            getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            registerReceiver(broadcastRefreshReceiver, new IntentFilter(AppConstant.NEW_MESSAGE_RECIEVER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(broadcastRefreshReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcastRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.hasExtra(AppConstant.SESSION_TIMEOUT_ACTION)) {
                    String time = "2";
                    if (intent.hasExtra("time"))
                        time = intent.getStringExtra("time");
                    showInfoCustomDialog(context, intent.getStringExtra(AppConstant.ROOM_ID), getString(R.string.delay_response).toUpperCase(), getString(R.string.delay_alert_message, time), getString(R.string.OK), null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void showInfoCustomDialog(final Context context, String roomId, String title, String message, String buttonText, final AppDialogs.DialogCallback dialogCallback) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.edittext).setVisibility(View.GONE);
        dialog.findViewById(R.id.button1).setVisibility(View.GONE);
        dialog.findViewById(R.id.button2).setVisibility(View.GONE);
        dialog.findViewById(R.id.button).setVisibility(View.VISIBLE);

        TextView titletext = dialog.findViewById(R.id.title);
        titletext.setText(title);
        TextView messagetext = dialog.findViewById(R.id.message);
        messagetext.setText(message);
        TextView button = dialog.findViewById(R.id.button);
        button.setText(buttonText);
        button.setOnClickListener(v -> {
            dialog.dismiss();
//            if (AppUtil.isInternetAvailable(this)) {
//                sendMessageNetworkCall(this, roomId, RequestBody.create(MediaType.parse("text/plain"), "END_SESSION"), null);
//            } else {
//                AppUtil.showToast(this, getString(R.string.internet_error));
//            }
        });
        dialog.show();
    }

    public void sendMessageNetworkCall(Context context, String room_id, RequestBody text, List<MultipartBody.Part> images) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).sendMessage(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), room_id, text, null), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    Log.e("continue", "success");
                } else {
                    Log.e("continue", "fail");
                }
            }

            @Override
            public void failed() {
                Log.e("continue", "onFailure");
            }
        });
    }

}
