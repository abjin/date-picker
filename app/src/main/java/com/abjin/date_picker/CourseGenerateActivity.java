package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abjin.date_picker.api.ApiClient;
import com.abjin.date_picker.api.DateCourseApiService;
import com.abjin.date_picker.api.models.DateCourseRequest;
import com.abjin.date_picker.api.models.DateCourseResponse;
import com.abjin.date_picker.preferences.UserPreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseGenerateActivity extends AppCompatActivity {

    private static final String TAG = "CourseGenerateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_generate);

        generateDateCourse();
    }

    private void generateDateCourse() {
        UserPreferenceManager prefManager = UserPreferenceManager.getInstance(this);

        String region = prefManager.getRegion();
        Set<String> interestsSet = prefManager.getInterests();
        List<String> interests = new ArrayList<>(interestsSet);
        String additional = prefManager.getAdditionalRequest();
        String weather = prefManager.getWeather();
        double budget = prefManager.getBudget();

        DateCourseRequest request = new DateCourseRequest(region, interests, budget, additional, weather);

        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.generateDateCourse(request).enqueue(new Callback<DateCourseResponse>() {
            @Override
            public void onResponse(Call<DateCourseResponse> call, Response<DateCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(CourseGenerateActivity.this, CourseResultActivity.class);
                    intent.putExtra("course_data", response.body());
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "API error: " + response.code());
                    Toast.makeText(CourseGenerateActivity.this, "코스 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DateCourseResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(CourseGenerateActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
