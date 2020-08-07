package com.nibou.nibouexpert.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.adapter.ReviewListAdapter;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.ActivityRatingBinding;
import com.nibou.nibouexpert.models.ProfileModel;
import com.nibou.nibouexpert.models.ReviewModel;
import com.nibou.nibouexpert.models.SurveyModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;

public class RatingActivity extends BaseActivity {

    private ActivityRatingBinding binding;
    private ReviewListAdapter reviewListAdapter;
    private Context context;
    private ReviewModel reviewModel;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rating);
        context = this;
        setToolbar();

        if (AppUtil.isInternetAvailable(context)) {
            if (LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken() != null)
                reviewNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showProfileData() {
        binding.reviewList.setLayoutManager(new LinearLayoutManager(context));
        reviewListAdapter = new ReviewListAdapter(context, reviewModel);
        binding.reviewList.setAdapter(reviewListAdapter);
    }

    private void reviewNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getReviewRequest(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    reviewModel = (ReviewModel) data;
                    if (reviewModel != null) {
                        reviewModel.reverseReviewList();
                        showProfileData();
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
