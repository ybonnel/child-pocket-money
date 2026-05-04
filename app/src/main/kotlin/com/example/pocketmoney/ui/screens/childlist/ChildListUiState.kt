package com.example.pocketmoney.ui.screens.childlist

import com.example.pocketmoney.domain.model.Child

data class ChildWithBalance(
    val child: Child,
    val balanceCents: Long,
)

data class ChildListUiState(
    val children: List<ChildWithBalance> = emptyList(),
    val isLoading: Boolean = true,
    val currencyCode: String = "EUR",
)
