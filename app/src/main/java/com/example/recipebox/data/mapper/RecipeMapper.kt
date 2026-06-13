package com.example.recipebox.data.mapper

import com.example.recipebox.data.local.Converters
import com.example.recipebox.data.local.entity.RecipeEntity
import com.example.recipebox.domain.model.CookingStep
import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.model.RecipeDetail

fun RecipeEntity.toDomainModel(): Recipe {
    return Recipe(
        id = id,
        name = name,
        category = category,
        imageUrl = imageUrl,
        cookTime = cookTime,
        servings = servings,
        instructions = instructions,
        isBookmarked = isBookmarked,
        isPersonal = id.contains("-")
    )
}

fun RecipeEntity.toDomainDetailModel(converters: Converters): RecipeDetail {
    val stepsList = instructions.split("\n")
        .filter { it.isNotBlank() }
        .mapIndexed { index, instruction ->
            CookingStep(stepNumber = index + 1, instruction = instruction.trim())
        }
    
    return RecipeDetail(
        id = id,
        name = name,
        category = category,
        imageUrl = imageUrl,
        cookTime = cookTime,
        servings = servings,
        summary = summary,
        description = instructions,
        ingredients = converters.toIngredientList(ingredientsJson),
        steps = stepsList,
        nutrition = converters.toNutritionInfo(nutritionJson),
        healthScore = healthScore,
        isVegetarian = isVegetarian,
        isVegan = isVegan,
        isGlutenFree = isGlutenFree,
        isDairyFree = isDairyFree,
        isBookmarked = isBookmarked,
        isPersonal = id.contains("-")
    )
}

fun RecipeDetail.toEntity(converters: Converters): RecipeEntity {
    return RecipeEntity(
        id = id,
        name = name,
        category = category,
        imageUrl = imageUrl,
        cookTime = cookTime,
        servings = servings,
        instructions = steps.joinToString("\n") { it.instruction },
        isBookmarked = isBookmarked,
        ingredientsJson = converters.fromIngredientList(ingredients),
        summary = summary,
        nutritionJson = converters.fromNutritionInfo(nutrition),
        healthScore = healthScore,
        isVegetarian = isVegetarian,
        isVegan = isVegan,
        isGlutenFree = isGlutenFree,
        isDairyFree = isDairyFree
    )
}
