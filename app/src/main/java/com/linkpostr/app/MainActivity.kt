package com.linkpostr.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.linkpostr.app.data.LinkPostrRepository
import com.linkpostr.app.data.ThemePreferences
import com.linkpostr.app.ui.LinkPostrScreen
import com.linkpostr.app.ui.LinkPostrViewModel
import com.linkpostr.app.ui.LinkPostrViewModelFactory
import com.linkpostr.app.ui.theme.LinkPostrTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {

    private val viewModel: LinkPostrViewModel by viewModels {
        LinkPostrViewModelFactory(
            repository = LinkPostrRepository.create(),
            themePreferences = ThemePreferences(applicationContext),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LinkPostrTheme(themeOption = uiState.selectedAppTheme) {
                LinkPostrScreen(viewModel = viewModel)
            }
        }
    }
}
