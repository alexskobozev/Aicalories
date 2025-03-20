package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.wishnewjam.aicalories.chat.presentation.ChatViewModel
import com.wishnewjam.aicalories.resources.Res
import com.wishnewjam.aicalories.resources.analyzing_product
import com.wishnewjam.aicalories.resources.input_label
import com.wishnewjam.aicalories.resources.input_placeholder
import com.wishnewjam.aicalories.resources.send_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatInputSection(
    inputText: String,
    inputIsValid: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    chatState: ChatViewModel.ModelState,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onTextChange,
            label = {
                Text(
                    stringResource(Res.string.input_label),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeholder = {
                Text(
                    stringResource(Res.string.input_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            trailingIcon = {
                val alpha by animateFloatAsState(
                    targetValue = if (inputIsValid) 1f else 0.5f,
                    label = "Send button alpha"
                )

                IconButton(
                    onClick = { if (inputIsValid) onSendClick() },
                    enabled = chatState != ChatViewModel.ModelState.Loading && inputIsValid,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .alpha(alpha)
                ) {
                    if (chatState == ChatViewModel.ModelState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Send,
                            contentDescription = stringResource(Res.string.send_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = chatState == ChatViewModel.ModelState.Loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(
                    text = stringResource(Res.string.analyzing_product),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
