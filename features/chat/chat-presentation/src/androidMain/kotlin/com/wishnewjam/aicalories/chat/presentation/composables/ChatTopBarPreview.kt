@file:OptIn(ExperimentalMaterial3Api::class)

package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview(showBackground = true)
@Composable
fun PreviewChatTopBar() {
    MaterialTheme {
        ChatTopBar(scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior())
    }
}
