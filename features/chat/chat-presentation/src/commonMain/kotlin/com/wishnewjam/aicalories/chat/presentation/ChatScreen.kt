@file:OptIn(ExperimentalMaterial3Api::class)

package com.wishnewjam.aicalories.chat.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wishnewjam.aicalories.chat.presentation.model.ChatResponseModelUi
import com.wishnewjam.aicalories.resources.Res
import com.wishnewjam.aicalories.resources.analyzing_product
import com.wishnewjam.aicalories.resources.app_name
import com.wishnewjam.aicalories.resources.empty_state_message
import com.wishnewjam.aicalories.resources.empty_state_title
import com.wishnewjam.aicalories.resources.error_prefix
import com.wishnewjam.aicalories.resources.error_title
import com.wishnewjam.aicalories.resources.input_label
import com.wishnewjam.aicalories.resources.input_placeholder
import com.wishnewjam.aicalories.resources.not_found_title
import com.wishnewjam.aicalories.resources.save_button
import com.wishnewjam.aicalories.resources.send_button
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen() {
    val chatViewModel: ChatViewModel = koinViewModel()
    val chatState by chatViewModel.modelState.collectAsState()
    val historyState by chatViewModel.historyState.collectAsState()
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var inputText by remember { mutableStateOf("") }
    val inputIsValid by remember { derivedStateOf { inputText.trim().isNotEmpty() } }

    LaunchedEffect(chatState) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Loading indicator
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

                if (historyState is ChatViewModel.ListState.Success) {
                    LazyColumn {
                        items(
                            items = (historyState as ChatViewModel.ListState.Success).otherModels,
                            key = { it.date.toString() },
                        ) {
                            ResponseCard(
                                chatResponse = it,
                                isClickable = false,
                                onSaveClick = {},
                            )
                        }
                    }
                }

                // Response section
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    if (chatState is ChatViewModel.ModelState.Success) {
                        val chatResponse =
                            (chatState as ChatViewModel.ModelState.Success).selectedModel
                        if (chatResponse != null) {
                            ResponseCard(
                                chatResponse = chatResponse,
                                isClickable = true,
                                onSaveClick = { chatViewModel.saveResponse() })
                        }
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
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
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
                                onClick = {
                                    if (inputIsValid) {
                                        chatViewModel.sendMessage(inputText)
                                        inputText = ""
                                    }
                                },
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
        }
    }
}

@Composable
private fun ResponseCard(
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
                chatResponse.error != null -> MaterialTheme.colorScheme.errorContainer.copy(
                    alpha = 0.7f
                )

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

@Composable
private fun FoodResponseContent(
    chatResponse: ChatResponseModelUi,
    isClickable: Boolean,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val title = chatResponse.foodName ?: ""

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chatResponse.calories ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = chatResponse.weight ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }




        Spacer(modifier = Modifier.height(16.dp))

        if (isClickable) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = { /* Add to favorites or save */ },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null
                    )
                }

                ElevatedButton(
                    onClick = onSaveClick,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.save_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorResponseContent(errorMessage: String) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

            Text(
                text = stringResource(Res.string.error_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }

        val errorPrefix = stringResource(Res.string.error_prefix)
        Text(
            text = errorMessage.removePrefix(errorPrefix),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(start = 48.dp)
        )
    }
}

@Composable
private fun NotFoundResponseContent(message: String) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

            Text(
                text = stringResource(Res.string.not_found_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(start = 48.dp)
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
            // Decorative element
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍎",
                    style = MaterialTheme.typography.displayMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.empty_state_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            val annotatedText = buildAnnotatedString {
                val msg = stringResource(Res.string.empty_state_message)
                append(msg)

                // Highlight a key phrase (предполагаем, что есть важная фраза в тексте)
                val keyPhrase = "продукт"
                val startIndex = msg.indexOf(keyPhrase)
                if (startIndex >= 0) {
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        start = startIndex,
                        end = startIndex + keyPhrase.length
                    )
                }
            }

            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
            )
        }
    }
}