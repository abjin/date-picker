package com.abjin.date_picker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.abjin.date_picker.api.models.UserPreferenceResponse;
import com.abjin.date_picker.preferences.UserPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationSelectActivity extends AppCompatActivity {

    private String selectedLocation = "홍대";
    private TextView tvSelectedLocation;
    private GridLayout gridLocations;
    private TextInputEditText etCustomLocation;
    private LocationManager locationManager;

    private static final int RC_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);

        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        gridLocations = findViewById(R.id.gridLocations);
        MaterialButton btnComplete = findViewById(R.id.btnComplete);
        etCustomLocation = findViewById(R.id.etCustomLocation);
        MaterialButton btnDetectLocation = findViewById(R.id.btnDetectLocation);

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

        // 현재 위치로 가져오기
        if (btnDetectLocation != null) {
            btnDetectLocation.setOnClickListener(v -> detectCurrentLocation());
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

    private boolean hasLocationPermission() {
        int fine = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        return fine == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                coarse == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                RC_LOCATION
        );
    }

    private void detectCurrentLocation() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if (locationManager == null) {
            Toast.makeText(this, "위치 서비스를 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("MissingPermission")
        Location lastKnown = getBestLastKnownLocation(locationManager);
        if (lastKnown != null) {
            reverseGeocodeAndApply(lastKnown);
            return;
        }

        // 마지막 위치가 없으면 한 번 업데이트 요청
        trySingleLocationUpdate();
    }

    @SuppressLint("MissingPermission")
    private void trySingleLocationUpdate() {
        if (!hasLocationPermission() || locationManager == null) return;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) provider = LocationManager.NETWORK_PROVIDER;

        final LocationListener oneTimeListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                reverseGeocodeAndApply(location);
                // cleanup
                if (ActivityCompat.checkSelfPermission(LocationSelectActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(LocationSelectActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(this);
                }
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(String provider) {}
            @Override public void onProviderDisabled(String provider) {}
        };

        try {
            locationManager.requestSingleUpdate(provider, oneTimeListener, Looper.getMainLooper());
        } catch (Exception e) {
            Log.e("LocationSelectActivity", "Failed to request location update", e);
            Toast.makeText(this, "현재 위치를 가져오지 못했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private Location getBestLastKnownLocation(LocationManager lm) {
        Location best = null;
        try {
            List<String> providers = lm.getProviders(true);
            for (String p : providers) {
                Location loc = lm.getLastKnownLocation(p);
                if (loc == null) continue;
                if (best == null || loc.getAccuracy() < best.getAccuracy()) {
                    best = loc;
                }
            }
        } catch (Exception e) {
            Log.w("LocationSelectActivity", "getBestLastKnownLocation: " + e.getMessage());
        }
        return best;
    }

    private void reverseGeocodeAndApply(Location location) {
        if (location == null) return;
        new Thread(() -> {
            String name = null;
            try {
                Geocoder geocoder = new Geocoder(LocationSelectActivity.this, Locale.KOREA);
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (list != null && !list.isEmpty()) {
                    Address addr = list.get(0);
                    // Prefer subLocality (동), fallback to locality (구/시)
                    String subLocality = addr.getSubLocality();
                    String locality = addr.getLocality();
                    String thoroughfare = addr.getThoroughfare(); // sometimes 동/로
                    if (subLocality != null && !subLocality.isEmpty()) {
                        name = subLocality;
                    } else if (thoroughfare != null && !thoroughfare.isEmpty()) {
                        name = thoroughfare;
                    } else if (locality != null && !locality.isEmpty()) {
                        name = locality;
                    }
                }
            } catch (IOException e) {
                Log.e("LocationSelectActivity", "Geocoder failed", e);
            }

            final String resultName = name;
            runOnUiThread(() -> {
                if (resultName != null && !resultName.isEmpty()) {
                    if (etCustomLocation != null) {
                        etCustomLocation.setText(resultName);
                        etCustomLocation.setSelection(resultName.length());
                    }
                    Toast.makeText(LocationSelectActivity.this, "현재 위치로 설정되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocationSelectActivity.this, "현재 위치의 동네를 찾지 못했습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION) {
            if (hasLocationPermission()) {
                detectCurrentLocation();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
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
