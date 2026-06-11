package com.example.recipebox.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.recipebox.domain.model.CookingStep
import com.example.recipebox.ui.state.IngredientInput
import com.example.recipebox.domain.model.RecipeDetail
import com.example.recipebox.domain.model.Ingredient
import java.util.UUID
import com.example.recipebox.ui.state.StepInput
import com.example.recipebox.presentation.viewmodel.RecipeViewModel
import com.example.recipebox.presentation.viewmodel.UiState
import com.example.recipebox.ui.navigation.Screen

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeBoxApp(
    viewModel: RecipeViewModel,
    settingsViewModel: com.example.recipebox.presentation.viewmodel.SettingsViewModel,
    mealPlanViewModel: com.example.recipebox.presentation.viewmodel.MealPlanViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("recipe_box_prefs", android.content.Context.MODE_PRIVATE) }
    var showOnboarding by rememberSaveable { 
        mutableStateOf(sharedPreferences.getBoolean("show_onboarding", true)) 
    }
    
    val navController = rememberNavController()

    val publicRecipesState by viewModel.publicRecipesState.collectAsState()
    val publicRecipes by viewModel.publicRecipes.collectAsState()
    val personalRecipes by viewModel.personalRecipes.collectAsState()
    val bookmarkedRecipes by viewModel.bookmarkedRecipes.collectAsState()
    val recipeDetailState by viewModel.recipeDetailState.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val triviaState by viewModel.triviaState.collectAsState()
    
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val currentLanguage by settingsViewModel.language.collectAsState()
    val mealPlanState by mealPlanViewModel.mealPlanState.collectAsState()

    var showDetail by rememberSaveable { mutableStateOf(false) }
    var showAddEdit by rememberSaveable { mutableStateOf(false) }
    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var selectedRecipeId by rememberSaveable { mutableStateOf<String?>(null) }

    // Find the selected recipe from the lists
    val selectedRecipe = selectedRecipeId?.let { id ->
        publicRecipes.find { it.id == id } 
            ?: personalRecipes.find { it.id == id }
            ?: bookmarkedRecipes.find { it.id == id }
    }

    if (showOnboarding) {
        OnboardingScreen(onFinish = { 
            showOnboarding = false 
            sharedPreferences.edit().putBoolean("show_onboarding", false).apply()
        })
        return
    }

    androidx.activity.compose.BackHandler(enabled = showDetail || showAddEdit) {
        if (showDetail) showDetail = false
        if (showAddEdit) showAddEdit = false
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val items = listOf(Screen.Home, Screen.Search, Screen.MealPlan, Screen.MyRecipes, Screen.Settings)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            // Dismiss overlays when switching tabs
                            showDetail = false
                            showAddEdit = false
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        recipesState = publicRecipesState,
                        triviaState = triviaState,
                        onRecipeClick = { recipe ->
                            selectedRecipeId = recipe.id
                            viewModel.fetchRecipeDetail(recipe.id)
                            showDetail = true
                        },
                        onAddClick = {
                            isEditMode = false
                            selectedRecipeId = null
                            showAddEdit = true
                        },
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onRetry = { viewModel.fetchPublicRecipes() }
                    )
                }
                composable(Screen.MealPlan.route) {
                    MealPlanScreen(
                        mealPlanState = mealPlanState,
                        onGeneratePlan = { calories, diet ->
                            mealPlanViewModel.fetchMealPlan(calories, diet)
                        },
                        onRecipeClick = { id ->
                            selectedRecipeId = id
                            viewModel.fetchRecipeDetail(id)
                            showDetail = true
                        }
                    )
                }
                composable(Screen.MyRecipes.route) {
                    ResepSayaScreen(
                        recipes = personalRecipes,
                        bookmarkedRecipes = bookmarkedRecipes,
                        onRecipeClick = { recipe ->
                            selectedRecipeId = recipe.id
                            viewModel.fetchRecipeDetail(recipe.id)
                            showDetail = true
                        },
                        onAddClick = {
                            isEditMode = false
                            selectedRecipeId = null
                            showAddEdit = true
                        },
                        onDeleteRecipe = { recipe ->
                            viewModel.deletePersonalRecipe(recipe)
                        }
                    )
                }
                composable(Screen.Search.route) {
                    PencarianScreen(
                        allRecipes = publicRecipes + personalRecipes,
                        onRecipeClick = { recipe ->
                            selectedRecipeId = recipe.id
                            viewModel.fetchRecipeDetail(recipe.id)
                            showDetail = true
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    ProfileSettingsScreen(
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { settingsViewModel.toggleDarkMode() },
                        currentLanguage = currentLanguage,
                        onLanguageToggle = { settingsViewModel.toggleLanguage() }
                    )
                }
            }

            if (showDetail) {
                when (val state = recipeDetailState) {
                    is UiState.Idle, is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    is UiState.Success -> {
                        DetailResepScreen(
                            recipe = state.data,
                            isTranslating = isTranslating,
                            onBack = { showDetail = false },
                            onEdit = {
                                showDetail = false
                                isEditMode = true
                                showAddEdit = true
                            },
                            onDelete = {
                                if (selectedRecipe != null) {
                                    viewModel.deletePersonalRecipe(selectedRecipe)
                                }
                                showDetail = false
                            },
                            onBookmarkToggle = {
                                viewModel.toggleBookmark(
                                    state.data.id,
                                    state.data.isBookmarked
                                )
                            }
                        )
                    }
                    is UiState.Error -> {
                        DetailResepScreen(
                            recipe = RecipeDetail(
                                id = selectedRecipeId ?: "",
                                name = selectedRecipe?.name ?: "Error",
                                category = selectedRecipe?.category ?: "",
                                imageUrl = selectedRecipe?.imageUrl ?: "",
                                cookTime = selectedRecipe?.cookTime ?: "",
                                servings = selectedRecipe?.servings ?: 0,
                                description = selectedRecipe?.instructions ?: "",
                                ingredients = emptyList(),
                                steps = listOf(
                                    CookingStep(1, selectedRecipe?.instructions ?: "")
                                ),
                                isBookmarked = selectedRecipe?.isBookmarked ?: false,
                                isPersonal = selectedRecipe?.isPersonal ?: false
                            ),
                            onBack = { showDetail = false },
                            onEdit = {
                                showDetail = false
                                isEditMode = true
                                showAddEdit = true
                            },
                            onDelete = {
                                if (selectedRecipe != null) {
                                    viewModel.deletePersonalRecipe(selectedRecipe)
                                }
                                showDetail = false
                            },
                            onBookmarkToggle = { }
                        )
                    }
                }
            }

            if (showAddEdit) {
                // Get current detail if available (for edit mode)
                val currentDetail = (recipeDetailState as? UiState.Success)?.data

                TambahEditResepScreen(
                    isEditMode = isEditMode,
                    initialName = if (isEditMode) (selectedRecipe?.name ?: "") else "",
                    initialCategory = if (isEditMode) (selectedRecipe?.category ?: "") else "",
                    initialImageUri = if (isEditMode) (selectedRecipe?.imageUrl ?: "") else "",
                    initialIngredients = if (isEditMode && currentDetail != null && currentDetail.ingredients.isNotEmpty()) {
                        currentDetail.ingredients.mapIndexed { index, ingredient ->
                            IngredientInput(
                                id = index,
                                name = ingredient.name,
                                qty = ingredient.quantity,
                                unit = ingredient.unit
                            )
                        }
                    } else {
                        listOf(IngredientInput(0))
                    },
                    initialSteps = if (isEditMode && selectedRecipe != null) {
                        val instructions = selectedRecipe.instructions
                        val stepTexts = instructions.split("\n").filter { it.isNotBlank() }
                        if (stepTexts.isNotEmpty()) {
                            stepTexts.mapIndexed { index, text ->
                                StepInput(index, text.trim())
                            }
                        } else {
                            listOf(StepInput(0))
                        }
                    } else {
                        listOf(StepInput(0))
                    },
                    onBack = { showAddEdit = false },
                    onSave = { name, category, ingredients, steps, imageUri ->
                        val combinedInstructions = steps.joinToString(separator = "\n") { it.instruction }
                        val domainIngredients = ingredients.filter { it.name.isNotBlank() }.map { Ingredient(name = it.name, quantity = it.qty, unit = it.unit) }
                        val domainSteps = steps.filter { it.instruction.isNotBlank() }.mapIndexed { index, step -> CookingStep(stepNumber = index + 1, instruction = step.instruction) }
                        
                        if (isEditMode && currentDetail != null) {
                            val updatedRecipe = currentDetail.copy(
                                name = name,
                                category = category,
                                description = combinedInstructions,
                                imageUrl = imageUri.ifBlank { currentDetail.imageUrl },
                                ingredients = domainIngredients,
                                steps = domainSteps
                            )
                            viewModel.updatePersonalRecipe(updatedRecipe)
                        } else {
                            val newDetail = RecipeDetail(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                category = category,
                                imageUrl = imageUri,
                                cookTime = "20 mins",
                                servings = 2,
                                summary = "",
                                description = combinedInstructions,
                                ingredients = domainIngredients,
                                steps = domainSteps,
                                isBookmarked = false,
                                isPersonal = true
                            )
                            viewModel.addPersonalRecipe(newDetail)
                        }
                        showAddEdit = false
                    }
                )
            }
        }
    }
}
