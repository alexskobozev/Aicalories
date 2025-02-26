package com.wishnewjam.aicalories.chat.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponseModel(
    val foodName: String?,
    val calories: Int?,
    val weight: Int?,
    val comment: String?,
    val error: String? = null,
    val date: LocalDateTime? = null,
)