package com.abjin.date_picker.api.models;

import java.util.ArrayList;
import java.util.List;

public class UserPreferenceRequest {
    private String region;
    private List<String> interests;
    private double budget;

    public UserPreferenceRequest(String region, List<String> interests, double budget) {
        this(region, interests, budget, null);
    }

    public UserPreferenceRequest(String region, List<String> interests, double budget, String additional) {
        this.region = region;
        this.budget = budget;
        this.interests = interests != null ? new ArrayList<>(interests) : new ArrayList<>();
        if (additional != null) {
            String extra = additional.trim();
            if (!extra.isEmpty()) {
                this.interests.add(extra);
            }
        }
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
