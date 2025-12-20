package com.abjin.date_picker.api.models;

import java.io.Serializable;

public class ViewCountResponse implements Serializable {
    private int viewCount;

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}