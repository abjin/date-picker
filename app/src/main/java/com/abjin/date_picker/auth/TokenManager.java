package com.abjin.date_picker.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHOTO_URL = "user_photo_url";
    private static final String KEY_USER_ID = "user_id";

    private static TokenManager instance;
    private SharedPreferences sharedPreferences;

    private TokenManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    public void saveToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void saveUserInfo(String id, String email, String name, String photoUrl) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PHOTO_URL, photoUrl);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserPhotoUrl() {
        return sharedPreferences.getString(KEY_USER_PHOTO_URL, null);
    }

    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
