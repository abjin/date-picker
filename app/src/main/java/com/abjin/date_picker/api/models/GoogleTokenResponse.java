package com.abjin.date_picker.api.models;

public class GoogleTokenResponse {
    private String token;
    private UserInfo user;

    public GoogleTokenResponse() {
    }

    public GoogleTokenResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
