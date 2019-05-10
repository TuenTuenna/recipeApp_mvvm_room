package com.jeffjeong.foodrecipes.persistence;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

// sql라이트는 특정 데이터 타입을 저장할수 없다. 배열 이 그중 하나
// 이 클래스는 sql 라이트로 저장할수 없는 데이터 타입을 저장가능한 데이터 타입으로 변환하는 클래스이다.
// 컨버터 클래스
public class Converters {

    // String을 제이슨 배열로 변환하는 메소드
    @TypeConverter
    public static String[] fromString(String value){
        Type listType = new TypeToken<String[]>(){}.getType();
        return new Gson().fromJson(value, listType);
    }


    // String 배열을 제이슨 객체로 변환하는 메소드
    @TypeConverter
    public static String fromArrayList(String[] list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    // 다른 자료형을 컨버트 하려거든 구글링 하면 나온다.


}



