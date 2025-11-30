package id.ac.pnm.food_recipe_app

import java.io.Serializable

data class Food(
    val id: Int,
    val title: String,
    val desc: String,
    val image: Int,
    val ingredients: List<String>,
    val steps: List<String>
) : Serializable
