@file:OptIn(ExperimentalMaterial3Api::class)

package com.wishnewjam.aicalories.chat.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wishnewjam.aicalories.resources.Res
import com.wishnewjam.aicalories.resources.analyzing_product
import com.wishnewjam.aicalories.resources.app_name
import com.wishnewjam.aicalories.resources.empty_state_message
import com.wishnewjam.aicalories.resources.empty_state_title
import com.wishnewjam.aicalories.resources.error_prefix
import com.wishnewjam.aicalories.resources.error_title
import com.wishnewjam.aicalories.resources.input_label
import com.wishnewjam.aicalories.resources.input_placeholder
import com.wishnewjam.aicalories.resources.not_found_message
import com.wishnewjam.aicalories.resources.not_found_title
import com.wishnewjam.aicalories.resources.save_button
import com.wishnewjam.aicalories.resources.send_button
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen() {
    val chatViewModel: ChatViewModel = koinViewModel()
    val chatResponse by chatViewModel.chatResponse.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(chatResponse) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize().padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize().padding(16.dp)
            ) {
                // Response section
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    if (chatResponse.isNotEmpty()) {
                        ResponseCard(chatResponse = chatResponse)
                    } else {
                        EmptyStateMessage()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text(stringResource(Res.string.input_label)) },
                            placeholder = { Text(stringResource(Res.string.input_placeholder)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    chatViewModel.sendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            enabled = !isLoading && inputText.isNotBlank(),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(4.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = stringResource(Res.string.send_button),
                                    tint = if (inputText.isBlank())
                                        MaterialTheme.colorScheme.outline
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = stringResource(Res.string.analyzing_product),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResponseCard(chatResponse: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        val errorPrefix = stringResource(Res.string.error_prefix)
        val notFoundMessage = stringResource(Res.string.not_found_message)

        when {
            chatResponse.startsWith(errorPrefix) -> {
                ErrorResponseContent(errorMessage = chatResponse)
            }

            chatResponse == notFoundMessage -> {
                NotFoundResponseContent(message = chatResponse)
            }

            else -> {
                // Normal food response
                FoodResponseContent(chatResponse = chatResponse)
            }
        }
    }
}

@Composable
private fun FoodResponseContent(chatResponse: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Extract title and content
        val lines = chatResponse.split("\n\n", limit = 2)
        val title = lines.firstOrNull() ?: ""
        val details = if (lines.size > 1) lines[1] else ""

        // Title with emoji
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Format the details as key-value pairs
        val detailLines = details.split("\n")
        detailLines.forEach { line ->
            if (line.isNotEmpty()) {
                val parts = line.split(":", limit = 2)
                if (parts.size == 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${parts[0]}:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = parts[1].trim(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Comment or other non-key-value content
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ElevatedButton(
                onClick = { /* Add to favorites or save */ }
            ) {
                Text(stringResource(Res.string.save_button))
            }
        }
    }
}

@Composable
private fun ErrorResponseContent(errorMessage: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.error_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(4.dp))

        val errorPrefix = stringResource(Res.string.error_prefix)
        Text(
            text = errorMessage.removePrefix(errorPrefix),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun NotFoundResponseContent(message: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.not_found_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyStateMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(Res.string.empty_state_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.empty_state_message),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}