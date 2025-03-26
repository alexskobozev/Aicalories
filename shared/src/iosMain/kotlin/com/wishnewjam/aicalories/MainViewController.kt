package com.wishnewjam.aicalories

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController {
    MainApp(
        module { }
    )
}