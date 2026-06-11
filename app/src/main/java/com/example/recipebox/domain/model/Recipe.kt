package com.example.recipebox.domain.model

data class Recipe(
    val id: String,
    val name: String,
    val category: String,
    val imageUrl: String,
    val cookTime: String,
    val servings: Int,
    val instructions: String = "",
    val isBookmarked: Boolean = false,
    val isPersonal: Boolean = false
)