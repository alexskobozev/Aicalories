package com.wishnewjam.aicalories.chat.data

import com.wishnewjam.aicalories.chat.data.model.ChatCompletionRequest
import com.wishnewjam.aicalories.chat.data.model.ChatCompletionResponse
import com.wishnewjam.aicalories.chat.data.model.ChatMessage
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.network.data.NetworkClient

class ChatRepositoryImpl(
    private val networkRepo: NetworkClient
) : ChatRepository {
    override suspend fun getChatResponse(message: String): String {
        val response =
            networkRepo.postData<ChatCompletionRequest, ChatCompletionResponse>(
                url = "",
                body = ChatCompletionRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        ChatMessage(role = "system", content = "You are a helpful assistant."),
                        ChatMessage(
                            role = "user",
                            content = message
                        )
                    ),
                    temperature = 0.7
                ),
            )
        return "Chat says: ${response.choices[0].message.content}"
    }
}