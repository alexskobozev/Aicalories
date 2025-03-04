package com.wishnewjam.aicalories.logging

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class Logger {
    companion object {
        fun init() {
            Napier.base(DebugAntilog())
        }
    }

    fun d(message: String, throwable: Throwable? = null, tag: String? = null) {
        Napier.d(message, throwable, tag)
    }

    fun e(throwable: Throwable? = null, tag: String? = null, message: () -> String) {
        Napier.e(throwable, tag, message)
    }
}