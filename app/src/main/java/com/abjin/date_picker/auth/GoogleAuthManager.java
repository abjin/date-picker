package com.abjin.date_picker.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleAuthManager {
    private final Context context;
    private GoogleSignInClient googleSignInClient;

    public GoogleAuthManager(Context context, String webClientId) {
        this.context = context;
        initializeGoogleSignIn(webClientId);
    }

    private void initializeGoogleSignIn(String webClientId) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    public GoogleSignInAccount handleSignInResult(Intent data) throws ApiException {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        return task.getResult(ApiException.class);
    }

    public String getIdToken(GoogleSignInAccount account) {
        if (account != null) {
            return account.getIdToken();
        }
        return null;
    }

    public String getAccountId(GoogleSignInAccount account) {
        if (account != null) {
            return account.getId();
        }
        return null;
    }

    public void signOut(OnSignOutListener listener) {
        googleSignInClient.signOut()
                .addOnCompleteListener((Activity) context, task -> {
                    if (listener != null) {
                        listener.onSignOutComplete(task.isSuccessful());
                    }
                });
    }

    public void revokeAccess(OnRevokeAccessListener listener) {
        googleSignInClient.revokeAccess()
                .addOnCompleteListener((Activity) context, task -> {
                    if (listener != null) {
                        listener.onRevokeAccessComplete(task.isSuccessful());
                    }
                });
    }

    public interface OnSignOutListener {
        void onSignOutComplete(boolean isSuccess);
    }

    public interface OnRevokeAccessListener {
        void onRevokeAccessComplete(boolean isSuccess);
    }
}
