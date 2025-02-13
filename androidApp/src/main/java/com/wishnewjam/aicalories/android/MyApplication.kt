package com.wishnewjam.aicalories.android

import android.app.Application
import com.wishnewjam.aicalories.network.di.networkDataModule
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                networkDataModule,
            )
        }
    }
}