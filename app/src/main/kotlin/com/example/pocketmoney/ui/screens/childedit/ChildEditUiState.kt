package com.example.pocketmoney.ui.screens.childedit

import com.example.pocketmoney.ui.theme.DefaultChildColor
import kotlinx.datetime.DayOfWeek

data class ChildEditUiState(
    val childId: Long? = null,
    val name: String = "",
    val colorArgb: Int = DefaultChildColor,
    val weeklyAllowanceStr: String = "0.00",
    val allowanceDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val allowanceActive: Boolean = true,
    val nameError: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val savedSuccessfully: Boolean = false,
)
