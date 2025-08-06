package com.clockwise.features.sidemenu.presentation

import com.clockwise.features.organization.data.model.BusinessUnit

data class SideMenuState(
    val isMenuOpen: Boolean = false,
    val businessUnit: BusinessUnit? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)