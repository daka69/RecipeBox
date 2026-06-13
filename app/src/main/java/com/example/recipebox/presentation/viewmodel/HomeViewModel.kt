package com.example.recipebox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebox.data.translation.TranslationService
import com.example.recipebox.domain.model.Recipe
import com.example.recipebox.domain.usecase.GetPublicRecipesUseCase
import com.example.recipebox.domain.usecase.GetRandomTriviaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPublicRecipesUseCase: GetPublicRecipesUseCase,
    private val getRandomTriviaUseCase: GetRandomTriviaUseCase,
    private val translationService: TranslationService
) : ViewModel() {

    private val _publicRecipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val publicRecipesState: StateFlow<UiState<List<Recipe>>> = _publicRecipesState.asStateFlow()

    private val _triviaState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val triviaState: StateFlow<UiState<String>> = _triviaState.asStateFlow()

    private val _language = MutableStateFlow(Language.EN)

    private val _translationReady = MutableStateFlow(false)
    val translationReady: StateFlow<Boolean> = _translationReady.asStateFlow()

    private var originalTrivia: String? = null

    init {
        fetchPublicRecipes()
        fetchTrivia()
        
        viewModelScope.launch {
            val ready = translationService.ensureModelDownloaded()
            _translationReady.value = ready
        }

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
                _publicRecipesState.value = UiState.Success(data)
            }.onFailure { error ->
                _publicRecipesState.value = UiState.Error(
                    error.message ?: "Failed to load recipes. Please check your internet connection."
                )
            }
        }
    }

    fun fetchTrivia() {
        viewModelScope.launch {
            _triviaState.value = UiState.Loading
            try {
                var triviaText = getRandomTriviaUseCase()
                originalTrivia = triviaText
                
                if (_language.value == Language.ID) {
                    triviaText = translationService.translate(triviaText)
                }
                
                _triviaState.value = UiState.Success(triviaText)
            } catch (e: Exception) {
                _triviaState.value = UiState.Error(e.message ?: "Failed to fetch trivia")
            }
        }
    }

    fun onLanguageChanged(newLang: Language) {
        if (_language.value == newLang) return
        _language.value = newLang

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
}
