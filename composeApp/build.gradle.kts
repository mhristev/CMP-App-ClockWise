import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "1.9.0"

    id("co.touchlab.skie") version "0.10.1"
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xuse-fir-lt=false")
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
        
        // Disable LightTree mode for iOS targets
        iosTarget.compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xuse-fir-lt=false")
            }
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            
            // DataStore dependency for Android
            implementation("androidx.datastore:datastore-preferences:1.0.0")
            
            // Android-specific implementation of multiplatform-settings
            implementation("com.russhwolf:multiplatform-settings:1.3.0")
            implementation("com.russhwolf:multiplatform-settings-datastore:1.3.0")
            
            // Android specific KVault implementation
            implementation("com.liftric:kvault:1.12.0")
            
            // Google Play Services for location
            implementation("com.google.android.gms:play-services-location:21.0.1")
            
            // Accompanist Permissions for handling permissions in Compose
            implementation("com.google.accompanist:accompanist-permissions:0.32.0")
        }
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.navigation)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.bundles.ktor)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            implementation(libs.kotlinx.datetime)
            
            // Multiplatform Settings for cross-platform data storage
            implementation("com.russhwolf:multiplatform-settings:1.3.0")
            implementation("com.russhwolf:multiplatform-settings-coroutines:1.3.0")
            
            // KVault for secure key-value storage
            implementation("com.liftric:kvault:1.12.0")
        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)
            
            // iOS-specific implementation of multiplatform-settings
            implementation("com.russhwolf:multiplatform-settings:1.3.0")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")
            
            // iOS specific KVault implementation
            implementation("com.liftric:kvault:1.12.0")
        }
    }
}

android {
    namespace = "com.clockwise"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.clockwise"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
//implementation(libs.androidx.core.i18n)
    //    implementation(libs.androidx.navigation.compose)
    debugImplementation(compose.uiTooling)
}
