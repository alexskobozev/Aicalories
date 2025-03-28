package com.wishnewjam.aicalories.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.chat.presentation.model.ChatResponseModelUi
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

    sealed class ModelState {
        data object Idle : ModelState()
        data object Loading : ModelState()
        data class Success(
            val selectedModel: ChatResponseModelUi?,
        ) : ModelState()

        data class ErrorState(val message: String) : ModelState()
    }

    sealed class ListState {
        data object Loading : ListState()
        data class Success(
            val otherModels: List<ChatResponseModelUi>,
        ) : ListState()

        data class ErrorState(val message: String) : ListState()
    }

    private var selectedModel: ChatResponseModel? = null
    private val _modelState = MutableStateFlow<ModelState>(ModelState.Idle)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    private val _historyState = MutableStateFlow<ListState>(ListState.Loading)
    val historyState: StateFlow<ListState> = _historyState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        _historyState.value = ListState.Loading
        viewModelScope.launch {
            chatRepository.getHistory().collect { list ->
                _historyState.value = ListState.Success(
                    otherModels = transformModels(list)
                )
            }
        }
    }

    private suspend fun transformModels(list: List<ChatResponseModel>) =
        list.map { model -> formatResponse(model) }

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        _modelState.value = ModelState.Loading
        viewModelScope.launch {
            chatRepository.getChatResponse(message).fold(
                onSuccess = { response ->
                    _modelState.value = ModelState.Success(formatResponse(response))
                    selectedModel = response
                },
                onFailure = { error ->
                    val errorPrefix = getString(Res.string.error_prefix)
                    _modelState.value = ModelState.ErrorState("$errorPrefix${error.message}")
                }
            )
        }
    }

    private suspend fun formatResponse(response: ChatResponseModel): ChatResponseModelUi {
        var foodName: String? = null
        var caloriesText: String? = null
        var weightText: String? = null
        var commentText: String? = null
        var error: String? = null
        val date: String = response.date.toString()
        if (response.error != null) {
            val errorPrefix = getString(Res.string.error_prefix)
            error = "$errorPrefix${response.error}"
        }

        if (response.foodName == null && response.calories == null) {
            error = getString(Res.string.not_found_message)
        }

        val productDefault = getString(Res.string.product)
        foodName = response.foodName ?: productDefault

        caloriesText = response.calories?.let { calories ->
            val template = getString(Res.string.calories)
            template.replace("%d", calories.toString())
        } ?: ""

        weightText = response.weight?.let { weight ->
            val template = getString(Res.string.weight)
            template.replace("%d", weight.toString())
        } ?: ""

        commentText = response.comment?.let { comment ->
            val template = getString(Res.string.note)
            template.replace("%s", comment)
        } ?: ""

        return ChatResponseModelUi(
            foodName = foodName,
            calories = caloriesText,
            weight = weightText,
            comment = commentText,
            error = error,
            date = date,
        )

//        return buildString {
//            append("🍎 $formattedName\n\n")
//            if (caloriesText.isNotEmpty()) append("$caloriesText\n")
//            if (weightText.isNotEmpty()) append("$weightText\n")
//            if (commentText.isNotEmpty()) append("\n$commentText")
//        }.trim()
    }

    fun saveResponse() {
        selectedModel?.let { chatRepository.saveChatResponse(it) }
        _modelState.value = ModelState.Idle
    }
}
