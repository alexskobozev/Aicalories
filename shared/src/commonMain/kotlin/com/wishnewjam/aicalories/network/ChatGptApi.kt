package com.wishnewjam.aicalories.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ChatGptApi(private val httpClient: HttpClient) {
    suspend fun sendChatCompletionRequest(
        apiKey: String,
        request: ChatCompletionRequest
    ): ChatCompletionResponse {
        return httpClient.post("https://api.openai.com/v1/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}