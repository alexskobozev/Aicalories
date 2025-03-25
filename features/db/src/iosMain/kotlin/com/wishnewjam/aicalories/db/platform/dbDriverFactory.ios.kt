package com.wishnewjam.aicalories.db.platform

import com.wishnewjam.aicalories.db.cache.DatabaseDriverFactory
import com.wishnewjam.aicalories.db.cache.IOSDatabaseDriverFactory

actual fun dbDriverFactory(): DatabaseDriverFactory {
    return IOSDatabaseDriverFactory()
}