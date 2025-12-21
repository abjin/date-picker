package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abjin.date_picker.auth.GoogleAuthManager;
import com.abjin.date_picker.auth.TokenManager;
import com.abjin.date_picker.preferences.UserPreferenceManager;
import com.google.android.material.button.MaterialButton;

public class MyPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        MaterialButton btnEditInterests = findViewById(R.id.btnEditInterests);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        // TokenManager를 통해 계정 정보 가져오기
        TokenManager tokenManager = TokenManager.getInstance(this);
        String userEmail = tokenManager.getUserEmail();
        String userName = tokenManager.getUserName();

        // 계정 정보 표시
        if (userName != null && !userName.isEmpty()) {
            tvUserEmail.setText(userName + " (" + (userEmail != null ? userEmail : "") + ")");
        } else if (userEmail != null) {
            tvUserEmail.setText(userEmail);
        }

        btnEditInterests.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, InterestSelectActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            String webClientId = getString(R.string.google_web_client_id);
            GoogleAuthManager googleAuthManager = new GoogleAuthManager(this, webClientId);
            UserPreferenceManager userPrefManager = UserPreferenceManager.getInstance(this);

            // Google 로그아웃
            googleAuthManager.signOut(isSuccess -> {
                if (isSuccess) {
                    // 로컬 데이터 삭제
                    tokenManager.clearAll();         // 토큰 + 사용자 기본 정보
                    userPrefManager.clearAll();      // 사용자 선호도

                    Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
