package com.jeffjeong.foodrecipes.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.jeffjeong.foodrecipes.R;
import com.jeffjeong.foodrecipes.models.Recipe;

// 레시피 뷰홀더 클래스
public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // ui
    TextView title, publisher, socialScore;
    AppCompatImageView image;

    // 레시피 리스너
    OnRecipeListener onRecipeListener;

    RequestManager requestManager;

    // 글라이드 프리로더 사이즈 프로바이더
    ViewPreloadSizeProvider viewPreloadSizeProvider;

    // 생성자 메소드
    // 매개변수에 레시피 리스너를 추가해준다.
    public RecipeViewHolder(@NonNull View itemView,
                            OnRecipeListener onRecipeListener,
                            RequestManager requestManager,
                            ViewPreloadSizeProvider preloadSizeProvider) {
        super(itemView);

        this.onRecipeListener = onRecipeListener;
        this.requestManager = requestManager;
        this.viewPreloadSizeProvider = preloadSizeProvider;


        title = itemView.findViewById(R.id.recipe_title);
        publisher = itemView.findViewById(R.id.recipe_publisher);
        socialScore = itemView.findViewById(R.id.recipe_social_score);
        image = itemView.findViewById(R.id.recipe_image);

        itemView.setOnClickListener(this);

    }

    //
    public void onBind(Recipe recipe){
        requestManager
                .load(recipe.getImage_url())
//                    .load(imgUrl)
                .into(image);
//        Log.d(TAG, "onBindViewHolder: " + mRecipes.get(i).getImage_url());
        // ui 값을 설정해준다.
        title.setText(recipe.getTitle());
        publisher.setText(recipe.getPublisher());
        socialScore.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

        // 어떠한 위젯이 캐쉬 되어야 하는지
        // 캐쉬하고자하는 이미지뷰
        viewPreloadSizeProvider.setView(image);
    }

    // 뷰가 클릭 되었을 때
    @Override
    public void onClick(View v) {
        // 레시피클릭 리스너를 발동시킨다. 매개변수에 클릭한 곳의 위치를 넣어준다.
        onRecipeListener.onRecipeClick(getAdapterPosition());
    }
}
