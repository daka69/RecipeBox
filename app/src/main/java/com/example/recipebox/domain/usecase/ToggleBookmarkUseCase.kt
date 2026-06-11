package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.repository.RecipeRepository

class ToggleBookmarkUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: String, isBookmarked: Boolean) {
        repository.toggleBookmark(recipeId, isBookmarked)
    }
}
