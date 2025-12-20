package com.abjin.date_picker;

import android.content.Intent;
import android.net.Uri;
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
import com.abjin.date_picker.api.models.BookmarkResponse;
import com.abjin.date_picker.api.models.DateCourseResponse;
import com.abjin.date_picker.api.models.ViewCountResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseResultActivity extends AppCompatActivity {

    private static final String TAG = "CourseResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvCourseTitle = findViewById(R.id.tvCourseTitle);
        TextView tvCourseDescription = findViewById(R.id.tvCourseDescription);
        RecyclerView rvCourseSpots = findViewById(R.id.rvCourseSpots);
        MaterialButton btnSave = findViewById(R.id.btnSave);
        MaterialButton btnShare = findViewById(R.id.btnShare);

        DateCourseResponse courseData = (DateCourseResponse) getIntent().getSerializableExtra("course_data");

        if (courseData != null) {
            tvCourseTitle.setText(courseData.getTitle());
            tvCourseDescription.setText(courseData.getCourseDescription());

            CourseSpotAdapter adapter = new CourseSpotAdapter(courseData.getPlaces());
            rvCourseSpots.setLayoutManager(new LinearLayoutManager(this));
            rvCourseSpots.setAdapter(adapter);

            incrementViewCount(courseData.getId());
        }

        btnSave.setOnClickListener(v -> {
            if (courseData != null) {
                bookmarkCourse(courseData.getId());
            }
        });

        btnShare.setOnClickListener(v -> {
            if (courseData == null) {
                Toast.makeText(this, "공유할 코스가 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            String shareUrl = "https://date-picker-share-web.vercel.app/?id=" + courseData.getId();
            String shareText = courseData.getTitle() + "\n" + shareUrl;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "데이트 코스 공유");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            try {
                startActivity(Intent.createChooser(shareIntent, "코스 공유하기"));
            } catch (Exception e) {
                Log.e(TAG, "No activity found to handle share", e);
                Toast.makeText(this, "공유할 앱이 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incrementViewCount(int courseId) {
        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.incrementViewCount(courseId).enqueue(new Callback<ViewCountResponse>() {
            @Override
            public void onResponse(Call<ViewCountResponse> call, Response<ViewCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "View count incremented: " + response.body().getViewCount());
                } else {
                    Log.e(TAG, "Failed to increment view count: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ViewCountResponse> call, Throwable t) {
                Log.e(TAG, "Network error while incrementing view count: " + t.getMessage());
            }
        });
    }

    private void bookmarkCourse(int courseId) {
        DateCourseApiService apiService = ApiClient.getClient(this).create(DateCourseApiService.class);
        apiService.bookmarkCourse(courseId).enqueue(new Callback<BookmarkResponse>() {
            @Override
            public void onResponse(Call<BookmarkResponse> call, Response<BookmarkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CourseResultActivity.this, "코스가 저장되었습니다", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Bookmark success: " + response.body().getId());
                } else if (response.code() == 409) {
                    Log.w(TAG, "Already bookmarked: " + response.code());
                    Toast.makeText(CourseResultActivity.this, "이미 북마크한 데이트 코스입니다", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Bookmark failed: " + response.code());
                    Toast.makeText(CourseResultActivity.this, "저장에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookmarkResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(CourseResultActivity.this, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class CourseSpotAdapter extends RecyclerView.Adapter<CourseSpotAdapter.ViewHolder> {
        private List<DateCourseResponse.Place> spots;

        CourseSpotAdapter(List<DateCourseResponse.Place> spots) {
            this.spots = spots;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_spot, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DateCourseResponse.Place spot = spots.get(position);
            holder.bind(spot, position + 1);
        }

        @Override
        public int getItemCount() {
            return spots != null ? spots.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSpotNumber, tvSpotName, tvSpotCategory, tvSpotDescription;

            ViewHolder(View itemView) {
                super(itemView);
                tvSpotNumber = itemView.findViewById(R.id.tvSpotNumber);
                tvSpotName = itemView.findViewById(R.id.tvSpotName);
                tvSpotCategory = itemView.findViewById(R.id.tvSpotCategory);
                tvSpotDescription = itemView.findViewById(R.id.tvSpotDescription);
            }

            void bind(DateCourseResponse.Place spot, int number) {
                tvSpotNumber.setText(String.valueOf(number));
                tvSpotName.setText(spot.getPlace());
                tvSpotDescription.setText(spot.getDescription());

                String link = spot.getLink();
                if (link != null && !link.isEmpty()) {
                    tvSpotCategory.setText("링크 보기");
                    tvSpotCategory.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        v.getContext().startActivity(intent);
                    });
                } else {
                    tvSpotCategory.setVisibility(View.GONE);
                }
            }
        }
    }
}
