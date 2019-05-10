package com.jeffjeong.foodrecipes.adapters;

// 레시피 리스너 인터페이스
public interface OnRecipeListener {

    // 레시피가 클릭 되었을 때
    void onRecipeClick(int position);

    // 카테고리가 클릭 되었을 때
    void onCategoryClick(String category);

}
