package com.jeffjeong.foodrecipes.util;

import android.util.Log;

import com.jeffjeong.foodrecipes.models.Recipe;

import java.util.List;

// 테스트 클래스
public class Testing {

    public static void printRecipes(List<Recipe> list, String tag){
        for(Recipe recipe: list){
            Log.d(tag, "printRecipes: " + recipe.getTitle());
        }
    }

}
