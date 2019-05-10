package com.jeffjeong.foodrecipes.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.jeffjeong.foodrecipes.R;
import com.jeffjeong.foodrecipes.models.Recipe;
import com.jeffjeong.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 레시피 리사이클러 어답터
public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ListPreloader.PreloadModelProvider<String>
{

    private static final String TAG = "테스트";

    // 타입
    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    // 레시피 리스트
    private List<Recipe> mRecipes;
    // 온 레시피 리스너
    private OnRecipeListener mOnRecipeListener;
    // 리퀘스트 매니져
    private RequestManager requestManager;

    // 리사이클러뷰 프리로더
    private ViewPreloadSizeProvider<String> preloadSizeProvider;

    // 생성자 메소드
    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener,
                                 RequestManager requestManager,
                                 ViewPreloadSizeProvider<String> viewPreloadSizeProvider) {
        this.mOnRecipeListener = mOnRecipeListener;
        this.requestManager = requestManager;
        this.preloadSizeProvider = viewPreloadSizeProvider;
    }

    //뷰홀더가 생성될 때
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // 뷰타입에 따라 화면에 보여준다.
        View view = null;
        switch (i){
            case RECIPE_TYPE:{
                // 리사이클러 뷰를 가져온다.
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false );
                // 레시피 뷰홀더를 반환한다.
                return new RecipeViewHolder(view, mOnRecipeListener, requestManager, preloadSizeProvider);
            }

            case LOADING_TYPE:{
                // 로딩 뷰를 가져온다.
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false );
                // 로딩 뷰홀더를 반환한다.
                return new LoadingViewHolder(view);
            }

            case EXHAUSTED_TYPE:{
                // 더이상 검색할 내용이 없는 뷰를 가져온다.
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false );
                // 더이상 검색할 내용이 없는 뷰홀더를 반환한다.
                return new SearchExhaustedViewHolder(view);
            }

            case CATEGORY_TYPE:{
                // 로딩 뷰를 가져온다.
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_category_list_item, viewGroup, false );
                // 로딩 뷰홀더를 반환한다.
                return new CategoryViewHolder(view, mOnRecipeListener, requestManager);
            }
            default:{
                // 리사이클러 뷰를 가져온다.
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false );
                // 레시피 뷰홀더를 반환한다.
                return new RecipeViewHolder(view, mOnRecipeListener, requestManager, preloadSizeProvider);
            }

        }





    }

    // 뷰 홀더가 묶일때
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        //
        int itemViewType = getItemViewType(i);
        if(itemViewType == RECIPE_TYPE){
           //
            ((RecipeViewHolder)viewHolder).onBind(mRecipes.get(i));
        }
        else if(itemViewType == CATEGORY_TYPE){
           //
            ((CategoryViewHolder)viewHolder).onBind(mRecipes.get(i));
        }


    }

    // 아이템 뷰타입을 가져오는 메소드
    // getItemViewType 은 해당 포지션이 매개변수로 들어온다.
    // 해당 아이템의 타이틀로 뷰타입을 정해주자
    @Override
    public int getItemViewType(int position) {

        // 소셜 랭크가 없으면
        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        // 해당 레시피의 타이틀이 LOADING... 이면 로딩타입으로 아이템 뷰타입을 변경한다.
        else if(mRecipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        // 해당 레시피의 타이틀이 EXHAUSTED... 이면 검색 자료 없음 타입으로 아이템 뷰타입을 변경한다.
        else if(mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }
        else {
            return RECIPE_TYPE;
        }
    }

    // 검색요청을 하는 중에 보여주는 로딩
    // 로딩만을 보여주는 메소드
    public void displayOnlyLoading(){
        // 레시피목록을 제거한다.
        clearRecipesList();

        Recipe recipe = new Recipe();
        // 타이틀을 기준으로 뷰타입을 정한다.
        recipe.setTitle("LOADING...");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    // 레시피 목록을 제거하는 메소드
    private void clearRecipesList(){
        // 레시피가 null 이면
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        else {
            // 레시피를 제거한다.
            mRecipes.clear();
        }
        // 데이터가 변경되었다고 알려준다.
        notifyDataSetChanged();
    }


    // 쿼리 검색자료 없음을 설정하는 메소드
    public void setQueryExhausted(){
        // 로딩을 숨긴다.
        hideLoading();
        // 레시피 하나를 인스턴스화 하고 검색자료 없음으로 설정한다.
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setTitle("EXHAUSTED...");
        // 레시피 리스트에 추가한다.
        mRecipes.add(exhaustedRecipe);
        // 어답터에 자료가 갱신되었다고 알려준다.
        notifyDataSetChanged();

    }

    // 로딩을 숨기는 메소드
    public void hideLoading(){
        // 로딩중이라면
        if(isLoading()){
            // 처음 레시피의 타이틀이 LOADING... 이면 - 레시피 검색 로딩
            if(mRecipes.get(0).getTitle().equals("LOADING...")){
                //해당 레시피를 제거한다. 즉 로딩 애니메이션 레시피를 제거한다.
                mRecipes.remove(0);
            }
            // 마지막 레시피의 타이틀의 LOADING... 이면 - 페이징 로딩
            else if(mRecipes.get(mRecipes.size()-1).equals("LOADING...")){
                //해당 레시피를 제거한다. 즉 로딩 애니메이션 레시피를 제거한다.
                mRecipes.remove(mRecipes.size()-1);
            }
            // 어답터에 자료가 갱신되었다고 알려준다.
            notifyDataSetChanged();
        }

    }

    // 페이징 로딩
    // 로딩을 화면에 보여주는 메소드
    public void displayLoading(){

        // 레시피들이 null이라면
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        // 로딩중이 아니면
        if(!isLoading()){

            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING...");
            // loading at bottom of screen
            mRecipes.add(recipe);

            notifyDataSetChanged();
        }

    }

    // 카테고리를 화면에 보여주는 메소드
    public void displaySearchCategories(){
        List<Recipe> categories = new ArrayList<>();
        //
        for(int i = 0; i < Constants.DEFAULT_SEARCH_CATEGORIES.length; i++){
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes = categories;
        // 데이터가 변경되었다고 알려준다.
        notifyDataSetChanged();
    }

    // 로딩 여부를 반환하는 메소드
    private boolean isLoading(){
        // 레시피가 비어있지 않고
        if(mRecipes != null){
            // 레시피가 있으면
            if(mRecipes.size() > 0){
                // 마지막 레시피의 타이틀이 "LOADING..." 이라면
                if(mRecipes.get(mRecipes.size() - 1).getTitle().equals("LOADING...")){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        if(mRecipes != null){
            return mRecipes.size();
        }
        return 0;
    }

    // 레시피를 설정하는 메소드
    public void setRecipes(List<Recipe> recipes){
        mRecipes = recipes;
        // 데이터가 변경되었다고 알려준다.
        notifyDataSetChanged();
    }

    //
    public Recipe getSelectedRecipe(int position){
        // 레시피들이 비어있지 않고
        if(mRecipes != null){
            // 레시피들이 있다면
            if(mRecipes.size() > 0){
                return mRecipes.get(position);
            }
        }
        return null;
    }


    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        // 글라이드 프리로더는 url을 캐싱하는것
        String url = mRecipes.get(position).getImage_url();

        // 스트링이 비었으면
        if(TextUtils.isEmpty(url)){
            // 빈 어레이 리스트를 반환한다.
            return Collections.emptyList();
        }
        // url이 들어간 싱글턴 어레이리스트를 반환한다.
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        // 캐시되어있는 해당 아이템을 반환한다.
        return requestManager.load(item);
    }
}
