package com.jeffjeong.foodrecipes.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jeffjeong.foodrecipes.R;
import com.jeffjeong.foodrecipes.models.Recipe;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    CircleImageView categoryImage;
    TextView categoryTitle;
    OnRecipeListener listener;

    // 글라이드 리퀘스트 매니져
    RequestManager requestManager;

    // 생성자 메소드
    public CategoryViewHolder(@NonNull View itemView,
                              OnRecipeListener listener,
                              RequestManager requestManager) {
        super(itemView);

        this.requestManager = requestManager;
        this.listener = listener;
        categoryImage = itemView.findViewById(R.id.category_image);
        categoryTitle = itemView.findViewById(R.id.category_title);

        // 온클릭리스너를 설정한다.
        itemView.setOnClickListener(this);
    }

    // 뷰를 묶어주는 메소드
    public void onBind(Recipe recipe){

        // 이미지 가져오기
        Uri path = Uri.parse("android.resource://com.jeffjeong.foodrecipes/drawable/" + recipe.getImage_url());
        requestManager
                .load(path)
                .into(categoryImage);

//        Log.d(TAG, "onBindViewHolder: " + mRecipes.get(i).getImage_url());

        // ui 값을 설정해준다.
        categoryTitle.setText(recipe.getTitle());

    }


    // 해당 아이템이 클릭되었을때
    @Override
    public void onClick(View v) {
        // 카테고리가 클릭되었다고 알려준다.
        listener.onCategoryClick(categoryTitle.getText().toString());
    }


}
