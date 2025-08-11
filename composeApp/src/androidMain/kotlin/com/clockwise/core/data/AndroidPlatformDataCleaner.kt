package com.clockwise.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

/**
 * Android-specific implementation of PlatformDataCleaner.
 * Clears DataStore preferences and other Android-specific storage.
 */
class AndroidPlatformDataCleaner(
    private val context: Context
) : PlatformDataCleaner {
    
    companion object {
        private val Context.userPreferencesDataStore by preferencesDataStore(
            name = "user_preferences"
        )
        private val Context.appSettingsDataStore by preferencesDataStore(
            name = "app_settings"
        )
    }
    
    override suspend fun clearPreferences() {
        try {
            // Clear user preferences DataStore
            context.userPreferencesDataStore.edit { preferences ->
                preferences.clear()
            }
            
            // Clear app settings DataStore
            context.appSettingsDataStore.edit { preferences ->
                preferences.clear()
            }
            
            // Clear SharedPreferences if any exist
            clearSharedPreferences()
            
            println("✅ Android preferences cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear Android preferences: ${e.message}")
            throw e
        }
    }
    
    override suspend fun clearCacheData() {
        try {
            // Clear internal cache directory
            context.cacheDir?.let { cacheDir ->
                if (cacheDir.exists()) {
                    cacheDir.deleteRecursively()
                    println("✅ Internal cache cleared")
                }
            }
            
            // Clear external cache directory if available
            context.externalCacheDir?.let { externalCacheDir ->
                if (externalCacheDir.exists()) {
                    externalCacheDir.deleteRecursively()
                    println("✅ External cache cleared")
                }
            }
            
            println("✅ Android cache data cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear Android cache: ${e.message}")
            throw e
        }
    }
    
    override suspend fun clearPlatformSpecificData() {
        try {
            // Clear any other Android-specific data storage
            // This could include:
            // - Room database if used
            // - Files in internal storage
            // - Any other Android-specific storage mechanisms
            
            clearInternalFiles()
            
            println("✅ Android platform-specific data cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear Android platform-specific data: ${e.message}")
            throw e
        }
    }
    
    private fun clearSharedPreferences() {
        try {
            // Get all SharedPreferences files and clear them
            val sharedPrefsDir = context.filesDir.parentFile?.resolve("shared_prefs")
            
            sharedPrefsDir?.listFiles()?.forEach { file ->
                if (file.name.endsWith(".xml")) {
                    val prefsName = file.nameWithoutExtension
                    context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply()
                }
            }
            
            println("✅ SharedPreferences cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear SharedPreferences: ${e.message}")
        }
    }
    
    private fun clearInternalFiles() {
        try {
            // Clear any files in internal storage that might contain user data
            context.filesDir?.let { filesDir ->
                filesDir.listFiles()?.forEach { file ->
                    if (file.isFile && file.name.contains("user", ignoreCase = true)) {
                        file.delete()
                    }
                }
            }
            
            println("✅ Internal files cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear internal files: ${e.message}")
        }
    }
}
