package com.ybonnel.childpocketmoney.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ybonnel.childpocketmoney.data.preferences.AppTheme
import com.ybonnel.childpocketmoney.domain.usecase.balance.ProcessDueAllowancesUseCase
import com.ybonnel.childpocketmoney.ui.navigation.PocketMoneyNavHost
import com.ybonnel.childpocketmoney.ui.theme.PocketMoneyTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Root composable that wraps theme and navigation.
 * The AppRootViewModel triggers ProcessDueAllowancesUseCase on creation.
 */
@Composable
fun PocketMoneyAppRoot(
    appTheme: AppTheme = AppTheme.SYSTEM,
    viewModel: AppRootViewModel = hiltViewModel(),
) {
    PocketMoneyTheme(appTheme = appTheme) {
        PocketMoneyNavHost()
    }
}

@HiltViewModel
class AppRootViewModel @Inject constructor(
    private val processAllowances: ProcessDueAllowancesUseCase,
) : androidx.lifecycle.ViewModel() {

    init {
        // Process due allowances on startup. viewModelScope handles lifecycle correctly.
        // The use case is idempotent, so running it here in addition to WorkManager is safe.
        viewModelScope.launch {
            processAllowances()
        }
    }
}
