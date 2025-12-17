package com.abjin.date_picker.api;

import com.abjin.date_picker.api.models.GoogleTokenRequest;
import com.abjin.date_picker.api.models.GoogleTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("/auth/google/login")
    Call<GoogleTokenResponse> googleLogin(@Body GoogleTokenRequest request);
}
