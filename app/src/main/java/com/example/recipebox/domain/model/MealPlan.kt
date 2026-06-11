package com.example.recipebox.domain.model

data class MealPlan(
    val meals: List<MealPlanRecipe>,
    val nutrients: MealPlanNutrients
)

data class MealPlanRecipe(
    val id: String,
    val title: String,
    val imageUrl: String,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String
)

data class MealPlanNutrients(
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double
)
