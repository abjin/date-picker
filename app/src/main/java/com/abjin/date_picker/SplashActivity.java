package com.abjin.date_picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.abjin.date_picker.auth.TokenManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        TextView tvAppName = findViewById(R.id.tvAppName);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);

        // Initial state
        ivLogo.setScaleX(0f);
        ivLogo.setScaleY(0f);
        tvAppName.setAlpha(0f);
        tvSubtitle.setAlpha(0f);

        // Animate logo
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0f, 1f);
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());
        scaleX.start();
        scaleY.start();

        // Animate text
        tvAppName.animate().alpha(1f).setDuration(800).start();
        tvSubtitle.animate().alpha(1f).setDuration(800).start();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TokenManager tokenManager = TokenManager.getInstance(SplashActivity.this);
            Intent intent;

            if (tokenManager.isLoggedIn()) {
                // 이미 로그인되어 있으면 HomeActivity로 이동
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                // 첫 방문이면 Onboarding으로 이동
                intent = new Intent(SplashActivity.this, Onboarding1Activity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
