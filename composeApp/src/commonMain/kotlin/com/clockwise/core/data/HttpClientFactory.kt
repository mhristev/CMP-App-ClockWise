package com.plcoding.bookpedia.core.data

import com.clockwise.features.auth.UserService
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {

    fun create(engine: HttpClientEngine, userService: UserService): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true         // Allow flexible parsing of JSON
                        coerceInputValues = true // Handle null values more gracefully
                        explicitNulls = false    // Make serialization more forgiving
                    }
                )
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = userService.authToken.value
                        if (token != null) {
                            BearerTokens(token, "")
                        } else {
                            null
                        }
                    }
                    refreshTokens {
                        val newToken = userService.getValidAuthToken()
                        if (newToken != null) {
                            BearerTokens(newToken, "")
                        } else {
                            null
                        }
                    }
                }
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}