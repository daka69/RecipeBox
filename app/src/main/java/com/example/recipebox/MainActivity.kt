package com.example.recipebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipebox.presentation.viewmodel.MealPlanViewModel
import com.example.recipebox.presentation.viewmodel.HomeViewModel
import com.example.recipebox.presentation.viewmodel.MyRecipesViewModel
import com.example.recipebox.presentation.viewmodel.RecipeDetailViewModel
import com.example.recipebox.presentation.viewmodel.SettingsViewModel
import com.example.recipebox.ui.screens.RecipeBoxApp
import com.example.recipebox.ui.theme.RecipeBoxTheme
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val myRecipesViewModel: MyRecipesViewModel = hiltViewModel()
            val detailViewModel: RecipeDetailViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val mealPlanViewModel: MealPlanViewModel = hiltViewModel()
            
            val currentLanguage by settingsViewModel.language.collectAsState()
            LaunchedEffect(currentLanguage) {
                homeViewModel.onLanguageChanged(currentLanguage)
                detailViewModel.onLanguageChanged(currentLanguage)
            }

            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            RecipeBoxTheme(darkTheme = isDarkMode) {
                RecipeBoxApp(
                    homeViewModel = homeViewModel,
                    myRecipesViewModel = myRecipesViewModel,
                    detailViewModel = detailViewModel,
                    settingsViewModel = settingsViewModel,
                    mealPlanViewModel = mealPlanViewModel
                )
            }
        }
    }
}