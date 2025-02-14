package com.wishnewjam.aicalories.network.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class NetworkClient(
    val httpClient: HttpClient,
    val apiKey: String,
) {
    suspend inline fun <reified B, reified R> postData(url: String, body: B): R =
        httpClient.post(url) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<R>()
}