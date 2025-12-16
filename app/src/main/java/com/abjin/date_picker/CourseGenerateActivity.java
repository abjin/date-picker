package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class CourseGenerateActivity extends AppCompatActivity {

    private static final int GENERATION_DELAY = 3000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable generationRunnable = () -> {
        Intent intent = new Intent(CourseGenerateActivity.this, CourseResultActivity.class);
        startActivity(intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_generate);

        // Simulate course generation
        handler.postDelayed(generationRunnable, GENERATION_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(generationRunnable);
    }
}
