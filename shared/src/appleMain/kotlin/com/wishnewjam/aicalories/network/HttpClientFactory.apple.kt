package com.wishnewjam.aicalories.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*


actual fun platformEngine(): HttpClientEngine = Darwin.create()