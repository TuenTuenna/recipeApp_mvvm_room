package com.jeffjeong.foodrecipes.requests.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jeffjeong.foodrecipes.models.Recipe;

import java.util.List;

// 레시피 서치 리퀘스트
public class RecipeSearchResponse {

    // 시리얼 라이드즈 네임을 함으로써 레트로핏이 알 수 있다.
    @SerializedName("count")
    // gson 이 시리얼 라이즈 혹은 디 시리얼 라이즈 할 수 있도록 하는 어노테이션
    @Expose()
    // 몇개의 자료가 들어왔는지 카운트
    private int count;

    @SerializedName("recipes")
    @Expose()
    // 들어온 자료들
    private List<Recipe> recipes;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    public int getCount() {
        return count;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    @Override
    public String toString() {
        return "RecipeSearchResponse{" +
                "count=" + count +
                ", recipes=" + recipes +
                ", error='" + error + '\'' +
                '}';
    }
}
