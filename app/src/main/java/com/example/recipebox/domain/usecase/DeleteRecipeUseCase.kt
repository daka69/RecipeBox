package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.repository.RecipeRepository

class DeleteRecipeUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe) {
        repository.deleteRecipe(recipe)
    }
}
