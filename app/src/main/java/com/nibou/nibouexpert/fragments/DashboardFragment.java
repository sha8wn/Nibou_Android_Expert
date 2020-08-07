package com.nibou.nibouexpert.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.activitys.EarningActivity;
import com.nibou.nibouexpert.activitys.RatingActivity;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.FragmentDashboardBinding;
import com.nibou.nibouexpert.models.ActiveChatSessionModel;
import com.nibou.nibouexpert.models.PaymentModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.DateFormatUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.concurrent.TimeUnit;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        context = getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        if (LocalPrefences.getInstance().getLocalProfileModel(context) != null) {
            binding.date.setText(context.getString(R.string.join_txt) + " " + DateFormatUtil.changeDateFormat(DateFormatUtil.getServerMilliSeconds(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getAttributes().getCreated_at()), "dd MMMM , yyyy"));
        }

        binding.rating.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RatingActivity.class);
            startActivity(intent);
        });
        binding.history.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EarningActivity.class);
            startActivity(intent);
        });

        binding.totalAmount.setText(AppUtil.getAmountSign(context) + "0");
        binding.monthAmount.setText(AppUtil.getAmountSign(context) + "0");
        binding.totalTime.setText("0 " + context.getString(R.string.minutes));
        binding.monthTime.setText("0 " + context.getString(R.string.minutes));

        if (AppUtil.isInternetAvailable(context)) {
            if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                getTotalEarningNetworkCall();
            }
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void getTotalEarningNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getTotalEarningRequest(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess && binding != null) {
                    PaymentModel paymentModel = (PaymentModel) data;
                    binding.totalAmount.setText(AppUtil.getAmountSign(context) + paymentModel.getData().getAttributes().getAmount());
                    if (paymentModel.getData().getAttributes().getTotalSeconds() != null) {
                        binding.totalTime.setText(((Long.parseLong(paymentModel.getData().getAttributes().getTotalSeconds()) % 3600) / 60) + " " + context.getString(R.string.minutes));
                    }
                }
                if (binding != null) {
                    if (AppUtil.isInternetAvailable(context)) {
                        getMonthEarningNetworkCall();
                    } else {
                        AppUtil.showToast(context, getString(R.string.internet_error));
                        AppDialogs.getInstance().showProgressBar(context, null, false);
                    }
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                }
            }

            @Override
            public void failed() {
                if (binding != null) {
                    if (AppUtil.isInternetAvailable(context)) {
                        getMonthEarningNetworkCall();
                    } else {
                        AppUtil.showToast(context, getString(R.string.internet_error));
                        AppDialogs.getInstance().showProgressBar(context, null, false);
                    }
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                }
            }
        });
    }


    private void getMonthEarningNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getMonthEarningRequest(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), DateFormatUtil.getFirstDateOfMonth()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess && binding != null) {
                    PaymentModel paymentModel = (PaymentModel) data;
                    binding.monthAmount.setText(AppUtil.getAmountSign(context) + paymentModel.getData().getAttributes().getAmount());
                    if (paymentModel.getData().getAttributes().getTotalSeconds() != null) {
                        binding.monthTime.setText(((Long.parseLong(paymentModel.getData().getAttributes().getTotalSeconds()) % 3600) / 60) + " " + context.getString(R.string.minutes));
                    }
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

}
