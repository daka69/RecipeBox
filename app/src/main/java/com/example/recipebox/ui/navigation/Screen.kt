package com.example.recipebox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.recipebox.R

sealed class Screen(val route: String, val labelResId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Rounded.Home)
    object Search : Screen("search", R.string.nav_search, Icons.Rounded.Search)
    object MealPlan : Screen("meal_plan", R.string.nav_meal_plan, Icons.Rounded.DateRange)
    object MyRecipes : Screen("my_recipes", R.string.nav_my_recipes, Icons.AutoMirrored.Rounded.MenuBook)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Rounded.Settings)
    
    // Screens not shown in bottom navigation
    object Detail : Screen("detail/{recipeId}", 0, Icons.Default.Info)
    object AddEdit : Screen("add_edit/{recipeId}", 0, Icons.Default.Edit)
}
