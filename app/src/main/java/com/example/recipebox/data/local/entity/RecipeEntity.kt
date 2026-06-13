package com.example.recipebox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val imageUrl: String,
    val cookTime: String,
    val servings: Int,
    val instructions: String,
    val isBookmarked: Boolean,
    val ingredientsJson: String = "[]",
    val summary: String = "",
    val nutritionJson: String = "{}",
    val healthScore: Int = 0,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val isCachedPublic: Boolean = false
)