package com.nibou.nibouexpert.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.actioncable.ActionSessionHandler;
import com.nibou.nibouexpert.activitys.*;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.FragmentSettingsBinding;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.HashMap;

public class SettingFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        binding.updateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        binding.changePwd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });


        binding.timing.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TimingActivity.class);
            startActivity(intent);
        });


        binding.bio.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BioActivity.class);
            startActivity(intent);

        });
        binding.specialization.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SpecializationActivity.class);
            startActivity(intent);
        });

        binding.feedback.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            startActivity(intent);

        });
        binding.langauge.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LanguageSettingActivity.class);
            startActivity(intent);
        });

        binding.logout.setOnClickListener(v -> {

            AppDialogs.getInstance().showConfirmCustomDialog(getActivity(), getString(R.string.logout), getString(R.string.logout_txt), getString(R.string.CANCEL), getString(R.string.OK), new AppDialogs.DialogCallback() {
                @Override
                public void response(boolean status) {
                    if (status) {
                        logoutNetworkCall();
                    }
                }
            });

        });
    }

    private void logoutNetworkCall() {
        HashMap<Object, Object> parameters = new HashMap<>();
        parameters.put("available", false);
        AppDialogs.getInstance().showProgressBar(getActivity(), null, true);
        ApiHandler.requestService(getActivity(), ApiClient.getClient().create(ApiEndPoint.class).logoutRequest(LocalPrefences.getInstance().getString(getActivity(), AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(getActivity()).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(getActivity(), null, false);
                clearLocalData();
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(getActivity(), null, false);
                clearLocalData();
            }
        });
    }

    private void clearLocalData() {
        ActionSessionHandler.getInstance(getActivity()).disconnectWS();
        String language = LocalPrefences.getInstance().getString(getActivity(), AppConstant.APP_LANGUAGE);
        boolean firstLaunch = LocalPrefences.getInstance().getBoolean(getActivity(), AppConstant.IS_FIRST_LAUNCH_SUCCESS);
        LocalPrefences.getInstance().clearSharePreference(getActivity());
        LocalPrefences.getInstance().putString(getActivity(), AppConstant.APP_LANGUAGE, language);
        LocalPrefences.getInstance().putBoolean(getActivity(), AppConstant.IS_FIRST_LAUNCH_SUCCESS, firstLaunch);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finishAffinity();
    }
}
