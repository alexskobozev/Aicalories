package com.wishnewjam.aicalories.chat.data

import com.wishnewjam.aicalories.chat.data.model.ChatCompletionRequest
import com.wishnewjam.aicalories.chat.data.model.ChatCompletionResponse
import com.wishnewjam.aicalories.chat.data.model.ChatResponseMapper
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.db.cache.MealsDatabase
import com.wishnewjam.aicalories.db.entity.MealEntry
import com.wishnewjam.aicalories.logging.Logger
import com.wishnewjam.aicalories.network.data.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ChatRepositoryImpl(
    private val networkRepo: NetworkClient,
    private val chatResponseMapper: ChatResponseMapper,
    private val chatRequestBuilder: GptRequestBuilder,
    private val database: MealsDatabase,
    private val logger: Logger,
) : ChatRepository {
    override suspend fun getChatResponse(message: String): Result<ChatResponseModel> {
        // Build the request
        val request = ChatCompletionRequest(
            model = "gpt-3.5-turbo",
            messages = chatRequestBuilder.buildChatMessageList(message),
            temperature = 0.7
        )

        // Make the API call
        return try {
            val response = networkRepo.postData<ChatCompletionRequest, ChatCompletionResponse>(
                url = "https://api.openai.com/v1/chat/completions",
                body = request
            )

            // Process the response
            try {
                // Extract the content from the first choice
                val content = response.choices.firstOrNull()?.message?.content
                    ?: return Result.failure(Exception("Empty response from API"))

                // Map the content to our domain modelpeaca
                val value = chatResponseMapper(content)
                value.date = LocalDateTime.now()
                Result.success(value)
            } catch (e: Exception) {
                logger.e(e) {
                    "Error mapping response: ${e.message}"
                }
                Result.failure(
                    Exception("Mapper error: ${e.message}, response: $response")
                )
            }
        } catch (e: Exception) {
            logger.e(e) {
                "Error request: ${e.message}"
            }
            Result.failure(
                Exception("Request error: ${e.message}")
            )
        }
    }

    override fun getHistory(): Flow<List<ChatResponseModel>> = database.getAllMealEntries()
        .map { mealEntries -> mealEntries.map { mealEntry -> mealEntry.toChatResponseModel() } }

    override fun saveChatResponse(model: ChatResponseModel) {
        database.insertMealEntry(model.toMealEntry())
    }
}

private fun MealEntry.toChatResponseModel(): ChatResponseModel = ChatResponseModel(
    foodName = foodName,
    calories = calories,
    weight = weight,
    comment = comment,
    date = LocalDateTime.parse(dateUtC)
)

private fun ChatResponseModel.toMealEntry(): MealEntry {
    return MealEntry(
        foodName = foodName.orEmpty(),
        calories = calories ?: 0,
        weight = weight ?: 0,
        comment = comment.orEmpty(),
        dateUtC = date.toString()
    )
}

fun LocalDateTime.Companion.now(): LocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

