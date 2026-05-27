package id.ac.pnm.food_recipe_app.data.local.dao

import androidx.room.*
import id.ac.pnm.food_recipe_app.data.local.entity.FoodEntity

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)

    @Query("SELECT * FROM foods")
    suspend fun getAllFoods(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE isFavorite = 1")
    suspend fun getFavoriteFoods(): List<FoodEntity>

    @Update
    suspend fun updateFood(food: FoodEntity)

    @Query("UPDATE foods SET isFavorite = :favorite WHERE id = :foodId")
    suspend fun updateFavorite(foodId: Int, favorite: Boolean)

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun getCount(): Int
}