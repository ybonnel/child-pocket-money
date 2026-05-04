package com.example.pocketmoney.ui.screens.childdetail

import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.domain.model.Child
import com.example.pocketmoney.domain.model.Transaction

data class ChildDetailUiState(
    val child: Child? = null,
    val balance: Money = Money.Zero,
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val currencyCode: String = "EUR",
    val deletedTransaction: Transaction? = null,
)
