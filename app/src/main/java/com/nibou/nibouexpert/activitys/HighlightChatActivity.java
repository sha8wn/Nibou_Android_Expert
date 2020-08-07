package com.nibou.nibouexpert.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.*;
import android.text.style.BackgroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.actioncable.MessageApiCall;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.ActivityChangePasswordBinding;
import com.nibou.nibouexpert.databinding.ActivityHighlightChatBinding;
import com.nibou.nibouexpert.models.MessageModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.ChatConstants;
import com.nibou.nibouexpert.utils.LocalPrefences;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.List;

public class HighlightChatActivity extends BaseActivity {

    private ActivityHighlightChatBinding binding;
    private Context context;
    public static int SUCCESS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_highlight_chat);
        context = this;
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.mainText.setText(getIntent().getStringExtra(ChatConstants.MESSAGE));
        binding.mainText.setOnClickListener(v -> binding.mainText.setSelection(0));

        binding.save.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    sendBookmarkedNetworkCall();
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });


        binding.mainText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (menu != null && menu.size() > 1) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                }
                return true;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

    }


    public void sendBookmarkedNetworkCall() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("message_id", getIntent().getStringExtra(ChatConstants.MESSAGE_ID));
        parameters.put("text", getSelectedText());
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).uploadBookmark(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), getIntent().getStringExtra(ChatConstants.ROOM_ID), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (binding != null)
                    binding.mainText.setSelection(0);
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.bookmarked_success_alert), getString(R.string.OK), status -> {
                        Intent intent = new Intent();
                        intent.putExtras(getIntent().getExtras());
                        setResult(SUCCESS, intent);
                        finish();
                    });
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.wrong_with_backend), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                if (binding != null)
                    binding.mainText.setSelection(0);
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private boolean screenValidate() {
        if (binding.mainText.getSelectionStart() == 0 && binding.mainText.getSelectionEnd() == 0) {
            binding.mainText.setSelection(0);
            AppUtil.showToast(this, getResources().getString(R.string.bookmarked_selection_alert));
            return false;
        }
        return true;
    }

    private String getSelectedText() {
        return (binding.mainText.getText().toString().substring(binding.mainText.getSelectionStart(), binding.mainText.getSelectionEnd()));
    }
}
