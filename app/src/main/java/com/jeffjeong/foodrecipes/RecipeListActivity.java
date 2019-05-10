package com.jeffjeong.foodrecipes;


import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.jeffjeong.foodrecipes.adapters.OnRecipeListener;
import com.jeffjeong.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.jeffjeong.foodrecipes.models.Recipe;

import com.jeffjeong.foodrecipes.util.Resource;
import com.jeffjeong.foodrecipes.util.Testing;
import com.jeffjeong.foodrecipes.util.VerticalSpacingItemDecorator;
import com.jeffjeong.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

import static com.jeffjeong.foodrecipes.viewmodels.RecipeListViewModel.QUERY_EXHAUSTED;


// 베이스 액티비티를 상속하는 레시피 리스트 액티비티 클래스
public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    // 뷰모델
    private RecipeListViewModel mRecipeListViewModel;

    // 리사이클러뷰
    private RecyclerView mRecyclerView;
    // 리사이클러뷰 어답터
    private RecipeRecyclerAdapter mAdapter;

    private SearchView mSearchView;

    // mvvm 은 모든 뷰모델에 변화가 있을때 마다 알수가 있다. 관찰된다. observed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        // 리사이클러뷰 리소스 아이디 설정
        mRecyclerView = (RecyclerView) findViewById(R.id.recipe_list);

        mSearchView = findViewById(R.id.search_view);

        // 뷰모델을 인스턴스화한다.
        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);


        // 리사이클러뷰를 시작한다.
        initRecyclerView();

        // 옵저버를 등록한다.
        // 라이브 데이터에 변경사항이 생기면 즉 get/post/put/patch/delete 등이 발생시
        subscribeObservers();

        // 서치뷰를 시작한다.
        initSearchView();


        // 서포트 액션바를 설정한다.
        // 매개변수로 커스텀 툴바를 넣어준다.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


    }


    // 옵저버를 구독하는 메소드
    private void subscribeObservers(){

        // 뷰모델에 레시피를 가져온다.
        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                // 리스트 응답이 null이 아니면
                if(listResource != null){
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    // 응답 데이터가 비어있지 않으면
                    if(listResource.data != null){
                        // 응답 데이터의 상태에 따라
                        switch (listResource.status){
                            case LOADING:{
                                // 레시피 리스트 뷰 모델의 페이지 넘버가 1보다 크면
                                if(mRecipeListViewModel.getPageNumber() > 1){
                                    // 페이지 로딩을 보여준다.
                                    mAdapter.displayLoading();
                                }
                                else { // 페이지 넘버가 1이면
                                    // 로딩만을 보여준다.
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            }

                            case ERROR:{
                                Log.e(TAG, "onChanged: cannot refresh the cache.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size());
                                // 로딩을 숨긴다.
                                mAdapter.hideLoading();
                                // 어답터에 레시피를 설정한다.
                                mAdapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();

                                // 응답메세지가 쿼리결과 더이상 없음이면
                                if(listResource.message.equals(QUERY_EXHAUSTED)){
                                    // 어답터에 쿼리결과가 더 없다고 설정한다.
                                    mAdapter.setQueryExhausted();
                                }

                                break;
                            }

                            case SUCCESS:{

                                Log.d(TAG, "onChanged: 캐시가 최신입니다.");
                                Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                // 로딩을 숨긴다.
                                mAdapter.hideLoading();
                                // 레시피를 설정한다.
                                mAdapter.setRecipes(listResource.data);

                                break;
                            }
                        }

                    }


                }
            }
        });

        // 리스트 뷰모델에서 뷰상태를 가져온다.
        // 옵저버를 설정한다.
        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable RecipeListViewModel.ViewState viewState) {
                // 뷰상태가 비어있지 않으면
                if(viewState != null){
                    switch (viewState){
                        case RECIPES:{
                            // 레시피는 다른 옵저버에 의해서 자동적으로 보여지게 될것이다.

                            break;
                        }
                        case CATEGORIES:{
                            // 검색 카테고리 목록을 보여준다.
                            displaySearchCategories();
                            break;
                        }
                    }
                }

            }
        });


    }

    // 글라이드를 시작하는 메소드
    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    // 레시피 검색 api 메소드
    private void searchRecipesApi(String query){

        // 리사이클러뷰를 처음으로 보낸다.
        mRecyclerView.smoothScrollToPosition(0);

        // 리스트뷰 모델 레시피리스트를 검색한다.
        mRecipeListViewModel.searchRecipesApi(query, 1);

        // 서치뷰의 포커스를 없앤다.
        mSearchView.clearFocus();

    }


    // 리사이클러뷰를 시작하는 메소드
    private void initRecyclerView(){

        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<>();

        // 어답터를 설정한다.
        mAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloader);
        // 아이템 데코레이터를 리사이클러뷰에 붙인다.
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);

        // 리사이클러뷰에 리니어 레이아웃 매니져를 설정한다.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 글라이드 프리로더를 설정한다. 한번에 30개 캐싱
        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(Glide.with(this),
                mAdapter,
                viewPreloader,
                30);

        // 설정한 프리로더를 스크롤 리스너에 붙인다.
        mRecyclerView.addOnScrollListener(preloader);


        // 리사이클러뷰에 스크롤 리스너를 붙인다.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 리사이클러뷰를 수직으로 이동하지 못하면 즉 맨 아래이다.
                if(!mRecyclerView.canScrollVertically(1)
                        // 그리고 리스트뷰 모델의 뷰 상태가 레시피 리스트이면
                        && mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.RECIPES){

                    // 리스트 뷰 모델의 다음페이지를 검색한다.
                    mRecipeListViewModel.searchNextPage();
                }

            }
        });

        // 리사이클러뷰에 어답터를 설정한다.
        mRecyclerView.setAdapter(mAdapter);
    }


    // 서치뷰를 시작하는 메소드
    private void initSearchView(){

        // 서치뷰에 쿼리 텍스트 리스너를 설정한다.
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 쿼리문자를 보냈을때
            @Override
            public boolean onQueryTextSubmit(String s) {

                // 레시피를 검색하는 쿼리를 날린다.
                searchRecipesApi(s);

                return false;
            }

            // 쿼리문자가 써질때
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }



    // 레시피가 클릭되었을때
    @Override
    public void onRecipeClick(int position) {
        // 해당 레시피의 아이디를 알아야한다.
        // 레시피 액티비티가 열린다.
        Intent intent = new Intent(this, RecipeActivity.class);
        // 넘기는 인텐트에 선택된 위치의 레시피객체를 넘긴다.
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        // 인텐트를 넘겨 액티비티를 시작한다.
        startActivity(intent);

    }

    // 카테고리가 클릭되었을때
    @Override
    public void onCategoryClick(String category) {
        // 해당 카테고리의 레시피를 검색하는 쿼리를 날린다.
        searchRecipesApi(category);
    }

    // 검색카테고리를 보여주는 메소드
    private void displaySearchCategories(){
        // 어답터에 검색 카테고리를 보여준다.
        mAdapter.displaySearchCategories();
    }


    // 뒤로가기 버튼이 클릭되었을때
    @Override
    public void onBackPressed() {
        // 리스트뷰 모델의 뷰 상태가 카테고리를 보고있는 상태이면
        if(mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.CATEGORIES){
            // 뒤로가기버튼이 작동된다.
            super.onBackPressed();
        }
        else {
            // 검색 요청을 취소한다.
            mRecipeListViewModel.cancelSearchRequest();
            // 뷰 카테고리를 설정한다.
            mRecipeListViewModel.setViewCatagories();
        }
    }

    // 메뉴 옵션 아이템이 선택되어졌을때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // 선택되어진 아이템의 아이디가 액션 카테고리라면
        if(item.getItemId() == R.id.action_categories){
            // 검색 카테고리를 보여준다.
            displaySearchCategories();
        }



        return super.onOptionsItemSelected(item);
    }

    // 옵션 메뉴가 생성될때
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
