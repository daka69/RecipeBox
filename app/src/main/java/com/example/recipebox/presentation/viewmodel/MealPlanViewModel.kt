package com.example.recipebox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebox.domain.model.MealPlan
import com.example.recipebox.domain.usecase.GetMealPlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val getMealPlanUseCase: GetMealPlanUseCase
) : ViewModel() {

    private val _mealPlanState = MutableStateFlow<UiState<MealPlan>>(UiState.Idle)
    val mealPlanState: StateFlow<UiState<MealPlan>> = _mealPlanState.asStateFlow()

    fun fetchMealPlan(targetCalories: Int? = null, diet: String? = null) {
        viewModelScope.launch {
            _mealPlanState.value = UiState.Loading
            try {
                val plan = getMealPlanUseCase(targetCalories, diet)
                _mealPlanState.value = UiState.Success(plan)
            } catch (e: Exception) {
                _mealPlanState.value = UiState.Error(e.message ?: "Failed to fetch meal plan")
            }
        }
    }
}
