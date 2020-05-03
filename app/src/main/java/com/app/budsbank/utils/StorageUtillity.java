package com.app.budsbank.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.budsbank.models.UserModel;

public class StorageUtillity {
    public static void saveDataInPreferences(Context c, String key, String value) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveDataInPreferences(Context c, String key, int value) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public static void saveDataInPreferences(Context c, String key, boolean value) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveDataInPreferences(Context c, String key, long  value) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static boolean getDataFromPreferences(Context c, String key, boolean defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getBoolean(key, defaultValue);
    }
    public static long  getDataFromPreferences(Context c, String key, long defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getLong(key, defaultValue);
    }


    public static String getDataFromPreferences(Context c, String key, String defaultValue) {
        if (c == null)
            return defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getString(key, defaultValue);
    }


    public static int getDataFromPreferences(Context c, String key, int defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getInt(key, defaultValue);
    }

    public static void clearAllPreferences(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    public static void saveUserModel(Context context, UserModel user) {
        if (user == null)
            return;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(AppConstants.SharedPreferencesKeys.USER_ID.getValue(), user.getUserId());
        editor.putString(AppConstants.SharedPreferencesKeys.PHONE_NUMBER.getValue(), user.getPhoneNumber());
        editor.putString(AppConstants.SharedPreferencesKeys.EMAIL.getValue(), user.getEmail());
        editor.putString(AppConstants.SharedPreferencesKeys.EMAIL_VERIFIED_AT.getValue(), user.getEmailVerifiedAt());
        editor.putString(AppConstants.SharedPreferencesKeys.USERNAME.getValue(), user.getUsername());
        editor.putString(AppConstants.SharedPreferencesKeys.FULL_NAME.getValue(), user.getFullName());
        editor.putString(AppConstants.SharedPreferencesKeys.PROFILE_IMAGE.getValue(), user.getProfileUrl());
        String coinStr = user.getCoinsEarned();
        if(Integer.parseInt(coinStr) <= 0)
            coinStr="0";
        editor.putString(AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), coinStr);
        editor.apply();
    }

    public static UserModel getUserModel(Context context) {
        UserModel model = new UserModel();
        model.setUserId(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.USER_ID.getValue(), ""));
        model.setPhoneNumber(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.PHONE_NUMBER.getValue(), ""));
        model.setEmail(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.EMAIL.getValue(), ""));
        model.setEmailVerifiedAt(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.EMAIL_VERIFIED_AT.getValue(), ""));
        model.setUsername(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.USERNAME.getValue(), ""));
        model.setFullName(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.FULL_NAME.getValue(), ""));
        model.setProfileUrl(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.PROFILE_IMAGE.getValue(), ""));
        model.setCoinsEarned(getDataFromPreferences(context,AppConstants.SharedPreferencesKeys.COINS_EARNED.getValue(), ""));
        return model;
    }
}
