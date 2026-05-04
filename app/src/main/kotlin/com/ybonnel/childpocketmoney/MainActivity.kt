package com.ybonnel.childpocketmoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybonnel.childpocketmoney.data.preferences.AppTheme
import com.ybonnel.childpocketmoney.data.preferences.UserPreferencesRepository
import com.ybonnel.childpocketmoney.ui.PocketMoneyAppRoot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by preferencesRepository.preferences
                .map { it.theme }
                .collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM)
            PocketMoneyAppRoot(appTheme = theme)
        }
    }
}
