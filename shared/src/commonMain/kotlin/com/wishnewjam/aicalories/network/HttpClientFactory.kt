package com.wishnewjam.aicalories.network

import com.wishnewjam.aicalories.platformEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(): HttpClient {
        return HttpClient(platformEngine()) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            defaultRequest {
                header("Accept", "application/json")
            }
        }
    }
}