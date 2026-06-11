package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetBookmarkedRecipesUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> {
        return repository.getBookmarkedRecipes()
    }
}
