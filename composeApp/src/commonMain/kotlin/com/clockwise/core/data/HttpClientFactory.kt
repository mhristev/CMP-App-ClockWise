package com.plcoding.bookpedia.core.data

import com.clockwise.features.auth.UserService
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object HttpClientFactory {

    /**
     * Creates authenticated HttpClient with manual Bearer token handling
     * Use this for all API calls that require authentication
     * Gets fresh tokens on every request to eliminate caching issues
     */
    fun createAuthenticated(engine: HttpClientEngine, userService: UserService): HttpClient {
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
                        println("[AUTH CLIENT] $message")
                    }
                }
                level = LogLevel.ALL
            }
            // Manual Bearer token handling - NO Auth plugin caching issues
            defaultRequest {
                contentType(ContentType.Application.Json)
                
                // Get fresh token on EVERY request to eliminate caching issues
                val token = runBlocking { userService.getValidAuthToken() }
                if (token != null) {
                    println("🔐 Manual Auth: Adding fresh Bearer token ${token.take(30)}... (length: ${token.length})")
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                } else {
                    println("⚠️ Manual Auth: No token available - request will fail")
                }
            }
        }
    }

    /**
     * Creates public HttpClient WITHOUT auth plugin
     * Use this for auth endpoints (login, register, refresh) that should not send Bearer tokens
     */
    fun createPublic(engine: HttpClientEngine): HttpClient {
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
                        println("[PUBLIC CLIENT] $message")
                    }
                }
                level = LogLevel.ALL
            }
            // NO Auth plugin - this client won't send Bearer tokens
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }

    /**
     * Legacy method for backward compatibility
     * @deprecated Use createAuthenticated() or createPublic() instead
     */
    @Deprecated("Use createAuthenticated() or createPublic() instead")
    fun create(engine: HttpClientEngine, userService: UserService): HttpClient {
        return createAuthenticated(engine, userService)
    }
}