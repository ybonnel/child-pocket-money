package com.example.pocketmoney.ui.screens.transactionedit

data class TransactionEditUiState(
    val amountStr: String = "",
    val label: String = "",
    val isCredit: Boolean = true,
    val amountError: Boolean = false,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val currencyCode: String = "EUR",
)
