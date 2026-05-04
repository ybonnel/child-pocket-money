package com.example.pocketmoney.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.data.preferences.AppTheme
import com.example.pocketmoney.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val currencyCode: String = "EUR",
    val theme: AppTheme = AppTheme.SYSTEM,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                _uiState.update {
                    it.copy(
                        currencyCode = prefs.currencyCode,
                        theme = prefs.theme,
                    )
                }
            }
        }
    }

    fun setCurrency(code: String) {
        viewModelScope.launch {
            preferencesRepository.setCurrency(code)
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }
}
