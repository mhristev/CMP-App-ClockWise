package com.clockwise.core.di

import android.content.Context
import com.clockwise.user.data.local.UserPreferences
import com.clockwise.user.data.local.AuthData
import com.clockwise.user.data.local.UserDto
import com.clockwise.user.domain.UserRole
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

private const val TOKEN = "token"
private const val REFRESH_TOKEN = "refresh_token"
private const val TOKEN_TYPE = "token_type"
private const val EXPIRES_IN = "expires_in"
private const val USER_ID = "user_id"
private const val USERNAME = "username"
private const val EMAIL = "email"
private const val ROLE = "role"
private const val BUSINESS_UNIT_ID = "business_unit_id"
private const val BUSINESS_UNIT_NAME = "business_unit_name"

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        
        // Provide SharedPreferencesSettings implementation
        single { 
            val context = get<Context>()
            val sharedPrefs = context.getSharedPreferences("clockwise_prefs", Context.MODE_PRIVATE)
            SharedPreferencesSettings(sharedPrefs)
        }
        
        // Create UserPreferences directly
        single { 
            // Create a simple mock implementation of UserPreferences that meets
            // our immediate needs for compilation
            val settings = get<SharedPreferencesSettings>()
            object : UserPreferences() {
                override suspend fun saveAuthData(
                    token: String, 
                    refreshToken: String, 
                    tokenType: String, 
                    expiresIn: Long, 
                    userId: String?, 
                    username: String, 
                    email: String, 
                    role: com.clockwise.user.domain.UserRole, 
                    businessUnitId: String?, 
                    businessUnitName: String?
                ) {
                    settings.putString(TOKEN, token)
                    settings.putString(REFRESH_TOKEN, refreshToken)
                    settings.putString(TOKEN_TYPE, tokenType)
                    settings.putLong(EXPIRES_IN, expiresIn)
                    settings.putString(USER_ID, userId ?: "")
                    settings.putString(USERNAME, username)
                    settings.putString(EMAIL, email)
                    settings.putString(ROLE, role.name)
                    settings.putString(BUSINESS_UNIT_ID, businessUnitId ?: "")
                    settings.putString(BUSINESS_UNIT_NAME, businessUnitName ?: "")
                }

                override suspend fun getAuthData(): com.clockwise.user.data.local.AuthData? {
                    try {
                        val token = settings.getStringOrNull(TOKEN) ?: return null
                        return com.clockwise.user.data.local.AuthData(
                            token = token,
                            refreshToken = settings.getStringOrNull(REFRESH_TOKEN) ?: return null,
                            tokenType = settings.getStringOrNull(TOKEN_TYPE) ?: return null,
                            expiresIn = settings.getLongOrNull(EXPIRES_IN) ?: return null,
                            user = com.clockwise.user.data.local.UserDto(
                                id = settings.getStringOrNull(USER_ID)?.takeIf { it.isNotEmpty() },
                                username = settings.getStringOrNull(USERNAME) ?: return null,
                                email = settings.getStringOrNull(EMAIL) ?: return null,
                                role = com.clockwise.user.domain.UserRole.valueOf(settings.getStringOrNull(ROLE) ?: return null),
                                businessUnitId = settings.getStringOrNull(BUSINESS_UNIT_ID)?.takeIf { it.isNotEmpty() },
                                businessUnitName = settings.getStringOrNull(BUSINESS_UNIT_NAME)?.takeIf { it.isNotEmpty() }
                            )
                        )
                    } catch (e: Exception) {
                        return null
                    }
                }

                override suspend fun clearAuthData() {
                    settings.clear()
                }
            }
        }
    }