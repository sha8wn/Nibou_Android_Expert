package com.nibou.nibouexpert.activitys;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.databinding.ActivityChatBinding;
import com.nibou.nibouexpert.fragments.ChatFragment;
import com.nibou.nibouexpert.models.ActiveChatSessionModel;
import com.nibou.nibouexpert.models.ProfileModel;
import com.nibou.nibouexpert.utils.*;

import java.util.concurrent.TimeUnit;

import static android.media.MediaExtractor.MetricsConstants.FORMAT;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private Context context;
    private TextView txt_timer;
    public ProfileModel profileModel;
    private long intialDuration = 0;

    private ImageView timeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        context = this;
        binding.toolbar.findViewById(R.id.title).setVisibility(View.VISIBLE);
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.dialog_button_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        if (getIntent().hasExtra(AppConstant.CUSTOMER_DETAILS_MODEL)) {
            ActiveChatSessionModel.Data data = (ActiveChatSessionModel.Data) getIntent().getSerializableExtra(AppConstant.CUSTOMER_DETAILS_MODEL);
            intialDuration = data.getAttributes().getDuration();
            for (int i = 0; i < data.getAttributes().getUsers().size(); i++) {
                if (!(data.getAttributes().getUsers().get(i).getData().getId().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId()))) {
                    profileModel = data.getAttributes().getUsers().get(i);
                    break;
                }
            }

            ((TextView) binding.toolbar.findViewById(R.id.title)).setText(profileModel.getData().getAttributes().getUsername());
        }

        AppUtil.setFragment(R.id.frameLayout_container, getSupportFragmentManager(), new ChatFragment(), "chatFragment", false, true);

        //initTimer();
    }

    public void initTimer() {
        binding.toolbar.findViewById(R.id.timer_view).setVisibility(View.VISIBLE);
        txt_timer = binding.toolbar.findViewById(R.id.timer);
        timeImage = binding.toolbar.findViewById(R.id.timer_image);
        TimerUtil.getTimerUtil().startTimer(intialDuration, new TimerUtil.TimerCallback() {

            @Override
            public void onTimerCompleted() {

            }

            @Override
            public void onTimerTick(String time, long minute, long second) {
                if (txt_timer != null) {
                    txt_timer.setText(time);
                    if (timeImage.getTag().equals("0")) {
                        timeImage.setTag("1");
                        timeImage.setImageResource(R.drawable.radio_button);
                    } else {
                        timeImage.setTag("0");
                        timeImage.setImageResource(R.drawable.un_radio_button);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TimerUtil.getTimerUtil().cancelTimer();
    }
}
