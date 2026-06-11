package com.example.recipebox.domain.usecase

import com.example.recipebox.domain.repository.RecipeRepository
import com.example.recipebox.domain.model.MealPlan

class GetMealPlanUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(targetCalories: Int? = null, diet: String? = null): MealPlan {
        return repository.generateMealPlan(targetCalories, diet)
    }
}
