# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ClockWise is a Kotlin Multiplatform mobile application for employee time tracking and workforce management. The app targets Android and iOS platforms using Compose Multiplatform for shared UI and business logic.

## Build Commands

### Development
```bash
./gradlew build                    # Build the entire project
./gradlew composeApp:assembleDebug # Build Android debug APK
./gradlew iosX64Test              # Run iOS simulator tests
```

### iOS
```bash
# Open iOS project in Xcode
open iosApp/iosApp.xcodeproj
```

## Architecture

The project follows Clean Architecture with clear separation between:

- **Domain Layer**: Business logic, use cases, and interfaces
- **Data Layer**: API clients, repositories, and data sources  
- **Presentation Layer**: ViewModels, UI state, and Compose screens

### Module Structure

#### Core Modules
- `core/di/` - Dependency injection with Koin
- `core/data/` - HTTP client, secure storage, shared data utilities
- `core/domain/` - Common models and error handling
- `core/platform/` - Platform abstraction layer

#### Feature Modules
Each feature follows the same structure:
```
features/[feature-name]/
├── data/
│   ├── dto/           # API response models
│   ├── network/       # API data sources
│   └── repository/    # Repository implementations
├── domain/
│   ├── model/         # Domain models
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business logic use cases
├── presentation/
│   ├── [FeatureScreen].kt
│   ├── [FeatureViewModel].kt
│   ├── [FeatureState].kt
│   └── [FeatureAction].kt
└── di/               # Feature-specific DI modules
```

#### Key Features
- **Auth**: User authentication and session management
- **Shift**: Weekly schedules and shift management
- **Availability**: Calendar-based availability tracking
- **Profile**: User profile and settings
- **Business**: Business unit management (admin only)
- **Organization**: Company and business unit data
- **Location/ClockIn**: Location-based time tracking (currently disabled)

### Platform-Specific Code

#### Android (`androidMain/`)
- Uses Google Play Services for location
- Implements platform-specific services in `[Feature]ServiceImpl.kt`
- Android manifest permissions and configurations

#### iOS (`iosMain/`)
- Uses Core Location framework
- Platform services follow same naming pattern
- Info.plist permissions and configurations

### Dependency Injection

Uses Koin for DI. Main modules are configured in `initKoin.kt`:
- `sharedModule` - Core shared dependencies
- `platformModule` - Platform-specific implementations  
- Feature modules (e.g., `authModule`, `organizationModule`)

### Navigation

- Uses Jetpack Navigation Compose
- Routes defined in `NavigationRoutes.kt`
- Main navigation in `App.kt` with bottom navigation
- Role-based access control via `AccessControl.kt`

### Data Management

- **Network**: Ktor HTTP client with authentication and logging
- **Storage**: 
  - KVault for secure token storage
  - Multiplatform Settings for preferences
  - DataStore on Android, UserDefaults on iOS

### Location Feature Status

The location-based clock-in feature is implemented but currently disabled for step-by-step integration. See `LOCATION_CLOCKIN_FEATURE.md` for detailed integration steps.

## Key Configuration Files

- `gradle/libs.versions.toml` - Dependency versions
- `composeApp/build.gradle.kts` - Main app configuration
- `local.properties` - Local SDK paths (not in git)

## Development Notes

### Testing
- Common tests in `commonTest/`
- Platform-specific tests in respective platform folders
- No specific test runner commands found - use standard Gradle test tasks

### Code Style
- Follows standard Kotlin conventions
- Uses Compose for UI across platforms
- Async operations with Kotlin Coroutines
- Repository pattern for data access

### Security
- Secure token storage with KVault
- Network security config for Android
- Location permissions properly requested
- Role-based access control implemented