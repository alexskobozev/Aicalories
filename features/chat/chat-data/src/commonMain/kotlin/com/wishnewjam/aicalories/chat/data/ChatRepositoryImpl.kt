package com.wishnewjam.aicalories.chat.data

import com.wishnewjam.aicalories.chat.data.model.ChatCompletionRequest
import com.wishnewjam.aicalories.chat.data.model.ChatCompletionResponse
import com.wishnewjam.aicalories.chat.data.model.ChatResponseMapper
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.network.data.NetworkClient

class ChatRepositoryImpl(
    private val networkRepo: NetworkClient,
    private val chatResponseMapper: ChatResponseMapper,
    private val chatRequestBuilder: GptRequestBuilder,
) : ChatRepository {
    override suspend fun getChatResponse(message: String): Result<ChatResponseModel> {
        // Build the request
        val request = ChatCompletionRequest(
            model = "gpt-3.5-turbo",
            messages = chatRequestBuilder.buildChatMessageList(message),
            temperature = 0.7
        )

        // Make the API call
        return try {
            val response = networkRepo.postData<ChatCompletionRequest, ChatCompletionResponse>(
                url = "https://api.openai.com/v1/chat/completions",
                body = request
            )

            // Process the response
            try {
                // Extract the content from the first choice
                val content = response.choices.firstOrNull()?.message?.content
                    ?: return Result.failure(Exception("Empty response from API"))

                // Map the content to our domain model
                Result.success(chatResponseMapper(content))
            } catch (e: Exception) {
                Result.failure(
                    Exception("Mapper error: ${e.message}, response: $response")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Request error: ${e.message}")
            )
        }
    }
}