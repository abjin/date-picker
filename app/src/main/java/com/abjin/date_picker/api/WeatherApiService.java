package com.abjin.date_picker.api;

import com.abjin.date_picker.api.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    // Example: /v1/forecast?latitude=37.57&longitude=126.98&current_weather=true
    @GET("v1/forecast")
    Call<WeatherResponse> getCurrentWeather(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current_weather") boolean currentWeather
    );
}

