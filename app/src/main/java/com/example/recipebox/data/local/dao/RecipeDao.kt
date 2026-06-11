package com.example.recipebox.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipebox.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM personal_recipes WHERE id LIKE '%-%'")
    fun getAllPersonalRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM personal_recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: String): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM personal_recipes WHERE isBookmarked = 1")
    fun getBookmarkedRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM personal_recipes WHERE isCachedPublic = 1")
    suspend fun getCachedPublicRecipes(): List<RecipeEntity>

    @Query("DELETE FROM personal_recipes WHERE isCachedPublic = 1 AND isBookmarked = 0 AND id NOT LIKE '%-%'")
    suspend fun clearCachedPublicRecipes()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Query("UPDATE personal_recipes SET isBookmarked = :isBookmarked WHERE id = :recipeId")
    suspend fun toggleBookmark(recipeId: String, isBookmarked: Boolean)
}