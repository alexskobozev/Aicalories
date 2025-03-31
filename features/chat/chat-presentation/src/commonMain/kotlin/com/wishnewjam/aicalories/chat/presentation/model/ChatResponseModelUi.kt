package com.wishnewjam.aicalories.chat.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class ChatResponseModelUi(
    val foodName: String? = null,
    val calories: String? = null,
    val weight: String? = null,
    val comment: String? = null,
    val error: String? = null,
    var date: String? = null,
)