package com.clockwise.user.domain

import com.clockwise.service.UserService

/**
 * Utility class for centralized role-based access control
 */
object AccessControl {
    /**
     * Checks if the current user has access to a specific screen
     * @param screen The screen to check access for
     * @param userService The UserService instance
     * @return true if the user has access, false otherwise
     */
    fun hasAccessToScreen(screen: String, userService: UserService): Boolean {
        val currentUser = userService.currentUser.value
        val userRole = currentUser?.role ?: UserRole.EMPLOYEE
        
        return when (screen) {
            "search" -> userRole == UserRole.MANAGER || userRole == UserRole.ADMIN
            else -> true // Default access for other screens
        }
    }
    
    /**
     * Checks if the current user has a specific role
     * @param role The role to check
     * @param userService The UserService instance
     * @return true if the user has the role, false otherwise
     */
    fun hasRole(role: UserRole, userService: UserService): Boolean {
        val currentUser = userService.currentUser.value
        val userRole = currentUser?.role ?: UserRole.EMPLOYEE
        return userRole == role
    }
    
    /**
     * Checks if the current user has any of the specified roles
     * @param roles The roles to check
     * @param userService The UserService instance
     * @return true if the user has any of the roles, false otherwise
     */
    fun hasAnyRole(roles: List<UserRole>, userService: UserService): Boolean {
        val currentUser = userService.currentUser.value
        val userRole = currentUser?.role ?: UserRole.EMPLOYEE
        return roles.contains(userRole)
    }
} 