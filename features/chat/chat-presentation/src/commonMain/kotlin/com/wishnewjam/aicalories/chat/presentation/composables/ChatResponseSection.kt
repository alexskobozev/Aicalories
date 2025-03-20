package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wishnewjam.aicalories.chat.presentation.ChatViewModel

@Composable
fun ChatResponseSection(
    chatState: ChatViewModel.ModelState,
    scrollState: androidx.compose.foundation.ScrollState,
    chatViewModel: ChatViewModel,
) {
    Box(
        modifier = Modifier
//            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        when (chatState) {
            is ChatViewModel.ModelState.Success -> {
                chatState.selectedModel?.let { chatResponse ->
                    ResponseCard(
                        chatResponse = chatResponse,
                        isClickable = true,
                        onSaveClick = { chatViewModel.saveResponse() }
                    )
                }
            }

            is ChatViewModel.ModelState.ErrorState -> {
                ErrorResponseContent(errorMessage = chatState.message)
            }

            else -> {
                EmptyStateMessage()
            }
        }
    }
}
