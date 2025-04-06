package com.clockwise.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DataStore<Preferences>> { 
        object : DataStore<Preferences> {
            override val data = kotlinx.coroutines.flow.MutableStateFlow<Preferences>(emptyPreferences())
            
            override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
                val currentPreferences = data.first()
                val updatedPreferences = transform(currentPreferences)
                data.value = updatedPreferences
                return updatedPreferences
            }
        }
    }
} 