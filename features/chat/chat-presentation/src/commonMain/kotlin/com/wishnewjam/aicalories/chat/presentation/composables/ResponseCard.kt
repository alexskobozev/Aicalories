package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wishnewjam.aicalories.chat.presentation.model.ChatResponseModelUi

@Composable
fun ResponseCard(
    chatResponse: ChatResponseModelUi,
    isClickable: Boolean,
    onSaveClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                chatResponse.error != null -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                chatResponse.foodName == null -> MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = 0.7f
                )
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        when {
            chatResponse.error != null -> {
                ErrorResponseContent(errorMessage = chatResponse.error)
            }
            chatResponse.foodName == null -> {
                NotFoundResponseContent(message = chatResponse.comment ?: "")
            }
            else -> {
                FoodResponseContent(
                    chatResponse = chatResponse,
                    isClickable = isClickable,
                    onSaveClick = onSaveClick
                )
            }
        }
    }
}
