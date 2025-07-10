package com.clockwise.core.config

/**
 * App configuration for development features
 */
object AppConfig {
    // This should be set to false in release builds
    const val IS_DEBUG_MODE = true
    
    /**
     * Test accounts for development mode only
     */
    object TestAccounts {
        data class TestAccount(
            val email: String,
            val password: String,
            val displayName: String
        )
        
        val EMPLOYEE = TestAccount(
            email = "employee@clockwise.com",
            password = "employee123",
            displayName = "Employee Test Account"
        )
        
        val MANAGER = TestAccount(
            email = "manager@clockwise.com",
            password = "manager123",
            displayName = "Manager Test Account"
        )
        
        val ADMIN = TestAccount(
            email = "admin@clockwise.com",
            password = "admin123",
            displayName = "Admin Test Account"
        )
        
        val ALL_TEST_ACCOUNTS = listOf(EMPLOYEE, MANAGER, ADMIN)
    }
} 