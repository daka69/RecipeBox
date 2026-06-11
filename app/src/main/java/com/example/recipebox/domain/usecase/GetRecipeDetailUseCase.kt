package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.model.RecipeDetail
import com.example.recipebox.domain.repository.RecipeRepository

class GetRecipeDetailUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): Result<RecipeDetail> {
        return try {
            val detail = repository.getRecipeDetailById(id)
            if (detail != null) {
                Result.success(detail)
            } else {
                Result.failure(Exception("Resep tidak ditemukan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
