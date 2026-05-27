package id.ac.pnm.food_recipe_app.data.local

import id.ac.pnm.food_recipe_app.Food
import id.ac.pnm.food_recipe_app.data.local.entity.FoodEntity

fun Food.toEntity(): FoodEntity{
    return FoodEntity(
        id = id,
        title = title,
        desc = desc,
        image = image,
        ingredients = ingredients,
        steps = steps,
        isFavorite = isFavorite
    )
}

fun FoodEntity.toFood(): Food {
    return Food(
        id = id,
        title = title,
        desc = desc,
        image = image,
        ingredients = ingredients,
        steps = steps,
        isFavorite = isFavorite
    )
}