package com.wishnewjam.aicalories.chat.data

import com.wishnewjam.aicalories.chat.data.model.ChatCompletionChoice
import com.wishnewjam.aicalories.chat.data.model.ChatCompletionResponse
import com.wishnewjam.aicalories.chat.data.model.ChatMessage
import com.wishnewjam.aicalories.chat.data.model.ChatResponseMapper
import com.wishnewjam.aicalories.chat.domain.model.ChatResponseModel
import com.wishnewjam.aicalories.network.data.NetworkClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatRepositoryImplTest : KoinTest {

    // Test data
    private val userMessage = "яблоко 100г"
    private val apiKey = "test-api-key"
    private val chatMessageList = listOf(
        ChatMessage(role = "system", content = "System prompt"),
        ChatMessage(role = "user", content = userMessage)
    )
    private val responseContent = """
        {
            "foodName": "Яблоко",
            "calories": 52,
            "weight": 100,
            "comment": "Средняя калорийность для свежего яблока"
        }
    """.trimIndent()
    private val mappedResponse = ChatResponseModel(
        foodName = "Яблоко",
        calories = 52,
        weight = 100,
        comment = "Средняя калорийность для свежего яблока"
    )

    // Test doubles
    private val testChatResponseMapper = TestChatResponseMapper()
    private val testRequestBuilder = TestGptRequestBuilder()
    private lateinit var mockHttpClient: HttpClient
    private lateinit var testNetworkClient: NetworkClient

    private val repository by inject<ChatRepositoryImpl>()

    @BeforeTest
    fun setup() {
        // Reset test doubles' state
        testChatResponseMapper.reset()
        testRequestBuilder.reset()

        // Configure default behavior
        testRequestBuilder.messageListToReturn = chatMessageList
        testChatResponseMapper.responseToReturn = mappedResponse

        // Create mock HttpClient
        mockHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond(
                        content = ByteReadChannel(
                            Json.encodeToString(
                                ChatCompletionResponse(
                                    id = "resp_123",
                                    `object` = "chat.completion",
                                    created = 1707734587,
                                    model = "gpt-3.5-turbo-0125",
                                    choices = listOf(
                                        ChatCompletionChoice(
                                            index = 0,
                                            message = ChatMessage(
                                                role = "assistant",
                                                content = responseContent
                                            ),
                                            finishReason = "stop"
                                        )
                                    )
                                )
                            )
                        ),
                        status = HttpStatusCode.OK,
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    )
                }
            }
        }

        // Create NetworkClient with mock HttpClient
        testNetworkClient = NetworkClient(mockHttpClient, apiKey)

        startKoin {
            modules(
                module {
                    single { testChatResponseMapper }
                    single { testRequestBuilder }
                    single { testNetworkClient }
                    single { ChatRepositoryImpl(get(), get(), get()) }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        mockHttpClient.close()
    }

    @Test
    fun getChatResponseReturnsSuccessWithMappedResponseWhenNetworkCallSucceeds() = runTest {
        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(mappedResponse, result.getOrNull())
        assertEquals(userMessage, testRequestBuilder.lastMessage)
        assertEquals(responseContent, testChatResponseMapper.lastContent)

        // Verify the request was made correctly
        val lastRequest = (mockHttpClient.engine as MockEngine).requestHistory.lastOrNull()
        assertTrue(lastRequest!!.url.toString().endsWith("/v1/chat/completions"))
        assertEquals("Bearer $apiKey", lastRequest.headers[HttpHeaders.Authorization])
    }

    @Test
    fun getChatResponseReturnsFailureWhenNetworkCallFails() = runTest {
        // Recreate the HttpClient and NetworkClient
        mockHttpClient.close()
        mockHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler { _ ->
                    respond(
                        content = ByteReadChannel("Internal Server Error"),
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
        testNetworkClient = NetworkClient(mockHttpClient, apiKey)

        // Update the DI container
        stopKoin()
        startKoin {
            modules(
                module {
                    single { testChatResponseMapper }
                    single { testRequestBuilder }
                    single { testNetworkClient }
                    single { ChatRepositoryImpl(get(), get(), get()) }
                }
            )
        }

        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Request error") == true)
    }

    @Test
    fun getChatResponseReturnsFailureWhenMappingFails() = runTest {
        // Setup mapper to throw an exception
        val mappingError = Exception("Invalid JSON format")
        testChatResponseMapper.exceptionToThrow = mappingError

        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.startsWith("Mapper error: Invalid JSON format") == true)
    }

    @Test
    fun getChatResponseHandlesEmptyResponse() = runTest {
        // Replace the mock engine to return empty content
        mockHttpClient.close()
        mockHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler { _ ->
                    val emptyResponse = ChatCompletionResponse(
                        id = "resp_123",
                        `object` = "chat.completion",
                        created = 1707734587,
                        model = "gpt-3.5-turbo-0125",
                        choices = listOf(
                            ChatCompletionChoice(
                                index = 0,
                                message = ChatMessage(role = "assistant", content = ""),
                                finishReason = "stop"
                            )
                        )
                    )

                    respond(
                        content = ByteReadChannel(Json.encodeToString(emptyResponse)),
                        status = HttpStatusCode.OK,
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    )
                }
            }
        }
        testNetworkClient = NetworkClient(mockHttpClient, apiKey)

        // Update mapper to return empty response model
        testChatResponseMapper.responseToReturn = ChatResponseModel(
            foodName = null,
            calories = null,
            weight = null,
            comment = "Не удалось получить ответ"
        )

        // Update the DI container
        stopKoin()
        startKoin {
            modules(
                module {
                    single { testChatResponseMapper }
                    single { testRequestBuilder }
                    single { testNetworkClient }
                    single { ChatRepositoryImpl(get(), get(), get()) }
                }
            )
        }

        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull()?.foodName)
        assertEquals(null, result.getOrNull()?.calories)
    }

    @Test
    fun getChatResponseHandlesArrayIndexOutOfBoundsException() = runTest {
        mockHttpClient.close()
        mockHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler { _ ->
                    val emptyChoicesResponse = ChatCompletionResponse(
                        id = "resp_123",
                        `object` = "chat.completion",
                        created = 1707734587,
                        model = "gpt-3.5-turbo-0125",
                        choices = emptyList() // Empty choices will cause IndexOutOfBoundsException
                    )

                    respond(
                        content = ByteReadChannel(Json.encodeToString(emptyChoicesResponse)),
                        status = HttpStatusCode.OK,
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    )
                }
            }
        }
        testNetworkClient = NetworkClient(mockHttpClient, apiKey)

        // Update the DI container
        stopKoin()
        startKoin {
            modules(
                module {
                    single { testChatResponseMapper }
                    single { testRequestBuilder }
                    single { testNetworkClient }
                    single { ChatRepositoryImpl(get(), get(), get()) }
                }
            )
        }

        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Index") == true)
    }


}

class TestGptRequestBuilder : GptRequestBuilder() {
    var messageListToReturn: List<ChatMessage>? = null
    var lastMessage: String? = null

    override fun buildChatMessageList(message: String): List<ChatMessage> {
        lastMessage = message
        return messageListToReturn ?: super.buildChatMessageList(message)
    }

    fun reset() {
        messageListToReturn = null
        lastMessage = null
    }
}

// Simple test doubles

class TestChatResponseMapper : ChatResponseMapper() {
    var responseToReturn: ChatResponseModel? = null
    var lastContent: String? = null
    var exceptionToThrow: Exception? = null

    override fun invoke(s: String?): ChatResponseModel {
        lastContent = s
        exceptionToThrow?.let { throw it }
        return responseToReturn ?: ChatResponseModel(
            null,
            null,
            null,
            "Default Test Response"
        )
    }

    fun reset() {
        responseToReturn = null
        lastContent = null
        exceptionToThrow = null
    }
}