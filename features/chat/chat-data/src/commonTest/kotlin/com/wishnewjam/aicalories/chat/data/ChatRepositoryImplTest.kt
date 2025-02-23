package com.wishnewjam.aicalories.chat.data

import com.wishnewjam.aicalories.chat.data.model.ChatCompletionResponse
import com.wishnewjam.aicalories.chat.data.model.ChatResponseMapper
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.network.data.NetworkClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

// Fake implementation of NetworkClient that returns a pre-set response.
class FakeNetworkClient : NetworkClient {
    var fakeResponse: ChatCompletionResponse? = null

    override suspend fun <Req, Res> postData(url: String, body: Req): Res {
        @Suppress("UNCHECKED_CAST")
        return fakeResponse as Res
    }
}

// Fake ChatResponseMapper that returns a predetermined ChatResponseModel.
class FakeChatResponseMapper : ChatResponseMapper {
    var fakeMapping: ChatResponseModel? = null

    override fun invoke(input: String): ChatResponseModel {
        return fakeMapping ?: ChatResponseModel(response = "Default mapping")
    }
}

// Fake GPT request builder that creates a simple chat message list.
class FakeGptRequestBuilder : GptRequestBuilder {
    override fun buildChatMessageList(message: String): List<ChatMessage> {
        // Assuming ChatMessage is a data class with 'role' and 'content'
        return listOf(ChatMessage(role = "user", content = message))
    }
}

// A simple data class for chat messages used in the test.
// Remove or replace this if your project already defines ChatMessage.
data class ChatMessage(val role: String, val content: String)

class ChatRepositoryImplTest {
    private val fakeNetworkClient = FakeNetworkClient()
    private val fakeChatResponseMapper = FakeChatResponseMapper()
    private val fakeGptRequestBuilder = FakeGptRequestBuilder()

    // Instantiate the repository with fake dependencies.
    private val repository = ChatRepositoryImpl(
        networkRepo = fakeNetworkClient,
        chatResponseMapper = fakeChatResponseMapper,
        chatRequestBuilder = fakeGptRequestBuilder
    )

    @Test
    fun testGetChatResponse() = runTest {
        // Arrange: set up the fake response content and mapping.
        val testMessage = "Hello"
        val fakeResponseContent = "Hi, how can I help?"

        // Set the fake response for the network client.
        fakeNetworkClient.fakeResponse = ChatCompletionResponse(
            choices = listOf(
                ChatCompletionResponse.Choice(
                    message = ChatCompletionResponse.Message(content = fakeResponseContent)
                )
            )
        )

        // Configure the mapper to produce a ChatResponseModel based on the fake response.
        fakeChatResponseMapper.fakeMapping = ChatResponseModel(response = "Mapped: $fakeResponseContent")

        // Act: call the repository method.
        val result = repository.getChatResponse(testMessage)

        // Assert: verify that the repository returns the mapped response.
        assertEquals("Mapped: $fakeResponseContent", result.response)
    }
}
