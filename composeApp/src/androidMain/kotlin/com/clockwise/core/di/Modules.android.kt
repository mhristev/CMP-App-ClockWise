package com.clockwise.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")


actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single { get<Context>().dataStore }
//        single { DatabaseFactory(androidApplication()) }
    }