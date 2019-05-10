package com.jeffjeong.foodrecipes.util;

import android.arch.lifecycle.LiveData;

import com.jeffjeong.foodrecipes.requests.responses.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

// 서비스제너레이터 클래스에서 레트로핏 빌더객체를 인스턴스화할때 레트로핏 콜 어답터를 제공할수 있는 팩토리 클래스
public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    /**
     * This method performs a number of checks and then returns the Response type for the Retrofit requests.
     * 이 메소드는 몇가지 확인을 하고
     * 레트로핏 요청에 대한 응답 타입을 반환한다.
     * (@bodyType is the ResponseType. It can be RecipeResponse or RecipeSearchResponse)
     * 응답은 레시피 응답이거나 레시피 검색 응답 일수 있다. (커스텀) 서버통신
     *
     *  CHECK #1) 반환타입은 라이브데이터이다. returnType returns LiveData
     *  CHECK #2) 라이브데이터 제레릭은 ApiResponse.class에 속해있다. Type LiveData<T> is of ApiResponse.class
     *  CHECK #3) ApiResponse 가 매개변수가 있도록 해야한다. 즉 ApiResponse<T> 존재한다. Make sure ApiResponse is parameterized. AKA: ApiResponse<T> exists.
     *
     */

    // 겟
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        // Check #1
        // Make sure the CallAdapter is returning a type of LiveData
        // 콜어답터가 라이브데이터를 꼭 반환하도록 할 것
        // 콜 어답터의 데이터가 라이브 데이터가 아니라면
        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return null;
        }

        // Check #2
        // Type that LiveData is wrapping
        // 라이브 데이터가 감싸고 있는 자료형
        // 라이브 데이터 속에 있는 자료형을 가져온다.
        Type observableType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType)returnType);

        // public LiveData<ApiResponse<R>> adapt(final Call<R> call)
        // 진짜 데이터가 뭔지 확인한다.
        // Check if it's of Type ApiResponse
        // ApiResponse 타입인지 확인한다.
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);

        // 가져온 자료형이 ApiResponse 가 아니라면
        if(rawObservableType != ApiResponse.class){
            // 자료형은 반드시 규정된 리소스여야 한다고 알려준다.
            throw new IllegalArgumentException("자료형은 반드시 규정된 리소스여야 합니다.");
        }

        // Check #3
        // Check if ApiResponse is parameterized. AKA: Does ApiResponse<T>
        // ApiResponse 에 매개변수가 있는지 여부 체크. 즉 ApiResponse가 T를 가지고 있느냐 (must wrap around T)
        // FYI: T is either RecipeResponse or T will be a RecipeSearchResponse
        // T 는 레시피 응답일수도 레시피 검색 응답일수도 있다. 커스텀 http통신
        // 콜 어답터의 자료형에 매개변수가 없으면
        if(!(observableType instanceof ParameterizedType)){
            // 리소스에 매개변수가 있어야 한다고 알려준다.
            throw new IllegalArgumentException("리소스에 매개변수가 있어야 합니다.");
        }
        // 응답 바디를 가져온다.
        Type bodyType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) observableType);

        //
        return new LiveDataCallAdapter<Type>(bodyType);
    }
}
