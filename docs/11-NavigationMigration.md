# Jetpack Navigation Migration Guide

## Migration from Reimagined Navigation to Jetpack Navigation Compose

This document details the migration from Reimagined Navigation library to the official Jetpack Navigation Compose with type-safe navigation.

## Overview

### What Changed
- **Removed:** Reimagined Navigation (`dev.olshevski.navigation:reimagined`)
- **Added:** Jetpack Navigation Compose with Kotlin Serialization for type safety
- **Approach:** Gradual migration with adapter pattern to minimize breaking changes

## Architecture Changes

### Old Navigation (Reimagined)
```kotlin
// Screen sealed class with Parcelable
sealed interface Screen : Parcelable {
    @Parcelize
    object CoinsList : Screen

    @Parcelize
    data class CoinDetail(val coinDetailMainData: CoinUiItem) : Screen
}

// NavHost with when expression
NavHost(controller = navController) { route ->
    when(route) {
        is Screen.CoinsList -> CoinsListScreen()
        is Screen.CoinDetail -> CoinDetailScreen(route.coinDetailMainData)
    }
}
```

### New Navigation (Jetpack Compose)
```kotlin
// Type-safe routes with Kotlin Serialization
@Serializable
object CoinsListRoute

@Serializable
data class CoinDetailRoute(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinImageUrl: String,
    val coinMarketCapRank: String
)

// Standard NavHost with composable blocks
NavHost(
    navController = navController,
    startDestination = CoinsListRoute
) {
    composable<CoinsListRoute> {
        CoinsListScreen()
    }

    composable<CoinDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<CoinDetailRoute>()
        CoinDetailScreen(route)
    }
}
```

## Implementation Details

### 1. Dependencies Updated

#### build.gradle (root)
```gradle
plugins {
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.9.25' apply false
}
```

#### presentation/build.gradle
```gradle
plugins {
    id 'org.jetbrains.kotlin.plugin.serialization'
}

dependencies {
    // Navigation
    implementation "androidx.navigation:navigation-compose:2.7.6"
    implementation "androidx.hilt:hilt-navigation-compose:1.1.0"

    // Serialization for type-safe routes
    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2"

    // Remove this after complete migration
    // implementation "dev.olshevski.navigation:reimagined-hilt:1.5.0"
}
```

### 2. Navigation Routes Created

**NavigationRoutes.kt**
```kotlin
package com.cointrend.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationRoute

@Serializable
object CoinsListRoute : NavigationRoute

@Serializable
object FavouriteCoinsListRoute : NavigationRoute

@Serializable
object SearchRoute : NavigationRoute

@Serializable
object SettingsRoute : NavigationRoute

@Serializable
object AboutRoute : NavigationRoute

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

### 3. Navigation Adapter Pattern

To enable gradual migration without breaking existing screens:

**NavigationAdapter.kt**
```kotlin
@Composable
fun rememberNavigationAdapter(
    navHostController: NavHostController
): ReimagineNavController<Screen> {
    // Bridge between old and new navigation
    // Forwards navigation calls to Jetpack Navigation
}

class NavigationAdapterImpl(
    private val jetpackController: NavHostController,
    private val reimaginedController: ReimagineNavController<Screen>
) : ReimagineNavController<Screen> by reimaginedController {

    override fun navigate(destination: Screen) {
        when (destination) {
            is Screen.CoinsList -> jetpackController.navigate(CoinsListRoute)
            is Screen.CoinDetail -> jetpackController.navigate(
                CoinDetailRoute(
                    coinId = destination.coinDetailMainData.id,
                    // ... map other fields
                )
            )
            // ... handle other screens
        }
    }

    override fun pop(): Boolean = jetpackController.popBackStack()
}
```

### 4. CoinTrendNavigation Composable

Centralized navigation logic:

```kotlin
@Composable
fun CoinTrendNavigation(
    navController: NavHostController,
    startDestinationViewModel: CoinsListViewModel,
    // ... other parameters
) {
    Scaffold(
        bottomBar = { /* Bottom navigation implementation */ }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = CoinsListRoute
        ) {
            composable<CoinsListRoute> {
                CoinsListScreenWrapper(navController, startDestinationViewModel)
            }

            composable<CoinDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<CoinDetailRoute>()
                CoinDetailScreenWrapper(route, navController)
            }

            // ... other screens
        }
    }
}
```

### 5. Updated MainActivity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CoinTrendTheme {
                val navController = rememberNavController() // Jetpack NavController

                BackHandler(enabled = navController.previousBackStackEntry == null) {
                    // Handle back press
                }

                Surface {
                    CoinTrendNavigation(
                        navController = navController,
                        // ... other parameters
                    )
                }
            }
        }
    }
}
```

