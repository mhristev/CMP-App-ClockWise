package com.clockwise

import android.app.Application
import android.util.Log
import com.clockwise.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class ClockWiseApplication: Application() {
    
    companion object {
        private const val TAG = "ClockWiseApplication"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            initKoin {
                androidContext(this@ClockWiseApplication)
            }
            Log.d(TAG, "Koin initialization successful")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Koin", e)
            // Handle initialization error gracefully
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "Application terminating")
    }
}