# CoinTrend Navigation Migration Guide

## Overview

This document outlines the migration from Reimagined Navigation library to official Jetpack Navigation Compose with type-safe navigation. The migration provides better type safety, improved performance, and official Google support.

## Migration Changes Summary

### 1. Dependencies Updated

#### Removed Dependencies
```gradle
// REMOVED: Reimagined Navigation
implementation "dev.olshevski.navigation:reimagined-hilt:1.5.0"
```

#### Added Dependencies
```gradle
// NEW: Jetpack Navigation Compose with type-safe navigation
implementation "androidx.navigation:navigation-compose:2.7.6"
implementation "androidx.hilt:hilt-navigation-compose:1.1.0"

// NEW: Kotlin Serialization for type-safe navigation routes
implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2"
```

#### Plugin Changes
```gradle
// Added to presentation/build.gradle
plugins {
    // ... existing plugins
    id 'org.jetbrains.kotlin.plugin.serialization'
}

// Added to root build.gradle
plugins {
    // ... existing plugins
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.9.25' apply false
}
```

### 2. Navigation Routes Architecture

#### Old Approach (Screen sealed class)
```kotlin
// models/Screen.kt
sealed interface Screen : Parcelable {
    @Parcelize
    object CoinsList : Screen

    @Parcelize
    data class CoinDetail(val coinDetailMainData: CoinUiItem) : Screen
}
```

#### New Approach (Serializable Routes)
```kotlin
// navigation/NavigationRoutes.kt
@Serializable
sealed interface NavigationRoute

@Serializable
object CoinsListRoute : NavigationRoute

@Serializable
@Parcelize
data class CoinDetailRoute(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinImageUrl: String,
    val coinMarketCapRank: String
) : NavigationRoute, Parcelable
```

### 3. MainActivity Changes

#### Old Approach
```kotlin
// MainActivity.kt - OLD
val navController = rememberNavController<Screen>(
    startDestination = Screen.CoinsList
)

NavBackHandler(navController)

val isBackStackEmpty by remember {
    derivedStateOf {
        navController.backstack.entries.size == 1
    }
}

// Scaffold with complex navigation setup
```

#### New Approach
```kotlin
// MainActivity.kt - NEW
val navController = rememberNavController()

BackHandler(enabled = navController.previousBackStackEntry == null) {
    // Handle back navigation
}

CoinTrendNavigation(
    navController = navController,
    startDestinationViewModel = startDestinationViewModel,
    // ... other parameters
)
```

### 4. Navigation Implementation

#### Old Approach (Reimagined Navigation)
```kotlin
NavHost(
    controller = navController
) { route ->
    when(route) {
        is Screen.CoinsList -> {
            CoinsListScreen(navController = navController, viewModel = startDestinationViewModel)
        }
        is Screen.CoinDetail -> {
            CoinDetailScreen(coinDetailMainUiData = route.coinDetailMainData, navController = navController)
        }
    }
}
```

#### New Approach (Jetpack Navigation Compose)
```kotlin
NavHost(
    navController = navController,
    startDestination = CoinsListRoute
) {
    composable<CoinsListRoute> {
        CoinsListScreen(
            navController = navController,
            viewModel = startDestinationViewModel,
            onCoinClick = { coinUiItem ->
                navController.navigate(
                    CoinDetailRoute(
                        coinId = coinUiItem.id,
                        coinName = coinUiItem.name,
                        coinSymbol = coinUiItem.symbol,
                        coinImageUrl = coinUiItem.imageUrl,
                        coinMarketCapRank = coinUiItem.marketCapRank
                    )
                )
            }
        )
    }

    composable<CoinDetailRoute> { backStackEntry ->
        val coinDetail = backStackEntry.toRoute<CoinDetailRoute>()
        val coinUiItem = CoinUiItem(
            id = coinDetail.coinId,
            name = coinDetail.coinName,
            symbol = coinDetail.coinSymbol,
            imageUrl = coinDetail.coinImageUrl,
            marketCapRank = coinDetail.coinMarketCapRank
        )
        CoinDetailScreen(
            coinDetailMainUiData = coinUiItem,
            navController = navController
        )
    }
}
```

### 5. Bottom Navigation Updates

#### Old Approach
```kotlin
// BottomNavigationItem enum referenced Screen directly
enum class BottomNavigationItem(val route: Screen, ...)

// Navigation logic
onClick = {
    if (item.route != currentDestination) {
        navController.popAll()
        navController.navigate(item.route)
    }
}
```

#### New Approach
```kotlin
// BottomNavigationItem enum can still reference Screen (for compatibility)
// But navigation logic updated

val isSelected = when (item) {
    BottomNavigationItem.Market -> currentRoute?.contains("CoinsListRoute") == true
    BottomNavigationItem.Favourites -> currentRoute?.contains("FavouriteCoinsListRoute") == true
    // ...
}

onClick = {
    if (!isSelected) {
        when (item) {
            BottomNavigationItem.Market -> {
                navController.navigate(CoinsListRoute) {
                    popUpTo(CoinsListRoute) { inclusive = true }
                }
            }
            // ...
        }
    }
}
```

## Key Benefits

