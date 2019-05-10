package com.jeffjeong.foodrecipes.persistence;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.jeffjeong.foodrecipes.models.Recipe;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

// 레시피 DataAccessObject 클래스
// 레시피 로컬 디비에 접근하는 클래스
@Dao
public interface RecipeDao {

    // 충돌하면 무시할것
    @Insert(onConflict = IGNORE)
    long[] insertRecipes(Recipe... recipe);

    // { id1, id2, id3.. etc }
    // 충돌되서 데이터를 삽입하지 못했다면 -1을 반환한다.
    // { -1, id2, -1,  }
    // 이렇게 하는 이유 모든 데이터 베이스를 교체 하고 싶지 않기 때문

    // 충돌되든 신경쓰지 않는다.
    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe recipe);

    // 업데이트 쿼리
    // 레시피를 업데이트한다.
    @Query("UPDATE recipes SET title = :title, publisher = :publisher, image_url = :image_url, social_rank = :social_rank " +
            "WHERE recipe_id = :recipe_id")
    void updateRecipe(String recipe_id, String title, String publisher, String image_url, float social_rank);

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query  || '%' OR ingredients LIKE '%' || :query || '%' " +
            "ORDER BY social_rank DESC LIMIT (:pageNumber * 30)")
    LiveData<List<Recipe>> searchRecipes(String query, int pageNumber);

    // 0 - 29 , PAGE 1
    // 30 - 59 , PAGE 2
    // 60 - 89 , PAGE 3
    @Query("SELECT * FROM recipes WHERE recipe_id = :recipe_id")
    LiveData<Recipe> getRecipe(String recipe_id);



}
