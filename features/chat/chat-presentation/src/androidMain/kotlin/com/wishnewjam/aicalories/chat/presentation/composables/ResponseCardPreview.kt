package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wishnewjam.aicalories.chat.presentation.model.ChatResponseModelUi

@Preview(showBackground = true)
@Composable
fun PreviewResponseCardError() {
    val errorResponse = ChatResponseModelUi(
        error = "Something went wrong",
        foodName = null,
        comment = "Unable to fetch food data"
    )
    ResponseCard(
        chatResponse = errorResponse,
        isClickable = false,
        onSaveClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewResponseCardNotFound() {
    val notFoundResponse = ChatResponseModelUi(
        error = null,
        foodName = null,
        comment = "Food not found"
    )
    ResponseCard(
        chatResponse = notFoundResponse,
        isClickable = false,
        onSaveClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewResponseCardFood() {
    val foodResponse = ChatResponseModelUi(
        error = null,
        foodName = "Pizza",
        comment = "A delicious slice of pizza",
        calories = "4234",
        weight = "14314g"
    )
    ResponseCard(
        chatResponse = foodResponse,
        isClickable = true,
        onSaveClick = {}
    )
}
