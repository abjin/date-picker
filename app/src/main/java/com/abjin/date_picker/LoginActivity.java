package com.abjin.date_picker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abjin.date_picker.api.ApiClient;
import com.abjin.date_picker.api.AuthApiService;
import com.abjin.date_picker.api.models.GoogleTokenRequest;
import com.abjin.date_picker.api.models.GoogleTokenResponse;
import com.abjin.date_picker.api.models.UserInfo;
import com.abjin.date_picker.auth.GoogleAuthManager;
import com.abjin.date_picker.auth.TokenManager;
import com.abjin.date_picker.preferences.UserPreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;

import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleAuthManager googleAuthManager;
    private TokenManager tokenManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = TokenManager.getInstance(this);

        // 이미 로그인된 경우 InterestSelectActivity로 이동
        if (tokenManager.isLoggedIn()) {
            navigateToInterestSelect();
            return;
        }

        String webClientId = getString(R.string.google_web_client_id);
        googleAuthManager = new GoogleAuthManager(this, webClientId);

        MaterialButton btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleAuthManager.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                showProgressDialog();
                GoogleSignInAccount account = googleAuthManager.handleSignInResult(data);
                String accountId = googleAuthManager.getAccountId(account);
                String idToken = googleAuthManager.getIdToken(account);
                sendTokenToBackend(accountId, idToken);
            } catch (ApiException e) {
                hideProgressDialog();
                Toast.makeText(this, "Google 로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendTokenToBackend(String accountId, String idToken) {
        AuthApiService apiService = ApiClient.getClient(this).create(AuthApiService.class);
        GoogleTokenRequest request = new GoogleTokenRequest(accountId, idToken);

        apiService.googleLogin(request).enqueue(new Callback<GoogleTokenResponse>() {
            @Override
            public void onResponse(Call<GoogleTokenResponse> call, Response<GoogleTokenResponse> response) {
                hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    GoogleTokenResponse tokenResponse = response.body();

                    // 토큰 저장
                    tokenManager.saveToken(tokenResponse.getToken());

                    // 사용자 정보 저장
                    if (tokenResponse.getUser() != null) {
                        UserInfo user = tokenResponse.getUser();
                        tokenManager.saveUserInfo(
                                user.getId(),
                                user.getEmail(),
                                user.getName(),
                                user.getProfileImageUrl()
                        );

                        // 사용자 선호도 저장
                        UserPreferenceManager userPrefManager = UserPreferenceManager.getInstance(LoginActivity.this);

                        if (user.getRegion() != null) {
                            userPrefManager.setRegion(user.getRegion());
                        }
                        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
                            userPrefManager.setInterests(new HashSet<>(user.getInterests()));
                        }
                        if (user.getBudget() != null) {
                            userPrefManager.setBudget(user.getBudget().floatValue());
                        }
                    }

                    navigateToInterestSelect();
                } else {
                    Toast.makeText(LoginActivity.this, "서버 인증 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GoogleTokenResponse> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToInterestSelect() {
        Intent intent = new Intent(LoginActivity.this, InterestSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("로그인 중...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
