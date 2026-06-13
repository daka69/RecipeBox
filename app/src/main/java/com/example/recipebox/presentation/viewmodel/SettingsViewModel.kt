package com.example.recipebox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

enum class Language(val label: String, val code: String) {
    ID("Bahasa Indonesia", "id"),
    EN("English", "en")
}


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: com.example.recipebox.data.local.datastore.SettingsDataStore
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow(Language.EN)
    val language: StateFlow<Language> = _language.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.isDarkMode.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
        viewModelScope.launch {
            dataStore.language.collect { langCode ->
                val lang = if (langCode == "id") Language.ID else Language.EN
                if (_language.value != lang) {
                    _language.value = lang
                    val localeList = LocaleListCompat.forLanguageTags(lang.code)
                    AppCompatDelegate.setApplicationLocales(localeList)
                }
            }
        }
    }

    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        viewModelScope.launch { dataStore.saveDarkMode(newMode) }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        viewModelScope.launch { dataStore.saveDarkMode(enabled) }
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == Language.ID) Language.EN else Language.ID
        _language.value = newLang
        
        // Update App Locale using Android standard
        val localeList = LocaleListCompat.forLanguageTags(newLang.code)
        AppCompatDelegate.setApplicationLocales(localeList)

        viewModelScope.launch { dataStore.saveLanguage(newLang.code) }
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
        val localeList = LocaleListCompat.forLanguageTags(lang.code)
        AppCompatDelegate.setApplicationLocales(localeList)

        viewModelScope.launch { dataStore.saveLanguage(lang.code) }
    }
}
