package com.abjin.date_picker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.abjin.date_picker.api.models.DateCourseResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CourseResultActivity extends AppCompatActivity {

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
        }

        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "코스가 저장되었습니다", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "공유 기능 준비중입니다", Toast.LENGTH_SHORT).show();
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
