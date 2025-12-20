package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abjin.date_picker.api.ApiClient;
import com.abjin.date_picker.api.DateCourseApiService;
import com.abjin.date_picker.api.models.DateCourseResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private DateCourseResponse recommendedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MaterialCardView cvRecommendedCourse = findViewById(R.id.cvRecommendedCourse);
        TextView tvRecommendedTitle = findViewById(R.id.tvRecommendedTitle);
        TextView tvRecommendedDescription = findViewById(R.id.tvRecommendedDescription);
        TextView tvRecommendedRegion = findViewById(R.id.tvRecommendedRegion);
        TextView tvRecommendedBudget = findViewById(R.id.tvRecommendedBudget);
        TextView tvRecommendedPlaceCount = findViewById(R.id.tvRecommendedPlaceCount);
        MaterialButton btnGenerateCourse = findViewById(R.id.btnGenerateCourse);
        MaterialButton btnEditConditions = findViewById(R.id.btnEditConditions);
        MaterialButton btnSavedCourses = findViewById(R.id.btnSavedCourses);

        loadRecommendedCourse(tvRecommendedTitle, tvRecommendedDescription, tvRecommendedRegion, tvRecommendedBudget, tvRecommendedPlaceCount);

        cvRecommendedCourse.setOnClickListener(v -> {
            if (recommendedCourse != null) {
                Intent intent = new Intent(HomeActivity.this, CourseResultActivity.class);
                intent.putExtra("course_data", recommendedCourse);
                startActivity(intent);
            }
        });

        btnGenerateCourse.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CourseGenerateActivity.class);
            startActivity(intent);
        });

        btnEditConditions.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, InterestSelectActivity.class);
            startActivity(intent);
        });

        btnSavedCourses.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookmarkedCoursesActivity.class);
            startActivity(intent);
        });
    }

    private void loadRecommendedCourse(TextView tvTitle, TextView tvDescription, TextView tvRegion, TextView tvBudget, TextView tvPlaceCount) {
        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.getDateCourses("views", 1).enqueue(new Callback<List<DateCourseResponse>>() {
            @Override
            public void onResponse(Call<List<DateCourseResponse>> call, Response<List<DateCourseResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    recommendedCourse = response.body().get(0);
                    tvTitle.setText(recommendedCourse.getTitle());
                    tvDescription.setText(recommendedCourse.getCourseDescription());
                    tvRegion.setText(recommendedCourse.getRegion());
                    tvBudget.setText("예산 " + recommendedCourse.getBudget() + "만원");
                    tvPlaceCount.setText(recommendedCourse.getPlaces().size() + "곳");
                    Log.d(TAG, "Loaded recommended course: " + recommendedCourse.getTitle());
                } else {
                    Log.e(TAG, "Failed to load recommended course: " + response.code());
                    Toast.makeText(HomeActivity.this, "추천 코스를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DateCourseResponse>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(HomeActivity.this, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(HomeActivity.this, MyPageActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
