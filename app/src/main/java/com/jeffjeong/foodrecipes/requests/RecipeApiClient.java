package com.jeffjeong.foodrecipes.requests;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.jeffjeong.foodrecipes.AppExecutors;
import com.jeffjeong.foodrecipes.models.Recipe;
import com.jeffjeong.foodrecipes.requests.responses.RecipeResponse;
import com.jeffjeong.foodrecipes.requests.responses.RecipeSearchResponse;
import com.jeffjeong.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.jeffjeong.foodrecipes.util.Constants.NETWORK_TIMEOUT;

// 레시피 api 클라이언트 클래스
public class RecipeApiClient {


    private static final String TAG = "테스트";
    private static RecipeApiClient instance;

    // 라이브 데이터 레시피들
    // MutableLiveData 가 들어간 이유 -> 로컬 캐쉬로 혹은 서버에서 라이브 데이터를 받기 때문에 Mutable을 사용하였다.
    private MutableLiveData<List<Recipe>> mRecipes;
//    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;


    // 라이브데이터 객체
    private MutableLiveData<Recipe> mRecipe;
//    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;

    // 요청 시간 만료 여부
    private MutableLiveData<Boolean> mRecipeRequestTimeout = new MutableLiveData<>();

    // 싱글톤
    public static RecipeApiClient getInstance(){
        if(instance == null){
            instance = new RecipeApiClient();
        }
        return instance;
    }

