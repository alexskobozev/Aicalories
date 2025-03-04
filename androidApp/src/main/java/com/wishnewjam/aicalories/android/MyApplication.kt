package com.wishnewjam.aicalories.android

import android.app.Application
import com.wishnewjam.aicalories.KoinManager
import com.wishnewjam.aicalories.logging.Logger

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.init()
        KoinManager.doInitKoin()
    }
}