package com.jeffjeong.foodrecipes;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// 앱 실행 클래스
public class AppExecutors {

    // 앱실행자들
    private static AppExecutors instance;

    // 싱글톤 적용 인스턴스 가져오는 메소드
    public static AppExecutors getInstance(){
        if(instance == null){
            instance = new AppExecutors();
        }
        return instance;
    }

    // 백그라운드 쓰레드
    private final Executor mDiskIO = Executors.newSingleThreadExecutor();

    // 백그라운드 쓰레드에 있다면 정보를 메인쓰레드에 보내는 실행자
    private final Executor mMainThreadExecutor = new MainThreadExecutor();

    //
    public Executor diskIO(){
        return mDiskIO;
    }

    //
    public Executor mainThread(){
        return mMainThreadExecutor;
    }


    // 메인쓰레드 실행자 클래스
    // 메인쓰레드에 포스팅 하는 클래스
    private static class MainThreadExecutor implements Executor {

        // 메인 쓰레드 핸들러
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            //
            mainThreadHandler.post(command);
        }


    }


}

