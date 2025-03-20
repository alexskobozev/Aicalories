package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.wishnewjam.aicalories.chat.presentation.ChatViewModel

@Composable
fun ChatLoadingIndicator(chatState: ChatViewModel.ModelState) {
    AnimatedVisibility(
        visible = chatState == ChatViewModel.ModelState.Loading,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            strokeCap = StrokeCap.Round
        )
    }
}
