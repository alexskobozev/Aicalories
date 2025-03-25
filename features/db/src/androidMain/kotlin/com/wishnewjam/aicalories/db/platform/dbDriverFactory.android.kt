package com.wishnewjam.aicalories.db.platform

import com.wishnewjam.aicalories.db.cache.AndroidDatabaseDriverFactory
import com.wishnewjam.aicalories.db.cache.DatabaseDriverFactory
import org.koin.mp.KoinPlatform.getKoin

actual fun dbDriverFactory(): DatabaseDriverFactory {
    return AndroidDatabaseDriverFactory(getKoin().get()) // FIXME: no context here https://stackoverflow.com/a/77378735
}