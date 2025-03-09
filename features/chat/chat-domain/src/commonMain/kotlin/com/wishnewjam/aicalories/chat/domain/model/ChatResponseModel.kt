package com.wishnewjam.aicalories.chat.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponseModel(
    val foodName: String? = null,
    val calories: Int? = null,
    val weight: Int? = null,
    val comment: String? = null,
    val error: String? = null,
    var date: LocalDateTime? = null,
) {
    companion object {
        fun empty() = ChatResponseModel()
    }
}