package com.wishnewjam.aicalories.logging.di

import com.wishnewjam.aicalories.logging.Logger
import org.koin.dsl.module

val loggerModule = module {
    single<Logger> {
        Logger()
    }
}