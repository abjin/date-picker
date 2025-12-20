package com.abjin.date_picker.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.abjin.date_picker.api.ApiClient;
import com.abjin.date_picker.api.UserApiService;
import com.abjin.date_picker.api.models.UserPreferenceRequest;
import com.abjin.date_picker.api.models.UserPreferenceResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPreferenceManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_REGION = "region";
    private static final String KEY_INTERESTS = "interests";
    private static final String KEY_BUDGET = "budget";
    private static final String KEY_ADDITIONAL_REQUEST = "additional_request";
    private static final String KEY_WEATHER = "weather";

    private static UserPreferenceManager instance;
    private SharedPreferences sharedPreferences;
    private Context context;

    private UserPreferenceManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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

    public void setAdditionalRequest(String text) {
        sharedPreferences.edit().putString(KEY_ADDITIONAL_REQUEST, text).apply();
    }

    public void setWeather(String weather) {
        sharedPreferences.edit().putString(KEY_WEATHER, weather).apply();
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

    public String getAdditionalRequest() {
        return sharedPreferences.getString(KEY_ADDITIONAL_REQUEST, "");
    }

    public String getWeather() {
        return sharedPreferences.getString(KEY_WEATHER, "");
    }

    public interface OnUpdateListener {
        void onSuccess(UserPreferenceResponse response);
        void onError(String errorMessage);
    }

    public void updateUserToServer(OnUpdateListener listener) {
        String region = getRegion();
        Set<String> interestsSet = getInterests();
        List<String> interests = new ArrayList<>(interestsSet);
        double budget = getBudget();
        String additional = getAdditionalRequest();

        UserPreferenceRequest request = new UserPreferenceRequest(region, interests, budget, additional);

        UserApiService apiService = ApiClient.getClient(context).create(UserApiService.class);
        apiService.updateUserPreference(request).enqueue(new Callback<UserPreferenceResponse>() {
            @Override
            public void onResponse(Call<UserPreferenceResponse> call, Response<UserPreferenceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onSuccess(response.body());
                    }
                } else {
                    if (listener != null) {
                        listener.onError("Failed to update user preference: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserPreferenceResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onError("Network error: " + t.getMessage());
                }
            }
        });
    }

    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
