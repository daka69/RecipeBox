package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.model.RecipeDetail
import com.example.recipebox.domain.repository.RecipeRepository

class UpdateRecipeUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeDetail) {
        repository.updateRecipe(recipe)
    }
}
