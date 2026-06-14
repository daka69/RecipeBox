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

import com.example.recipebox.presentation.viewmodel.UiState
import com.example.recipebox.ui.navigation.Screen

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navDeepLink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeBoxApp(
    homeViewModel: com.example.recipebox.presentation.viewmodel.HomeViewModel,
    myRecipesViewModel: com.example.recipebox.presentation.viewmodel.MyRecipesViewModel,
    detailViewModel: com.example.recipebox.presentation.viewmodel.RecipeDetailViewModel,
    settingsViewModel: com.example.recipebox.presentation.viewmodel.SettingsViewModel,
    mealPlanViewModel: com.example.recipebox.presentation.viewmodel.MealPlanViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("recipe_box_prefs", android.content.Context.MODE_PRIVATE) }
    var showOnboarding by rememberSaveable { 
        mutableStateOf(sharedPreferences.getBoolean("show_onboarding", true)) 
    }
    
    val navController = rememberNavController()

    val publicRecipesState by homeViewModel.publicRecipesState.collectAsState()
    val publicRecipes by remember(publicRecipesState) {
        derivedStateOf {
            if (publicRecipesState is UiState.Success) {
                (publicRecipesState as UiState.Success).data
            } else {
                emptyList()
            }
        }
    }
    val personalRecipes by myRecipesViewModel.personalRecipes.collectAsState()
    val bookmarkedRecipes by myRecipesViewModel.bookmarkedRecipes.collectAsState()
    val recipeDetailState by detailViewModel.recipeDetailState.collectAsState()
    val isTranslating by detailViewModel.isTranslating.collectAsState()
    val triviaState by homeViewModel.triviaState.collectAsState()
    
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val currentLanguage by settingsViewModel.language.collectAsState()
    val mealPlanState by mealPlanViewModel.mealPlanState.collectAsState()

    // Overlays state removed. Navigation state is handled by NavController.

    if (showOnboarding) {
        OnboardingScreen(onFinish = { 
            showOnboarding = false 
            sharedPreferences.edit().putBoolean("show_onboarding", false).apply()
        })
        return
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
                        icon = { Icon(screen.icon, contentDescription = androidx.compose.ui.res.stringResource(screen.labelResId)) },
                        label = { Text(androidx.compose.ui.res.stringResource(screen.labelResId)) },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

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
                            navController.navigate("detail/${recipe.id}")
                        },
                        onAddClick = {
                            navController.navigate("add_edit/new")
                        },
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onRetry = { homeViewModel.fetchPublicRecipes() }
                    )
                }
                composable(Screen.MealPlan.route) {
                    MealPlanScreen(
                        mealPlanState = mealPlanState,
                        onGeneratePlan = { calories, diet ->
                            mealPlanViewModel.fetchMealPlan(calories, diet)
                        },
                        onRecipeClick = { id ->
                            navController.navigate("detail/$id")
                        }
                    )
                }
                composable(Screen.MyRecipes.route) {
                    ResepSayaScreen(
                        recipes = personalRecipes,
                        bookmarkedRecipes = bookmarkedRecipes,
                        onRecipeClick = { recipe ->
                            navController.navigate("detail/${recipe.id}")
                        },
                        onAddClick = {
                            navController.navigate("add_edit/new")
                        },
                        onDeleteRecipe = { recipe ->
                            myRecipesViewModel.deletePersonalRecipe(recipe)
                        }
                    )
                }
                composable(Screen.Search.route) {
                    PencarianScreen(
                        allRecipes = publicRecipes + personalRecipes,
                        onRecipeClick = { recipe ->
                            navController.navigate("detail/${recipe.id}")
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
                            composable(
                    route = Screen.Detail.route,
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "recipebox://detail/{recipeId}" },
                        navDeepLink { uriPattern = "https://www.recipebox.app/detail/{recipeId}" },
                        navDeepLink { uriPattern = "http://www.recipebox.app/detail/{recipeId}" }
                    )
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId")
                    
                    LaunchedEffect(recipeId) {
                        if (recipeId != null) {
                            detailViewModel.fetchRecipeDetail(recipeId)
                        }
                    }
                    
                    val selectedRecipe = recipeId?.let { id ->
                        publicRecipes.find { it.id == id } 
                            ?: personalRecipes.find { it.id == id }
                            ?: bookmarkedRecipes.find { it.id == id }
                    }
                    
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
                                onBack = { navController.popBackStack() },
                                onEdit = {
                                    navController.navigate("add_edit/${state.data.id}")
                                },
                                onDelete = {
                                    val recipeToDelete = selectedRecipe ?: com.example.recipebox.domain.model.Recipe(
                                        id = state.data.id,
                                        name = state.data.name,
                                        category = state.data.category,
                                        imageUrl = state.data.imageUrl,
                                        cookTime = state.data.cookTime,
                                        servings = state.data.servings,
                                        instructions = state.data.description,
                                        isBookmarked = state.data.isBookmarked,
                                        isPersonal = state.data.isPersonal
                                    )
                                    myRecipesViewModel.deletePersonalRecipe(recipeToDelete)
                                    navController.popBackStack()
                                },
                                onBookmarkToggle = {
                                    detailViewModel.toggleBookmark(
                                        state.data.id,
                                        state.data.isBookmarked
                                    )
                                }
                            )
                        }
                        is UiState.Error -> {
                            DetailResepScreen(
                                recipe = RecipeDetail(
                                    id = recipeId ?: "",
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
                                onBack = { navController.popBackStack() },
                                onEdit = {
                                    navController.navigate("add_edit/${recipeId}")
                                },
                                onDelete = {
                                    if (selectedRecipe != null) {
                                        myRecipesViewModel.deletePersonalRecipe(selectedRecipe)
                                    }
                                    navController.popBackStack()
                                },
                                onBookmarkToggle = { }
                            )
                        }
                    }
                }

                composable(route = Screen.AddEdit.route) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId")
                    val isEditMode = recipeId != "new"
                    
                    val selectedRecipe = if (isEditMode) {
                        recipeId?.let { id ->
                            publicRecipes.find { it.id == id } 
                                ?: personalRecipes.find { it.id == id }
                                ?: bookmarkedRecipes.find { it.id == id }
                        }
                    } else null
                    
                    val currentDetail = (recipeDetailState as? UiState.Success)?.data
                    
                    TambahEditResepScreen(
                        isEditMode = isEditMode,
                        initialName = if (isEditMode) (selectedRecipe?.name ?: "") else "",
                        initialCategory = if (isEditMode) (selectedRecipe?.category ?: "") else "",
                        initialCookTime = if (isEditMode) (selectedRecipe?.cookTime ?: "") else "",
                        initialServings = if (isEditMode) (selectedRecipe?.servings?.toString() ?: "") else "",
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
                        initialSteps = if (isEditMode && currentDetail != null && currentDetail.steps.isNotEmpty()) {
                            currentDetail.steps.mapIndexed { index, step ->
                                StepInput(index, step.instruction)
                            }
                        } else if (isEditMode && selectedRecipe != null) {
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
                        onBack = { navController.popBackStack() },
                        onSave = { name, category, cookTime, servings, ingredients, steps, imageUri ->
                            val combinedInstructions = steps.joinToString(separator = "\n") { it.instruction.trim() }
                            val domainIngredients = ingredients.filter { it.name.isNotBlank() }.map { Ingredient(name = it.name.trim(), quantity = it.qty.trim(), unit = it.unit.trim()) }
                            val domainSteps = steps.filter { it.instruction.isNotBlank() }.mapIndexed { index, step -> CookingStep(stepNumber = index + 1, instruction = step.instruction.trim()) }
                            
                            val finalCookTime = cookTime.trim().ifEmpty { context.getString(com.example.recipebox.R.string.default_cook_time) }
                            val finalServings = servings.trim().toIntOrNull() ?: 2
    
                            if (isEditMode && currentDetail != null) {
                                val updatedRecipe = currentDetail.copy(
                                    name = name.trim(),
                                    category = category.trim(),
                                    cookTime = finalCookTime,
                                    servings = finalServings,
                                    description = combinedInstructions,
                                    imageUrl = imageUri.ifBlank { currentDetail.imageUrl },
                                    ingredients = domainIngredients,
                                    steps = domainSteps
                                )
                                myRecipesViewModel.updatePersonalRecipe(updatedRecipe)
                                navController.popBackStack()
                            } else {
                                val newDetail = RecipeDetail(
                                    id = java.util.UUID.randomUUID().toString(),
                                    name = name.trim(),
                                    category = category.trim(),
                                    imageUrl = imageUri,
                                    cookTime = finalCookTime,
                                    servings = finalServings,
                                    summary = "",
                                    description = combinedInstructions,
                                    ingredients = domainIngredients,
                                    steps = domainSteps,
                                    isBookmarked = false,
                                    isPersonal = true
                                )
                                myRecipesViewModel.addPersonalRecipe(newDetail)
                                navController.popBackStack()
                                navController.navigate(Screen.MyRecipes.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
