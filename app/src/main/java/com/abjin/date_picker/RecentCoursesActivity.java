package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentCoursesActivity extends AppCompatActivity {

    private static final String TAG = "RecentCourses";
    private RecyclerView rvRecentCourses;
    private TextView tvEmptyState;
    private RecentCoursesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_courses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvRecentCourses = findViewById(R.id.rvRecentCourses);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        adapter = new RecentCoursesAdapter(new ArrayList<>());
        rvRecentCourses.setLayoutManager(new LinearLayoutManager(this));
        rvRecentCourses.setAdapter(adapter);

        loadRecentCourses();
    }

    private void loadRecentCourses() {
        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.getDateCourses("createdAt", 50).enqueue(new Callback<List<DateCourseResponse>>() {
            @Override
            public void onResponse(Call<List<DateCourseResponse>> call, Response<List<DateCourseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DateCourseResponse> courses = response.body();
                    if (courses.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rvRecentCourses.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvRecentCourses.setVisibility(View.VISIBLE);
                        adapter.updateCourses(courses);
                    }
                    Log.d(TAG, "Loaded " + courses.size() + " recent courses");
                } else {
                    Log.e(TAG, "Failed to load recent courses: " + response.code());
                    Toast.makeText(RecentCoursesActivity.this, "최근 코스를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DateCourseResponse>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(RecentCoursesActivity.this, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class RecentCoursesAdapter extends RecyclerView.Adapter<RecentCoursesAdapter.ViewHolder> {
        private List<DateCourseResponse> courses;

        RecentCoursesAdapter(List<DateCourseResponse> courses) {
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
                    .inflate(R.layout.item_course, parent, false);
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
            TextView tvCourseRegion, tvCourseBudget, tvCourseTitle, tvCourseDescription, tvPlaceCount, tvViewCount;

            ViewHolder(View itemView) {
                super(itemView);
                tvCourseRegion = itemView.findViewById(R.id.tvCourseRegion);
                tvCourseBudget = itemView.findViewById(R.id.tvCourseBudget);
                tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
                tvCourseDescription = itemView.findViewById(R.id.tvCourseDescription);
                tvPlaceCount = itemView.findViewById(R.id.tvPlaceCount);
                tvViewCount = itemView.findViewById(R.id.tvViewCount);
            }

            void bind(DateCourseResponse course) {
                tvCourseRegion.setText(course.getRegion());
                tvCourseBudget.setText(course.getBudget() + "만원");
                tvCourseTitle.setText(course.getTitle());
                tvCourseDescription.setText(course.getCourseDescription());
                tvPlaceCount.setText(course.getPlaces().size() + "곳");
                tvViewCount.setText("조회 " + course.getViewCount() + "회");

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(RecentCoursesActivity.this, CourseResultActivity.class);
                    intent.putExtra("course_data", course);
                    startActivity(intent);
                });
            }
        }
    }
}