### 1. Type Safety
- **Before**: No compile-time checking of navigation arguments
- **After**: Full compile-time type checking with Kotlin serialization

### 2. Improved Performance
- **Before**: Custom navigation implementation
- **After**: Official Google implementation with optimizations

### 3. Better Developer Experience
- **Before**: Manual route management
- **After**: Automatic route generation and type-safe navigation

### 4. Future Compatibility
- **Before**: Third-party library with uncertain support
- **After**: Official Jetpack library with long-term support

## Migration Steps

1. **Update Dependencies**
   - Remove Reimagined Navigation dependency
   - Add Jetpack Navigation Compose and serialization dependencies
   - Add serialization plugin

2. **Create Navigation Routes**
   - Create new `NavigationRoutes.kt` with serializable routes
   - Ensure complex data is properly serialized

3. **Update MainActivity**
   - Replace Reimagined Navigation with Jetpack Navigation
   - Update back handler logic
   - Use new `CoinTrendNavigation` composable

4. **Create CoinTrendNavigation**
   - Centralize navigation logic
   - Handle bottom navigation
   - Implement type-safe navigation between screens

5. **Create Screen Wrappers** (Interim Solution)
   - Create wrapper composables for existing screens
   - Extract navigation logic from screens to wrappers
   - Allow gradual migration without breaking existing screens

6. **Update Screen Composables** (Long-term)
   - Modify screens to accept navigation callbacks instead of direct NavController
   - Remove Reimagined Navigation imports
   - This improves testability and separation of concerns

## Implementation Status

### ✅ Completed
- Dependencies updated in `build.gradle` files
- Navigation routes created in `/navigation/NavigationRoutes.kt`
- MainActivity updated to use Jetpack Navigation
- CoinTrendNavigation composable created
- Screen wrappers implemented for gradual migration

### 🔄 In Progress / Next Steps
- Update individual screen composables to remove NavController dependencies
- Remove Reimagined Navigation imports from screen files
- Implement actual screen content in wrapper functions
- Add transition animations
- Add deep link support

## Potential Issues and Solutions

### 1. Complex Object Navigation
**Problem**: Passing complex objects between screens
**Solution**: Break down complex objects into primitive properties or use JSON serialization

### 2. Back Stack Management
**Problem**: Different back stack behavior
**Solution**: Use appropriate `popUpTo` configurations and inclusive flags

### 3. Deep Links
**Problem**: Need to support deep linking
**Solution**: Add navigation deep link support with proper route patterns

### 4. Shared Element Transitions
**Problem**: Maintaining shared element transitions
**Solution**: Ensure SharedElementsRoot is properly positioned in navigation hierarchy

## Testing Considerations

1. **Navigation Testing**
   - Use `TestNavHostController` for testing navigation flows
   - Test route parameter passing and retrieval

2. **Screen Testing**
   - Mock navigation functions when testing individual screens
   - Use `NavController.setCurrentDestination()` in tests

3. **Integration Testing**
   - Test complete navigation flows between screens
   - Verify bottom navigation state changes

## Performance Improvements

1. **Reduced Memory Usage**: Jetpack Navigation manages back stack more efficiently
2. **Better State Management**: Automatic SavedStateHandle support
3. **Lazy Loading**: Screens are only composed when needed
4. **Optimized Recomposition**: Better recomposition scoping

## Files Created/Modified

### New Files
- `/presentation/src/main/java/com/cointrend/presentation/navigation/NavigationRoutes.kt`
- `/presentation/src/main/java/com/cointrend/presentation/navigation/CoinTrendNavigation.kt`
- `/JETPACK_NAVIGATION_MIGRATION_GUIDE.md` (this file)

### Modified Files
- `/presentation/src/main/java/com/cointrend/presentation/MainActivity.kt`
- `/presentation/build.gradle`
- `/build.gradle`

## Code Quality and Best Practices

### Type Safety
```kotlin
// ✅ Type-safe navigation with compile-time checking
navController.navigate(
    CoinDetailRoute(
        coinId = "bitcoin",
        coinName = "Bitcoin",
        coinSymbol = "BTC",
        coinImageUrl = "...",
        coinMarketCapRank = "1"
    )
)

// ❌ Old approach with runtime errors possible
navController.navigate(Screen.CoinDetail(coinDetailMainData))
```

### Testability
```kotlin
// ✅ Screen with navigation callbacks (testable)
@Composable
fun CoinsListScreen(
    viewModel: CoinsListViewModel,
    onCoinClick: (CoinUiItem) -> Unit
)

// ❌ Screen with direct navigation dependency (hard to test)
@Composable
fun CoinsListScreen(
    navController: NavController<Screen>,
    viewModel: CoinsListViewModel
)
```

### Performance
- Reduced memory footprint with official navigation implementation
- Better back stack management
- Optimized recomposition patterns

## Conclusion

This migration improves the app's navigation architecture with:
- **Type Safety**: Compile-time checking prevents navigation errors
- **Performance**: Official Google implementation with optimizations
- **Maintainability**: Centralized navigation logic and clear patterns
- **Future-Proofing**: Official Jetpack library with long-term support
- **Testability**: Improved separation of concerns

The migration provides a solid foundation for modern Android navigation while maintaining backward compatibility during the transition period.