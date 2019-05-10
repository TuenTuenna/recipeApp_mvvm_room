package com.jeffjeong.foodrecipes.requests.responses;

import java.io.IOException;

import retrofit2.Response;

// 응답 클래스
// <T> 어떤 타입이든
public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error){
        //
        return new ApiErrorResponse<>(!error.getMessage().equals("") ? error.getMessage() : "알수없는 에러\n인터넷 연결을 확인해주세요");
    }

    // 오버로딩
    public ApiResponse<T> create(Response<T> response){
        // 응답이 성공적이라면
        if(response.isSuccessful()){
            // 응답의 바디를 가져온다.
            T body = response.body();

            // 응답바디가 레시피검색의 인스턴스라면
            if(body instanceof RecipeSearchResponse){
                // API 키가 만료되지 않았다면
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeSearchResponse)body)){
                    String errorMsg = "Api 키가 유효하지 않거나 만료되었습니다.";
                    return new ApiErrorResponse<>(errorMsg);
                }
            }

            // 응답바디가 레시피검색의 인스턴스라면
            if(body instanceof RecipeResponse){
                // API 키가 만료되지 않았다면
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeResponse)body)){
                    String errorMsg = "Api 키가 유효하지 않거나 만료되었습니다.";
                    return new ApiErrorResponse<>(errorMsg);
                }
            }

            // 응답이 없거나 비어있다면
            if(body == null || response.code() == 204){ // 204 is empty response code
                return new ApiEmptyResponse<>();
            }
            else {
                return new ApiSuccessResponse<>(body);
            }
        }
        else { // 성공적이지 못하면
            String errorMsg = "";
            try{
                errorMsg = response.errorBody().string();
            }catch (IOException e) {
                e.printStackTrace();
                errorMsg = response.message();
            }
            // 에러 메세지를 반환한다.
            return new ApiErrorResponse<>(errorMsg);
        }
    }

    //응답 성공 클래스
    public class ApiSuccessResponse<T> extends ApiResponse<T>{

        // 응답 바디
        private T body;

        // 생성자 메소드
        ApiSuccessResponse(T body) {
            this.body = body;
        }

        // getter 메소드
        public T getBody(){
            return body;
        }
    }

    // 응답 에러 클래스
    public class ApiErrorResponse<T> extends ApiResponse<T>{

        private String errorMessage;

        //생성자 메소드
        ApiErrorResponse(String errrorMessage){
            this.errorMessage = errrorMessage;
        }

        // getter 메소드
        public String getErrorMessage(){
            return errorMessage;
        }

    }

    //
    public class ApiEmptyResponse<T> extends ApiResponse<T>{};







}
