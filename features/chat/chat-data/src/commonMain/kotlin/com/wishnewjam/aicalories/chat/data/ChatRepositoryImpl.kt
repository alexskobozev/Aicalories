package com.wishnewjam.aicalories.chat.data

import com.wishnewjam.aicalories.chat.data.model.ChatCompletionRequest
import com.wishnewjam.aicalories.chat.data.model.ChatCompletionResponse
import com.wishnewjam.aicalories.chat.data.model.ChatMessage
import com.wishnewjam.aicalories.chat.data.model.ChatResponseMapper
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.chat.domain.ChatRepository
import com.wishnewjam.aicalories.network.data.NetworkClient

class ChatRepositoryImpl(
    private val networkRepo: NetworkClient,
    private val chatResponseMapper: ChatResponseMapper,
) : ChatRepository {
    override suspend fun getChatResponse(message: String): ChatResponseModel {
        val response =
            networkRepo.postData<ChatCompletionRequest, ChatCompletionResponse>(
                url = "https://api.openai.com/v1/chat/completions",
                body = ChatCompletionRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        ChatMessage(
                            role = "system", content = """
Ты — ассистент, который получает название (или описание) продукта на русском языке и возвращает информацию о калорийности. 
Требования к ответу:
1) Отвечай только в формате JSON, без дополнительного текста или пояснений. 
2) Если можешь определить продукт, верни приблизительную калорийность среднего продукта исходя из веса. если был введен вес то посчитай относительно веса. Если вес был не введен - то прикинь по среднему значению
3) Если данных недостаточно или неясно, что за продукт, верни JSON, где будет информация о том, что не удалось определить продукт.
4) Всегда возвращай ответы на том языке, на котором был сделан запрос.

Пример структуры ответа (поля можете расширять при необходимости):
{
  "foodName": "Пирожок",
  "calories": 500,
  "weight": 300,
  "comment": "Приблизительный показатель. Уточните, пожалуйста, начинку."
}
Если продукт не определён:
{
  "foodName": null,
  "calories": null,
  "weight": 300,
  "comment": "Извините, я не понял, что конкретно вы имели в виду."
}
"""
                        ),
                        ChatMessage(
                            role = "user",
                            content = message
                        )
                    ),
                    temperature = 0.7
                ),
            )
        return chatResponseMapper(response.choices[0].message.content)
    }
}