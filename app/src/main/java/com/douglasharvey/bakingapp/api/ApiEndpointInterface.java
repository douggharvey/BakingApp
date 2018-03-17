package com.douglasharvey.bakingapp.api;

import com.douglasharvey.bakingapp.models.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiEndpointInterface {

    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipe();

}
