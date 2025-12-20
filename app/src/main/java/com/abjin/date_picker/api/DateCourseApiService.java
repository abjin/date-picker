package com.abjin.date_picker.api;

import com.abjin.date_picker.api.models.BookmarkResponse;
import com.abjin.date_picker.api.models.DateCourseRequest;
import com.abjin.date_picker.api.models.DateCourseResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DateCourseApiService {
    @POST("/date-courses")
    Call<DateCourseResponse> generateDateCourse(@Body DateCourseRequest request);

    @POST("/date-courses/{id}/bookmark")
    Call<BookmarkResponse> bookmarkCourse(@Path("id") int courseId);

    @GET("/date-courses/bookmarks")
    Call<List<DateCourseResponse>> getBookmarkedCourses();
}