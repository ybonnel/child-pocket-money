package com.example.pocketmoney.ui.screens.childdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.data.preferences.UserPreferencesRepository
import com.example.pocketmoney.domain.model.Transaction
import com.example.pocketmoney.domain.repository.TransactionRepository
import com.example.pocketmoney.domain.usecase.balance.ObserveBalanceUseCase
import com.example.pocketmoney.domain.usecase.child.ObserveChildUseCase
import com.example.pocketmoney.domain.usecase.transaction.DeleteTransactionUseCase
import com.example.pocketmoney.domain.usecase.transaction.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val transactionRepository: TransactionRepository,
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
                    state.copy(deletedTransaction = current.deletedTransaction)
                }
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
            _uiState.update { it.copy(deletedTransaction = transaction) }
        }
    }

    fun undoDelete() {
        val tx = _uiState.value.deletedTransaction ?: return
        _uiState.update { it.copy(deletedTransaction = null) }
        viewModelScope.launch {
            // Re-insert the transaction
            transactionRepository.insert(tx)
        }
    }

    fun clearDeletedTransaction() {
        _uiState.update { it.copy(deletedTransaction = null) }
    }
}
