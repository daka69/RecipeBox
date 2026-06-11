package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.repository.RecipeRepository

class SearchRecipesUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(
        allRecipes: List<Recipe>,
        query: String,
        category: String
    ): List<Recipe> {
        return allRecipes.filter { recipe ->
            val matchQuery = query.isBlank() || recipe.name.contains(query, ignoreCase = true)
            val matchCategory = category == "All" || recipe.category == category
            matchQuery && matchCategory
        }
    }
}
