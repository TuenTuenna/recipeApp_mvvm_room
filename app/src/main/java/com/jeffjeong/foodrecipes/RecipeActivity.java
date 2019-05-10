package com.jeffjeong.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jeffjeong.foodrecipes.models.Recipe;
import com.jeffjeong.foodrecipes.util.Resource;
import com.jeffjeong.foodrecipes.viewmodels.RecipeViewModel;

// 레시피 액티비티
public class RecipeActivity extends BaseActivity {

    private static final String TAG = "테스트";

    // UI components
    private AppCompatImageView mRecipeImage;
    private TextView mRecipeTitle, mRecipeRank;
    private LinearLayout mRecipeIngredientsContainer;
    private ScrollView mScrollView;

    private RecipeViewModel mRecipeViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 레이아웃 리소스를 연결한다.
        setContentView(R.layout.activity_recipe);

        // ui 콤포넌트들에 리소스 아이디를 연결한다.
        mRecipeImage = findViewById(R.id.recipe_image);
        mRecipeTitle = findViewById(R.id.recipe_title);
        mRecipeRank = findViewById(R.id.recipe_social_score);
        mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
        mScrollView = findViewById(R.id.parent);

        // 레시피 뷰모델을 가져온다.
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);


        // 쿼리가 시작될때
        // 프로그래스바를 보여준다.
        showProgressBar(true);

        // 옵저버를 구독한다.
//        subscribeObservers();

        // 인텐트에 들어있는 데이터를 가져온다.
        getIncomingIntent();



    }

    // 들어오는 인텐트를 가져오는 메소드
    private void getIncomingIntent(){
        // "recipe" 라는 데이터가 인텐트와 함께 들어온다면
        if(getIntent().hasExtra("recipe")){
            // 레시피를 가져온다.
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            Log.d(TAG, "getIncomingIntent: "+ recipe.toString());
            // 해당 레시피 아이디의 옵저버를 등록한다.
            subscribeObservers(recipe.getRecipe_id());
        }
    }


    // 옵저버 등록 메소드
    private void subscribeObservers(final String recipeId){
        mRecipeViewModel.searchRecipeApi(recipeId).observe(this, new Observer<Resource<Recipe>>() {
            @Override
            public void onChanged(@Nullable Resource<Recipe> recipeResource) {
                // 레시피가 null 이 아니고
                if(recipeResource != null){
                    // 레시피의 데이터가 비어있지 않으면
                    if(recipeResource.data != null){
                        // 레시피의 상태에 따라
                        switch (recipeResource.status){
                            case LOADING: {
                                // 프로그래스바를 보여준다. - 베이스액티비티
                                showProgressBar(true);
                                break;
                            }

                            case ERROR: {
                                Log.e(TAG, "onChanged: status: 에러, 레시피: " + recipeResource.data.getTitle());
                                Log.e(TAG, "onChanged: 에러 메세지: " + recipeResource.message);

                                showParent();
                                // 프로그래스바를 숨긴다 - 베이스액티비티
                                showProgressBar(false);
                                // 레시피 UI를 설정한다.
                                setRecipeProperties(recipeResource.data);
                                break;
                            }

                            case SUCCESS: {
                                Log.d(TAG, "onChanged: 캐시가 갱신되었습니다.");
                                Log.d(TAG, "onChanged: status: 성공, 레시피: " + recipeResource.data.getTitle());
                                //
                                showParent();
                                // 프로그래스바를 숨긴다 - 베이스액티비티
                                showProgressBar(false);
                                // 레시피 UI를 설정한다.
                                setRecipeProperties(recipeResource.data);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    // 레시피 속성을 설정하는 메소드
    private void setRecipeProperties(Recipe recipe){
        // 레시피가 비어있지 않으면
        if(recipe != null){
            // UI
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.white_background)
                    .error(R.drawable.white_background);
            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(recipe.getImage_url())
                    .into(mRecipeImage);
            // 레시피 타이틀
            mRecipeTitle.setText(recipe.getTitle());
            mRecipeRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

            // 재료 뷰를 설정한다.
            setIngredients(recipe);
        }

    }

    // 재료를 설정하는 메소드
    private void setIngredients(Recipe recipe){
        // 재료에 있는 기존 모든 뷰를 제거
        mRecipeIngredientsContainer.removeAllViews();

        // 레시피의 재료를 설정한다.
        if(recipe.getIngredients() != null){
            for(String ingredient : recipe.getIngredients()){
                // 재료 수 만큼 텍스트뷰를 추가한다.
                TextView textView = new TextView(this);
                textView.setText(ingredient);
                textView.setTextSize(15);

                // 레이아웃 매개변수를 설정한다. width, height
                textView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                // 컨테이너에 뷰를 추가한다.
                mRecipeIngredientsContainer.addView(textView);
            }
        }
        else {
            TextView textView = new TextView(this);
            textView.setText("에러: 재료들을 가져오지 못했습니다.\n인터넷 연결을 확인해주세요.");
            textView.setTextSize(15);

            // 레이아웃 매개변수를 설정한다. width, height
            textView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            // 컨테이너에 뷰를 추가한다.
            mRecipeIngredientsContainer.addView(textView);
        }

    }


    // 부모뷰를 보여주는 메소드
    private void showParent(){
        mScrollView.setVisibility(View.VISIBLE);
    }













} // RecipeActivity 클래스
