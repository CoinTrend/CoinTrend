# CoinTrend UI Components Documentation

## Design System

### Theme Configuration

#### Color Scheme
```kotlin
// Dark Theme (Primary)
StocksDarkBackground = Color(0xFF000000)
StocksDarkPrimaryText = Color(0xFFFFFFFF)
StocksDarkSecondaryText = Color(0xFF888888)
StocksDarkSelectedCard = Color(0xFF1A1A1A)
StocksDarkSelectedChip = Color(0xFF2A2A2A)

// Semantic Colors
PositiveGreen = Color(0xFF4CAF50)
NegativeRed = Color(0xFFF44336)
```

#### Typography
Material 3 Typography system with custom text styles

#### Theme Provider
```kotlin
@Composable
fun CoinTrendTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
)
```

## Core Components

### 1. Coin List Items

#### CoinWithMarketDataItem
Full-featured coin list item with market data
```kotlin
@Composable
fun CoinWithMarketDataItem(
    coin: CoinWithMarketData,
    onCoinClick: (String) -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: (String) -> Unit
)
```

Features:
- Coin logo, name, and symbol
- Current price and market cap
- 24h price change percentage
- Favorite toggle button
- Ripple click effect

#### CoinWithMarketDataItemCompact
Compact version for dense lists
```kotlin
@Composable
fun CoinWithMarketDataItemCompact(
    coin: CoinWithMarketData,
    onCoinClick: (String) -> Unit
)
```

### 2. Chart Components

#### LineChart
Custom chart implementation for price history
```kotlin
@Composable
fun LineChart(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Dp = 2.dp,
    showGrid: Boolean = true
)
```

Features:
- Smooth line rendering
- Touch interactions
- Grid lines
- Value labels
- Animations

### 3. Custom Controls

#### SegmentedControl
iOS-style segmented control for options
```kotlin
@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelection: (Int) -> Unit,
    modifier: Modifier = Modifier
)
```

Used for:
- Time period selection (1D, 1W, 1M, etc.)
- View mode switching
- Filter options

### 4. Loading States

#### Placeholder Components
Shimmer effect placeholders using Accompanist
```kotlin
@Composable
fun CoinItemPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer()
            )
    )
}
```

#### Pull-to-Refresh
SwipeRefresh implementation
```kotlin
@Composable
fun RefreshableContent(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
        content = content
    )
}
```

### 5. Lists

#### Reorderable LazyColumn
Drag-and-drop list for favorite coins
```kotlin
@Composable
fun ReorderableFavoritesList(
    coins: List<Coin>,
    onReorder: (from: Int, to: Int) -> Unit
) {
    ReorderableColumn(
        state = rememberReorderableState(),
        onMove = { from, to ->
            onReorder(from.index, to.index)
        }
    ) {
        items(coins) { coin ->
            ReorderableItem(
                key = coin.id,
                defaultDraggingModifier = Modifier
            ) { isDragging ->
                CoinItem(coin = coin, isDragging = isDragging)
            }
        }
    }
}
```

## Screen Components

### 1. CoinsListScreen
Main screen showing top coins and trending
```kotlin
@Composable
fun CoinsListScreen(
    viewModel: CoinsListViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit
)
```

Features:
- Tab layout (Top Coins / Trending)
- Pull-to-refresh
- Loading states
- Error handling
- Search FAB

### 2. CoinDetailScreen
Detailed view of a single coin
```kotlin
@Composable
fun CoinDetailScreen(
    coinId: String,
    viewModel: CoinDetailViewModel,
    onBackPress: () -> Unit
)
```

Features:
- Price chart with time controls
- Market statistics
- Price alerts
- Add to favorites
- Share functionality

### 3. SearchScreen
Coin search with instant results
```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToDetail: (String) -> Unit,
    onBackPress: () -> Unit
)
```

Features:
- Real-time search
- Search history
- Trending searches
- Results filtering

### 4. FavoriteCoinsScreen
User's watchlist management
```kotlin
@Composable
fun FavoriteCoinsScreen(
    viewModel: FavoriteCoinsViewModel,
    onNavigateToDetail: (String) -> Unit
)
```

Features:
- Drag-to-reorder
- Swipe-to-delete
- Bulk actions
- Empty state

### 5. SettingsScreen
App configuration options
```kotlin
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackPress: () -> Unit
)
```

Settings:
- Currency selection
- Refresh interval
- Notifications
- Theme (if light theme added)
- Data usage

## Common Composables

### Navigation Components

#### TopBar
```kotlin
@Composable
fun CoinTrendTopBar(
    title: String,
    onBackPress: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
)
```

#### BottomBar
```kotlin
@Composable
fun CoinTrendBottomBar(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit
)
```

### Dialogs and Sheets

#### ConfirmationDialog
```kotlin
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
)
```

#### BottomSheet
```kotlin
@Composable
fun CoinOptionsBottomSheet(
    coin: Coin,
    onAddToFavorites: () -> Unit,
    onSetAlert: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
)
```

## State Management

### Screen States
```kotlin
data class CoinsListState(
    val topCoinsList: List<CoinUiModel>,
    val trendingCoinsList: List<CoinUiModel>,
    val lastUpdateDate: String,
    val state: CoinsListUiState
)

sealed class CoinsListUiState {
    object Idle : CoinsListUiState()
    object Loading : CoinsListUiState()
    object Refreshing : CoinsListUiState()
    data class Error(val message: String) : CoinsListUiState()
}
```

### UI Models
```kotlin
data class CoinUiModel(
    val id: String,
    val rank: Int,
    val symbol: String,
    val name: String,
    val imageUrl: String,
    val currentPrice: String,
    val priceChangePercentage24h: String,
    val priceChangeColor: Color,
    val marketCap: String
)
```

## Animations

### Shared Element Transitions
Custom implementation for smooth transitions between screens
```kotlin
@Composable
fun SharedElement(
    key: Any,
    screenKey: Any,
    transitionSpec: SharedElementTransitionSpec
) {
    // Custom shared element implementation
}
```

### Content Animations
- Fade in/out for loading states
- Slide animations for screen transitions
- Scale animations for favorites
- Shimmer effects for placeholders

## Accessibility

### Content Descriptions
```kotlin
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = stringResource(
        if (isFavorite) R.string.remove_from_favorites
        else R.string.add_to_favorites
    )
)
```

### Semantic Properties
```kotlin
Modifier.semantics {
    contentDescription = "Coin: ${coin.name}"
    onClick(label = "View details") { true }
}
```

## Best Practices

### Performance
1. Use `remember` for expensive computations
2. Implement `key` in lazy lists
3. Use `ImmutableList` for state
4. Avoid recomposition with stable parameters

### Reusability
1. Extract common UI patterns
2. Use composition over inheritance
3. Create themed components
4. Provide sensible defaults

### Testing
1. Use `ComposeTestRule` for UI tests
2. Test state changes
3. Verify accessibility
4. Test user interactions