package com.abjin.date_picker.weather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.abjin.date_picker.api.WeatherApiClient;
import com.abjin.date_picker.api.WeatherApiService;
import com.abjin.date_picker.api.models.WeatherResponse;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherManager {
    private static final String TAG = "WeatherManager";
    private final Context context;
    private final WeatherApiService api;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public WeatherManager(Context context) {
        this.context = context.getApplicationContext();
        this.api = WeatherApiClient.getClient().create(WeatherApiService.class);
    }

    public interface SubtitleCallback {
        void onResult(String subtitle);
    }

    public static class WeatherData {
        public final float temperature;
        public final int code;
        public final String description;
        public final String subtitle;

        public WeatherData(float temperature, int code, String description, String subtitle) {
            this.temperature = temperature;
            this.code = code;
            this.description = description;
            this.subtitle = subtitle;
        }
    }

    public interface WeatherDataCallback {
        void onResult(WeatherData data);
    }

    public void fetchWeatherForRegion(String region, WeatherDataCallback callback) {
        if (region == null || region.trim().isEmpty()) {
            post(() -> callback.onResult(null));
            return;
        }
        new Thread(() -> {
            double[] latlon = geocodeRegion(region);
            if (latlon == null) {
                post(() -> callback.onResult(null));
                return;
            }
            api.getCurrentWeather(latlon[0], latlon[1], true).enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCurrent_weather() != null) {
                        float temp = response.body().getCurrent_weather().getTemperature();
                        int code = response.body().getCurrent_weather().getWeathercode();
                        String desc = mapWeatherCodeToKo(code);
                        String emoji = emojiForWeatherCode(code);
                        String subtitle = String.format(Locale.getDefault(), "%s %.0f°C · %s", emoji, temp, desc);
                        post(() -> callback.onResult(new WeatherData(temp, code, desc, subtitle)));
                    } else {
                        post(() -> callback.onResult(null));
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    post(() -> callback.onResult(null));
                }
            });
        }).start();
    }

    private void post(Runnable r) {
        mainHandler.post(r);
    }
    private double[] geocodeRegion(String region) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.KOREA);
            List<Address> list = geocoder.getFromLocationName("대한민국 " + region, 1);
            if (list != null && !list.isEmpty()) {
                Address a = list.get(0);
                return new double[]{a.getLatitude(), a.getLongitude()};
            }
        } catch (IOException e) {
            Log.w(TAG, "Geocoding failed: " + e.getMessage());
        }
        return null;
    }

    private String mapWeatherCodeToKo(int code) {
        if (code == 0) return "맑음";
        if (code == 1 || code == 2) return "구름 조금";
        if (code == 3) return "흐림";
        if (code == 45 || code == 48) return "안개";
        if (code == 51 || code == 53 || code == 55) return "이슬비";
        if (code == 56 || code == 57) return "착빙 이슬비";
        if (code == 61 || code == 63 || code == 65) return "비";
        if (code == 66 || code == 67) return "얼어붙는 비";
        if (code == 71 || code == 73 || code == 75) return "눈";
        if (code == 77) return "싸락눈";
        if (code == 80 || code == 81 || code == 82) return "소나기";
        if (code == 85 || code == 86) return "소낙눈";
        if (code == 95) return "천둥번개";
        if (code == 96 || code == 99) return "우박 동반 천둥";
        return "날씨";
    }

    private String emojiForWeatherCode(int code) {
        if (code == 0) return "☀️";
        if (code == 1 || code == 2) return "🌤";
        if (code == 3) return "☁️";
        if (code == 45 || code == 48) return "🌫️";
        if (code == 61 || code == 63 || code == 65 || code == 80 || code == 81 || code == 82) return "🌧️";
        if (code == 71 || code == 73 || code == 75 || code == 85 || code == 86) return "🌨️";
        if (code == 95 || code == 96 || code == 99) return "⛈️";
        return "🌡️";
    }
}
