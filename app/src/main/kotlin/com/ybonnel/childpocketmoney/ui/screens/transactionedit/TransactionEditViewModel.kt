package com.ybonnel.childpocketmoney.ui.screens.transactionedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.data.preferences.UserPreferencesRepository
import com.ybonnel.childpocketmoney.domain.usecase.transaction.AddCreditUseCase
import com.ybonnel.childpocketmoney.domain.usecase.transaction.AddDebitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionEditViewModel @Inject constructor(
    private val addCredit: AddCreditUseCase,
    private val addDebit: AddDebitUseCase,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionEditUiState())
    val uiState: StateFlow<TransactionEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                _uiState.update { it.copy(currencyCode = prefs.currencyCode) }
            }
        }
    }

    fun setInitialSign(sign: Int) {
        _uiState.update { it.copy(isCredit = sign > 0) }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amountStr = amount, amountError = false) }
    }

    fun onLabelChange(label: String) {
        _uiState.update { it.copy(label = label) }
    }

    fun onSignToggle(isCredit: Boolean) {
        _uiState.update { it.copy(isCredit = isCredit) }
    }

    fun save(childId: Long) {
        val state = _uiState.value
        val amount = Money.fromString(state.amountStr)
        if (amount == null || amount.cents <= 0) {
            _uiState.update { it.copy(amountError = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                if (state.isCredit) {
                    addCredit(childId = childId, amount = amount, label = state.label)
                } else {
                    addDebit(childId = childId, amount = amount, label = state.label)
                }
                _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "Erreur lors de l'enregistrement")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
