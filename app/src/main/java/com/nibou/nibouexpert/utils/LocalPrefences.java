package com.nibou.nibouexpert.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.nibou.nibouexpert.models.AccessTokenModel;
import com.nibou.nibouexpert.models.ProfileModel;

public class LocalPrefences {

    interface PREF_VAR {
        String SHER_PREF = "nibou_db";
    }

    private String USER_DETAILS = "user_details";
    private String TOKEN_DETAILS = "token_details";

    private LocalPrefences() {
    }

    private static LocalPrefences mInstance = null;

    public static LocalPrefences getInstance() {
        if (mInstance == null) {
            mInstance = new LocalPrefences();
        }
        return mInstance;
    }

    public ProfileModel getLocalProfileModel(Context context) {
        return new Gson().fromJson(getString(context, USER_DETAILS), ProfileModel.class);
    }

    public void saveLocalProfileModel(Context context, ProfileModel profileModel) {
        putString(context, USER_DETAILS, new Gson().toJson(profileModel));
    }

    public AccessTokenModel getLocalAccessTokenModel(Context context) {
        return new Gson().fromJson(getString(context, TOKEN_DETAILS), AccessTokenModel.class);
    }

    public void saveLocalAccessTokenModel(Context context, AccessTokenModel accessTokenModel) {
        putString(context, TOKEN_DETAILS, new Gson().toJson(accessTokenModel));
    }

    public boolean isUserLogin(Context context) {
        if (getLocalProfileModel(context) != null && getLocalProfileModel(context).getData() != null && getLocalProfileModel(context).getData().getId() != null && !getLocalProfileModel(context).getData().getId().isEmpty()) {
            return true;
        }
        return false;
    }

    public void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_VAR.SHER_PREF, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(Context context, String key) {
        SharedPreferences spref = context.getSharedPreferences(PREF_VAR.SHER_PREF, Context.MODE_PRIVATE);
        return spref.getString(key, null);
    }

    public void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_VAR.SHER_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(Context context, String key) {
        SharedPreferences spref = context.getSharedPreferences(PREF_VAR.SHER_PREF, Context.MODE_PRIVATE);
        return spref.getInt(key, 0);
    }

    public void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_VAR.SHER_PREF, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(Context context, String key) {
        SharedPreferences spref = context.getSharedPreferences(PREF_VAR.SHER_PREF, Context.MODE_PRIVATE);
        return spref.getBoolean(key, false);
    }

    public void clearSharePreference(Context context) {
        context.getSharedPreferences(PREF_VAR.SHER_PREF, 0).edit().clear().apply();
    }
}
