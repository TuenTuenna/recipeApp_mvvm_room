package com.jeffjeong.foodrecipes.requests;

import android.arch.lifecycle.LiveData;

import com.jeffjeong.foodrecipes.requests.responses.ApiResponse;
import com.jeffjeong.foodrecipes.requests.responses.RecipeResponse;
import com.jeffjeong.foodrecipes.requests.responses.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// 레트로핏 api 인터페이스
public interface RecipeApi {

    // https://www.food2fork.com/api/search?key=YOUR_API_KEY&q=chicken%20breast&page=2

    // SEARCH
    // 음식을 검색하다
    @GET("api/search")
    LiveData<ApiResponse<RecipeSearchResponse>> searchRecipe(
            // 처음 쿼리에는 ? 퀘스쳔 마크 자동으로 달아준다.
            @Query("key") String key,
            // 그 이후에는 & 마크를 계속 달아준다.
            @Query("q") String query,
            @Query("page") String page
    );

    // https://www.food2fork.com/api/get?key=YOUR_API_KEY&rId=35382

    // GET
    // 해당 레시피 아이디로 검색하다
    @GET("api/get")
    LiveData<ApiResponse<RecipeResponse>> getRecipe(
            @Query("key") String key,
            @Query("rId") String recipe_id
    );






}
