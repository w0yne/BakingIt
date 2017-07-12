package com.w0yne.android.bakingit.net;

import com.w0yne.android.bakingit.data.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}
