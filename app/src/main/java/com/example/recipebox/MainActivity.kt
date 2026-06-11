package com.example.recipebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebox.presentation.viewmodel.MealPlanViewModel
import com.example.recipebox.presentation.viewmodel.RecipeViewModel
import com.example.recipebox.presentation.viewmodel.SettingsViewModel
import com.example.recipebox.ui.screens.RecipeBoxApp
import com.example.recipebox.ui.theme.RecipeBoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: RecipeViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val mealPlanViewModel: MealPlanViewModel = hiltViewModel()
            
            // Sync settings language with recipeviewmodel
            val currentLanguage by settingsViewModel.language.collectAsState()
            LaunchedEffect(currentLanguage) {
                viewModel.onLanguageChanged(currentLanguage)
            }

            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            RecipeBoxTheme(darkTheme = isDarkMode) {
                RecipeBoxApp(
                    viewModel = viewModel,
                    settingsViewModel = settingsViewModel,
                    mealPlanViewModel = mealPlanViewModel
                )
            }
        }
    }
}