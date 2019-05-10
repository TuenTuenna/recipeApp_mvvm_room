package com.jeffjeong.foodrecipes.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.jeffjeong.foodrecipes.models.Recipe;
import com.jeffjeong.foodrecipes.util.Constants;

// 레시피 디비 추상클래스 (로컬)
@Database(entities = {Recipe.class}, version = 1)
// annotation 으로 타입컨버터를 뭐쓰는지 알려준다.
// 이로써 배열을 sql라이트에 저장가능하다.
@TypeConverters({Converters.class})
public abstract class RecipeDatabase extends RoomDatabase {


    // 로컬 디비 이름
    public static final String DATABASE_NAME = "recipes_db";
    private static RecipeDatabase instance;

    // 싱글턴 패턴
    // 인스턴스 가져온는 메소드
    public static RecipeDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RecipeDatabase.class,
                    DATABASE_NAME
            ).build();
        }

        return instance;
    }

    // 레시피 dao
    public abstract RecipeDao getRecipeDao();


}
