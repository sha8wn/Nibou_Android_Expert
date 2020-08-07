package com.nibou.nibouexpert.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.esafirm.imagepicker.model.Image;
import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.ActivityProfileBinding;
import com.nibou.nibouexpert.models.AccessTokenModel;
import com.nibou.nibouexpert.models.ProfileModel;
import com.nibou.nibouexpert.utils.*;
import com.ybs.countrypicker.CountryPicker;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;
    private Context context;
    private MediaUtil mediaUtil;

    private File selectedPdfFile;
    private File selectedProfileFile;
    private ProfileModel profileModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        context = this;
        setToolbar();
        mediaUtil = new MediaUtil(context);

        binding.userImageView.setOnClickListener(v -> mediaUtil.openSingleImageGallery((isCamera, imageResult) -> {
            AppUtil.hideKeyBoard(context);
            ArrayList<Image> imagesList = (ArrayList<Image>) imageResult;
            if (imagesList != null && imagesList.size() > 0) {
                mediaUtil.compressMultipleImages(imagesList, 80, new MediaUtil.CompressImageCallback() {
                    @Override
                    public void response(Bitmap bitmap, String fileName, String path) {
                        if (path != null) {
                            runOnUiThread(() -> {
                                selectedProfileFile = new File(path);
                                showImage(selectedProfileFile.getAbsolutePath());
                            });
                        }
                    }
                });
            }
        }));

        binding.viewProfile.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (binding.viewProfile.getText().equals(getString(R.string.view_profile))) {
                Intent intent = new Intent(context, PDFViewActivity.class);
                intent.putExtra(AppConstant.URL, AppConstant.BASE_URL + profileModel.getData().getAttributes().getPdf().getUrl());
                startActivity(intent);
            } else {
                mediaUtil.openDocumentGallery((status, fileName, path) -> {
                    if (status) {
                        selectedPdfFile = new File(path);
                        binding.viewProfile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_sign, 0);
                    } else {
                        AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.pdf_attached_alert), getString(R.string.OK), null);
                    }
                });
            }
        });

        binding.country.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
            picker.setListener((name, code, dialCode, flagDrawableResID) -> {
                ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                binding.country.setText(name);
                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        });

        binding.save.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    updateProfileNetworkCall();
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });


        binding.delete.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            AppDialogs.getInstance().showConfirmCustomDialog(context, getString(R.string.delete).toUpperCase(), getString(R.string.delete_message_alert), getString(R.string.CANCEL), getString(R.string.OK), new AppDialogs.DialogCallback() {
                @Override
                public void response(boolean status) {
                    if (status) {
                        if (AppUtil.isInternetAvailable(context)) {
                            deletePdfProfileNetworkCall();
                        } else {
                            AppUtil.showToast(context, getString(R.string.internet_error));
                        }
                    }
                }
            });
        });

        if (AppUtil.isInternetAvailable(context)) {
            profileNetworkCall();
        } else {
            showProfileData();
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }


    private void setToolbar() {
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mediaUtil.onRequestPermissionsResult(requestCode);
    }

    private void showProfileData() {
        profileModel = LocalPrefences.getInstance().getLocalProfileModel(context);
        if (profileModel != null) {
            binding.name.setText(profileModel.getData().getAttributes().getName());
            binding.name.setSelection(binding.name.getText().length());
            binding.mail.setText(profileModel.getData().getAttributes().getEmail());
            binding.country.setText(profileModel.getData().getAttributes().getCountry());
            binding.city.setText(profileModel.getData().getAttributes().getCity());
            binding.city.setSelection(binding.city.getText().length());
            if (profileModel.getData().getAttributes().getPdf() != null && profileModel.getData().getAttributes().getPdf().getUrl() != null) {
                binding.delete.setVisibility(View.VISIBLE);
                binding.viewProfile.setText(getString(R.string.view_profile));
            } else {
                binding.viewProfile.setText(getString(R.string.upload_profile));
                binding.delete.setVisibility(View.GONE);
            }

            if (profileModel.getData().getAttributes().getAvatar() != null && profileModel.getData().getAttributes().getAvatar().getUrl() != null) {
                showImage(AppConstant.BASE_URL + profileModel.getData().getAttributes().getAvatar().getUrl());
            }
        } else {
            binding.viewProfile.setText(getString(R.string.upload_profile));
            binding.delete.setVisibility(View.GONE);
        }
    }

    private void showImage(String url) {
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(binding.userImageView);
    }

    private boolean screenValidate() {
        if (TextUtils.isEmpty(binding.name.getText().toString().trim())) {
            AppUtil.showToast(this, getResources().getString(R.string.name_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.country.getText().toString().trim())) {
            AppUtil.showToast(this, getResources().getString(R.string.country_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.city.getText().toString().trim())) {
            AppUtil.showToast(this, getResources().getString(R.string.city_empty_alert));
            return false;
        }
        return true;
    }

    private void updateProfileNetworkCall() {
        HashMap<String, RequestBody> parameters = new HashMap<>();
        parameters.put("name", RequestBody.create(MediaType.parse("text/plain"), binding.name.getText().toString()));
        parameters.put("country", RequestBody.create(MediaType.parse("text/plain"), binding.country.getText().toString()));
        parameters.put("city", RequestBody.create(MediaType.parse("text/plain"), binding.city.getText().toString()));

        MultipartBody.Part profileBody = null;
        if (selectedProfileFile != null)
            profileBody = MultipartBody.Part.createFormData("avatar", System.currentTimeMillis() + ".png", RequestBody.create(MediaType.parse("multipart/form-data"), selectedProfileFile));

        MultipartBody.Part pdfBody = null;
        if (selectedPdfFile != null)
            pdfBody = MultipartBody.Part.createFormData("pdf", System.currentTimeMillis() + ".pdf", RequestBody.create(MediaType.parse("multipart/form-data"), selectedPdfFile));


        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).updateMyProfileWithFile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters, profileBody, pdfBody), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    LocalPrefences.getInstance().saveLocalProfileModel(context, (ProfileModel) data);
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.profile_success_alert), getString(R.string.OK), status -> onBackPressed());
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.wrong_with_backend), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }


    private void deletePdfProfileNetworkCall() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("remove_pdf", "true");
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).updateMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    LocalPrefences.getInstance().saveLocalProfileModel(context, (ProfileModel) data);
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.pdf_profile_delete_alert), getString(R.string.OK), status -> showProfileData());
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.wrong_with_backend), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private void profileNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), "languages,reviews,expertises,user_credit_cards"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    LocalPrefences.getInstance().saveLocalProfileModel(context, (ProfileModel) data);
                    showProfileData();
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

}
