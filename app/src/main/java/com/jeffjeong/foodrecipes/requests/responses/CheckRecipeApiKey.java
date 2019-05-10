package com.jeffjeong.foodrecipes.requests.responses;

// api 키가 만료 됬는지 여부 확인하는 클래스
public class CheckRecipeApiKey {

    // protected 해당 패키지 안에서만 접근 가능하다.
    protected static boolean isRecipeApiKeyValid(RecipeSearchResponse response){
        return response.getError() == null;
    }

    protected static boolean isRecipeApiKeyValid(RecipeResponse response) {
        return response.getError() == null;
    }

}
