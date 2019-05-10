package com.jeffjeong.foodrecipes.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jeffjeong.foodrecipes.models.Recipe;
import com.jeffjeong.foodrecipes.repositories.RecipeRepository;
import com.jeffjeong.foodrecipes.util.Resource;

import java.util.List;

// 레시피리스트 뷰모델
// 뷰모델 선택지가 2가지인데 ViewModel / AndroidViewModel
// application 을 사용하려면 AndroidViewModel 을 사용하면 된다.
public class RecipeListViewModel extends AndroidViewModel {

    public static final String QUERY_EXHAUSTED = "No more results";

    private static final String TAG = "RecipeListViewModel";

    //public static final int CATEGORIES = 1;
    //public static final int RECIPES = 2;
    // 위와 같은 것이다.
    public enum ViewState {CATEGORIES, RECIPES};

    // 뷰스테이트 라이브데이터
    private MutableLiveData<ViewState> viewState;

    // 레시피 미디에이터 라이브데이터
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    // 레시피 리포지토리
    private RecipeRepository recipeRepository;

    // query extras
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;
    private long requestStartTime;


    // 생성자 메소드
    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        // 레시피 리포지토리 인스턴스 가져오기 (싱글턴)
        recipeRepository = RecipeRepository.getInstance(application);
        // 뷰스테이트를 카테고리로 시작한다.
        init();

    }

    // 시작하는 메소드
    private void init(){
        // 뷰 스테이트가 비어있다면
        if(viewState == null){
            // 뷰스테이트를 카테고리로 설정해준다.
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
        //
    }

    // 뷰상태를 가져오는 메소드
    public LiveData<ViewState> getViewState(){
        return viewState;
    }

    // 레시피를 가져오는 메소드
    public LiveData<Resource<List<Recipe>>> getRecipes(){
        // 레시피를 반환한다.
        return recipes;
    }

    // 페이지 넘버를 가져오는 메소드
    public int getPageNumber(){
        return pageNumber;
    }

    // 뷰를 카테고리로 설정하는 메소드
    public void setViewCatagories(){
        // 뷰상태는 카테고리를 보고있다.
        viewState.setValue(ViewState.CATEGORIES);
    }


    // 레시피 검색 api
    public void searchRecipesApi(String query, int pageNumber){
        // 쿼리가 실행중이지 않으면
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            // 쿼리 끝남여부
            isQueryExhausted = false;
            // 검색쿼리를 실행
            executeSearch();
        }

    }

    // 다음페이지를 검색하는 메소드
    public void searchNextPage(){
        // 쿼리결과가 더있고 쿼리가 진행중이 아닐때
        if(!isQueryExhausted && !isPerformingQuery){
            // 다음페이지
            pageNumber++;
            // 검색을 시작한다.
            executeSearch();
        }
    }

    // 검색을 실행하는 메소드
    private void executeSearch(){

        // 현재 시간을 가져온다.
        requestStartTime = System.currentTimeMillis();

        // 요청 취소여부
        cancelRequest = false;

        // 쿼리검색중여부 트루
        isPerformingQuery = true;

        // 뷰상태 설정 : 레시피 목록 보고있다.
        viewState.setValue(ViewState.RECIPES);

        // 리포지토리의 메소드를 호출한다.
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);

        // recipes 는 MediatorLiveData 이기 때문에
        // 소스를 추가할 수 있다.
        // 받은 것을 레시피에 추가하고 옵저버를 설정한다.
        // recipes 에 소스가 추가되면 onChanged 메소드가 발동된다.
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {

                // 요청 취소여부가 아니면
                if(!cancelRequest){
                    // 레시피 값을 변경된 값으로 설정한다.
                    // 데이터에 반응한다.
                    if(listResource != null){
                        //
                        recipes.setValue(listResource);
                        // 응답 상태가 성공이면
                        if(listResource.status == Resource.Status.SUCCESS){
                            // 요청되기까지 얼마나 걸렸는지 확인
                            Log.d(TAG, "onChanged: 리퀘스트 타임: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " ");
                            isPerformingQuery = false;
                            //
                            if(listResource.data != null){
                                if(listResource.data.size() == 0){
                                    Log.d(TAG, "onChanged: query is exhausted");
                                    // 쿼리로 가져올 자료가 없다고 알려준다.
                                    recipes.setValue(
                                            new Resource<List<Recipe>>(
                                                    Resource.Status.ERROR,
                                                    listResource.data,
                                                    QUERY_EXHAUSTED
                                            )
                                    );
                                }
                            }
                            // 소스를 제거한다.
                            // must remove or it will keep listening to repository
                            recipes.removeSource(repositorySource);
                        } //  응답 상태가 에러이면
                        else if (listResource.status == Resource.Status.ERROR){
                            // 요청되기까지 얼마나 걸렸는지 확인
                            Log.d(TAG, "onChanged: 리퀘스트 타임: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " ");
                            // 쿼리진행중 여부를 폴스로 한다.
                            isPerformingQuery = false;
                            // 소스를 제거한다.
                            recipes.removeSource(repositorySource);
                        }
                    }
                    else {
                        // 가져온 데이터 목록이 null 이면 리포지토리 소스를 제거한다.
                        // 제거하지 않으면 계속 관찰하고 있을것이기 때문이다.
                        // 더이상 관찰할 필요가 없으면 제거하는것
                        recipes.removeSource(repositorySource);
                    }
                }
                else {
                    // 검색소스를 제거한다.
                    // 즉 하던걸 멈춘다.
                    recipes.removeSource(repositorySource);
                }
            }
        });
    }

    // 검색요청을 취소하는 메소드
    public void cancelSearchRequest(){
        // 쿼리가 진행중이면
        if(isPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: 검색요청을 취소하는 중...");
            // 요청 취소여부 true
            cancelRequest = true;
            // 쿼리진행여부 false
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }


} // RecipeListViewModel
