package com.wishnewjam.aicalories.chat.data.model

import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import kotlinx.serialization.json.Json

open class ChatResponseMapper {
    open operator fun invoke(s: String?): ChatResponseModel {
        val responseModel = Json.decodeFromString<ChatResponseModel>(s ?: "")
        // TODO: handle errors
        return responseModel
    }
}