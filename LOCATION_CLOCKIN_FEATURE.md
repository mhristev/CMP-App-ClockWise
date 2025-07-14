# Location-Based Clock-In Feature

This document describes the implementation of the location-based clock-in feature for the ClockWise mobile application.

## ⚠️ Current Status

**IMPORTANT**: The location-based clock-in feature is currently implemented but temporarily disabled for step-by-step integration. To avoid compilation issues, some imports and DI modules are commented out.

## Step-by-Step Integration

### Phase 1: Basic Compilation ✅
- [x] Core location models and interfaces created
- [x] Basic Android location service implemented  
- [x] Basic iOS location service implemented (simplified)
- [x] All compilation errors resolved

### Phase 2: Enable Location Services (Next Steps)
1. **Uncomment Platform Services**: Enable location services in platform modules
2. **Enable DI Modules**: Uncomment locationModule in initKoin.kt
3. **Test Basic Location**: Verify location permissions and basic GPS functionality

### Phase 3: Enable Clock-In Feature  
1. **Enable Clock-In Module**: Uncomment clockInModule in initKoin.kt
2. **Enable Navigation**: Uncomment ClockInScreenRoot in AppNavigation.kt
3. **Integration Testing**: Test end-to-end clock-in flow

### Phase 4: Production Polish
1. **Error Handling**: Enhance error scenarios and edge cases
2. **iOS Core Location**: Replace simplified iOS implementation with full Core Location
3. **UI/UX Polish**: Refine user interface and experience
4. **Performance**: Optimize location tracking and battery usage

## Overview

The location-based clock-in feature ensures that employees can only clock in when they are physically present at their assigned workplace. The feature uses device GPS/location services to verify the user's proximity to their business unit address.

## Key Features

- **Location Verification**: Checks if the user is within a specified radius (default: 100 meters) of their workplace
- **Cross-Platform**: Works on both Android and iOS using platform-specific location services
- **Permission Management**: Handles location permission requests gracefully
- **Real-time Status**: Shows current location status and distance from workplace
- **Error Handling**: Provides clear feedback when location is unavailable or permissions are denied

## Architecture

The feature follows the project's clean architecture principles with clear separation of concerns:

### Domain Layer
- `Location.kt` - Core location models and data classes
- `LocationRepository.kt` - Interface for location operations
- `CheckClockInEligibilityUseCase.kt` - Business logic for determining clock-in eligibility
- `ClockInUseCase.kt` - Orchestrates the complete clock-in process

### Data Layer
- `LocationRepositoryImpl.kt` - Implementation of location repository
- `PlatformLocationService.kt` - Platform-specific location service interface
- `AndroidLocationService.kt` - Android implementation using Google Play Services
- `IOSLocationService.kt` - iOS implementation using Core Location

### Presentation Layer
- `ClockInViewModel.kt` - Manages UI state and user interactions
- `ClockInScreen.kt` - Main UI composable
- `ClockInState.kt` - UI state model
- `ClockInAction.kt` - User action definitions

## Platform-Specific Implementations

### Android
- Uses Google Play Services Location API (`FusedLocationProviderClient`)
- Requires `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions
- Handles location settings and provider availability

### iOS
- Uses Core Location framework (`CLLocationManager`)
- Requires `NSLocationWhenInUseUsageDescription` permission
- Handles authorization status changes

## Configuration

### Dependencies
The feature requires the following dependencies (already added):

```kotlin
// Android
implementation("com.google.android.gms:play-services-location:21.0.1")

// Multiplatform
implementation("com.russhwolf:multiplatform-settings:1.3.0")
implementation("com.liftric:kvault:1.12.0")
```

### Permissions

**Android** (`AndroidManifest.xml`):
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**iOS** (`Info.plist`):
```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>This app needs location access to verify you are at your workplace for clock-in.</string>
```

## Usage

### Integration in App

1. **Add to Navigation**: Include the clock-in screen in your navigation setup
```kotlin
@Composable
fun AppNavigation() {
    // ... other screens
    composable("clockin") {
        ClockInScreenRoot()
    }
}
```

2. **Dependency Injection**: The modules are automatically included in `initKoin()`
```kotlin
// Already configured in initKoin.kt
modules(
    sharedModule,
    platformModule,
    authModule,
    locationModule,    // Location services
    clockInModule      // Clock-in feature
)
```

### Business Unit Setup

The feature requires business units to have address information with coordinates:

```kotlin
val businessUnit = BusinessUnit(
    id = "unit-123",
    name = "Main Office",
    companyId = "company-456",
    address = BusinessUnitAddress(
        street = "123 Business St",
        city = "Business City", 
        state = "BC",
        zipCode = "12345",
        country = "USA",
        latitude = 37.7749,    // San Francisco coordinates
        longitude = -122.4194
    )
)
```

## User Experience

### Clock-In Flow
1. User opens the clock-in screen
2. App checks location permissions
3. If permissions granted, app gets current location
4. App calculates distance to workplace
5. If within radius (≤100m): Clock-in button enabled
6. If too far: Shows distance and disables clock-in
7. User taps clock-in button
8. App sends request with location data to server

### UI States
- **Loading**: Showing spinner while getting location
- **Eligible**: Green status, clock-in button enabled
- **Too Far**: Red status, shows distance, button disabled
- **No Permission**: Shows permission request dialog
- **Location Disabled**: Shows message to enable location services

## API Integration

The feature sends clock-in requests with location data:

```kotlin
// Request payload
{
    "userId": "user-123",
    "businessUnitId": "unit-456", 
    "latitude": 37.7749,
    "longitude": -122.4194,
    "accuracy": 5.0,
    "timestamp": 1625097600000
}

// Response
{
    "id": "clockin-789",
    "userId": "user-123",
    "businessUnitId": "unit-456",
    "clockInTime": 1625097600000,
    "location": {
        "latitude": 37.7749,
        "longitude": -122.4194,
        "accuracy": 5.0
    },
    "status": "SUCCESS"
}
```

## Security Considerations

1. **Location Data**: Only collected when needed for clock-in
2. **Server Validation**: Server should validate location on backend
3. **Accuracy**: Uses high-accuracy location for precise verification
4. **Timeouts**: Location requests have reasonable timeouts
5. **Permission Respect**: Respects user permission choices

## Testing

### Manual Testing
1. Test with location enabled/disabled
2. Test permission denial/acceptance
3. Test at different distances from workplace
4. Test with poor GPS signal
5. Test airplane mode scenarios

### Automated Testing
- Unit tests for use cases and repository logic
- Platform-specific tests for location services
- UI tests for screen interactions

## Troubleshooting

### Common Issues

**Location not updating**:
- Check device location settings
- Verify app permissions
- Check GPS signal strength

**Clock-in disabled despite being at workplace**:
- Verify business unit coordinates are correct
- Check if radius needs adjustment
- Consider GPS accuracy limitations

**Permission issues**:
- Android: Check system settings > App permissions
- iOS: Check Settings > Privacy & Security > Location Services

## Future Enhancements

1. **Configurable Radius**: Allow admins to set different radii per business unit
2. **Geofencing**: Use native geofencing for better battery optimization
3. **Offline Support**: Cache last known good location temporarily
4. **Multiple Locations**: Support employees assigned to multiple business units
5. **Smart Suggestions**: Suggest enabling location when approaching workplace

## Dependencies

This feature integrates with:
- User authentication system
- Business unit management
- Company/organization data
- Secure storage for tokens
- Network layer for API calls
