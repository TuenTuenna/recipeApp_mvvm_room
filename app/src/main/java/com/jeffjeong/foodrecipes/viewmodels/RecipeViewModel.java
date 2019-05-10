package com.jeffjeong.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.jeffjeong.foodrecipes.models.Recipe;
import com.jeffjeong.foodrecipes.repositories.RecipeRepository;
import com.jeffjeong.foodrecipes.util.Resource;

// 레시피 뷰 모델 클래스
public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository recipeRepository;

    // 생성자 메소드
    public RecipeViewModel(@NonNull Application application) {
        super(application);

        // 리포지토리 설정
        recipeRepository = RecipeRepository.getInstance(application);

    }

    // 리포지토리 결과를 ui 로 알려줘야한다.
    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId){
        return recipeRepository.searchRecipeApi(recipeId);
    }

}