package id.ac.pnm.food_recipe_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val desc: String,
    val image: Int,
    val ingredients: List<String>,
    val steps: List<String>,

    val isFavorite: Boolean = false
)