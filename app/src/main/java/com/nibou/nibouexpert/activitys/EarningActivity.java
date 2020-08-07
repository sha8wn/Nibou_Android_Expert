package com.nibou.nibouexpert.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.adapter.EarningListAdapter;
import com.nibou.nibouexpert.adapter.ReviewListAdapter;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.ActivityEarningBinding;
import com.nibou.nibouexpert.models.PaymentListModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.DateFormatUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.ArrayList;

public class EarningActivity extends BaseActivity {

    private ActivityEarningBinding binding;
    private Context context;
    private PaymentListModel paymentListModel;
    private EarningListAdapter earningListAdapter;


    private void setToolbar() {
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earning);
        context = this;
        setToolbar();

        if (AppUtil.isInternetAvailable(context)) {
            if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                getMonthDaysEarningNetworkCall(1, 1000);
            }
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showData() {
        binding.earningList.setLayoutManager(new LinearLayoutManager(context));
        earningListAdapter = new EarningListAdapter(context, paymentListModel);
        binding.earningList.setAdapter(earningListAdapter);
    }

    private void getMonthDaysEarningNetworkCall(int page_number, int page_size) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getOverallByDayEarningRequest(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), true, page_number, page_size), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess && binding != null) {
                    paymentListModel = (PaymentListModel) data;
                    showData();
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }
}
