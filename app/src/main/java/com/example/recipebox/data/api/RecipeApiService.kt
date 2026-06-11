package com.example.recipebox.data.remote.api

import com.example.recipebox.data.remote.response.SpoonacularRandomResponse
import com.example.recipebox.data.remote.response.SpoonacularRecipeDto
import com.example.recipebox.data.remote.response.SpoonacularSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {

    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("number") number: Int = 50
    ): SpoonacularRandomResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeById(
        @Path("id") id: String,
        @Query("includeNutrition") includeNutrition: Boolean = true
    ): SpoonacularRecipeDto

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String = "",
        @Query("number") number: Int = 20,
        @Query("addRecipeInformation") addInfo: Boolean = true
    ): SpoonacularSearchResponse

    @GET("food/trivia/random")
    suspend fun getRandomTrivia(): com.example.recipebox.data.remote.response.TriviaResponse

    @GET("mealplanner/generate")
    suspend fun generateMealPlan(
        @Query("timeFrame") timeFrame: String = "day",
        @Query("targetCalories") targetCalories: Int? = null,
        @Query("diet") diet: String? = null
    ): com.example.recipebox.data.remote.response.MealPlanResponse
}