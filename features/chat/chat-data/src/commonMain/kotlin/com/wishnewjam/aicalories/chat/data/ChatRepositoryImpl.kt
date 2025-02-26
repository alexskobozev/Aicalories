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
        val requestResult =
            networkRepo.postData<ChatCompletionRequest, Result<ChatCompletionResponse>>(
                url = "https://api.openai.com/v1/chat/completions",
                body = ChatCompletionRequest(
                    model = "gpt-3.5-turbo",
                    messages = chatRequestBuilder.buildChatMessageList(message),
                    temperature = 0.7
                ),
            ).getOrElse { th: Throwable ->
                return Result.failure(
                    Exception("Request error: ${th.message}")
                )
            }
        return runCatching {
            chatResponseMapper(requestResult.choices[0].message.content)
        }.recoverCatching { th: Throwable ->
            return Result.failure(
                Exception("Mapper error: ${th.message}, response: $requestResult")
            )
        }
    }
}