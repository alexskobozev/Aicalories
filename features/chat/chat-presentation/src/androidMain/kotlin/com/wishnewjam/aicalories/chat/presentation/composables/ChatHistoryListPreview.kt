package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wishnewjam.aicalories.chat.presentation.model.ChatResponseModelUi

@Preview(showBackground = true)
@Composable
fun PreviewChatHistoryList() {
    // Create a list of sample responses
    val sampleHistory = listOf(
        ChatResponseModelUi(
            error = null,
            foodName = "Pizza",
            comment = "A delicious cheesy slice",
            date = System.currentTimeMillis().toString()
        ),
        ChatResponseModelUi(
            error = "Network Error",
            foodName = null,
            comment = "Unable to fetch data",
            date = (System.currentTimeMillis() - 100_000L).toString()
        ),
        ChatResponseModelUi(
            error = null,
            foodName = null,
            comment = "Food not found",
            date = (System.currentTimeMillis() - 200_000L).toString()
        )
    )
    // Render the ChatHistoryList with the sample data
    ChatHistoryList(otherModels = sampleHistory)
}
