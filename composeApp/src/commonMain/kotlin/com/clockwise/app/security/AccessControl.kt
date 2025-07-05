package com.clockwise.app.security

import com.clockwise.core.model.UserRole

/**
 * Utility class for centralized role-based access control
 */
object AccessControl {
    /**
     * Checks if the current user has access to a specific screen
     * @param screen The screen to check access for
     * @param currentUserRole The current user's role
     * @return true if the user has access, false otherwise
     */
    fun hasAccessToScreen(screen: String, currentUserRole: UserRole?): Boolean {
        val userRole = currentUserRole ?: UserRole.EMPLOYEE
        
        return when (screen) {
            "search" -> userRole == UserRole.MANAGER || userRole == UserRole.ADMIN
            "business" -> userRole == UserRole.MANAGER || userRole == UserRole.ADMIN
            else -> true // Default access for other screens
        }
    }
    
    /**
     * Checks if the current user has a specific role
     * @param role The role to check
     * @param currentUserRole The current user's role
     * @return true if the user has the role, false otherwise
     */
    fun hasRole(role: UserRole, currentUserRole: UserRole?): Boolean {
        val userRole = currentUserRole ?: UserRole.EMPLOYEE
        return userRole == role
    }
    
    /**
     * Checks if the current user has any of the specified roles
     * @param roles The roles to check
     * @param currentUserRole The current user's role
     * @return true if the user has any of the roles, false otherwise
     */
    fun hasAnyRole(roles: List<UserRole>, currentUserRole: UserRole?): Boolean {
        val userRole = currentUserRole ?: UserRole.EMPLOYEE
        return roles.contains(userRole)
    }
}