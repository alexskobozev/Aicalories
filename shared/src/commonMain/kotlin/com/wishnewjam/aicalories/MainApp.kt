package com.wishnewjam.aicalories

import androidx.compose.runtime.Composable
import com.wishnewjam.aicalories.chat.di.chatDataModule
import com.wishnewjam.aicalories.chat.presentation.composables.ChatScreen
import com.wishnewjam.aicalories.network.di.networkDataModule
import com.wishnewjam.aicalories.logging.di.loggerModule
import org.koin.compose.KoinApplication

@Composable
fun MainApp() {
    KoinApplication(
        application = {
            modules(
                networkDataModule,
                chatDataModule,
                loggerModule
            )
        }
    ) {
        ChatScreen()
    }
}