## Migration Steps

### Phase 1: Setup (Complete ✅)
1. ✅ Add Jetpack Navigation dependencies
2. ✅ Add Kotlin Serialization plugin
3. ✅ Create NavigationRoutes.kt with type-safe routes
4. ✅ Create NavigationAdapter for compatibility

### Phase 2: Core Navigation (Complete ✅)
1. ✅ Create CoinTrendNavigation composable
2. ✅ Update MainActivity to use Jetpack Navigation
3. ✅ Implement bottom navigation with new system
4. ✅ Create screen wrappers for gradual migration

### Phase 3: Screen Migration (In Progress)
1. ⏳ Update each screen to remove NavController dependency
2. ⏳ Replace navigation calls with callbacks
3. ⏳ Remove Reimagined Navigation imports

### Phase 4: Cleanup (Pending)
1. ⏳ Remove NavigationAdapter once all screens updated
2. ⏳ Remove Reimagined Navigation dependency
3. ⏳ Clean up old Screen sealed class
4. ⏳ Update tests

## Screen Migration Pattern

### Before (with Reimagined Navigation)
```kotlin
@Composable
fun CoinsListScreen(
    navController: ReimagineNavController<Screen>,
    viewModel: CoinsListViewModel
) {
    // Screen content
    CoinItem(
        onClick = {
            navController.navigate(Screen.CoinDetail(coinData))
        }
    )
}
```

### After (with Jetpack Navigation)
```kotlin
@Composable
fun CoinsListScreen(
    onNavigateToCoinDetail: (CoinUiItem) -> Unit,
    viewModel: CoinsListViewModel
) {
    // Screen content
    CoinItem(
        onClick = {
            onNavigateToCoinDetail(coinData)
        }
    )
}
```

## Benefits of Migration

### 1. Official Support
- Long-term maintenance by Google
- Regular updates and bug fixes
- Better documentation and community support

### 2. Type Safety
- Compile-time checking of navigation arguments
- No string-based routes
- Kotlin Serialization for complex data

### 3. Better Integration
- Works seamlessly with other Jetpack libraries
- Better support for deep links
- Improved testing capabilities

### 4. Performance
- Optimized by Google for Android
- Better memory management
- Faster navigation transitions

## Common Issues & Solutions

### Issue 1: Screen Still Uses Old NavController
**Solution:** Use NavigationAdapter temporarily:
```kotlin
val navigationAdapter = rememberNavigationAdapter(navController)
OldScreen(navController = navigationAdapter)
```

### Issue 2: Complex Data Passing
**Solution:** Pass only essential data in route, fetch details in ViewModel:
```kotlin
@Serializable
data class CoinDetailRoute(
    val coinId: String // Pass only ID
    // Fetch full data in CoinDetailViewModel
)
```

### Issue 3: Back Navigation
**Solution:** Use NavController.popBackStack():
```kotlin
Button(onClick = { navController.popBackStack() }) {
    Text("Back")
}
```

## Testing

### Navigation Testing
```kotlin
@Test
fun testNavigation() {
    val navController = TestNavHostController(context)

    composeTestRule.setContent {
        CoinTrendNavigation(navController = navController)
    }

    // Test navigation
    composeTestRule.onNodeWithText("Bitcoin").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    assertTrue(route?.contains("CoinDetailRoute") == true)
}
```

## Next Steps

1. **Complete Screen Migration**: Update remaining screens to remove direct NavController dependency
2. **Remove Adapter**: Once all screens migrated, remove NavigationAdapter
3. **Add Deep Links**: Implement deep link support with new navigation
4. **Add Animations**: Implement enter/exit animations
5. **Update Tests**: Write comprehensive navigation tests

## Resources

- [Jetpack Navigation Compose Documentation](https://developer.android.com/jetpack/compose/navigation)
- [Type-safe Navigation](https://developer.android.com/guide/navigation/navigation-type-safety)
- [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html)
- [Migration Guide](https://developer.android.com/guide/navigation/navigation-migrate)

## Summary

The migration from Reimagined Navigation to Jetpack Navigation Compose provides:
- ✅ Official Google support
- ✅ Type-safe navigation with Kotlin Serialization
- ✅ Better performance and integration
- ✅ Gradual migration path with adapter pattern
- ✅ Future-proof architecture

The adapter pattern allows the app to work during migration, enabling a gradual transition without breaking existing functionality.