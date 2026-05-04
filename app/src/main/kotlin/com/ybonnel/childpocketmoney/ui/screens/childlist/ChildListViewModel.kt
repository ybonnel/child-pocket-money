package com.ybonnel.childpocketmoney.ui.screens.childlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ybonnel.childpocketmoney.data.preferences.UserPreferencesRepository
import com.ybonnel.childpocketmoney.domain.usecase.balance.ObserveBalanceUseCase
import com.ybonnel.childpocketmoney.domain.usecase.child.ObserveChildrenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChildListViewModel @Inject constructor(
    observeChildren: ObserveChildrenUseCase,
    private val observeBalance: ObserveBalanceUseCase,
    preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<ChildListUiState> = combine(
        observeChildren().flatMapLatest { children ->
            if (children.isEmpty()) {
                flowOf(emptyList<ChildWithBalance>())
            } else {
                val balanceFlows = children.map { child ->
                    observeBalance(child.id).map { balance ->
                        ChildWithBalance(child, balance.cents)
                    }
                }
                combine(balanceFlows) { it.toList() }
            }
        },
        preferencesRepository.preferences,
    ) { childrenWithBalances, prefs ->
        ChildListUiState(
            children = childrenWithBalances,
            isLoading = false,
            currencyCode = prefs.currencyCode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChildListUiState(isLoading = true),
    )
}
