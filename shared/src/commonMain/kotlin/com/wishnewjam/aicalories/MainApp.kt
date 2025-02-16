package com.wishnewjam.aicalories

import ChatScreen
import androidx.compose.runtime.Composable
import com.wishnewjam.aicalories.chat.di.chatDataModule
import com.wishnewjam.aicalories.network.di.networkDataModule
import org.koin.compose.KoinApplication

@Composable
fun MainApp() {
    KoinApplication(
        application = {
            modules(
                networkDataModule,
                chatDataModule,
            )
        }
    ) {
        ChatScreen()
    }
}