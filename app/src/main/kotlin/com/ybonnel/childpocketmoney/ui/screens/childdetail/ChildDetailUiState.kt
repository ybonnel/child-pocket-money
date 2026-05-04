package com.ybonnel.childpocketmoney.ui.screens.childdetail

import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.domain.model.Child
import com.ybonnel.childpocketmoney.domain.model.Transaction

data class ChildDetailUiState(
    val child: Child? = null,
    val balance: Money = Money.Zero,
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val currencyCode: String = "EUR",
    /** Pending delete waiting for snackbar undo decision — null when none. */
    val pendingDelete: Transaction? = null,
    val error: String? = null,
)
