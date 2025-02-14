package com.wishnewjam.aicalories.android

import android.app.Application
import com.wishnewjam.aicalories.KoinManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinManager.doInitKoin()
    }
}