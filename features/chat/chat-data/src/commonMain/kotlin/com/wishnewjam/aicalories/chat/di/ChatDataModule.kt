package com.wishnewjam.aicalories.chat.di

import com.wishnewjam.aicalories.chat.data.ChatRepositoryImpl
import com.wishnewjam.aicalories.chat.data.model.ChatResponseMapper
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.chat.presentation.ChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chatDataModule = module {
    single<ChatRepository> {
        ChatRepositoryImpl(networkRepo = get(), chatResponseMapper = get())
    }
    single<ChatResponseMapper> {
        ChatResponseMapper()
    }
//    factory {  }
    viewModel { ChatViewModel(chatRepository = get()) }
}