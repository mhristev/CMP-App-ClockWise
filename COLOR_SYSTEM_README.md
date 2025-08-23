# ClockWise Professional Color System

## 🎨 Overview

The ClockWise app now features a comprehensive, professional color system based on the Purple-Pink-Yellow palette you provided. This system supports both light and dark modes and provides centralized color management.

## 📁 File Structure

```
composeApp/src/commonMain/kotlin/com/clockwise/app/theme/
├── ClockWiseColors.kt          # Central color definitions
├── ClockWiseTheme.kt           # Theme provider and integration
├── Typography.kt               # Professional typography system
├── Shape.kt                    # Modern rounded corner system
├── Effects.kt                  # Gradients and visual effects
└── ProfessionalComponents.kt   # Professional UI components
```

## 🎯 Key Features

### ✅ **Centralized Color Management**
- All colors defined in `ClockWiseColors.kt`
- Easy to change brand colors in one place
- Semantic color naming (primary, secondary, accent, etc.)

### ✅ **Light & Dark Mode Support** 
- Complete color schemes for both themes
- Proper contrast ratios for accessibility
- Automatic theme switching based on system preference

### ✅ **Feature-Specific Color Schemes**
- **Shift Scheduling**: Purple tones
- **Time Tracking**: Yellow accents  
- **Profile Settings**: Pink highlights
- **Business Management**: Darker purples

### ✅ **Professional Color Palette**
Based on your provided screenshots:

**Light Mode:**
- Primary: `#7768C6` (Medium Purple)
- Secondary: `#F9B0C3` (Soft Pink)
- Accent: `#FFC212` (Bright Yellow)
- Background: `#F9FAFB` (Light Gray)
- Surface: `#FFFFFF` (White)

**Dark Mode:**
- Primary: `#8C7AE6` (Bright Purple)
- Secondary: `#F78FB3` (Rose Pink)
- Accent: `#FFD43B` (Vibrant Yellow)
- Background: `#111827` (Dark Navy)
- Surface: `#1F2937` (Dark Gray)

## 🚀 Usage

### Basic Theme Usage
```kotlin
// In your App.kt (already implemented)
ClockWiseThemeProvider {
    // Your app content
}

// Access colors anywhere in your app
@Composable
fun MyComponent() {
    Text(
        text = "Hello",
        color = ClockWiseTheme.colors.primary
    )
}
```

### Feature-Specific Themes
```kotlin
// For shift-related screens
ShiftTheme {
    WeeklyScheduleScreen()
}

// For profile screens
ProfileTheme {
    ProfileScreen()
}
```

### Using Gradients
```kotlin
// Professional gradient buttons
GradientButton(
    text = "Save Changes",
    onClick = { /* action */ },
    gradient = ClockWiseGradients.PrimaryVertical
)

// Feature cards with gradients
FeatureCard(
    title = "Weekly Schedule",
    subtitle = "View your upcoming shifts",
    featureType = FeatureType.Shift
)
```

### Professional Components
```kotlin
// Professional card with gradient background
ProfessionalCard(
    gradient = ClockWiseGradients.LightBackground
) {
    Text("Card content")
}

// Status badges
StatusBadge(
    text = "Active",
    type = StatusType.Success
)
```

## 🎨 Available Colors

### Brand Colors
- `ClockWiseBrandColors.PrimaryPurple` - `#46467A`
- `ClockWiseBrandColors.SecondaryPurple` - `#7768C6`
- `ClockWiseBrandColors.AccentYellow` - `#FFC212`
- `ClockWiseBrandColors.SecondaryPink` - `#F9B0C3`
- `ClockWiseBrandColors.LightAccent` - `#E0DFFD`

### Theme Colors (Reactive)
- `ClockWiseTheme.colors.primary`
- `ClockWiseTheme.colors.secondary`
- `ClockWiseTheme.colors.accent`
- `ClockWiseTheme.colors.background`
- `ClockWiseTheme.colors.surface`
- `ClockWiseTheme.colors.success`
- `ClockWiseTheme.colors.warning`
- `ClockWiseTheme.colors.error`

### Gradients
- `ClockWiseGradients.PrimaryVertical`
- `ClockWiseGradients.SecondaryVertical`
- `ClockWiseGradients.AccentVertical`
- `ClockWiseGradients.ShiftGradient`
- `ClockWiseGradients.TimeTrackingGradient`
- `ClockWiseGradients.ProfileGradient`
- `ClockWiseGradients.BusinessGradient`

## 🔄 Migration from Old Colors

The system maintains backward compatibility with existing color references while providing new professional alternatives:

```kotlin
// Old way (still works but deprecated)
color = ShiftColors.Primary

// New way (recommended)
color = ShiftColors.primary()  // Reactive to theme changes
```

## 🎛️ Customization

### Changing Brand Colors
Simply update the values in `ClockWiseBrandColors`:

```kotlin
object ClockWiseBrandColors {
    val PrimaryPurple = Color(0xFF46467A)  // Change this
    val SecondaryPurple = Color(0xFF7768C6)  // Change this
    // ... other colors
}
```

### Adding New Feature Colors
1. Add colors to feature objects in `ClockWiseColors.kt`
2. Create gradients in `Effects.kt`
3. Add theme variants in `ClockWiseTheme.kt`

## ✨ Visual Improvements

The new system provides:
- **Professional gradients** on buttons and cards
- **Proper shadows** and elevation
- **Modern rounded corners** throughout
- **Glass morphism effects** for premium feel
- **Semantic status colors** (green=success, red=error, etc.)
- **Feature-specific theming** for intuitive navigation

## 🔧 Build Status

✅ **App compiles successfully** with the new color system
✅ **Backward compatibility** maintained
✅ **All existing functionality** preserved
✅ **Professional visual enhancements** applied

The ClockWise app now features a modern, professional design system that's easy to maintain and customize while providing an excellent user experience with proper light/dark mode support!