package com.example.recipebox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebox.data.translation.TranslationService
import com.example.recipebox.domain.model.CookingStep
import com.example.recipebox.domain.model.Ingredient
import com.example.recipebox.domain.model.RecipeDetail
import com.example.recipebox.domain.usecase.GetRecipeDetailUseCase
import com.example.recipebox.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val translationService: TranslationService
) : ViewModel() {

    private val _recipeDetailState = MutableStateFlow<UiState<RecipeDetail>>(UiState.Idle)
    val recipeDetailState: StateFlow<UiState<RecipeDetail>> = _recipeDetailState.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _language = MutableStateFlow(Language.EN)

    private var originalDetail: RecipeDetail? = null

    init {
        viewModelScope.launch {
            translationService.ensureModelDownloaded()
        }
    }

    fun fetchRecipeDetail(id: String) {
        viewModelScope.launch {
            _recipeDetailState.value = UiState.Loading
            val result = getRecipeDetailUseCase(id)
            result.onSuccess { detail ->
                originalDetail = detail
                _recipeDetailState.value = UiState.Success(detail)
                
                if (_language.value == Language.ID && !detail.isPersonal) {
                    translateAndSetDetail(detail)
                }
            }.onFailure { error ->
                _recipeDetailState.value = UiState.Error(
                    error.message ?: "Failed to load recipe detail."
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
            _recipeDetailState.value = UiState.Success(detail)
        } finally {
            _isTranslating.value = false
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

    fun onLanguageChanged(newLang: Language) {
        if (_language.value == newLang) return
        _language.value = newLang

        val detail = originalDetail
        val currentState = _recipeDetailState.value
        if (detail != null && currentState is UiState.Success && !detail.isPersonal) {
            viewModelScope.launch {
                if (newLang == Language.ID) {
                    _recipeDetailState.value = UiState.Success(detail)
                    translateAndSetDetail(detail)
                } else {
                    _recipeDetailState.value = UiState.Success(detail)
                }
            }
        }
    }

    fun forceSetDetail(detail: RecipeDetail) {
        originalDetail = detail
        _recipeDetailState.value = UiState.Success(detail)
    }
}
