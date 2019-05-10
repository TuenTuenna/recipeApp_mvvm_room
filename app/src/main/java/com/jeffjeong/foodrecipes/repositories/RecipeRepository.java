package com.jeffjeong.foodrecipes.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jeffjeong.foodrecipes.AppExecutors;
import com.jeffjeong.foodrecipes.models.Recipe;
import com.jeffjeong.foodrecipes.persistence.RecipeDao;
import com.jeffjeong.foodrecipes.persistence.RecipeDatabase;
import com.jeffjeong.foodrecipes.requests.ServiceGenerator;
import com.jeffjeong.foodrecipes.requests.responses.ApiResponse;
import com.jeffjeong.foodrecipes.requests.responses.RecipeResponse;
import com.jeffjeong.foodrecipes.requests.responses.RecipeSearchResponse;
import com.jeffjeong.foodrecipes.util.Constants;
import com.jeffjeong.foodrecipes.util.NetworkBoundResource;
import com.jeffjeong.foodrecipes.util.Resource;

import java.util.List;

// 레시피 리포지토리 클래스
public class RecipeRepository {

    private static final String TAG = "RecipeRepository";

    private static RecipeRepository instance;

    private RecipeDao recipeDao;

    // 싱글턴 패턴 적용 인스턴스가져오기
    // dao 를 인스턴스화 할때  context를 요구함으로 매개변수에 넣어준다.
    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    // 생성자 메소드
    private RecipeRepository(Context context) {
        // 레시피 dao를 인스턴스화한다.
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    // api search
    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()){

            // 레트로핏 응답을 로컬디비에 캐시로 넣는다.
            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {
                // api키가 만료되면 레시피 리스트는 null일것이다.
                // 레트로핏 응답이 null이 아니면
                if(item.getRecipes() != null){

                    // 응답를 배열에 담는다.
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];

                    int index = 0;
                    for(long rowId: recipeDao.insertRecipes((Recipe[])(item.getRecipes().toArray(recipes)))){
                        // 아이디가 -1 이면
                        if(rowId == -1){
                            Log.d(TAG, "saveCallResult: 충돌... 이 레시피는 이미 캐시에 들어있습니다. ");
                            // 레시피가 이미 존재한다면 재료 혹은 타임스탬프를 설정하기 싫다.
                            // 그것들은 지워질 것이다.
                            // 업데이트
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }

                }
            }

            // 캐시 갱신여부
            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return true;
            }

            // 로컬디비에서 가져온다.
            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                // 로컬디비에서 검색한다.
                return recipeDao.searchRecipes(query, pageNumber);
            }


            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi()
                        .searchRecipe(
                                Constants.API_KEY,
                                query,
                                String.valueOf(pageNumber)
                        );
            }
        }.getAsLiveData();
    }

    // 레시피 아이디로 검색 api 메소드
    public LiveData<Resource<Recipe>> searchRecipeApi(final String recipeId){
        // 캐시객체, 네트워크요청객체,
        return new NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors.getInstance()){

            // 서버응답을 로컬디비에 저장한다.
            @Override
            protected void saveCallResult(@NonNull RecipeResponse item) {
                // api 키가 만료되었다면 item은 null 일 것이다.
                if(item.getRecipe() != null){
                    // 레시피의 시간을 현재시간으로 설정한다.
                    item.getRecipe().setTimestamp((int)(System.currentTimeMillis() / 1000));
                    // 로컬디비에 레시피를 넣는다.
                    recipeDao.insertRecipe(item.getRecipe());
                }
            }

            // 레시피 갱신여부
            @Override
            protected boolean shouldFetch(@Nullable Recipe data) {
                Log.d(TAG, "shouldFetch: recipe: " + data.toString());
                // 현재시간을 가져온다.
                // 밀리 세컨즈 이니까 1000 으로 나눠준다. 초 단위로 가져온다.
                int currentTime = (int)(System.currentTimeMillis() / 1000);
                Log.d(TAG, "shouldFetch: 현재시간: " + currentTime);
                // 기존시간을 가져온다.
                int lastRefresh = data.getTimestamp();
                Log.d(TAG, "shouldFetch: 마지막 새로고침: " + lastRefresh);
                Log.d(TAG, "shouldFetch: it's been " + ((currentTime - lastRefresh) / 60 / 60 / 24) + " days since this recipe was refreshed");

                // 갱신시간을 넘기면
                if((currentTime - data.getTimestamp() >= Constants.RECIPE_REFRESH_TIME)){
                    // 갱신한다.
                    Log.d(TAG, "shouldFetch: 레시피를 갱신해야합니까? " +  true);
                    return true;
                }

                Log.d(TAG, "shouldFetch: 레시피를 갱신해야합니까? " +  false);
                return false;
            }

            // 로컬 디비에서 가져온다
            @NonNull
            @Override
            protected LiveData<Recipe> loadFromDb() {
                // 레시피 아이디로 가져온다.
                return recipeDao.getRecipe(recipeId);
            }

            // 레트로핏 콜을 만든다.
            // 서버에 보내는 메소드
            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeResponse>> createCall() {
                // 서비스 제너레이터에서 RecipeApi 인스턴스를 부른다.
                // https://www.food2fork.com/api/get?key=YOUR_API_KEY&rId=35382
                return ServiceGenerator.getRecipeApi().getRecipe(
                        Constants.API_KEY,
                        recipeId
                );
            }
        }.getAsLiveData(); // 라이브데이터로 반환
    }

}
