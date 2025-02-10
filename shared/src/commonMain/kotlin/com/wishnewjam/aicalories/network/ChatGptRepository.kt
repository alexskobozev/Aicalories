package com.wishnewjam.aicalories.network

class ChatGptRepository {
    private val httpClient = HttpClientFactory.create()
    private val chatGptApi = ChatGptApi(httpClient)

    suspend fun getChatCompletion(
        apiKey: String,
        userMessage: String
    ): String {
        // Construct a minimal request
        val request = ChatCompletionRequest(
            model = "gpt-4o-mini-2024-07-18",
            messages = listOf(
                ChatMessage(role = "system", content = "You are a helpful assistant."),
                ChatMessage(role = "user", content = userMessage)
            ),
            temperature = 0.7
        )

        // Call the API
        val response = chatGptApi.sendChatCompletionRequest(
            apiKey = apiKey,
            request = request
        )

        // Extract the assistant’s answer (first choice)
        val assistantMessage = response.choices.firstOrNull()?.message?.content
        return assistantMessage ?: "[No response]"
    }
}