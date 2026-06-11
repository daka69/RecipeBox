package com.example.recipebox.ui.state

data class IngredientInput(
    val id: Int,
    val name: String = "",
    val qty: String = "",
    val unit: String = ""
)

data class StepInput(val id: Int, val instruction: String = "")
