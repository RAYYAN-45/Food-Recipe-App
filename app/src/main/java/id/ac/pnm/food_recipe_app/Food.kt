package id.ac.pnm.food_recipe_app

import java.io.Serializable

data class Food(
    val id: Int = 0,
    val title: String,
    val desc: String,
    val image: Int,
    val ingredients: List<String>,
    val steps: List<String>,

    val isFavorite: Boolean = false
) : Serializable//byte agar dapat dikirim antar Activity/Fragment.
