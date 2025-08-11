package com.clockwise.core.data

/**
 * Service responsible for comprehensive data clearing during logout and account deletion.
 * This ensures all user-related data is properly removed from all storage mechanisms.
 */
interface DataClearingService {
    suspend fun clearAllUserData()
    suspend fun clearSecureStorage()
    suspend fun clearPreferences()
    suspend fun clearCacheData()
    suspend fun clearPlatformSpecificData()
}

/**
 * Default implementation of DataClearingService that orchestrates clearing
 * data from all storage mechanisms used by the app.
 */
class DefaultDataClearingService(
    private val secureStorage: SecureStorage,
    private val platformDataCleaner: PlatformDataCleaner
) : DataClearingService {
    
    override suspend fun clearAllUserData() {
        try {
            println("🧹 Starting comprehensive data clearing...")
            
            // Clear secure storage (KVault)
            clearSecureStorage()
            
            // Clear preferences (DataStore/UserDefaults)
            clearPreferences()
            
            // Clear any cached data
            clearCacheData()
            
            // Clear platform-specific data
            clearPlatformSpecificData()
            
            println("✅ Comprehensive data clearing completed successfully")
        } catch (e: Exception) {
            println("❌ Error during data clearing: ${e.message}")
            throw e
        }
    }
    
    override suspend fun clearSecureStorage() {
        try {
            secureStorage.clearAllData()
            println("✅ Secure storage cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear secure storage: ${e.message}")
            throw e
        }
    }
    
    override suspend fun clearPreferences() {
        try {
            platformDataCleaner.clearPreferences()
            println("✅ Preferences cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear preferences: ${e.message}")
            throw e
        }
    }
    
    override suspend fun clearCacheData() {
        try {
            platformDataCleaner.clearCacheData()
            println("✅ Cache data cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear cache data: ${e.message}")
            throw e
        }
    }
    
    override suspend fun clearPlatformSpecificData() {
        try {
            platformDataCleaner.clearPlatformSpecificData()
            println("✅ Platform-specific data cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear platform-specific data: ${e.message}")
            throw e
        }
    }
}

/**
 * Platform-specific data clearing interface.
 * Each platform implements this to clear their specific storage mechanisms.
 */
interface PlatformDataCleaner {
    suspend fun clearPreferences()
    suspend fun clearCacheData()
    suspend fun clearPlatformSpecificData()
}
