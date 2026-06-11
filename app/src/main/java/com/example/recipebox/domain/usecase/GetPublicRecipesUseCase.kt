package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.repository.RecipeRepository

class GetPublicRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): Result<List<Recipe>> {
        return try {
            val recipes = repository.getPublicRecipes()
            Result.success(recipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
