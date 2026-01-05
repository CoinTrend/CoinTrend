# Navigation System Explained - Reimagined Navigation

## Understanding the Navigation Implementation

You noticed something interesting - the `NavHost` doesn't have `composable {}` blocks like traditional Jetpack Navigation. This app uses **Reimagined Navigation** library, which works differently.

## How Reimagined Navigation Works

### Traditional Jetpack Navigation Compose
```kotlin
// What you might expect
NavHost(
    navController = navController,
    startDestination = "coins_list"
) {
    composable("coins_list") {
        CoinsListScreen()
    }
    composable("coin_detail/{coinId}") { backStackEntry ->
        val coinId = backStackEntry.arguments?.getString("coinId")
        CoinDetailScreen(coinId = coinId)
    }
}
```

### Reimagined Navigation (Current Implementation)
```kotlin
// What this app uses - notice the lambda parameter
NavHost(
    controller = navController
) { route ->  // 👈 This is the key difference!
    when(route) {
        is Screen.CoinsList -> CoinsListScreen()
        is Screen.CoinDetail -> CoinDetailScreen(route.coinDetailMainData)
        is Screen.Settings -> SettingsScreen()
    }
}
```

## Key Differences

### 1. Type-Safe Routes with Sealed Classes
```kotlin
// Instead of string routes, uses sealed classes
sealed interface Screen : Parcelable {
    @Parcelize
    object CoinsList : Screen

    @Parcelize
    object Settings : Screen

    @Parcelize
    data class CoinDetail(val coinDetailMainData: CoinUiItem) : Screen
}
```

### 2. Direct Parameter Passing
```kotlin
// Can pass complex objects directly
navController.navigate(
    Screen.CoinDetail(
        coinDetailMainData = CoinUiItem(
            id = "bitcoin",
            name = "Bitcoin",
            price = 50000.0
            // ... other properties
        )
    )
)
```

### 3. Navigation Controller
```kotlin
// Create controller with type parameter
val navController = rememberNavController<Screen>(
    startDestination = Screen.CoinsList
)
```

## Bottom Navigation Integration

### Current Implementation Breakdown

```kotlin
// 1. Define bottom navigation items with Screen routes
enum class BottomNavigationItem(
    val route: Screen,  // 👈 Links to Screen sealed class
    val icon: ImageVector,
    @StringRes val title: Int
) {
    Market(
        route = Screen.CoinsList,
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        title = R.string.market
    ),
    Favourites(
        route = Screen.FavouriteCoinsList,
        icon = Icons.Default.Star,
        title = R.string.favorite
    )
    // ... more items
}

// 2. Track current destination
val currentDestination by remember {
    derivedStateOf {
        navController.backstack.entries.first().destination
    }
}

// 3. Bottom navigation bar
NavigationBar {
    BottomNavigationItem.entries.forEach { item ->
        NavigationBarItem(
            selected = item.route == currentDestination,
            onClick = {
                if (item.route != currentDestination) {
                    navController.popAll()  // Clear backstack
                    navController.navigate(item.route)  // Navigate
                }
            },
            icon = { Icon(item.icon, contentDescription = null) },
            label = { Text(stringResource(item.title)) }
        )
    }
}
```

## How Navigation Flow Works

### 1. App Startup
```kotlin
// MainActivity.kt
val navController = rememberNavController<Screen>(
    startDestination = Screen.CoinsList  // Start here
)
```

### 2. Bottom Tab Navigation
```kotlin
// When user taps a bottom tab
onClick = {
    navController.popAll()  // Clear all screens
    navController.navigate(Screen.FavouriteCoinsList)  // Go to new screen
}
```

### 3. Deep Navigation (e.g., to Coin Detail)
```kotlin
// From CoinsListScreen
onCoinClick = { coin ->
    navController.navigate(
        Screen.CoinDetail(
            coinDetailMainData = mapper.mapToUiItem(coin)
        )
    )
}
```

### 4. Back Navigation
```kotlin
// Handled automatically by NavBackHandler
NavBackHandler(navController)

// Or manually
onBackClick = {
    navController.pop()
}
```

## Why This Approach?

### Advantages of Reimagined Navigation

1. **Type Safety**: No string-based routes that can have typos
2. **Complex Data Passing**: Pass entire objects, not just primitives
3. **Compile-Time Safety**: Errors caught at compile time
4. **Simpler API**: Less boilerplate than Jetpack Navigation
5. **Better IDE Support**: Auto-complete and refactoring work perfectly

