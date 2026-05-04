package com.ybonnel.childpocketmoney.ui.screens.childdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ybonnel.childpocketmoney.data.preferences.UserPreferencesRepository
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.usecase.balance.ObserveBalanceUseCase
import com.ybonnel.childpocketmoney.domain.usecase.child.ObserveChildUseCase
import com.ybonnel.childpocketmoney.domain.usecase.transaction.DeleteTransactionUseCase
import com.ybonnel.childpocketmoney.domain.usecase.transaction.ObserveTransactionsUseCase
import com.ybonnel.childpocketmoney.domain.usecase.transaction.ReinsertTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChildDetailViewModel @Inject constructor(
    private val observeChild: ObserveChildUseCase,
    private val observeTransactions: ObserveTransactionsUseCase,
    private val observeBalance: ObserveBalanceUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val reinsertTransaction: ReinsertTransactionUseCase,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChildDetailUiState())
    val uiState: StateFlow<ChildDetailUiState> = _uiState.asStateFlow()

    private var childId: Long = -1L

    fun loadChild(childId: Long) {
        if (this.childId == childId) return
        this.childId = childId

        viewModelScope.launch {
            combine(
                observeChild(childId),
                observeTransactions(childId),
                observeBalance(childId),
                preferencesRepository.preferences,
            ) { child, transactions, balance, prefs ->
                ChildDetailUiState(
                    child = child,
                    balance = balance,
                    transactions = transactions,
                    isLoading = false,
                    currencyCode = prefs.currencyCode,
                )
            }.collect { state ->
                _uiState.update { current ->
                    // Preserve ephemeral UI state not in DB
                    state.copy(
                        pendingDelete = current.pendingDelete,
                        error = current.error,
                    )
                }
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                deleteTransactionUseCase(transaction)
                // Replace pending delete — if the user swipes a second item before
                // acting on the first snackbar, the first is simply discarded
                // (the DB delete already happened; the snackbar for A closes and B appears).
                _uiState.update { it.copy(pendingDelete = transaction, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erreur lors de la suppression") }
            }
        }
    }

    fun undoDelete() {
        val tx = _uiState.value.pendingDelete ?: return
        _uiState.update { it.copy(pendingDelete = null) }
        viewModelScope.launch {
            try {
                reinsertTransaction(tx)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Annulation impossible") }
            }
        }
    }

    fun clearPendingDelete() {
        _uiState.update { it.copy(pendingDelete = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
