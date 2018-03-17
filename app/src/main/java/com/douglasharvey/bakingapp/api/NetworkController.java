package com.douglasharvey.bakingapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkController {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static Retrofit getClient() {

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        //todo consider a timeout here - refer other example/docs
        okHttpBuilder.networkInterceptors().add(httpLoggingInterceptor);
        okHttpBuilder.build();
        //todo investigate for what purpose this is used.
        Gson gson = new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpBuilder.build())
                .build();
    }
}
