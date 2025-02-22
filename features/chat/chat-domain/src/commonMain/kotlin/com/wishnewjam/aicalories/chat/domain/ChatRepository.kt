package com.wishnewjam.aicalories.chat.domain

import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel

interface ChatRepository {
    suspend fun getChatResponse(message: String): ChatResponseModel
}