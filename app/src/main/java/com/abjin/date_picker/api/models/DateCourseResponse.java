package com.abjin.date_picker.api.models;

import java.io.Serializable;
import java.util.List;

public class DateCourseResponse implements Serializable {
    private String title;
    private String courseDescription;
    private List<CourseItem> course;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public List<CourseItem> getCourse() {
        return course;
    }

    public void setCourse(List<CourseItem> course) {
        this.course = course;
    }

    public static class CourseItem implements Serializable {
        private String place;
        private String description;
        private String link;

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
