package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.wishnewjam.aicalories.chat.presentation.model.ChatResponseModelUi

@Composable
fun ChatHistoryList(otherModels: List<ChatResponseModelUi>) {
    LazyColumn {
        items(
            items = otherModels,
            key = { it.date.toString() }
        ) { chatResponse ->
            ResponseCard(
                chatResponse = chatResponse,
                isClickable = false,
                onSaveClick = {}
            )
        }
    }
}
