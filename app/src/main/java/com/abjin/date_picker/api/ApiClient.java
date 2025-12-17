package com.abjin.date_picker.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.abjin.date_picker.auth.TokenManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://datepicker-api-server.vercel.app";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static class AuthInterceptor implements Interceptor {
        private Context context;

        AuthInterceptor(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            TokenManager tokenManager = TokenManager.getInstance(context);
            String token = tokenManager.getAccessToken();

            if (token != null && !token.isEmpty()) {
                request = request.newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
            }
            return chain.proceed(request);
        }
    }
}
