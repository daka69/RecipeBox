package com.example.recipebox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class Language(val label: String, val code: String) {
    ID("Bahasa Indonesia", "id"),
    EN("English", "en")
}

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow(Language.EN)
    val language: StateFlow<Language> = _language.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == Language.ID) Language.EN else Language.ID
        _language.value = newLang
        
        // Update App Locale using Android standard
        val localeList = LocaleListCompat.forLanguageTags(newLang.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
        val localeList = LocaleListCompat.forLanguageTags(lang.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
