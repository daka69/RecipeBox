package com.example.recipebox.domain.repository

import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.model.RecipeDetail
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun getPublicRecipes(): List<Recipe>
    fun getPersonalRecipes(): Flow<List<Recipe>>
    suspend fun getRecipeById(id: String): Recipe?
    suspend fun getRecipeDetailById(id: String): RecipeDetail?
    suspend fun addRecipe(recipe: RecipeDetail)
    suspend fun updateRecipe(recipe: RecipeDetail)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun toggleBookmark(recipeId: String, isBookmarked: Boolean)
    fun getBookmarkedRecipes(): Flow<List<Recipe>>
    suspend fun generateMealPlan(targetCalories: Int?, diet: String?): com.example.recipebox.domain.model.MealPlan
    suspend fun getRandomTrivia(): String
}
