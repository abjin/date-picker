package com.abjin.date_picker.api.models;

import java.util.List;

public class UserPreferenceRequest {
    private String region;
    private List<String> interests;
    private double budget;

    public UserPreferenceRequest(String region, List<String> interests, double budget) {
        this.region = region;
        this.interests = interests;
        this.budget = budget;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}