### Disadvantages

1. **Non-Standard**: Not the official Android solution
2. **Less Community Support**: Smaller community than Jetpack Navigation
3. **Limited Documentation**: Less tutorials and examples
4. **Migration Cost**: If Google deprecates support, migration needed

## Modern Alternative: Type-Safe Jetpack Navigation

### With Navigation Compose 2.8+ (Recommended)
```kotlin
// Using Kotlin Serialization for type safety
@Serializable
object CoinsListRoute

@Serializable
data class CoinDetailRoute(
    val coinId: String,
    val coinName: String
)

// Setup navigation
NavHost(
    navController = navController,
    startDestination = CoinsListRoute
) {
    composable<CoinsListRoute> {
        CoinsListScreen(
            onNavigateToCoinDetail = { coin ->
                navController.navigate(
                    CoinDetailRoute(
                        coinId = coin.id,
                        coinName = coin.name
                    )
                )
            }
        )
    }

    composable<CoinDetailRoute> { backStackEntry ->
        val args: CoinDetailRoute = backStackEntry.toRoute()
        CoinDetailScreen(
            coinId = args.coinId,
            coinName = args.coinName
        )
    }
}
```

## Complete Navigation Example

### Setting Up Navigation with Bottom Bar

```kotlin
@Composable
fun MainApp() {
    val navController = rememberNavController<Screen>(
        startDestination = Screen.CoinsList
    )

    // Track current screen for bottom bar
    val currentScreen by remember {
        derivedStateOf {
            navController.backstack.entries.firstOrNull()?.destination
        }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentScreen)) {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        navController.popAll()
                        navController.navigate(screen)
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            controller = navController,
            modifier = Modifier.padding(paddingValues)
        ) { screen ->
            when (screen) {
                is Screen.CoinsList -> {
                    CoinsListScreen(
                        onNavigateToCoinDetail = { coin ->
                            navController.navigate(
                                Screen.CoinDetail(coin)
                            )
                        }
                    )
                }
                is Screen.CoinDetail -> {
                    CoinDetailScreen(
                        coin = screen.coinDetailMainData,
                        onBack = { navController.pop() }
                    )
                }
                is Screen.Settings -> {
                    SettingsScreen(
                        onBack = { navController.pop() }
                    )
                }
            }
        }
    }
}
```

## Navigation State Persistence

### Saving Navigation State
```kotlin
// The library handles this automatically with Parcelable
@Parcelize
data class CoinDetail(val coinDetailMainData: CoinUiItem) : Screen

// coinDetailMainData must also be Parcelable
@Parcelize
data class CoinUiItem(
    val id: String,
    val name: String,
    val price: Double
) : Parcelable
```

## Best Practices for This Navigation System

### 1. Keep Screens Lightweight
```kotlin
// Good: Pass only essential data
@Parcelize
data class CoinDetail(val coinId: String) : Screen

// Avoid: Passing large objects if possible
@Parcelize
data class CoinDetail(val entireCoinData: LargeCoinObject) : Screen
```

### 2. Handle Deep Links
```kotlin
fun handleDeepLink(deepLink: String): Screen {
    return when {
        deepLink.contains("coin/") -> {
            val coinId = deepLink.substringAfter("coin/")
            Screen.CoinDetail(coinId = coinId)
        }
        deepLink.contains("settings") -> Screen.Settings
        else -> Screen.CoinsList
    }
}
```

### 3. Navigation Testing
```kotlin
@Test
fun testNavigation() {
    val navController = TestNavController<Screen>()

    // Test navigation
    navController.navigate(Screen.Settings)
    assertEquals(Screen.Settings, navController.currentScreen)

    // Test back navigation
    navController.pop()
    assertEquals(Screen.CoinsList, navController.currentScreen)
}
```

## Summary

The navigation system in this app:
1. Uses **Reimagined Navigation** library, not Jetpack Navigation
2. Employs **type-safe sealed classes** instead of string routes
3. Allows **direct object passing** between screens
4. Integrates with **bottom navigation** through enum mapping
5. Provides **compile-time safety** for navigation

This is why you don't see `composable {}` blocks - the library uses a different pattern where the `NavHost` receives the current route as a parameter and you handle it with a `when` expression.