package com.wishnewjam.aicalories.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.resources.Res
import com.wishnewjam.aicalories.resources.calories
import com.wishnewjam.aicalories.resources.error_prefix
import com.wishnewjam.aicalories.resources.not_found_message
import com.wishnewjam.aicalories.resources.note
import com.wishnewjam.aicalories.resources.product
import com.wishnewjam.aicalories.resources.weight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatResponse = MutableStateFlow("")
    val chatResponse: StateFlow<String> = _chatResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            chatRepository.getChatResponse(message).fold(
                onSuccess = { response ->
                    _chatResponse.value = formatResponse(response)
                    _isLoading.value = false
                },
                onFailure = { error ->
                    val errorPrefix = getString(Res.string.error_prefix)
                    _chatResponse.value = "$errorPrefix${error.message}"
                    _isLoading.value = false
                }
            )
        }
    }

    private suspend fun formatResponse(response: ChatResponseModel): String {
        if (response.error != null) {
            val errorPrefix = getString(Res.string.error_prefix)
            return "$errorPrefix${response.error}"
        }

        if (response.foodName == null && response.calories == null) {
            return getString(Res.string.not_found_message)
        }

        val productDefault = getString(Res.string.product)
        val formattedName = response.foodName ?: productDefault

        val caloriesText = response.calories?.let { calories ->
            val template = getString(Res.string.calories)
            template.replace("%d", calories.toString())
        } ?: ""

        val weightText = response.weight?.let { weight ->
            val template = getString(Res.string.weight)
            template.replace("%d", weight.toString())
        } ?: ""

        val commentText = response.comment?.let { comment ->
            val template = getString(Res.string.note)
            template.replace("%s", comment)
        } ?: ""

        return buildString {
            append("🍎 $formattedName\n\n")
            if (caloriesText.isNotEmpty()) append("$caloriesText\n")
            if (weightText.isNotEmpty()) append("$weightText\n")
            if (commentText.isNotEmpty()) append("\n$commentText")
        }.trim()
    }
}