package com.abjin.date_picker.api.models;

public class WeatherResponse {
    private CurrentWeather current_weather;

    public CurrentWeather getCurrent_weather() {
        return current_weather;
    }

    public static class CurrentWeather {
        private float temperature;
        private int weathercode;

        public float getTemperature() {
            return temperature;
        }

        public int getWeathercode() {
            return weathercode;
        }
    }
}

