package com.example.pocketmoney.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.pocketmoney.data.preferences.AppTheme
import com.example.pocketmoney.domain.usecase.balance.ProcessDueAllowancesUseCase
import com.example.pocketmoney.ui.navigation.PocketMoneyNavHost
import com.example.pocketmoney.ui.theme.PocketMoneyTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Root composable that wraps theme and navigation.
 * Also triggers ProcessDueAllowancesUseCase on startup.
 */
@Composable
fun PocketMoneyAppRoot(
    appTheme: AppTheme = AppTheme.SYSTEM,
    viewModel: AppRootViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.processAllowances()
    }

    PocketMoneyTheme(appTheme = appTheme) {
        PocketMoneyNavHost()
    }
}

@HiltViewModel
class AppRootViewModel @Inject constructor(
    private val processAllowances: ProcessDueAllowancesUseCase,
) : androidx.lifecycle.ViewModel() {

    suspend fun processAllowances() {
        withContext(Dispatchers.IO) {
            processAllowances.invoke()
        }
    }
}
