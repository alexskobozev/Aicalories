package com.wishnewjam.aicalories.db.di

import com.wishnewjam.aicalories.db.cache.Database
import com.wishnewjam.aicalories.db.platform.dbDriverFactory
import org.koin.dsl.module

val databaseModule = module {
    single<Database> {
        Database(databaseDriverFactory = dbDriverFactory())
    }
}