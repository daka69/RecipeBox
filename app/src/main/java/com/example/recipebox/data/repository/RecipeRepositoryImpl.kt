package com.example.recipebox.data.repository

import com.example.recipebox.data.local.dao.RecipeDao
import com.example.recipebox.data.local.entity.RecipeEntity
import com.example.recipebox.data.remote.api.RecipeApiService
import com.example.recipebox.domain.model.CookingStep
import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.model.RecipeDetail
import com.example.recipebox.domain.repository.RecipeRepository
import com.example.recipebox.data.mapper.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepositoryImpl(
    private val apiService: RecipeApiService,
    private val dao: RecipeDao,
    private val converters: com.example.recipebox.data.local.Converters
) : RecipeRepository {

    override suspend fun getPublicRecipes(): List<Recipe> {
        return try {
            val response = apiService.getRandomRecipes(number = 50)
            val recipes = response.recipes.map { dto ->
                Recipe(
                    id = dto.id.toString(),
                    name = dto.title,
                    category = dto.getCategory(),
                    imageUrl = dto.image ?: "",
                    cookTime = dto.getCookTimeFormatted(),
                    servings = dto.servings,
                    instructions = dto.getCleanInstructions(),
                    isBookmarked = false,
                    isPersonal = false
                )
            }

            // Cache the fetched recipes
            dao.clearCachedPublicRecipes()
            val entitiesToCache = recipes.map { r ->
                RecipeEntity(
                    id = r.id,
                    name = r.name,
                    category = r.category,
                    imageUrl = r.imageUrl,
                    cookTime = r.cookTime,
                    servings = r.servings,
                    instructions = r.instructions,
                    isBookmarked = false,
                    isCachedPublic = true
                )
            }
            dao.insertRecipes(entitiesToCache)

            recipes
        } catch (e: Exception) {
            // Fallback to cache if no internet
            val cachedEntities = dao.getCachedPublicRecipes()
            if (cachedEntities.isNotEmpty()) {
                cachedEntities.map { entity ->
                    entity.toDomainModel()
                }
            } else {
                throw Exception("No network connection and no cached recipes available. Please check your internet connection.")
            }
        }
    }

    override suspend fun getRecipeDetailById(id: String): RecipeDetail? {
        val isNumericId = id.all { it.isDigit() }

        if (isNumericId) {
            // It's an API recipe. Try to fetch from Spoonacular API first for full details.
            try {
                val dto = apiService.getRecipeById(id)
                val ingredients = dto.toIngredientList()

                // Get raw steps strings from analyzedInstructions, or fallback to full instructions text
                val rawStrings = dto.analyzedInstructions
                    ?.flatMap { group -> group.steps }
                    ?.map { it.step }
                    ?.takeIf { it.isNotEmpty() }
                    ?: listOf(dto.getCleanInstructions())

                val allSteps = mutableListOf<String>()
                rawStrings.forEach { text ->
                    var cleanText = text.replace(Regex("(?<=[a-zA-Z])\\.(?=\\d+\\.?)"), ". ")
                    cleanText = cleanText.replace(Regex("(?<=\\d)\\.(?=[a-zA-Z])"), ". ")
                    cleanText = cleanText.replace(Regex("(?<=[a-z])\\.(?=[A-Z])"), ". ")

                    val parts = cleanText.split(Regex("(?<=\\s|^)\\d+\\.\\s"))
                        .map { it.trim() }
                        .filter { it.length > 2 }
                    
                    if (parts.isEmpty()) {
                        if (cleanText.isNotBlank()) allSteps.add(cleanText.trim())
                    } else {
                        allSteps.addAll(parts)
                    }
                }

                val steps = allSteps
                    .filter { it.isNotBlank() }
                    .mapIndexed { index, instruction ->
                        CookingStep(stepNumber = index + 1, instruction = instruction)
                    }

                val localRecipe = dao.getRecipeById(dto.id.toString())
                val isBookmarked = localRecipe?.isBookmarked ?: false

                return RecipeDetail(
                    id = dto.id.toString(),
                    name = dto.title,
                    category = dto.getCategory(),
                    imageUrl = dto.image ?: "",
                    cookTime = dto.getCookTimeFormatted(),
                    servings = dto.servings,
                    summary = dto.getCleanSummary(),
                    description = dto.getCleanInstructions(),
                    ingredients = ingredients,
                    steps = steps,
                    nutrition = dto.toNutritionInfo(),
                    healthScore = dto.healthScore.toInt(),
                    isVegetarian = dto.vegetarian,
                    isVegan = dto.vegan,
                    isGlutenFree = dto.glutenFree,
                    isDairyFree = dto.dairyFree,
                    sourceUrl = dto.sourceUrl ?: "",
                    isBookmarked = isBookmarked,
                    isPersonal = false
                )
            } catch (e: Exception) {
                // Fallback to local DB if offline
                val localRecipe = dao.getRecipeById(id) ?: return null
                return localRecipe.toDomainDetailModel(converters)
            }
        } else {
            // It's a personal recipe, always fetch from local DB
            val localRecipe = dao.getRecipeById(id) ?: return null
            return localRecipe.toDomainDetailModel(converters)
        }
    }

    override fun getPersonalRecipes(): Flow<List<Recipe>> {
        return dao.getAllPersonalRecipes().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        val entity = dao.getRecipeById(id)
        return entity?.toDomainModel()
    }

    override suspend fun addRecipe(recipe: RecipeDetail) {
        dao.insertRecipe(recipe.toEntity(converters))
    }

    override suspend fun updateRecipe(recipe: RecipeDetail) {
        dao.updateRecipe(recipe.toEntity(converters))
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        dao.deleteRecipe(
            RecipeEntity(
                id = recipe.id,
                name = recipe.name,
                category = recipe.category,
                imageUrl = recipe.imageUrl,
                cookTime = recipe.cookTime,
                servings = recipe.servings,
                instructions = recipe.instructions,
                isBookmarked = recipe.isBookmarked
            )
        )
    }

    override suspend fun toggleBookmark(recipeId: String, isBookmarked: Boolean) {
        val existing = dao.getRecipeById(recipeId)
        val isNumericId = recipeId.all { it.isDigit() }

        if (existing != null) {
            if (!isBookmarked && isNumericId) {
                // Remove API recipe from cache when unbookmarked
                dao.deleteRecipe(existing)
            } else {
                dao.toggleBookmark(recipeId, isBookmarked)
            }
        } else {
            val detail = getRecipeDetailById(recipeId)
            if (detail != null) {
                dao.insertRecipe(detail.toEntity(converters).copy(isBookmarked = isBookmarked))
            }
        }
    }

    override fun getBookmarkedRecipes(): Flow<List<Recipe>> {
        return dao.getBookmarkedRecipes().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun generateMealPlan(targetCalories: Int?, diet: String?): com.example.recipebox.domain.model.MealPlan {
        val response = apiService.generateMealPlan(
            timeFrame = "day",
            targetCalories = targetCalories,
            diet = diet
        )
        return com.example.recipebox.domain.model.MealPlan(
            meals = response.meals.map {
                com.example.recipebox.domain.model.MealPlanRecipe(
                    id = it.id.toString(),
                    title = it.title,
                    imageUrl = it.imageUrl,
                    readyInMinutes = it.readyInMinutes,
                    servings = it.servings,
                    sourceUrl = it.sourceUrl ?: ""
                )
            },
            nutrients = com.example.recipebox.domain.model.MealPlanNutrients(
                calories = response.nutrients.calories,
                protein = response.nutrients.protein,
                fat = response.nutrients.fat,
                carbohydrates = response.nutrients.carbohydrates
            )
        )
    }

    override suspend fun getRandomTrivia(): String {
        return try {
            val response = apiService.getRandomTrivia()
            response.text
        } catch (e: Exception) {
            "Did you know? Cooking at home can improve your diet and save money!"
        }
    }
}
