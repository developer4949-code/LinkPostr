package com.linkpostr.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.linkpostr.app.data.LinkPostrRepository
import com.linkpostr.app.ui.LinkPostrScreen
import com.linkpostr.app.ui.LinkPostrViewModel
import com.linkpostr.app.ui.LinkPostrViewModelFactory
import com.linkpostr.app.ui.theme.LinkPostrTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LinkPostrViewModel by viewModels {
        LinkPostrViewModelFactory(LinkPostrRepository.create())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LinkPostrTheme {
                LinkPostrScreen(viewModel = viewModel)
            }
        }
    }
}

