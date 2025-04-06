//package com.clockwise.core.platform
//
//import androidx.datastore.preferences.preferencesDataStore
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//
//class Platform {
//    val dataStore: DataStore<Preferences> by preferencesDataStore(
//        name = "user_preferences",
//        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
//    )
//}