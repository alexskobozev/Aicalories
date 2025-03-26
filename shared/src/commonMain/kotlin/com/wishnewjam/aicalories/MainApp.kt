package com.wishnewjam.aicalories

import androidx.compose.runtime.Composable
import com.wishnewjam.aicalories.chat.di.chatDataModule
import com.wishnewjam.aicalories.chat.presentation.composables.ChatScreen
import com.wishnewjam.aicalories.db.di.mealsDatabaseImplModule
import com.wishnewjam.aicalories.logging.di.loggerModule
import com.wishnewjam.aicalories.network.di.networkDataModule
import org.koin.compose.KoinApplication
import org.koin.core.module.Module

@Composable
fun MainApp(contextProvider: Module) {
    KoinApplication(
        application = {
            modules(
                contextProvider,
                networkDataModule,
                chatDataModule,
                loggerModule,
                mealsDatabaseImplModule
            )
        }
    ) {
        ChatScreen()
    }
}