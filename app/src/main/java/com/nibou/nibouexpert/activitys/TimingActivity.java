package com.nibou.nibouexpert.activitys;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.ActivityTimingBinding;
import com.nibou.nibouexpert.models.ProfileModel;
import com.nibou.nibouexpert.models.TimingModel;
import com.nibou.nibouexpert.utils.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Calendar;
import java.util.HashMap;

public class TimingActivity extends BaseActivity {
    private ActivityTimingBinding binding;
    private boolean isMonSelected;
    private boolean isTueSelected;
    private boolean isWedSelected;
    private boolean isThurSelected;
    private boolean isFriSelected;
    private boolean isSatSelected;
    private boolean isSunSelected;

    private String START_TIME = "00:00";
    private String END_TIME = "00:00";

    private Context context;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timing);
        context = this;
        setToolbar();
        init();

        if (AppUtil.isInternetAvailable(context)) {
            getTimingNetworkCall(true);
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showProfileData(TimingModel timingModel) {
        if (timingModel != null) {
            for (int i = 0; i < timingModel.getData().size(); i++) {
                if (timingModel.getData().get(i).getAttributes().getDay_number() == 1) {
                    isMonSelected = true;
                    binding.monday.setTag(timingModel.getData().get(i).getId());
                    selectedDayTiming(binding.monday, binding.startTimeMon, binding.endTimeMon, timingModel.getData().get(i).getAttributes().getTime_from(), timingModel.getData().get(i).getAttributes().getTime_to());
                } else if (timingModel.getData().get(i).getAttributes().getDay_number() == 2) {
                    isTueSelected = true;
                    binding.tuesday.setTag(timingModel.getData().get(i).getId());
                    selectedDayTiming(binding.tuesday, binding.startTimeTue, binding.endTimeTue, timingModel.getData().get(i).getAttributes().getTime_from(), timingModel.getData().get(i).getAttributes().getTime_to());
                } else if (timingModel.getData().get(i).getAttributes().getDay_number() == 3) {
                    isWedSelected = true;
                    binding.wednesday.setTag(timingModel.getData().get(i).getId());
                    selectedDayTiming(binding.wednesday, binding.startTimeWed, binding.endTimeWed, timingModel.getData().get(i).getAttributes().getTime_from(), timingModel.getData().get(i).getAttributes().getTime_to());
                } else if (timingModel.getData().get(i).getAttributes().getDay_number() == 4) {
                    isThurSelected = true;
                    binding.thursday.setTag(timingModel.getData().get(i).getId());
                    selectedDayTiming(binding.thursday, binding.startTimeThur, binding.endTimeThur, timingModel.getData().get(i).getAttributes().getTime_from(), timingModel.getData().get(i).getAttributes().getTime_to());
                } else if (timingModel.getData().get(i).getAttributes().getDay_number() == 5) {
                    isFriSelected = true;
                    binding.friday.setTag(timingModel.getData().get(i).getId());
                    selectedDayTiming(binding.friday, binding.startTimeFri, binding.endTimeFri, timingModel.getData().get(i).getAttributes().getTime_from(), timingModel.getData().get(i).getAttributes().getTime_to());
                } else if (timingModel.getData().get(i).getAttributes().getDay_number() == 6) {
                    isSatSelected = true;
                    binding.saturday.setTag(timingModel.getData().get(i).getId());
                    selectedDayTiming(binding.saturday, binding.startTimeSat, binding.endTimeSat, timingModel.getData().get(i).getAttributes().getTime_from(), timingModel.getData().get(i).getAttributes().getTime_to());
                } else if (timingModel.getData().get(i).getAttributes().getDay_number() == 7) {
                    isSunSelected = true;
                    binding.sunday.setTag(timingModel.getData().get(i).getId());
                    selectedDayTiming(binding.sunday, binding.startTimeSun, binding.endTimeSun, timingModel.getData().get(i).getAttributes().getTime_from(), timingModel.getData().get(i).getAttributes().getTime_to());
                }
            }
        }
    }


    private void init() {
        binding.monday.setOnClickListener(v -> {
            if (isMonSelected){
                showDeleteTimingDialog(binding.monday, binding.startTimeMon, binding.endTimeMon);
            } else {
                isMonSelected = true;
                selectedDayTiming(binding.monday, binding.startTimeMon, binding.endTimeMon, null, null);
            }
        });

        binding.tuesday.setOnClickListener(v -> {
            if (isTueSelected) {
                showDeleteTimingDialog(binding.tuesday, binding.startTimeTue, binding.endTimeTue);
            } else {
                isTueSelected = true;
                binding.tuesday.setTextColor(getResources().getColor(R.color.screen_title_color));
                binding.startTimeTue.setVisibility(View.VISIBLE);
                binding.endTimeTue.setVisibility(View.VISIBLE);
            }
        });

        binding.wednesday.setOnClickListener(v -> {
            if (isWedSelected) {
                showDeleteTimingDialog(binding.wednesday, binding.startTimeWed, binding.endTimeWed);
            } else {
                isWedSelected = true;
                binding.wednesday.setTextColor(getResources().getColor(R.color.screen_title_color));
                binding.startTimeWed.setVisibility(View.VISIBLE);
                binding.endTimeWed.setVisibility(View.VISIBLE);
            }
        });

        binding.thursday.setOnClickListener(v -> {
            if (isThurSelected) {
                showDeleteTimingDialog(binding.thursday, binding.startTimeThur, binding.endTimeThur);
            } else {
                isThurSelected = true;
                binding.thursday.setTextColor(getResources().getColor(R.color.screen_title_color));
                binding.startTimeThur.setVisibility(View.VISIBLE);
                binding.endTimeThur.setVisibility(View.VISIBLE);
            }
        });

        binding.friday.setOnClickListener(v -> {
            if (isFriSelected) {
                showDeleteTimingDialog(binding.friday, binding.startTimeFri, binding.endTimeFri);
            } else {
                isFriSelected = true;
                binding.friday.setTextColor(getResources().getColor(R.color.screen_title_color));
                binding.startTimeFri.setVisibility(View.VISIBLE);
                binding.endTimeFri.setVisibility(View.VISIBLE);
            }
        });

        binding.saturday.setOnClickListener(v -> {
            if (isSatSelected) {
                showDeleteTimingDialog(binding.saturday, binding.startTimeSat, binding.endTimeSat);
            } else {
                isSatSelected = true;
                binding.saturday.setTextColor(getResources().getColor(R.color.screen_title_color));
                binding.startTimeSat.setVisibility(View.VISIBLE);
                binding.endTimeSat.setVisibility(View.VISIBLE);
            }
        });

        binding.sunday.setOnClickListener(v -> {
            if (isSunSelected) {
                showDeleteTimingDialog(binding.sunday, binding.startTimeSun, binding.endTimeSun);
            } else {
                isSunSelected = true;
                binding.sunday.setTextColor(getResources().getColor(R.color.screen_title_color));
                binding.startTimeSun.setVisibility(View.VISIBLE);
                binding.endTimeSun.setVisibility(View.VISIBLE);
            }
        });


        binding.startTimeMon.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.startTimeMon, hour, min)));
        binding.endTimeMon.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.endTimeMon, hour, min)));
        binding.startTimeTue.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.startTimeTue, hour, min)));
        binding.endTimeTue.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.endTimeTue, hour, min)));
        binding.startTimeWed.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.startTimeWed, hour, min)));
        binding.endTimeWed.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.endTimeWed, hour, min)));
        binding.startTimeThur.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.startTimeThur, hour, min)));
        binding.endTimeThur.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.endTimeThur, hour, min)));
        binding.startTimeFri.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.startTimeFri, hour, min)));
        binding.endTimeFri.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.endTimeFri, hour, min)));
        binding.startTimeSat.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.startTimeSat, hour, min)));
        binding.endTimeSat.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.endTimeSat, hour, min)));
        binding.startTimeSun.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.startTimeSun, hour, min)));
        binding.endTimeSun.setOnClickListener(v -> openTimePicker((hour, min) -> checkTimeValidation(binding.endTimeSun, hour, min)));

        binding.save.setOnClickListener(v -> {
            if (AppUtil.isInternetAvailable(context)) {
                AppDialogs.getInstance().showProgressBar(context, null, true);
                saveTimingNetworkCall();
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });

    }

    private void selectedDayTiming(TextView day, TextView start, TextView end, String startTime, String endTime) {
        day.setTextColor(getResources().getColor(R.color.screen_title_color));
        start.setVisibility(View.VISIBLE);
        end.setVisibility(View.VISIBLE);
        if (startTime != null)
            start.setText(startTime);
        if (endTime != null)
            end.setText(endTime);
    }

    private void checkTimeValidation(TextView textView, int mHour, int mMinute) {
        if (textView == binding.endTimeSun) {
            isValid(textView, binding.startTimeSun, false, mHour, mMinute);
        } else if (textView == binding.startTimeSun) {
            isValid(textView, binding.endTimeSun, true, mHour, mMinute);
        } else if (textView == binding.endTimeSat) {
            isValid(textView, binding.startTimeSat, false, mHour, mMinute);
        } else if (textView == binding.startTimeSat) {
            isValid(textView, binding.endTimeSat, true, mHour, mMinute);
        } else if (textView == binding.endTimeFri) {
            isValid(textView, binding.startTimeFri, false, mHour, mMinute);
        } else if (textView == binding.startTimeFri) {
            isValid(textView, binding.endTimeFri, true, mHour, mMinute);
        } else if (textView == binding.endTimeThur) {
            isValid(textView, binding.startTimeThur, false, mHour, mMinute);
        } else if (textView == binding.startTimeThur) {
            isValid(textView, binding.endTimeThur, true, mHour, mMinute);
        } else if (textView == binding.endTimeWed) {
            isValid(textView, binding.startTimeWed, false, mHour, mMinute);
        } else if (textView == binding.startTimeWed) {
            isValid(textView, binding.endTimeWed, true, mHour, mMinute);
        } else if (textView == binding.endTimeTue) {
            isValid(textView, binding.startTimeTue, false, mHour, mMinute);
        } else if (textView == binding.startTimeTue) {
            isValid(textView, binding.endTimeTue, true, mHour, mMinute);
        } else if (textView == binding.endTimeMon) {
            isValid(textView, binding.startTimeMon, false, mHour, mMinute);
        } else if (textView == binding.startTimeMon) {
            isValid(textView, binding.endTimeMon, true, mHour, mMinute);
        }
    }

    private void isValid(TextView textView, TextView checkedTextView, boolean status, int mHour, int mMinute) {
        if (checkedTextView.getText().equals(START_TIME)) {
            textView.setText(getTime(mHour, mMinute));
        } else {
            if (isTimeValid(status, checkedTextView.getText().toString(), mHour, mMinute)) {
                textView.setText(getTime(mHour, mMinute));
                binding.save.setVisibility(View.VISIBLE);
            } else {
                textView.setText(END_TIME);
            }
        }
    }

    private String getTime(int mHour, int mMinute) {
        return String.format("%02d:%02d", mHour, mMinute);
    }

    private boolean isTimeValid(boolean isEndTime, String othertime, int mHour, int mMinute) {
        int h = Integer.parseInt(othertime.split(":")[0]);
        int m = Integer.parseInt(othertime.split(":")[1]);

        if (isEndTime) {
            if (h == mHour && m <= mMinute) {
                AppUtil.showToast(context, getString(R.string.please_select_timing));
                return false;
            } else if (h < mHour) {
                AppUtil.showToast(context, getString(R.string.please_select_timing));
                return false;
            }
        } else {
            if (mHour == h && mMinute <= m) {
                AppUtil.showToast(context, getString(R.string.please_select_timing));
                return false;
            } else if (mHour < h) {
                AppUtil.showToast(context, getString(R.string.please_select_timing));
                return false;
            }
        }
        return true;
    }

    private void showDeleteTimingDialog(TextView day, TextView start, TextView end) {
        if (day.getTag() != null && !day.getTag().toString().trim().isEmpty()) {
            AppDialogs.getInstance().showConfirmCustomDialog(context, getString(R.string.alert), getResources().getString(R.string.timing_delete_alert, day.getText().toString()), getString(R.string.CANCEL), getString(R.string.OK), new AppDialogs.DialogCallback() {
                @Override
                public void response(boolean status) {
                    if (status) {
                        if (AppUtil.isInternetAvailable(context)) {
                            deleteTimingNetworkCall(day.getTag().toString(), day, start, end);
                        } else {
                            AppUtil.showToast(context, getString(R.string.internet_error));
                        }
                    }
                }
            });
        }
    }

    private void openTimePicker(AppListner.onTimePickerClick onTimePickerClick) {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> onTimePickerClick.onTimeSelected(hourOfDay, minute), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void getTimingNetworkCall(boolean isShow) {
        if (isShow)
            AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getUserTimimgs(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    showProfileData((TimingModel) data);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }


    private void saveTimingNetworkCall() {
        Call<ResponseBody> call = null;
        HashMap<String, Object> parameters = new HashMap<>();
        if (isMonSelected && !binding.startTimeMon.getText().equals(START_TIME) && !binding.endTimeMon.getText().equals(END_TIME)) {
            parameters.put("day_number", 1);
            parameters.put("time_from", binding.startTimeMon.getText().toString());
            parameters.put("time_to", binding.endTimeMon.getText().toString());
            isMonSelected = false;
            call = ApiClient.getClient().create(ApiEndPoint.class).saveUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters);
            if (binding.monday.getTag() != null)
                call = ApiClient.getClient().create(ApiEndPoint.class).updateUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), binding.monday.getTag().toString(), parameters);
        } else if (isTueSelected && !binding.startTimeTue.getText().equals(START_TIME) && !binding.endTimeTue.getText().equals(END_TIME)) {
            parameters.put("day_number", 2);
            parameters.put("time_from", binding.startTimeTue.getText().toString());
            parameters.put("time_to", binding.endTimeTue.getText().toString());
            isTueSelected = false;
            call = ApiClient.getClient().create(ApiEndPoint.class).saveUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters);
            if (binding.tuesday.getTag() != null)
                call = ApiClient.getClient().create(ApiEndPoint.class).updateUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), binding.tuesday.getTag().toString(), parameters);
        } else if (isWedSelected && !binding.startTimeWed.getText().equals(START_TIME) && !binding.endTimeWed.getText().equals(END_TIME)) {
            parameters.put("day_number", 3);
            parameters.put("time_from", binding.startTimeWed.getText().toString());
            parameters.put("time_to", binding.endTimeWed.getText().toString());
            isWedSelected = false;
            call = ApiClient.getClient().create(ApiEndPoint.class).saveUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters);
            if (binding.wednesday.getTag() != null)
                call = ApiClient.getClient().create(ApiEndPoint.class).updateUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), binding.wednesday.getTag().toString(), parameters);
        } else if (isThurSelected && !binding.startTimeThur.getText().equals(START_TIME) && !binding.endTimeThur.getText().equals(END_TIME)) {
            parameters.put("day_number", 4);
            parameters.put("time_from", binding.startTimeThur.getText().toString());
            parameters.put("time_to", binding.endTimeThur.getText().toString());
            isThurSelected = false;
            call = ApiClient.getClient().create(ApiEndPoint.class).saveUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters);
            if (binding.thursday.getTag() != null)
                call = ApiClient.getClient().create(ApiEndPoint.class).updateUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), binding.thursday.getTag().toString(), parameters);
        } else if (isFriSelected && !binding.startTimeFri.getText().equals(START_TIME) && !binding.endTimeFri.getText().equals(END_TIME)) {
            parameters.put("day_number", 5);
            parameters.put("time_from", binding.startTimeFri.getText().toString());
            parameters.put("time_to", binding.endTimeFri.getText().toString());
            isFriSelected = false;
            call = ApiClient.getClient().create(ApiEndPoint.class).saveUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters);
            if (binding.friday.getTag() != null)
                call = ApiClient.getClient().create(ApiEndPoint.class).updateUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), binding.friday.getTag().toString(), parameters);
        } else if (isSatSelected && !binding.startTimeSat.getText().equals(START_TIME) && !binding.endTimeSat.getText().equals(END_TIME)) {
            parameters.put("day_number", 6);
            parameters.put("time_from", binding.startTimeSat.getText().toString());
            parameters.put("time_to", binding.endTimeSat.getText().toString());
            isSatSelected = false;
            call = ApiClient.getClient().create(ApiEndPoint.class).saveUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters);
            if (binding.saturday.getTag() != null)
                call = ApiClient.getClient().create(ApiEndPoint.class).updateUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), binding.saturday.getTag().toString(), parameters);
        } else if (isSunSelected && !binding.startTimeSun.getText().equals(START_TIME) && !binding.endTimeSun.getText().equals(END_TIME)) {
            parameters.put("day_number", 7);
            parameters.put("time_from", binding.startTimeSun.getText().toString());
            parameters.put("time_to", binding.endTimeSun.getText().toString());
            isSunSelected = false;
            call = ApiClient.getClient().create(ApiEndPoint.class).saveUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters);
            if (binding.sunday.getTag() != null)
                call = ApiClient.getClient().create(ApiEndPoint.class).updateUserTimimg(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), binding.sunday.getTag().toString(), parameters);
        }
        ApiHandler.requestService(context, call, new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    if (isTueSelected) {
                        saveTimingNetworkCall();
                    } else if (isWedSelected) {
                        saveTimingNetworkCall();
                    } else if (isThurSelected) {
                        saveTimingNetworkCall();
                    } else if (isFriSelected) {
                        saveTimingNetworkCall();
                    } else if (isSatSelected) {
                        saveTimingNetworkCall();
                    } else if (isSunSelected) {
                        saveTimingNetworkCall();
                    } else {
                        AppDialogs.getInstance().showProgressBar(context, null, false);
                        AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.timing_success_alert), getString(R.string.OK), status -> onBackPressed());
                    }
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.wrong_with_backend), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }


    public void deleteTimingNetworkCall(String id, TextView day, TextView start, TextView end) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).deleteUserTimimg(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), id), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    day.setTextColor(getResources().getColor(R.color.timing_screen_deselect));
                    start.setVisibility(View.INVISIBLE);
                    end.setVisibility(View.INVISIBLE);
                    if (AppUtil.isInternetAvailable(context)) {
                        getTimingNetworkCall(false);
                    } else {
                        AppDialogs.getInstance().showProgressBar(context, null, false);
                        AppUtil.showToast(context, getString(R.string.internet_error));
                    }
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.wrong_with_backend), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

}
