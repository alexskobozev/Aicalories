package com.wishnewjam.aicalories

import com.wishnewjam.aicalories.chat.di.chatDataModule
import com.wishnewjam.aicalories.network.di.networkDataModule
import org.koin.core.context.startKoin

object KoinManager {
    fun doInitKoin() {
        startKoin {
            modules(
                networkDataModule,
                chatDataModule,
            )
        }
    }
}
