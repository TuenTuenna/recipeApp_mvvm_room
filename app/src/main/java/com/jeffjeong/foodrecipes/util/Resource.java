package com.jeffjeong.foodrecipes.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// 리소스 클래스
// https://developer.android.com/jetpack/docs/guide#best-practices
// A generic class that contains data and status about loading this data.
public class Resource<T> {

    @NonNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    // Loading : 데이터를 받을때
    // Success : 네트워크 요청이 성공적일때
    // Error : 에러일때
    public enum Status { SUCCESS, ERROR, LOADING }

}
