package com.wishnewjam.aicalories.network.domain

import io.ktor.client.statement.HttpResponse

interface NetworkRepository {
    suspend fun postData(url: String, body: String): HttpResponse
}