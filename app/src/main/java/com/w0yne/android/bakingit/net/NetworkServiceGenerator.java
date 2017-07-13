package com.w0yne.android.bakingit.net;

import android.support.annotation.VisibleForTesting;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceGenerator {

    private static String sBaseUrl = "http://go.udacity.com/";

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    @VisibleForTesting
    public static void setBaseUrl(String baseUrl) {
        sBaseUrl = baseUrl;
    }

    public static String getBaseUrl() {
        return sBaseUrl;
    }
}
