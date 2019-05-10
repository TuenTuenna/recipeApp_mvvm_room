package com.jeffjeong.foodrecipes.requests;

import com.google.gson.Gson;
import com.jeffjeong.foodrecipes.util.Constants;
import com.jeffjeong.foodrecipes.util.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.jeffjeong.foodrecipes.util.Constants.CONNECTION_TIMEOUT;
import static com.jeffjeong.foodrecipes.util.Constants.READ_TIMEOUT;
import static com.jeffjeong.foodrecipes.util.Constants.WRITE_TIMEOUT;

// 레트로핏 인스턴스를 제공하는 클래스
public class ServiceGenerator {

    // 레트로핏은 okHttp를 이용해서 통신을 하는 라이브러리이다.
    private static OkHttpClient client = new OkHttpClient.Builder()

            // establish connection to server 핸드쉐이킹 악수
            // 서버와의 연결 시간제한 10초
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte read from the server
            // 서버로 부터 각 바이트를 읽는 시간 제한 2초
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte sent to server
            // 서버에 보내진 각바이트의 시간 제한 2초
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            // 실패시 다시 접속시도여부
            .retryOnConnectionFailure(false)

            .build();

    // 레트로핏 빌더
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
                    .client(client)
            // 라이브데이터 콜 어답터를 부를수 있는 팩토리 클래스가 필요하다.
            .addCallAdapterFactory(new LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create());


    // 레트로핏
    private static Retrofit retrofit = retrofitBuilder.build();

    //
    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);


    // 인스턴스를 반환하는 메소드
    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }

}
