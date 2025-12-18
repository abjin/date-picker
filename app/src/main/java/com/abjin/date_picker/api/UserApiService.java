package com.abjin.date_picker.api;

import com.abjin.date_picker.api.models.UserPreferenceRequest;
import com.abjin.date_picker.api.models.UserPreferenceResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface UserApiService {
    @PUT("/users/me")
    Call<UserPreferenceResponse> updateUserPreference(@Body UserPreferenceRequest request);
}