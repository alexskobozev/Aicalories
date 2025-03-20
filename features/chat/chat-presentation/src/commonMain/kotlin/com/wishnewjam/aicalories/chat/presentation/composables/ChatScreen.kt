@file:OptIn(ExperimentalMaterial3Api::class)

package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.wishnewjam.aicalories.chat.presentation.ChatViewModel
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
        topBar = { ChatTopBar(scrollBehavior) }
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
                ChatLoadingIndicator(chatState)

                if (historyState is ChatViewModel.ListState.Success) {
                    ChatHistoryList(
                        otherModels = (historyState as ChatViewModel.ListState.Success).otherModels
                    )
                }

                ChatResponseSection(chatState, scrollState, chatViewModel)

                Spacer(modifier = Modifier.height(16.dp))

                ChatInputSection(
                    inputText = inputText,
                    inputIsValid = inputIsValid,
                    onTextChange = { inputText = it },
                    onSendClick = {
                        if (inputIsValid) {
                            chatViewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    chatState = chatState
                )
            }
        }
    }
}






