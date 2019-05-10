package com.jeffjeong.foodrecipes.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.jeffjeong.foodrecipes.AppExecutors;
import com.jeffjeong.foodrecipes.requests.responses.ApiResponse;

// abstract 클래스 (추상클래스) 는 스스로 인스턴스화 하지 못하고
// 오로지 extends 로만 사용 될수 있다.
// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private static final String TAG = "NetworkBoundResource";

    // 앱 실행자들
    private AppExecutors appExecutors;

    // 옵저버를 안에 할당하고 다른 소스들에 설정하기 때문에 Mediator 라이브 데이터 여야한다.
    // 중재자 라이브데이터 -> ui 에 관찰될 데이터
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    // 생성자 메소드
    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    // 시작하는 메소드
    private void init(){
        // update LiveData for Loading status
        // 상태를 로딩하기 위해 라이브 데이터를 갱신한다.
        // 다음에 뭐가 오는지 확인하기 위해 results 캐시 객체를 로딩으로 설정한다.
        // 사용자에게 로딩중을 알려주게된다.
        results.setValue((Resource<CacheObject>) Resource.loading(null));

        // observe LiveData source from local db
        // 라이브데이터 소스를 로컬 디비에서 관찰한다.
        // 1) 로컬디비를 관찰한다. observe local db
        final LiveData<CacheObject> dbSource = loadFromDb();

        // results 에 소스를 추가한다.
        // 소스에 디비 소스가 추가되면 onChanged 메소드가 발동된다.
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {
                // 디비 소스를 제거하지 않으면 계속 듣는다.
                // 디비 소스를 제거한다.
                // 즉 캐시를 보지않는다.
                results.removeSource(dbSource);

                // 디비 캐시 갱신여부가 트루이면
                if(shouldFetch(cacheObject)){
                    // 네트워크로 부터 데이터를 가져온다.
                    // 2) 조건을 충족하면 네트워크에 쿼리문을 날린다. if <condition/> query the network
                    fetchFromNetwork(dbSource);
                }
                else { // 디비 캐시 갱신여부가 폴스이면
                    // 캐시를 가져온다.
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            // 새값으로 설정한다.
                            // 로딩이 성공적이면 그것을 사용자에게 알려준다.
                            // status 가 success으로 바뀐다.
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });

    }


    /**
     * 1) 로컬디비를 관찰한다. observe local db
     * 2) 조건을 충족하면 네트워크에 쿼리문을 날린다. if <condition/> query the network
     * 3) 로컬 디비 관찰을 멈춘다. stop observing the local db
     * 4) 로컬 디비에 새 데이터를 넣는다. insert new data into local db
     * 5) 네트워크로 부터 갱신된 데이터를 보기 위해 로컬 디비를 다시 관찰하기 시작한다. begin observing local db again to see the refreshed data from network
     * @param dbSource
     */
    // 네크워크로 부터 가져오는 메소드
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource){
        Log.d(TAG, "fetchFromNetwork: 호출");
        // update LiveData for loading status
        // 상태를 로딩하기 위해 라이브 데이터를 업데이트한다.
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {
                // 로딩중이라고 알려준다.
                setValue(Resource.loading(cacheObject));
            }
        });

        // 캐시를 보고 로딩상태를 보여준다.

        // api 응답
        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();


        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<RequestObject> requestObjectApiResponse) {
                // 지금껏 사용한 소스를 제거한다.
                results.removeSource(dbSource);
                // 응답 소스를 제거한다.
                results.removeSource(apiResponse);

                // 다양한 경우를 핸들링
                // Success, error, loading
                /*
                 *  3 cases:
                 *      1) ApiSuccessResponse
                 *      2) ApiEmptyResponse
                 *      3) ApiErrorResponse
                 */

                // 매개변수로 들어온 리퀘스트객체 인스턴스가 api 성공 응답 이면
                if(requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse){
                    Log.d(TAG, "onChanged: ApiSuccessResponse.");

                    // 백그라운드 쓰레드 워커
                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            // 백그라운드에서 작동하는 메소드
                            // 서버응답을 로컬 디비에 저장한다.
                            // 응답 바디를 가져와서 그것을 로컬 디비에 저장한다.
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse)requestObjectApiResponse));

                            // 메인쓰레드를 따로 뺀 이유는 postValue 대신 setValue를 사용하기 위해
                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    //
                                    results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                        @Override
                                        public void onChanged(@Nullable CacheObject cacheObject) {
                                            // 리소스를 성공적으로 설정한다.
                                            setValue(Resource.success(cacheObject));
                                        }
                                    });
                                }
                            });

                        }
                    });

                } // 매개변수로 들어온 리퀘스트객체 인스턴스가 api 빈 응답 이면
                else if(requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse){
                    Log.d(TAG, "onChanged: ApiEmptyResponse.");
                    // 메인 쓰레드를 실행한다.
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(@Nullable CacheObject cacheObject) {
                                    // 응답은 성공적이지만 반환되는 것은 없다.
                                    setValue(Resource.success(cacheObject));
                                }
                            });
                        }
                    });


                } // 매개변수로 들어온 리퀘스트객체 인스턴스가 api 에러 응답 이면
                else if(requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse){
                    Log.d(TAG, "onChanged: ApiErrorResponse.");
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(
                                    Resource.error(
                                            ((ApiResponse.ApiErrorResponse)requestObjectApiResponse).getErrorMessage(), cacheObject)
                            );
                        }
                    });

                }


            }
        });

    }

    // 응답을 진행하는 메소드
    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response){
        // api 응답으로 부터 바디를 가져온다.
        return (CacheObject) response.getBody();
    }


    // 값을 설정하는 메소드
    private void setValue(Resource<CacheObject> newValue){
        // 매개변수로 들어온 새값이 현재값과 다르다면
        if(results.getValue() != newValue){
            // 현재 값을 새값으로 설정한다.
            results.setValue(newValue);
            // postValue 를 바로 실행 안된다.
            // setValue 는 즉각 실행된다.
        }
    }

    // API 응답 데이터 결과를 디비에 저장하기 위해 불려지는 메소드
    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    // 디비 캐시를 갱신할지 정하는 메소드
    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    // 데이터를 디비에서 가져오는 메소드
    // Called to get the cached data from the database.
    @NonNull @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    // 레트로핏 Call 객체를 라이브 데이터로 변환시키는 메소드
    // 이렇게 함으로써 러너블이나 백그라운드 쓰레드가 필요없게된다.
    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // 데이터를 UI 에 반환하는 메소드
    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return results;
    }


}