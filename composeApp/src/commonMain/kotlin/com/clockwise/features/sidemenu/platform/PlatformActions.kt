package com.clockwise.features.sidemenu.platform

interface PlatformActions {
    fun makePhoneCall(phoneNumber: String)
    fun sendEmail(emailAddress: String)
    fun openDirections(latitude: Double, longitude: Double, locationName: String)
}