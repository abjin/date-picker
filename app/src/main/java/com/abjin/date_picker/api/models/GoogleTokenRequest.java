package com.abjin.date_picker.api.models;

public class GoogleTokenRequest {
    private String id;
    private String idToken;

    public GoogleTokenRequest() {
    }

    public GoogleTokenRequest(String id, String idToken) {
        this.id = id;
        this.idToken = idToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
