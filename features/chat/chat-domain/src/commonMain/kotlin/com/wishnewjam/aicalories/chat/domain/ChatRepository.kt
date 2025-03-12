package com.wishnewjam.aicalories.chat.domain

import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatResponse(message: String): Result<ChatResponseModel>
    fun getHistory(): Flow<List<ChatResponseModel>>
    fun saveChatResponse(model: ChatResponseModel)
}