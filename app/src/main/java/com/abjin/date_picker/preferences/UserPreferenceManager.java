package com.abjin.date_picker.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class UserPreferenceManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_REGION = "region";
    private static final String KEY_INTERESTS = "interests";
    private static final String KEY_BUDGET = "budget";

    private static UserPreferenceManager instance;
    private SharedPreferences sharedPreferences;

    private UserPreferenceManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferenceManager(context);
        }
        return instance;
    }

    public void setRegion(String region) {
        sharedPreferences.edit().putString(KEY_REGION, region).apply();
    }

    public void setInterests(Set<String> interests) {
        sharedPreferences.edit().putStringSet(KEY_INTERESTS, interests).apply();
    }

    public void setBudget(float budget) {
        sharedPreferences.edit().putFloat(KEY_BUDGET, budget).apply();
    }

    public String getRegion() {
        return sharedPreferences.getString(KEY_REGION, null);
    }

    public Set<String> getInterests() {
        return sharedPreferences.getStringSet(KEY_INTERESTS, new HashSet<>());
    }

    public float getBudget() {
        return sharedPreferences.getFloat(KEY_BUDGET, 0f);
    }

    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
