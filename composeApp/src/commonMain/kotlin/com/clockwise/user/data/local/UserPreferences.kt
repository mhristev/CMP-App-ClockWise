//package com.clockwise.user.data.local
//
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.*
//import com.clockwise.user.domain.UserRole
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//class UserPreferences(private val dataStore: DataStore<Preferences>) {
//    private val json = Json { ignoreUnknownKeys = true }
//
//    companion object {
//        private val TOKEN = stringPreferencesKey("token")
//        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
//        private val TOKEN_TYPE = stringPreferencesKey("token_type")
//        private val EXPIRES_IN = longPreferencesKey("expires_in")
//        private val USER_ID = stringPreferencesKey("user_id")
//        private val USERNAME = stringPreferencesKey("username")
//        private val EMAIL = stringPreferencesKey("email")
//        private val ROLE = stringPreferencesKey("role")
//        private val BUSINESS_UNIT_ID = stringPreferencesKey("business_unit_id")
//        private val BUSINESS_UNIT_NAME = stringPreferencesKey("business_unit_name")
//    }
//
//    suspend fun saveAuthData(
//        token: String,
//        refreshToken: String,
//        tokenType: String,
//        expiresIn: Long,
//        userId: String?,
//        username: String,
//        email: String,
//        role: UserRole,
//        businessUnitId: String?,
//        businessUnitName: String?
//    ) {
//        dataStore.edit { preferences ->
//            preferences[TOKEN] = token
//            preferences[REFRESH_TOKEN] = refreshToken
//            preferences[TOKEN_TYPE] = tokenType
//            preferences[EXPIRES_IN] = expiresIn
//            preferences[USER_ID] = userId ?: ""
//            preferences[USERNAME] = username
//            preferences[EMAIL] = email
//            preferences[ROLE] = role.name
//            preferences[BUSINESS_UNIT_ID] = businessUnitId ?: ""
//            preferences[BUSINESS_UNIT_NAME] = businessUnitName ?: ""
//        }
//    }
//
//    suspend fun getAuthData(): AuthData? {
//        return dataStore.data.map { preferences ->
//            try {
//                AuthData(
//                    token = preferences[TOKEN] ?: return@map null,
//                    refreshToken = preferences[REFRESH_TOKEN] ?: return@map null,
//                    tokenType = preferences[TOKEN_TYPE] ?: return@map null,
//                    expiresIn = preferences[EXPIRES_IN] ?: return@map null,
//                    user = UserDto(
//                        id = preferences[USER_ID]?.takeIf { it.isNotEmpty() },
//                        username = preferences[USERNAME] ?: return@map null,
//                        email = preferences[EMAIL] ?: return@map null,
//                        role = UserRole.valueOf(preferences[ROLE] ?: return@map null),
//                        businessUnitId = preferences[BUSINESS_UNIT_ID]?.takeIf { it.isNotEmpty() },
//                        businessUnitName = preferences[BUSINESS_UNIT_NAME]?.takeIf { it.isNotEmpty() }
//                    )
//                )
//            } catch (e: Exception) {
//                null
//            }
//        }.first()
//    }
//
//    suspend fun clearAuthData() {
//        dataStore.edit { preferences ->
//            preferences.clear()
//        }
//    }
//}
//
//data class AuthData(
//    val token: String,
//    val refreshToken: String,
//    val tokenType: String,
//    val expiresIn: Long,
//    val user: UserDto
//)
//
//data class UserDto(
//    val id: String?,
//    val username: String,
//    val email: String,
//    val role: UserRole,
//    val businessUnitId: String?,
//    val businessUnitName: String?
//)