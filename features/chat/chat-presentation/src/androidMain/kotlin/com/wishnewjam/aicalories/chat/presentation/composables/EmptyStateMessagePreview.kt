package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview(showBackground = true)
@Composable
fun PreviewEmptyStateMessage() {
    MaterialTheme {
        Surface {
            EmptyStateMessage()
        }
    }
}
