package com.clockwise

import android.app.Application
import com.clockwise.core.di.initKoin
import com.clockwise.core.di.sharedModule

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ClockWiseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@ClockWiseApplication)
        }
    }
}