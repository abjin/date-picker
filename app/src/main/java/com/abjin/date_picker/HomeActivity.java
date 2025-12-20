package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MaterialCardView cvRecommendedCourse = findViewById(R.id.cvRecommendedCourse);
        MaterialButton btnGenerateCourse = findViewById(R.id.btnGenerateCourse);
        MaterialButton btnRefresh = findViewById(R.id.btnRefresh);
        MaterialButton btnEditConditions = findViewById(R.id.btnEditConditions);
        MaterialButton btnSavedCourses = findViewById(R.id.btnSavedCourses);

        cvRecommendedCourse.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CourseResultActivity.class);
            startActivity(intent);
        });

        btnGenerateCourse.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CourseGenerateActivity.class);
            startActivity(intent);
        });

        btnRefresh.setOnClickListener(v -> {
            // TODO: Implement refresh logic
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
