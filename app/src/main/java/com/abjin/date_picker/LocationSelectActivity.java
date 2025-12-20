package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.abjin.date_picker.api.models.UserPreferenceResponse;
import com.abjin.date_picker.preferences.UserPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;

public class LocationSelectActivity extends AppCompatActivity {

    private String selectedLocation = "홍대";
    private TextView tvSelectedLocation;
    private GridLayout gridLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);

        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        gridLocations = findViewById(R.id.gridLocations);
        MaterialButton btnComplete = findViewById(R.id.btnComplete);
        TextInputEditText etCustomLocation = findViewById(R.id.etCustomLocation);

        // 기존 region 로드
        UserPreferenceManager userPrefManager = UserPreferenceManager.getInstance(this);
        String existingRegion = userPrefManager.getRegion();
        if (existingRegion != null && !existingRegion.isEmpty()) {
            selectedLocation = existingRegion;
        }
        tvSelectedLocation.setText(selectedLocation);

        String[] locations = {"홍대", "강남", "신촌", "명동", "이태원",
                              "건대", "잠실", "압구정", "성수", "여의도"};

        for (String location : locations) {
            addLocationButton(location);
        }

        // 직접 입력: 입력할 때 자동 선택
        if (etCustomLocation != null) {
            etCustomLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String input = s != null ? s.toString().trim() : "";
                    if (!input.isEmpty()) {
                        selectedLocation = input;
                        tvSelectedLocation.setText(input);
                        updateAllButtonStyles();
                    }
                }
            });
        }

        btnComplete.setOnClickListener(v -> {
            // 선택한 region 저장
            userPrefManager.setRegion(selectedLocation);

            // 서버에 사용자 정보 업데이트
            userPrefManager.updateUserToServer(new UserPreferenceManager.OnUpdateListener() {
                @Override
                public void onSuccess(UserPreferenceResponse response) {
                    Log.d("LocationSelectActivity", "User preference updated successfully");
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("LocationSelectActivity", "Failed to update user preference: " + errorMessage);
                }
            });

            Intent intent = new Intent(LocationSelectActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void addLocationButton(String location) {
        MaterialButton button = new MaterialButton(this);
        button.setText(location);
        button.setTextSize(14);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(48);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        button.setLayoutParams(params);

        button.setCornerRadius(dpToPx(12));
        updateButtonStyle(button, location.equals(selectedLocation));

        button.setOnClickListener(v -> {
            selectedLocation = location;
            tvSelectedLocation.setText(location);
            updateAllButtonStyles();
        });

        gridLocations.addView(button);
    }

    private void updateAllButtonStyles() {
        for (int i = 0; i < gridLocations.getChildCount(); i++) {
            MaterialButton button = (MaterialButton) gridLocations.getChildAt(i);
            String location = button.getText().toString();
            updateButtonStyle(button, location.equals(selectedLocation));
        }
    }

    private void updateButtonStyle(MaterialButton button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundColor(getColor(R.color.primary));
            button.setTextColor(getColor(R.color.white));
        } else {
            button.setBackgroundColor(getColor(R.color.surface_variant));
            button.setTextColor(getColor(R.color.text_secondary));
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
