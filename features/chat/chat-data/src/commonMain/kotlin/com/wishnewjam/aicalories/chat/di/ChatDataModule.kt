package com.wishnewjam.aicalories.chat.di

import com.wishnewjam.aicalories.chat.data.ChatRepositoryImpl
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import org.koin.dsl.module

val chatDataModule = module {
    single<ChatRepository> {
        ChatRepositoryImpl(networkRepo = get())
    }
}