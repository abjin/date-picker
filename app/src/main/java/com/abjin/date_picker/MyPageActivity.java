package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        MaterialButton btnEditInterests = findViewById(R.id.btnEditInterests);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        btnEditInterests.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, InterestSelectActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
