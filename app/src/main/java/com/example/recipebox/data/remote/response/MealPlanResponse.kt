package com.example.recipebox.data.remote.response

data class MealPlanResponse(
    val meals: List<MealPlanRecipeDto>,
    val nutrients: MealPlanNutrientsDto
)

data class MealPlanRecipeDto(
    val id: Int,
    val title: String,
    val imageType: String?,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String?
) {
    // Helper to generate the image URL since the API only returns imageType
    val imageUrl: String
        get() = "https://spoonacular.com/recipeImages/$id-312x231.${imageType ?: "jpg"}"
}

data class MealPlanNutrientsDto(
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double
)

data class TriviaResponse(
    val text: String
)
