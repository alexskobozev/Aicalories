package com.wishnewjam.aicalories.network.di

import com.wishnewjam.aicalories.network.data.ApiKey
import com.wishnewjam.aicalories.network.data.NetworkClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import platformEngine

val networkDataModule = module {
    single<HttpClient> {
        HttpClient(platformEngine()) {
            install(Logging){
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            install(ContentNegotiation){
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }
    single<NetworkClient> {
        NetworkClient(httpClient = get(), apiKey = get<ApiKey>().apiKey)
    }

    single<ApiKey> {
        // TODO: hide
        ApiKey("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")
    }
}