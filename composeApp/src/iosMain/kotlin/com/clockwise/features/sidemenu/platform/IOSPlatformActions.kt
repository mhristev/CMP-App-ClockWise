package com.clockwise.features.sidemenu.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import com.clockwise.features.sidemenu.platform.PlatformActions

class IOSPlatformActions : PlatformActions {
    
    override fun makePhoneCall(phoneNumber: String) {
        try {
            val url = NSURL.URLWithString("tel://$phoneNumber")
            url?.let {
                if (UIApplication.sharedApplication.canOpenURL(it)) {
                    UIApplication.sharedApplication.openURL(it)
                }
            }
        } catch (e: Exception) {
            // Handle error - could show alert or log
        }
    }
    
    override fun sendEmail(emailAddress: String) {
        try {
            val url = NSURL.URLWithString("mailto:$emailAddress")
            url?.let {
                if (UIApplication.sharedApplication.canOpenURL(it)) {
                    UIApplication.sharedApplication.openURL(it)
                }
            }
        } catch (e: Exception) {
            // Handle error - could show alert or log
        }
    }
    
    override fun openDirections(latitude: Double, longitude: Double, locationName: String) {
        try {
            // Try Apple Maps first
            val appleMapUrl = NSURL.URLWithString("http://maps.apple.com/?q=$locationName&ll=$latitude,$longitude")
            appleMapUrl?.let {
                if (UIApplication.sharedApplication.canOpenURL(it)) {
                    UIApplication.sharedApplication.openURL(it)
                    return
                }
            }
            
            // Fallback to Google Maps
            val googleMapUrl = NSURL.URLWithString("https://maps.google.com/?q=$latitude,$longitude")
            googleMapUrl?.let {
                if (UIApplication.sharedApplication.canOpenURL(it)) {
                    UIApplication.sharedApplication.openURL(it)
                }
            }
        } catch (e: Exception) {
            // Handle error - could show alert or log
        }
    }
}