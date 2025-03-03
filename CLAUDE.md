# AiCalories Development Guide

## Build and Test Commands
- Build: `./gradlew build`
- Run tests: `./gradlew test` 
- Run single test: `./gradlew :features:chat:chat-data:testDebugUnitTest --tests "com.wishnewjam.aicalories.chat.data.ChatRepositoryImplTest.getChatResponseReturnsSuccessWithMappedResponseWhenNetworkCallSucceeds"`
- Clean: `./gradlew clean`

## Code Style Guidelines
- **Package Structure**: Follow feature-module architecture with data/domain/presentation layers
- **Kotlin Conventions**: Use idiomatic Kotlin with extension functions, null safety features
- **Imports**: Group by standard library, third-party, and project imports; avoid wildcard imports
- **Naming**: Use descriptive names; classes are PascalCase, functions/variables are camelCase
- **Error Handling**: Use Kotlin Result for operations that can fail; include descriptive error messages
- **Dependency Injection**: Use Koin for DI across modules
- **Testing**: Write unit tests for repositories and business logic; use test doubles where appropriate
- **Multiplatform**: Consider KMM compatibility for all shared code
- **Serialization**: Use kotlinx.serialization for JSON processing