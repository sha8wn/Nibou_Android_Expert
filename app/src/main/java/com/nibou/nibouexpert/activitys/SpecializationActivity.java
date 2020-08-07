package com.nibou.nibouexpert.activitys;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.ActivitySpecializationBinding;
import com.nibou.nibouexpert.models.ProfileModel;
import com.nibou.nibouexpert.models.SurveyModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.ArrayList;
import java.util.HashMap;

public class SpecializationActivity extends BaseActivity {
    private ActivitySpecializationBinding binding;
    private ArrayList<Box> boxArrayList;

    private Context context;
    private SurveyModel surveyModel;


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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_specialization);
        context = this;
        setToolbar();
        init();

        if (AppUtil.isInternetAvailable(context)) {
            getExpertiseNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }

        if (AppUtil.isInternetAvailable(context)) {
            profileNetworkCall();
        } else {
            showProfileData();
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showProfileData() {
        binding.mainView.setVisibility(View.VISIBLE);
        ProfileModel profileModel = LocalPrefences.getInstance().getLocalProfileModel(context);
        if (profileModel != null) {
            boxArrayList.clear();
            binding.skill1.setText("");
            if (profileModel.getIncluded() != null) {
                for (ProfileModel.Data obj : profileModel.getIncluded()) {
                    if (obj.getType().equals("expertises")) {
                        if (binding.skill1.getText().toString().trim().isEmpty()) {
                            binding.skill1.setText(obj.getAttributes().getTitle());
                            binding.skill1.setTag(obj.getId());
                        } else {
                            addNewBox(obj.getId(), obj.getAttributes().getTitle());
                        }
                    }
                }
            } else {
                binding.skill1.setVisibility(View.GONE);
            }
        }
    }

    private void init() {

        boxArrayList = new ArrayList<>();

        binding.save.setVisibility(View.INVISIBLE);
        binding.delete.setVisibility(View.INVISIBLE);
        binding.add.setOnClickListener(v -> {
            if (boxArrayList.size() > 0 && boxArrayList.get(boxArrayList.size() - 1).getTextView().getText().toString().isEmpty()) {
                AppUtil.showToast(context, getString(R.string.please_fill_previous));
            } else {
                addNewBox(null, null);
            }
        });

        binding.delete.setOnClickListener(v -> {
//            if (isServerDelete()) {
//                ArrayList<Integer> list = getExperties(true);
//                if (AppUtil.isInternetAvailable(context)) {
//                    saveExpertiesNetworkCall(true, list);
//                } else {
//                    AppUtil.showToast(context, getString(R.string.internet_error));
//                }
//            } else {
//                deleteSelectedBox();
//            }
        });

        binding.save.setOnClickListener(v -> {
            ArrayList<Integer> list = getExperties(false);
            if (list != null && list.size() > 0) {
                if (AppUtil.isInternetAvailable(context)) {
                    if (screenValidate())
                        saveExpertiesNetworkCall(false, list);
                } else {
                    AppUtil.showToast(context, getString(R.string.internet_error));
                }
            }
        });
    }

    private boolean screenValidate() {
        for (int i = 0; i < boxArrayList.size(); i++) {
            if (boxArrayList.get(i).getTextView().getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isServerDelete() {
        for (int i = 0; i < boxArrayList.size(); i++) {
            if (boxArrayList.get(i).isSelected && boxArrayList.get(i).getExpertiesId() != 0) {
                return true;
            }
        }
        return false;
    }

    private void deleteBox(View imageView) {
        Box box = (Box) imageView.getTag();
        box.setSelected(true);
        imageView.setTag(box);
        if (isServerDelete()) {
            ArrayList<Integer> list = getExperties(true);
            if (AppUtil.isInternetAvailable(context)) {
                saveExpertiesNetworkCall(true, list);
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        } else {
            deleteSelectedBox();
        }
    }


    private void addNewBox(String id, String text) {
//        TextView tv = new TextView(this);
//        tv.setHint(getString(R.string.please_select));
//        tv.setBackgroundResource(R.drawable.transparent_background);
//        tv.setPadding((int) getResources().getDimension(R.dimen.dim_15), (int) getResources().getDimension(R.dimen.dim_15), (int) getResources().getDimension(R.dimen.dim_15), (int) getResources().getDimension(R.dimen.dim_15));
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        ll.setMargins(0, (int) getResources().getDimension(R.dimen.dim_20), 0, 0);
//        tv.setTextSize(14.0F);
//        tv.setTextColor(getResources().getColor(R.color.screen_title_color));
//        tv.setTypeface(Typeface.create("ubuntu_m", Typeface.NORMAL));
//        tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
//        tv.setLayoutParams(ll);


        View box_view = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.box_view, null);
        TextView tv = box_view.findViewById(R.id.box);
        ImageView cross = box_view.findViewById(R.id.cross);
        binding.addExpertiseLl.removeAllViews();
        for (int i = 0; i < boxArrayList.size(); i++) {
            binding.addExpertiseLl.addView(boxArrayList.get(i).getRelativeLayout());
            boxArrayList.get(i).getTextView().setTag(boxArrayList.get(i));
            boxArrayList.get(i).getTextView().setOnClickListener(v -> openList((TextView) v));
            boxArrayList.get(i).getImageView().setOnClickListener(v -> deleteBox(v));
            boxArrayList.get(i).getImageView().setTag(boxArrayList.get(i));
        }
        Box box = new Box(tv, cross, box_view, false, boxArrayList.size());

        if (text != null)
            tv.setText(text);
        if (id != null)
            box.setExpertiesId(Integer.parseInt(id));
        boxArrayList.add(box);

        tv.setOnClickListener(v -> openList(tv));
        cross.setOnClickListener(v -> deleteBox(v));
        tv.setTag(box);
        cross.setTag(box);
        binding.addExpertiseLl.addView(box_view);

//        tv.setOnLongClickListener(v -> {
//            Box box1 = (Box) v.getTag();
//            if (box1.isSelected) {
//                binding.save.setVisibility(View.VISIBLE);
//                binding.delete.setVisibility(View.INVISIBLE);
//                box1.getRelativeLayout().setBackground(ContextCompat.getDrawable(context, R.drawable.transparent_background));
//                box1.setSelected(false);
//            } else {
//                if (!isAnySelectedBox()) {
//                    box1.setSelected(true);
//                    binding.save.setVisibility(View.INVISIBLE);
//                    binding.delete.setVisibility(View.VISIBLE);
//                    box1.getRelativeLayout().setBackground(ContextCompat.getDrawable(context, R.drawable.grey_round_background));
//                }
//            }
//            v.setTag(box1);
//            return true;
//        });
    }

    private void deleteSelectedBox() {
        for (int i = 0; i < boxArrayList.size(); i++) {
            if (boxArrayList.get(i).isSelected) {
                boxArrayList.remove(i);
                break;
            }
        }
        binding.addExpertiseLl.removeAllViews();
        for (int i = 0; i < boxArrayList.size(); i++) {
            binding.addExpertiseLl.addView(boxArrayList.get(i).getRelativeLayout());
            boxArrayList.get(i).getTextView().setTag(boxArrayList.get(i));
            boxArrayList.get(i).getTextView().setOnClickListener(v -> openList((TextView) v));
            boxArrayList.get(i).getImageView().setOnClickListener(v -> deleteBox(v));
            boxArrayList.get(i).getImageView().setTag(boxArrayList.get(i));
        }
        if (boxArrayList.size() > 0) {
            binding.save.setVisibility(View.VISIBLE);
            binding.delete.setVisibility(View.INVISIBLE);
        } else {
            binding.save.setVisibility(View.INVISIBLE);
            binding.delete.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isAnySelectedBox() {
        for (int i = 0; i < boxArrayList.size(); i++) {
            if (boxArrayList.get(i).isSelected) {
                return true;
            }
        }
        return false;
    }

    class Box {
        private TextView textView;
        private View relativeLayout;
        private ImageView imageView;
        private boolean isSelected;
        private int position;

        private int expertiesId;

        public Box(TextView textView, ImageView imageView, View relativeLayout, boolean isSelected, int position) {
            this.textView = textView;
            this.imageView = imageView;
            this.relativeLayout = relativeLayout;
            this.isSelected = isSelected;
            this.position = position;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getExpertiesId() {
            return expertiesId;
        }

        public void setExpertiesId(int expertiesId) {
            this.expertiesId = expertiesId;
        }

        public View getRelativeLayout() {
            return relativeLayout;
        }

        public void setRelativeLayout(View relativeLayout) {
            this.relativeLayout = relativeLayout;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }


    private void openList(TextView tv) {
        ArrayList<String> language = getLanguageDialogList();
        if (language.size() > 0) {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.listview);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            dialog.show();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, language);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                showSelectedData(tv, listView.getItemAtPosition(position).toString());
                dialog.cancel();
            });
        }
    }

    private void showSelectedData(TextView textView, String item) {
        if (binding.delete.getVisibility() != View.VISIBLE)
            binding.save.setVisibility(View.VISIBLE);
        for (SurveyModel.Data data : surveyModel.getData()) {
            if (data.getAttributes().getTitle().equals(item)) {
                textView.setText(item);
                Box box = (Box) textView.getTag();
                box.setExpertiesId(Integer.parseInt(data.getId()));
                textView.setTag(box);
            }
        }
    }

    private ArrayList<String> getLanguageDialogList() {
        ArrayList<String> language = new ArrayList<String>();
        if (surveyModel != null && surveyModel.getData() != null && surveyModel.getData().size() > 0) {
            for (SurveyModel.Data data : surveyModel.getData()) {
                if (!isItemAlreadySelected(data.getAttributes().getTitle()))
                    language.add(data.getAttributes().getTitle());
            }
        }
        return language;
    }

    private boolean isItemAlreadySelected(String item) {
        if (item.equals(binding.skill1.getText()))
            return true;
        for (int i = 0; i < boxArrayList.size(); i++) {
            if (boxArrayList.get(i).getTextView().getText().equals(item))
                return true;
        }

        return false;

    }

    private ArrayList<Integer> getExperties(boolean withoutSelectedBoxId) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < boxArrayList.size(); i++) {
            if (withoutSelectedBoxId) {
                if (!boxArrayList.get(i).isSelected) {
                    if (boxArrayList.get(i).getExpertiesId() != 0)
                        list.add(boxArrayList.get(i).getExpertiesId());
                }
            } else {
                if (boxArrayList.get(i).getExpertiesId() != 0)
                    list.add(boxArrayList.get(i).getExpertiesId());
            }
        }
        if (binding.skill1.getTag() != null)
            list.add(Integer.parseInt(binding.skill1.getTag().toString()));
        return list;
    }


    private void saveExpertiesNetworkCall(boolean isDelete, ArrayList<Integer> list) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("expertise_ids", list);

        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).updateMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    if (isDelete) {
                        AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.experties_delete_alert), getString(R.string.OK), status -> deleteSelectedBox());
                    } else {
                        AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.experties_success_alert), getString(R.string.OK), status -> onBackPressed());
                    }
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

    private void getExpertiseNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getExpertise(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    surveyModel = (SurveyModel) data;
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private void profileNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), "languages,expertises"), new ApiHandler.CallBack() {
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
