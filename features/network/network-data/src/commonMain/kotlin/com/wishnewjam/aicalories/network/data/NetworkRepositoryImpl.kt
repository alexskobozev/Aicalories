package com.wishnewjam.aicalories.network.data

import com.wishnewjam.aicalories.network.domain.NetworkRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class NetworkRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
) : NetworkRepository {
    override suspend fun postData(url: String, body: String): HttpResponse =
        httpClient.post(url) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
}