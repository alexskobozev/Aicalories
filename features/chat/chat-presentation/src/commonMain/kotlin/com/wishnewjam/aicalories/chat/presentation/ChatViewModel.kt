package com.wishnewjam.aicalories.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatResponse = MutableStateFlow("")
    val chatResponse: StateFlow<String> = _chatResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(message: String) {
        viewModelScope.launch {
            _isLoading.value = true
            chatRepository.getChatResponse(message).fold(
                onSuccess = {
                    _chatResponse.value = it.toString()
                    _isLoading.value = false
                },
                onFailure = {
                    _chatResponse.value = "Ошибка: ${it.message}"
                    _isLoading.value = false
                }
            )
        }
    }
}