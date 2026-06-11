package com.example.recipebox.domain.model

data class RecipeDetail(
    val id: String,
    val name: String,
    val category: String,
    val imageUrl: String,
    val cookTime: String,
    val servings: Int,
    val summary: String = "",       // Deskripsi singkat makanan
    val description: String,        // Instructions text
    val ingredients: List<Ingredient>,
    val steps: List<CookingStep>,
    val nutrition: NutritionInfo = NutritionInfo(),
    val healthScore: Int = 0,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val sourceUrl: String = "",
    val isBookmarked: Boolean = false,
    val isPersonal: Boolean = false
)
