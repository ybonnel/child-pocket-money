package com.ybonnel.childpocketmoney.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ChildListRoute

@Serializable
data class ChildDetailRoute(val childId: Long)

@Serializable
data class ChildEditRoute(val childId: Long? = null)

@Serializable
data class TransactionEditRoute(
    val childId: Long,
    val initialSign: Int = 1  // +1 = credit, -1 = debit
)

@Serializable
data object SettingsRoute
