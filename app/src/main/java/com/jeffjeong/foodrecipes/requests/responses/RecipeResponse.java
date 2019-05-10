package com.jeffjeong.foodrecipes.requests.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jeffjeong.foodrecipes.models.Recipe;

// 서버로 부터 응답을 받는 레시피 응답 클래스
public class RecipeResponse {

    // 시리얼 라이드즈 네임을 함으로써 레트로핏이 알 수 있다.
    @SerializedName("recipe")
    // gson 이 시리얼 라이즈 혹은 디 시리얼 라이즈 할 수 있도록 하는 어노테이션
    @Expose()
    private Recipe recipe;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    //레시피를 가져오는 메소드
    public Recipe getRecipe(){
        return recipe;
    }

    // 디버깅용
    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                '}';
    }



}
