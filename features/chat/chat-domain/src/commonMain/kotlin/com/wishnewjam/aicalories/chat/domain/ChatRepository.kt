package com.wishnewjam.aicalories.chat.domain

interface ChatRepository {
    suspend fun getChatResponse(message: String): String
}