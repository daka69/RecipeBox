package com.example.recipebox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Rounded.Home)
    object Search : Screen("search", "Search", Icons.Rounded.Search)
    object MealPlan : Screen("meal_plan", "Meal Plan", Icons.Rounded.DateRange)
    object MyRecipes : Screen("my_recipes", "My Recipes", Icons.AutoMirrored.Rounded.MenuBook)
    object Settings : Screen("settings", "Settings", Icons.Rounded.Settings)
}
