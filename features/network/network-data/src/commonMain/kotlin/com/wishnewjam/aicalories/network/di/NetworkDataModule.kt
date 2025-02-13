package com.wishnewjam.aicalories.network.di

import com.wishnewjam.aicalories.network.data.ApiKey
import com.wishnewjam.aicalories.network.data.NetworkRepositoryImpl
import com.wishnewjam.aicalories.network.domain.NetworkRepository
import org.koin.dsl.module

val networkDataModule = module {
    single<NetworkRepository> {
        NetworkRepositoryImpl(httpClient = get(), apiKey = get<ApiKey>().apiKey)
    }

    single<ApiKey> {
        // TODO: hide
        ApiKey("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")
    }
}