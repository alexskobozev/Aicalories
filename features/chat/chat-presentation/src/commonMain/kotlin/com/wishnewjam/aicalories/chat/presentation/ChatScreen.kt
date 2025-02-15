@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wishnewjam.aicalories.chat.presentation.ChatViewModel

@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel()) {
    val chatResponse by chatViewModel.chatResponse.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chat") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Введите сообщение") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { chatViewModel.sendMessage(inputText) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Отправка..." else "Отправить")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ответ: $chatResponse",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}