    // 생성자 메소드
    private RecipeApiClient() {
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    // 레시피들 라이브 데이터를 가져오는 메소드
    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    // 레시피 라이브 데이터를 가져오는 메소드
    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    // 요청 시간 만료 여부를 가져오는 메소드
    public LiveData<Boolean> isRecipeRequestTimedOut() {
        return mRecipeRequestTimeout;
    }

    // 레시피들 검색 메소드
//    public void searchRecipesApi(String query, int pageNumber){
//
//        if(mRetrieveRecipesRunnable != null){
//            mRetrieveRecipesRunnable = null;
//        }
//
//        //
//        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
//
//        // 핸들러 타임아웃
//        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipesRunnable);
//
//        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
//            @Override
//            public void run() {
//
//
//                // 데이터 요청을 한지 3초가 되면 사용자에게 제한시간이 넘었다고 알려준다.
//                // 요청을 멈춘다.
//                handler.cancel(true);
//            }
//        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
//
//
//    }

//     레시피 검색 메소드
//    public void searchRecipeById(String recipeId){
//        // 러너블이 비어 있으면
//        if(mRetrieveRecipeRunnable != null){
//            // 러너블을 null로 만든다.
//            mRetrieveRecipeRunnable = null;
//        }
//
//        // 러너블 객체를 인스턴스화 한다.
//        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);
//
//        // 러너블을 실행한다.
////        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeRunnable);
//
//        // 레시피 리퀘스트 타임아웃이 아니라고 설정한다.
//        mRecipeRequestTimeout.setValue(false);
//
//        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
//            @Override
//            public void run() {
//                // TODO: 사용자에게 시간이 지났다고 알려준다.
//                mRecipeRequestTimeout.postValue(true);
//                // 3초가 지나면
//                // 요청을 취소한다.
//                handler.cancel(true);
//            }
//        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
//
//
//    }




    // 데이터를 가져오는 러너블 클래스
//    private class RetrieveRecipesRunnable implements Runnable{
//
//        private String query;
//        private int pageNumber;
//        boolean cancelRequest;
//
//        // 생성자 메소드
//        public RetrieveRecipesRunnable(String query, int pageNumber) {
//            this.query = query;
//            this.pageNumber = pageNumber;
//            cancelRequest = false;
//        }
//
//        @Override
//        public void run() {
//            // 레트로핏2 응답 객체
//            try {
//                // 서버에 쿼리문을 보낸다. 그래서 백그라운드 쓰레드에서 작업이 이루어져야한다.
////                Response response = getRecipes(query, pageNumber).execute();
//                // 사용자로부터 요청이 취소되었다면
//                if(cancelRequest){
//                    return;
//                }
//                // 응답이 ok 이면
//                if(response.code() == 200){
//                    // 데이터를 가져온다.
//                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
//                    // 첫번째 페이지 라면
//                    if(pageNumber == 1){
//                        // setValue 는 백그라운드 쓰레드 아닌거
//                        // postValue 는 백그라운드 쓰레드
//                        mRecipes.postValue(list);
//                    }
//                    else{ // 첫번째 페이지가 아니라면
//                        // 현재 리스트를 가져온다.
//                        List<Recipe> currentRecipes = mRecipes.getValue();
//                        // 현재 리스트에 추가한다.
//                        currentRecipes.addAll(list);
//                        //
//                        mRecipes.postValue(currentRecipes);
//
//                    }
//                }
//                else { // 응답이 성공적이지 못하면
//                    // 에러메세지를 보여준다.
//                    String error = response.errorBody().toString();
//                    Log.e(TAG, "run: " + error );
//                    mRecipes.postValue(null);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                mRecipes.postValue(null);
//            }
//        }
//
////        // 레시피들을 가져오는 메소드
////        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber){
////            return ServiceGenerator.getRecipeApi().searchRecipe(
////                    Constants.API_KEY,
////                    query,
////                    String.valueOf(pageNumber)
////            );
////        }
//        // 러너블 요청취소메소드
//        private void cancelRequest(){
//            Log.d(TAG, "cancelRequest: canceling the search request");
//            cancelRequest = true;
//        }
//
//
//    } // RetrieveRecipesRunnable 클래스

    // 레시피 데이터를 가져오는 러너블
//    private class RetrieveRecipeRunnable implements Runnable{
//
//        private String recipeId;
//        boolean cancelRequest;
//
//
//        // 생성자 메소드
//        public RetrieveRecipeRunnable(String recipeId) {
//            this.recipeId = recipeId;
//            cancelRequest = false;
//        }
//
//        @Override
//        public void run() {
//            // 레트로핏2 응답 객체
//            try {
//                // 서버에 쿼리문을 보낸다. 그래서 백그라운드 쓰레드에서 작업이 이루어져야한다.
//                Response response = getRecipe(recipeId).execute();
//                // 사용자로부터 요청이 취소되었다면
//                if(cancelRequest){
//                    return;
//                }
//                // 응답이 ok 이면
//                if(response.code() == 200){
//                    // 데이터를 가져온다.
//                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe();
//                        // 라이브 데이터에 넣는다. -> 라이브 데이터를 업데이트한다.
//                        mRecipe.postValue(recipe);
//                }
//                else { // 응답이 성공적이지 못하면
//                    // 에러메세지를 보여준다.
//                    String error = response.errorBody().toString();
//                    Log.e(TAG, "run: " + error );
//                    mRecipe.postValue(null);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                mRecipe.postValue(null);
//            }
//        }
//        // 레시피 하나를 가져오는 메소드
//        private Call<RecipeResponse> getRecipe(String recipeId){
//            return ServiceGenerator.getRecipeApi().getRecipe(
//                    Constants.API_KEY,
//                    recipeId
//            );
//        }
//
//        // 러너블 요청취소메소드
//        private void cancelRequest(){
//            Log.d(TAG, "cancelRequest: canceling the search request");
//            cancelRequest = true;
//        }
//
//
//    } // RetrieveRecipesRunnable 클래스


    // 리퀘스트를 취소하는 메소드
//    public void cancelRequest(){
//        // 러너블이 비어있지 않으면
//        if(mRetrieveRecipesRunnable != null){
//            // 러너블 요청을 취소한다.
//            mRetrieveRecipesRunnable.cancelRequest();
//        }
//
//    }





}



