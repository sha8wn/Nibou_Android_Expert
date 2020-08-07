package com.nibou.nibouexpert.activitys;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.databinding.ActivityHomeChatBinding;
import com.nibou.nibouexpert.fragments.ChatListFragment;
import com.nibou.nibouexpert.fragments.DashboardFragment;
import com.nibou.nibouexpert.fragments.SettingFragment;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.Locale;

public class HomeChatActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private long backPressedClickTime;
    private ActivityHomeChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_chat);
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            binding.bottomNavigationView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            binding.bottomNavigationView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this);
        binding.bottomNavigationView.setSelectedItemId(R.id.chat);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.chat:
                if (getSupportFragmentManager().findFragmentByTag("chatlist") == null)
                    AppUtil.setFragment(R.id.frameLayout_container, getSupportFragmentManager(), new ChatListFragment(), "chatlist", false, true);
                break;
            case R.id.dashboard:
                if (getSupportFragmentManager().findFragmentByTag("dashboard") == null)
                    AppUtil.setFragment(R.id.frameLayout_container, getSupportFragmentManager(), new DashboardFragment(), "dashboard", false, true);
                break;
            case R.id.setting:
                if (getSupportFragmentManager().findFragmentByTag("setting") == null)
                    AppUtil.setFragment(R.id.frameLayout_container, getSupportFragmentManager(), new SettingFragment(), "setting", false, true);
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        checkForAppExit();
    }

    private void checkForAppExit() {
        if ((backPressedClickTime + 2000) > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, getResources().getString(R.string.press_back_message), Toast.LENGTH_SHORT).show();
        }
        backPressedClickTime = System.currentTimeMillis();
    }

}

