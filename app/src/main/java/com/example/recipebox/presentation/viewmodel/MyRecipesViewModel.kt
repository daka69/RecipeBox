package com.example.recipebox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.model.RecipeDetail
import com.example.recipebox.domain.usecase.AddRecipeUseCase
import com.example.recipebox.domain.usecase.DeleteRecipeUseCase
import com.example.recipebox.domain.usecase.GetBookmarkedRecipesUseCase
import com.example.recipebox.domain.usecase.GetPersonalRecipesUseCase
import com.example.recipebox.domain.usecase.UpdateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRecipesViewModel @Inject constructor(
    private val getPersonalRecipesUseCase: GetPersonalRecipesUseCase,
    private val getBookmarkedRecipesUseCase: GetBookmarkedRecipesUseCase,
    private val addRecipeUseCase: AddRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _personalRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val personalRecipes: StateFlow<List<Recipe>> = _personalRecipes.asStateFlow()

    private val _bookmarkedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val bookmarkedRecipes: StateFlow<List<Recipe>> = _bookmarkedRecipes.asStateFlow()

    init {
        observePersonalRecipes()
    }

    private fun observePersonalRecipes() {
        viewModelScope.launch {
            getPersonalRecipesUseCase().collect { recipes ->
                _personalRecipes.value = recipes
            }
        }
        
        viewModelScope.launch {
            getBookmarkedRecipesUseCase().collect { recipes ->
                _bookmarkedRecipes.value = recipes
            }
        }
    }

    fun addPersonalRecipe(recipe: RecipeDetail) {
        viewModelScope.launch {
            addRecipeUseCase(recipe)
        }
    }

    fun updatePersonalRecipe(recipe: RecipeDetail) {
        viewModelScope.launch {
            updateRecipeUseCase(recipe)
        }
    }

    fun deletePersonalRecipe(recipe: Recipe) {
        viewModelScope.launch {
            deleteRecipeUseCase(recipe)
        }
    }
}
