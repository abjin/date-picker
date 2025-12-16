package com.abjin.date_picker;

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

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
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

        RecyclerView rvCourseSpots = findViewById(R.id.rvCourseSpots);
        MaterialButton btnSave = findViewById(R.id.btnSave);
        MaterialButton btnShare = findViewById(R.id.btnShare);

        // Sample data
        List<CourseSpot> spots = new ArrayList<>();
        spots.add(new CourseSpot("감성 카페", "카페", "분위기 좋은 로스터리 카페", "1시간"));
        spots.add(new CourseSpot("이탈리안 레스토랑", "식사", "파스타와 와인이 맛있는 곳", "1.5시간"));
        spots.add(new CourseSpot("한강 산책로", "산책", "야경이 아름다운 한강 공원", "30분"));

        CourseSpotAdapter adapter = new CourseSpotAdapter(spots);
        rvCourseSpots.setLayoutManager(new LinearLayoutManager(this));
        rvCourseSpots.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "코스가 저장되었습니다", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "공유 기능 준비중입니다", Toast.LENGTH_SHORT).show();
        });
    }

    static class CourseSpot {
        String name;
        String category;
        String description;
        String duration;

        CourseSpot(String name, String category, String description, String duration) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.duration = duration;
        }
    }

    static class CourseSpotAdapter extends RecyclerView.Adapter<CourseSpotAdapter.ViewHolder> {
        private List<CourseSpot> spots;

        CourseSpotAdapter(List<CourseSpot> spots) {
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
            CourseSpot spot = spots.get(position);
            holder.bind(spot, position + 1);
        }

        @Override
        public int getItemCount() {
            return spots.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSpotNumber, tvSpotName, tvSpotCategory, tvSpotDescription, tvSpotDuration;

            ViewHolder(View itemView) {
                super(itemView);
                tvSpotNumber = itemView.findViewById(R.id.tvSpotNumber);
                tvSpotName = itemView.findViewById(R.id.tvSpotName);
                tvSpotCategory = itemView.findViewById(R.id.tvSpotCategory);
                tvSpotDescription = itemView.findViewById(R.id.tvSpotDescription);
                tvSpotDuration = itemView.findViewById(R.id.tvSpotDuration);
            }

            void bind(CourseSpot spot, int number) {
                tvSpotNumber.setText(String.valueOf(number));
                tvSpotName.setText(spot.name);
                tvSpotCategory.setText(spot.category);
                tvSpotDescription.setText(spot.description);
                tvSpotDuration.setText("예상 소요시간: " + spot.duration);
            }
        }
    }
}
