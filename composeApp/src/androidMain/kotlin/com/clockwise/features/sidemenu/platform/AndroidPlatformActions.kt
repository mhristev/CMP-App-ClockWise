package com.clockwise.features.sidemenu.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.clockwise.features.sidemenu.platform.PlatformActions

class AndroidPlatformActions(private val context: Context) : PlatformActions {
    
    override fun makePhoneCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle error - could show toast or log
        }
    }
    
    override fun sendEmail(emailAddress: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$emailAddress")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle error - could show toast or log
        }
    }
    
    override fun openDirections(latitude: Double, longitude: Double, locationName: String) {
        try {
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($locationName)")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps") // Prefer Google Maps
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            // If Google Maps is not installed, fall back to any map app
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            // Handle error - could show toast or log
        }
    }
}