package com.clockwise

import android.app.Application
import com.clockwise.core.di.sharedModule
import com.plcoding.bookpedia.di.initKoin
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