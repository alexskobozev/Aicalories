package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wishnewjam.aicalories.chat.presentation.ChatViewModel

@Preview(showBackground = true)
@Composable
fun PreviewChatInputSectionIdle() {
    MaterialTheme {
        Surface {
            ChatInputSection(
                inputText = "Hello, Chat!",
                inputIsValid = true,
                onTextChange = {},
                onSendClick = {},
                // Assuming Idle is a valid state in your ChatViewModel.ModelState
                chatState = ChatViewModel.ModelState.Idle
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatInputSectionLoading() {
    MaterialTheme {
        Surface {
            ChatInputSection(
                inputText = "Processing...",
                inputIsValid = true,
                onTextChange = {},
                onSendClick = {},
                chatState = ChatViewModel.ModelState.Loading
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatInputSectionInvalid() {
    MaterialTheme {
        Surface {
            ChatInputSection(
                inputText = "",
                inputIsValid = false,
                onTextChange = {},
                onSendClick = {},
                chatState = ChatViewModel.ModelState.Idle
            )
        }
    }
}
