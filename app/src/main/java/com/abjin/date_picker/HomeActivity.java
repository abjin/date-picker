package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abjin.date_picker.api.ApiClient;
import com.abjin.date_picker.api.DateCourseApiService;
import com.abjin.date_picker.api.models.DateCourseResponse;
import com.abjin.date_picker.preferences.UserPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private DateCourseResponse recommendedCourse;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateToolbarTitle();

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

        // Setup horizontal scroll sections
        RecyclerView rvPopularCourses = findViewById(R.id.rvPopularCourses);
        RecyclerView rvRecentCourses = findViewById(R.id.rvRecentCourses);
        TextView tvViewAllPopular = findViewById(R.id.tvViewAllPopular);
        TextView tvViewAllRecent = findViewById(R.id.tvViewAllRecent);

        HorizontalCourseAdapter popularAdapter = new HorizontalCourseAdapter(new ArrayList<>());
        HorizontalCourseAdapter recentAdapter = new HorizontalCourseAdapter(new ArrayList<>());

        rvPopularCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPopularCourses.setAdapter(popularAdapter);

        rvRecentCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecentCourses.setAdapter(recentAdapter);

        loadPopularCourses(popularAdapter);
        loadRecentCourses(recentAdapter);

        tvViewAllPopular.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PopularCoursesActivity.class);
            startActivity(intent);
        });

        tvViewAllRecent.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RecentCoursesActivity.class);
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

    private void loadPopularCourses(HorizontalCourseAdapter adapter) {
        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.getDateCourses("views", 5).enqueue(new Callback<List<DateCourseResponse>>() {
            @Override
            public void onResponse(Call<List<DateCourseResponse>> call, Response<List<DateCourseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateCourses(response.body());
                    Log.d(TAG, "Loaded " + response.body().size() + " popular courses");
                } else {
                    Log.e(TAG, "Failed to load popular courses: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<DateCourseResponse>> call, Throwable t) {
                Log.e(TAG, "Network error loading popular courses: " + t.getMessage());
            }
        });
    }

    private void loadRecentCourses(HorizontalCourseAdapter adapter) {
        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.getDateCourses("latest", 5).enqueue(new Callback<List<DateCourseResponse>>() {
            @Override
            public void onResponse(Call<List<DateCourseResponse>> call, Response<List<DateCourseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateCourses(response.body());
                    Log.d(TAG, "Loaded " + response.body().size() + " recent courses");
                } else {
                    Log.e(TAG, "Failed to load recent courses: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<DateCourseResponse>> call, Throwable t) {
                Log.e(TAG, "Network error loading recent courses: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        String region = UserPreferenceManager.getInstance(this).getRegion();
        if (region != null && !region.isEmpty()) {
            toolbar.setTitle(region);
        } else {
            toolbar.setTitle("지역을 선택해주세요");
        }
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

    class HorizontalCourseAdapter extends RecyclerView.Adapter<HorizontalCourseAdapter.ViewHolder> {
        private List<DateCourseResponse> courses;

        HorizontalCourseAdapter(List<DateCourseResponse> courses) {
            this.courses = courses;
        }

        void updateCourses(List<DateCourseResponse> newCourses) {
            this.courses = newCourses;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_horizontal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DateCourseResponse course = courses.get(position);
            holder.bind(course);
        }

        @Override
        public int getItemCount() {
            return courses != null ? courses.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCourseTitle, tvCourseDescription, tvCourseRegion, tvCourseBudget, tvPlaceCount, tvViewCount;

            ViewHolder(View itemView) {
                super(itemView);
                tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
                tvCourseDescription = itemView.findViewById(R.id.tvCourseDescription);
                tvCourseRegion = itemView.findViewById(R.id.tvCourseRegion);
                tvCourseBudget = itemView.findViewById(R.id.tvCourseBudget);
                tvPlaceCount = itemView.findViewById(R.id.tvPlaceCount);
                tvViewCount = itemView.findViewById(R.id.tvViewCount);
            }

            void bind(DateCourseResponse course) {
                tvCourseTitle.setText(course.getTitle());
                tvCourseDescription.setText(course.getCourseDescription());
                tvCourseRegion.setText(course.getRegion());
                tvCourseBudget.setText(course.getBudget() + "만원");
                tvPlaceCount.setText(course.getPlaces().size() + "곳");
                tvViewCount.setText("조회 " + course.getViewCount() + "회");

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(HomeActivity.this, CourseResultActivity.class);
                    intent.putExtra("course_data", course);
                    startActivity(intent);
                });
            }
        }
    }
}
