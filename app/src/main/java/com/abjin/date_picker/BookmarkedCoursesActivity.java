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

public class BookmarkedCoursesActivity extends AppCompatActivity {

    private static final String TAG = "BookmarkedCourses";
    private RecyclerView rvBookmarkedCourses;
    private TextView tvEmptyState;
    private BookmarkedCoursesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked_courses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvBookmarkedCourses = findViewById(R.id.rvBookmarkedCourses);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        adapter = new BookmarkedCoursesAdapter(new ArrayList<>());
        rvBookmarkedCourses.setLayoutManager(new LinearLayoutManager(this));
        rvBookmarkedCourses.setAdapter(adapter);

        loadBookmarkedCourses();
    }

    private void loadBookmarkedCourses() {
        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.getBookmarkedCourses().enqueue(new Callback<List<DateCourseResponse>>() {
            @Override
            public void onResponse(Call<List<DateCourseResponse>> call, Response<List<DateCourseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DateCourseResponse> courses = response.body();
                    if (courses.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rvBookmarkedCourses.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvBookmarkedCourses.setVisibility(View.VISIBLE);
                        adapter.updateCourses(courses);
                    }
                    Log.d(TAG, "Loaded " + courses.size() + " bookmarked courses");
                } else {
                    Log.e(TAG, "Failed to load bookmarks: " + response.code());
                    Toast.makeText(BookmarkedCoursesActivity.this, "저장한 코스를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DateCourseResponse>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(BookmarkedCoursesActivity.this, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class BookmarkedCoursesAdapter extends RecyclerView.Adapter<BookmarkedCoursesAdapter.ViewHolder> {
        private List<DateCourseResponse> courses;

        BookmarkedCoursesAdapter(List<DateCourseResponse> courses) {
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
                    Intent intent = new Intent(BookmarkedCoursesActivity.this, CourseResultActivity.class);
                    intent.putExtra("course_data", course);
                    startActivity(intent);
                });
            }
        }
    }
}
