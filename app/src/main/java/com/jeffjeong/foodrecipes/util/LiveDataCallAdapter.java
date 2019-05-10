package com.jeffjeong.foodrecipes.util;

import android.arch.lifecycle.LiveData;

import com.jeffjeong.foodrecipes.requests.responses.ApiResponse;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

// 레트로핏 콜 객체를 라이브데이터로 변환하면 좋은점
// : 백그라운드 쓰레드를 따로 돌릴 필요가 없다. 라이브 데이터는 값이 변경되면 자동적으로 갱신되기때문에 코드가 간결해진다.
// 레트로핏 콜 객체를 라이브 데이터로 변환하는 어답터 클래스
public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    // 여러가지 api 응답들을 추가하면 된다.
    // RecipeResponse, RecipeSearchResponse
    private Type responseType;

    // 생성자 메소드
    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(final Call<R> call) {
        return new LiveData<ApiResponse<R>>() {
            @Override
            protected void onActive() {
                super.onActive();
                // TODO: 레트로핏 콜 객체를 라이브 데이터로 변환한다.
                final ApiResponse apiResponse = new ApiResponse();
                call.enqueue(new Callback<R>() {
                    @Override
                    public void onResponse(Call<R> call, Response<R> response) {
                        postValue(apiResponse.create(response));
                    }

                    @Override
                    public void onFailure(Call<R> call, Throwable t) {
                        postValue(apiResponse.create(t));
                    }
                });
            }
        };
    }

}
