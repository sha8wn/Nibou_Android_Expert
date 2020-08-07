package com.nibou.nibouexpert.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.databinding.ActivityLanguageBinding;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.Locale;

public class LanguageActivity extends BaseActivity {
    private ActivityLanguageBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        context = this;
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> onBackPressed());
        binding.toolbar.findViewById(R.id.back_arrow).setVisibility(View.INVISIBLE);
        binding.english.setOnClickListener(v -> moveToNextScreen(AppConstant.ENGLISH));
        binding.arabic.setOnClickListener(v -> moveToNextScreen(AppConstant.ARABIC));
        binding.turkish.setOnClickListener(v -> moveToNextScreen(AppConstant.TURKISH));
    }

    private void moveToNextScreen(String lang) {
        LocalPrefences.getInstance().putString(context, AppConstant.APP_LANGUAGE, lang);
        Configuration conf = getResources().getConfiguration();
        conf.setLocale(new Locale(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE)));
        getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
        if (LocalPrefences.getInstance().isUserLogin(context)) {
            Intent intent = new Intent(context, HomeChatActivity.class);
            intent.putExtra("login", true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
