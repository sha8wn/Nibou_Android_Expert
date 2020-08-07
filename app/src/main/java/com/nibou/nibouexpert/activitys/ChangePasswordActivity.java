package com.nibou.nibouexpert.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.ActivityChangePasswordBinding;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.HashMap;

public class ChangePasswordActivity extends BaseActivity {
    private ActivityChangePasswordBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        context = this;
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.save.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    changePasswordNetworkCall();
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }

    private boolean screenValidate() {
        if (TextUtils.isEmpty(binding.etCurrentPassword.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.currentpwd_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etNewPassword.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.newpwd_empty_alert));
            return false;
        } else if (binding.etNewPassword.getText().toString().length() < 6) {
            AppUtil.showToast(this, getResources().getString(R.string.pwd_length_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etRepeatPassword.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.repeatpwd_empty_alert));
            return false;
        } else if (!TextUtils.equals(binding.etNewPassword.getText(), binding.etRepeatPassword.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.pwd_mismatch_alert));
            return false;
        }
        return true;
    }

    private void changePasswordNetworkCall() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("old_password", binding.etCurrentPassword.getText().toString());
        parameters.put("password", binding.etNewPassword.getText().toString());
        parameters.put("password_confirmation", binding.etRepeatPassword.getText().toString());

        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).changePassword(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.pwd_change_success_alert), getString(R.string.OK), status -> onBackPressed());
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.pwd_change_failed_alert), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }
}
