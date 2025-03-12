package com.wishnewjam.aicalories.chat.data

import com.wishnewjam.aicalories.chat.data.model.ChatCompletionRequest
import com.wishnewjam.aicalories.chat.data.model.ChatCompletionResponse
import com.wishnewjam.aicalories.chat.data.model.ChatResponseMapper
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.logging.Logger
import com.wishnewjam.aicalories.network.data.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ChatRepositoryImpl(
    private val networkRepo: NetworkClient,
    private val chatResponseMapper: ChatResponseMapper,
    private val chatRequestBuilder: GptRequestBuilder,
    private val logger: Logger,
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

                // Map the content to our domain modelpeaca
                val value = chatResponseMapper(content)
                value.date = LocalDateTime.now()
                Result.success(value)
            } catch (e: Exception) {
                logger.e(e) {
                    "Error mapping response: ${e.message}"
                }
                Result.failure(
                    Exception("Mapper error: ${e.message}, response: $response")
                )
            }
        } catch (e: Exception) {
            logger.e(e) {
                "Error request: ${e.message}"
            }
            Result.failure(
                Exception("Request error: ${e.message}")
            )
        }
    }

    private val _modelsFlow = MutableStateFlow<List<ChatResponseModel>>(listOf())

    override fun getHistory(): Flow<List<ChatResponseModel>> = _modelsFlow.asStateFlow()

    override fun saveChatResponse(model: ChatResponseModel) {
        val updatedList = _modelsFlow.value.toMutableList().apply {
            add(model)
        }
        _modelsFlow.value = updatedList
    }
}

fun LocalDateTime.Companion.now(): LocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())