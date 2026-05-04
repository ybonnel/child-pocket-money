package com.ybonnel.childpocketmoney.ui.screens.childlist

import com.ybonnel.childpocketmoney.domain.model.Child

data class ChildWithBalance(
    val child: Child,
    val balanceCents: Long,
)

data class ChildListUiState(
    val children: List<ChildWithBalance> = emptyList(),
    val isLoading: Boolean = true,
    val currencyCode: String = "EUR",
)
