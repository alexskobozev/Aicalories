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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
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

    // Custom JSON configuration matching what's in your app
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    // Test doubles
    private val testChatResponseMapper = TestChatResponseMapper()
    private val testRequestBuilder = TestGptRequestBuilder()
    private lateinit var mockHttpClient: HttpClient
    private lateinit var testNetworkClient: NetworkClient

    // Use lazy injection to avoid issues when recreating Koin context
    private val repository by inject<ChatRepositoryImpl>()

    @BeforeTest
    fun setup() {
        // Reset test doubles' state
        testChatResponseMapper.reset()
        testRequestBuilder.reset()

        // Configure default behavior
        testRequestBuilder.messageListToReturn = chatMessageList
        testChatResponseMapper.responseToReturn = mappedResponse

        // Create mock HttpClient with default success response
        setupDefaultMockHttpClient()

        // Initialize Koin
        setupKoin()
    }

    private fun setupDefaultMockHttpClient() {
        mockHttpClient = HttpClient(MockEngine) {
            // Install ContentNegotiation plugin
            install(ContentNegotiation) {
                json(jsonConfig)
            }

            engine {
                addHandler { request ->
                    respond(
                        content = ByteReadChannel(
                            jsonConfig.encodeToString(
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
    }

    private fun setupKoin() {
        // Make sure to stop Koin first if it's already started
        try {
            stopKoin()
        } catch (e: IllegalStateException) {
            // Koin wasn't started, that's fine
        }

        // Start Koin with our test dependencies
        startKoin {
            modules(
                module {
                    single<ChatResponseMapper> { testChatResponseMapper }
                    single<GptRequestBuilder> { testRequestBuilder }
                    single<NetworkClient> { testNetworkClient }
                    single { ChatRepositoryImpl(get(), get(), get()) }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() {
        try {
            stopKoin()
        } catch (e: IllegalStateException) {
            // Koin wasn't started, that's fine
        }
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
        assertTrue(lastRequest?.url.toString().endsWith("/v1/chat/completions"),
            "Expected URL to end with /v1/chat/completions, but was ${lastRequest?.url}")
        assertEquals("Bearer $apiKey", lastRequest?.headers?.get(HttpHeaders.Authorization))
    }

    @Test
    fun getChatResponseReturnsFailureWhenNetworkCallFails() = runTest {
        // Close existing HttpClient
        mockHttpClient.close()

        // Create new HttpClient that returns an error
        mockHttpClient = HttpClient(MockEngine) {
            // Install ContentNegotiation plugin
            install(ContentNegotiation) {
                json(jsonConfig)
            }

            engine {
                addHandler { _ ->
                    respond(
                        content = ByteReadChannel("Internal Server Error"),
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        // Create new NetworkClient with the new HttpClient
        testNetworkClient = NetworkClient(mockHttpClient, apiKey)

        // Reinitialize Koin with the new NetworkClient
        setupKoin()

        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isFailure, "Expected result to be a failure but was success")
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue(errorMessage.contains("Request error"),
            "Expected error message to contain 'Request error', but was: $errorMessage")
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
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue(errorMessage.startsWith("Mapper error: Invalid JSON format"),
            "Expected error message to start with 'Mapper error: Invalid JSON format', but was: $errorMessage")
    }

    @Test
    fun getChatResponseHandlesEmptyResponse() = runTest {
        // Close existing HttpClient
        mockHttpClient.close()

        // Create new HttpClient that returns empty content
        mockHttpClient = HttpClient(MockEngine) {
            // Install ContentNegotiation plugin
            install(ContentNegotiation) {
                json(jsonConfig)
            }

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
                        content = ByteReadChannel(jsonConfig.encodeToString(emptyResponse)),
                        status = HttpStatusCode.OK,
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    )
                }
            }
        }

        // Create new NetworkClient with the new HttpClient
        testNetworkClient = NetworkClient(mockHttpClient, apiKey)

        // Update mapper to return empty response model
        testChatResponseMapper.responseToReturn = ChatResponseModel(
            foodName = null,
            calories = null,
            weight = null,
            comment = "Не удалось получить ответ"
        )

        // Reinitialize Koin with the new NetworkClient
        setupKoin()

        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull()?.foodName)
        assertEquals(null, result.getOrNull()?.calories)
    }

    @Test
    fun getChatResponseHandlesArrayIndexOutOfBoundsException() = runTest {
        // Close existing HttpClient
        mockHttpClient.close()

        // Create new HttpClient that returns empty choices
        mockHttpClient = HttpClient(MockEngine) {
            // Install ContentNegotiation plugin
            install(ContentNegotiation) {
                json(jsonConfig)
            }

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
                        content = ByteReadChannel(jsonConfig.encodeToString(emptyChoicesResponse)),
                        status = HttpStatusCode.OK,
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    )
                }
            }
        }

        // Create new NetworkClient with the new HttpClient
        testNetworkClient = NetworkClient(mockHttpClient, apiKey)

        // Reinitialize Koin with the new NetworkClient
        setupKoin()

        // Act
        val result = repository.getChatResponse(userMessage)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Index") == true ||
                result.exceptionOrNull()?.cause?.message?.contains("Index") == true)
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