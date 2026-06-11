package com.example.recipebox.data.remote.response

import com.example.recipebox.domain.model.Ingredient
import com.example.recipebox.domain.model.NutritionInfo

// Response for /recipes/random
data class SpoonacularRandomResponse(
    val recipes: List<SpoonacularRecipeDto>
)

// Response for /recipes/complexSearch
data class SpoonacularSearchResponse(
    val results: List<SpoonacularRecipeDto>,
    val offset: Int = 0,
    val number: Int = 0,
    val totalResults: Int = 0
)

// Main recipe DTO
data class SpoonacularRecipeDto(
    val id: Int,
    val title: String,
    val image: String? = null,
    val servings: Int = 4,
    val readyInMinutes: Int = 30,
    val instructions: String? = null,
    val summary: String? = null,
    val dishTypes: List<String>? = null,
    val cuisines: List<String>? = null,
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val dairyFree: Boolean = false,
    val healthScore: Double = 0.0,
    val sourceUrl: String? = null,
    val nutrition: SpoonacularNutritionDto? = null,
    val extendedIngredients: List<SpoonacularIngredientDto>? = null,
    val analyzedInstructions: List<SpoonacularInstructionGroup>? = null
) {
    fun toIngredientList(): List<Ingredient> {
        return extendedIngredients?.map { ing ->
            Ingredient(
                name = ing.name,
                quantity = if (ing.amount % 1.0 == 0.0) {
                    ing.amount.toInt().toString()
                } else {
                    String.format("%.1f", ing.amount)
                },
                unit = ing.unit
            )
        } ?: emptyList()
    }

    fun getCategory(): String {
        return dishTypes?.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Main Course"
    }

    fun getCleanSummary(): String {
        return summary
            ?.replace(Regex("<[^>]*>"), "")
            ?.replace("&nbsp;", " ")
            ?.replace("&amp;", "&")
            ?.replace("&lt;", "<")
            ?.replace("&gt;", ">")
            ?.trim()
            ?: ""
    }

    fun getCleanInstructions(): String {
        return instructions
            ?.replace(Regex("<[^>]*>"), "")
            ?.replace("&nbsp;", " ")
            ?.replace("&amp;", "&")
            ?.replace("&lt;", "<")
            ?.replace("&gt;", ">")
            ?.trim()
            ?: ""
    }

    fun getCookTimeFormatted(): String {
        return if (readyInMinutes > 60) {
            val hours = readyInMinutes / 60
            val mins = readyInMinutes % 60
            if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
        } else {
            "${readyInMinutes} mins"
        }
    }

    fun toNutritionInfo(): NutritionInfo {
        val nutrients = nutrition?.nutrients ?: return NutritionInfo()
        fun find(name: String): String {
            val n = nutrients.find { it.name.equals(name, ignoreCase = true) }
            return if (n != null) "${String.format("%.0f", n.amount)}${n.unit}" else ""
        }
        return NutritionInfo(
            calories = find("Calories"),
            fat = find("Fat"),
            carbs = find("Carbohydrates"),
            protein = find("Protein")
        )
    }
}

data class SpoonacularNutritionDto(
    val nutrients: List<SpoonacularNutrientDto>? = null
)

data class SpoonacularNutrientDto(
    val name: String = "",
    val amount: Double = 0.0,
    val unit: String = "",
    val percentOfDailyNeeds: Double = 0.0
)

data class SpoonacularIngredientDto(
    val id: Int? = null,
    val name: String = "",
    val amount: Double = 0.0,
    val unit: String = "",
    val original: String? = null
)

data class SpoonacularInstructionGroup(
    val name: String? = null,
    val steps: List<SpoonacularStepDto> = emptyList()
)

data class SpoonacularStepDto(
    val number: Int,
    val step: String
)