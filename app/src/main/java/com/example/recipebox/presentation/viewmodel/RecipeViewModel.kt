package com.example.recipebox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebox.data.translation.TranslationService
import com.example.recipebox.domain.model.CookingStep
import com.example.recipebox.domain.model.Ingredient
import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.model.RecipeDetail
import com.example.recipebox.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// Language enum moved to SettingsViewModel

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getPublicRecipesUseCase: GetPublicRecipesUseCase,
    private val getPersonalRecipesUseCase: GetPersonalRecipesUseCase,
    private val getBookmarkedRecipesUseCase: GetBookmarkedRecipesUseCase,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val addRecipeUseCase: AddRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val getRandomTriviaUseCase: GetRandomTriviaUseCase,
    private val translationService: TranslationService
) : ViewModel() {

    // Public recipes state with loading/error
    private val _publicRecipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val publicRecipesState: StateFlow<UiState<List<Recipe>>> = _publicRecipesState.asStateFlow()

    // Simple list for backward compat
    private val _publicRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val publicRecipes: StateFlow<List<Recipe>> = _publicRecipes.asStateFlow()

    private val _personalRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val personalRecipes: StateFlow<List<Recipe>> = _personalRecipes.asStateFlow()

    private val _bookmarkedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val bookmarkedRecipes: StateFlow<List<Recipe>> = _bookmarkedRecipes.asStateFlow()

    // Recipe detail state
    private val _recipeDetailState = MutableStateFlow<UiState<RecipeDetail>>(UiState.Loading)
    val recipeDetailState: StateFlow<UiState<RecipeDetail>> = _recipeDetailState.asStateFlow()

    // Translation status
    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    // Language state for translation tracking
    private val _language = MutableStateFlow(Language.EN)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _triviaState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val triviaState: StateFlow<UiState<String>> = _triviaState.asStateFlow()

    private val _translationReady = MutableStateFlow(false)
    val translationReady: StateFlow<Boolean> = _translationReady.asStateFlow()

    // Store original (English) texts for re-translation
    private var originalDetail: RecipeDetail? = null
    private var originalTrivia: String? = null

    init {
        fetchPublicRecipes()
        observePersonalRecipes()
        fetchTrivia()
        // Pre-download translation model
        viewModelScope.launch {
            val ready = translationService.ensureModelDownloaded()
            _translationReady.value = ready
        }

        // Auto-translate trivia when model becomes ready
        viewModelScope.launch {
            translationReady.collect { isReady ->
                if (isReady && _language.value == Language.ID) {
                    val trivia = originalTrivia
                    if (trivia != null) {
                        val translatedText = translationService.translate(trivia)
                        _triviaState.value = UiState.Success(translatedText)
                    }
                }
            }
        }
    }

    fun fetchPublicRecipes() {
        viewModelScope.launch {
            _publicRecipesState.value = UiState.Loading
            val result = getPublicRecipesUseCase()
            result.onSuccess { data ->
                _publicRecipes.value = data
                _publicRecipesState.value = UiState.Success(data)
            }.onFailure { error ->
                _publicRecipesState.value = UiState.Error(
                    error.message ?: "Gagal memuat resep. Periksa koneksi internet."
                )
            }
        }
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

    fun fetchRecipeDetail(id: String) {
        viewModelScope.launch {
            _recipeDetailState.value = UiState.Loading
            val result = getRecipeDetailUseCase(id)
            result.onSuccess { detail ->
                originalDetail = detail
                // Immediately show the original detail first so UI doesn't block
                _recipeDetailState.value = UiState.Success(detail)
                
                // Auto-translate in background if language is Indonesian and recipe is from API
                if (_language.value == Language.ID && !detail.isPersonal) {
                    translateAndSetDetail(detail)
                }
            }.onFailure { error ->
                _recipeDetailState.value = UiState.Error(
                    error.message ?: "Gagal memuat detail resep."
                )
            }
        }
    }

    private suspend fun translateAndSetDetail(detail: RecipeDetail) {
        _isTranslating.value = true
        try {
            val translatedName = translationService.translate(detail.name)
            val translatedSummary = translationService.translate(detail.summary)
            val translatedDesc = translationService.translate(detail.description)

            val translatedIngredients = detail.ingredients.map { ing ->
                Ingredient(
                    name = translationService.translate(ing.name),
                    quantity = ing.quantity,
                    unit = ing.unit
                )
            }

            val translatedSteps = detail.steps.map { step ->
                CookingStep(
                    stepNumber = step.stepNumber,
                    instruction = translationService.translate(step.instruction)
                )
            }

            val translatedDetail = detail.copy(
                name = translatedName,
                summary = translatedSummary,
                description = translatedDesc,
                ingredients = translatedIngredients,
                steps = translatedSteps
            )

            _recipeDetailState.value = UiState.Success(translatedDetail)
        } catch (_: Exception) {
            // Fallback to original
            _recipeDetailState.value = UiState.Success(detail)
        } finally {
            _isTranslating.value = false
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

    fun toggleBookmark(recipeId: String, currentlyBookmarked: Boolean) {
        viewModelScope.launch {
            val newStatus = !currentlyBookmarked
            toggleBookmarkUseCase(recipeId, newStatus)
            
            val currentState = _recipeDetailState.value
            if (currentState is UiState.Success && currentState.data.id == recipeId) {
                val updatedDetail = currentState.data.copy(isBookmarked = newStatus)
                _recipeDetailState.value = UiState.Success(updatedDetail)
                originalDetail = originalDetail?.copy(isBookmarked = newStatus)
            }
        }
    }

    fun searchRecipes(allRecipes: List<Recipe>, query: String, category: String): List<Recipe> {
        return searchRecipesUseCase(allRecipes, query, category)
    }

    fun onLanguageChanged(newLang: Language) {
        if (_language.value == newLang) return
        _language.value = newLang

        // Re-translate current detail if we have one
        val detail = originalDetail
        val currentState = _recipeDetailState.value
        if (detail != null && currentState is UiState.Success && !detail.isPersonal) {
            viewModelScope.launch {
                if (newLang == Language.ID) {
                    // Show English original immediately with translating indicator
                    _recipeDetailState.value = UiState.Success(detail)
                    translateAndSetDetail(detail)
                } else {
                    // Switch back to English original
                    _recipeDetailState.value = UiState.Success(detail)
                }
            }
        }

        // Re-translate trivia
        val trivia = originalTrivia
        if (trivia != null) {
            viewModelScope.launch {
                if (newLang == Language.ID) {
                    val translatedTrivia = translationService.translate(trivia)
                    _triviaState.value = UiState.Success(translatedTrivia)
                } else {
                    _triviaState.value = UiState.Success(trivia)
                }
            }
        }
    }



    fun fetchTrivia() {
        viewModelScope.launch {
            _triviaState.value = UiState.Loading
            try {
                var triviaText = getRandomTriviaUseCase()
                originalTrivia = triviaText
                
                // Translate if language is Indonesian
                if (_language.value == Language.ID) {
                    triviaText = translationService.translate(triviaText)
                }
                
                _triviaState.value = UiState.Success(triviaText)
            } catch (e: Exception) {
                _triviaState.value = UiState.Error(e.message ?: "Failed to fetch trivia")
            }
        }
    }
}