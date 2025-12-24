# CoinTrend State Management Documentation

## Overview

The app uses a **Unidirectional Data Flow (UDF)** pattern with **MVVM** architecture, leveraging Compose's state management capabilities and Kotlin Flows for reactive programming.

## State Management Principles

### 1. Single Source of Truth
Each screen has one immutable state object that represents the entire UI state.

### 2. Unidirectional Data Flow
```
Events → ViewModel → State → UI
   ↑                           ↓
   └───────── User Input ←─────┘
```

### 3. Immutable State
State objects are immutable and updated via `copy()` functions.

## State Architecture

### ViewModel State Pattern

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val useCase: ExampleUseCase
) : ViewModel() {

    // Single mutable state holder
    var state by mutableStateOf(ExampleState())
        private set

    // State update functions
    fun updateState(newData: Data) {
        state = state.copy(data = newData)
    }
}
```

### State Data Classes

```kotlin
data class CoinsListState(
    val topCoinsList: ImmutableList<CoinUiModel> = persistentListOf(),
    val trendingCoinsList: ImmutableList<CoinUiModel> = persistentListOf(),
    val lastUpdateDate: String = "",
    val state: CoinsListUiState = CoinsListUiState.Idle
)

sealed class CoinsListUiState {
    object Idle : CoinsListUiState()
    object Loading : CoinsListUiState()
    object Refreshing : CoinsListUiState()
    data class Error(val message: String) : CoinsListUiState()
}
```

## Flow-Based State Management

### Using Kotlin Flows

```kotlin
class GetTopCoinsFlowUseCase @Inject constructor(
    private val repository: TopCoinsRepository
) {
    operator fun invoke(params: Unit): Flow<Resultat<TopCoinsData>> {
        return repository.getTopCoinsFlow()
            .map { coins ->
                Resultat.success(TopCoinsData(coins))
            }
            .catch { exception ->
                emit(Resultat.failure(exception))
            }
    }
}
```

### Flow Collection in ViewModels

```kotlin
private fun initTopCoinsFlowCollection() {
    topCoinsFlowJob = getTopCoinsFlowUseCase(Unit)
        .onEach { result ->
            handleGetTopCoinsState(result)
        }
        .catch { exception ->
            handleGetTopCoinsState(Resultat.failure(exception))
            cancelTopCoinsFlowCollection()
        }
        .launchIn(viewModelScope)
}
```

## Result State Management

### Using Resultat Library

The app uses the `Resultat` library for handling loading states alongside success/failure:

```kotlin
sealed class Resultat<out T> {
    data class Success<T>(val value: T) : Resultat<T>()
    data class Failure(val exception: Throwable) : Resultat<Nothing>()
    object Loading : Resultat<Nothing>()
}

// Usage
when (result) {
    is Resultat.Loading -> {
        state = state.copy(uiState = CoinsListUiState.Loading)
    }
    is Resultat.Success -> {
        state = state.copy(
            topCoinsList = mapper.mapToUiModel(result.value),
            uiState = CoinsListUiState.Idle
        )
    }
    is Resultat.Failure -> {
        state = state.copy(
            uiState = CoinsListUiState.Error(result.exception.message)
        )
    }
}
```

## Compose State Integration

### State Hoisting

```kotlin
@Composable
fun CoinsListScreen(
    viewModel: CoinsListViewModel = hiltViewModel()
) {
    val state = viewModel.state

    CoinsListContent(
        state = state,
        onRefresh = viewModel::refresh,
        onCoinClick = viewModel::onCoinSelected
    )
}

@Composable
private fun CoinsListContent(
    state: CoinsListState,
    onRefresh: () -> Unit,
    onCoinClick: (String) -> Unit
) {
    // Pure UI component with no business logic
}
```

### Remember and State

```kotlin
@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()

    SearchBar(
        query = searchQuery,
        onQueryChange = { newQuery ->
            searchQuery = newQuery
            viewModel.search(newQuery)
        }
    )
}
```

## Side Effects Management

### LaunchedEffect

```kotlin
@Composable
fun CoinDetailScreen(coinId: String) {
    LaunchedEffect(coinId) {
        viewModel.loadCoinDetails(coinId)
    }
}
```

### DisposableEffect

```kotlin
@Composable
fun LivePriceUpdates(coinId: String) {
    DisposableEffect(coinId) {
        viewModel.startPriceUpdates(coinId)

        onDispose {
            viewModel.stopPriceUpdates()
        }
    }
}
```

## Persistent State

### SavedStateHandle

```kotlin
@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase
) : ViewModel() {

    private val coinId: String = savedStateHandle.get<String>("coinId")!!

    var state by mutableStateOf(CoinDetailState())
        private set
}
```

### DataStore for Preferences

```kotlin
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> {
        return dataStore.data.map { preferences ->
            Settings(
                currency = preferences[CURRENCY_KEY] ?: "USD",
                refreshInterval = preferences[REFRESH_KEY] ?: 30
            )
        }
    }

    override suspend fun updateCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
}
```

## Navigation State

### Screen State with Reimagined Navigation

```kotlin
@Parcelize
sealed class Screen : Parcelable {
    object CoinsList : Screen()
    data class CoinDetail(val coinId: String) : Screen()
    object Search : Screen()
    object Favorites : Screen()
    object Settings : Screen()
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController<Screen>()

    NavBackHandler(navController)

    NavHost(
        controller = navController,
        startDestination = Screen.CoinsList
    ) {
        scene<Screen.CoinsList> {
            CoinsListScreen(
                onNavigateToDetail = { coinId ->
                    navController.navigate(Screen.CoinDetail(coinId))
                }
            )
        }
        // Other screens...
    }
}
```

## Complex State Updates

### Optimistic Updates

```kotlin
fun toggleFavorite(coinId: String) {
    // Optimistic update
    state = state.copy(
        favoriteCoins = if (coinId in state.favoriteCoins) {
            state.favoriteCoins - coinId
        } else {
            state.favoriteCoins + coinId
        }
    )

    // Actual update
    viewModelScope.launch {
        try {
            if (coinId in state.favoriteCoins) {
                addFavoriteCoinUseCase(coinId)
            } else {
                removeFavoriteCoinUseCase(coinId)
            }
        } catch (e: Exception) {
            // Revert on error
            state = state.copy(
                favoriteCoins = if (coinId in state.favoriteCoins) {
                    state.favoriteCoins - coinId
                } else {
                    state.favoriteCoins + coinId
                }
            )
        }
    }
}
```

### Debounced State Updates

```kotlin
class SearchViewModel : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    init {
        searchQuery
            .debounce(300)
            .filter { it.isNotBlank() }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                searchCoinsUseCase(query)
            }
            .onEach { result ->
                state = state.copy(searchResults = result)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}
```

## Testing State Management

### ViewModel Testing

```kotlin
@Test
fun `when refresh is called, state updates to refreshing`() = runTest {
    // Given
    val viewModel = CoinsListViewModel(
        getTopCoinsFlowUseCase = mockk(),
        refreshTopCoinsUseCase = mockk()
    )

    // When
    viewModel.refresh()

    // Then
    assertEquals(CoinsListUiState.Refreshing, viewModel.state.uiState)
}
```

### State Flow Testing

```kotlin
@Test
fun `coins flow emits updated data`() = runTest {
    val repository = FakeTopCoinsRepository()
    val useCase = GetTopCoinsFlowUseCase(repository)

    useCase(Unit).test {
        assertEquals(Resultat.Loading, awaitItem())
        assertEquals(Resultat.Success(testCoins), awaitItem())
        cancelAndConsumeRemainingEvents()
    }
}
```

## Best Practices

### 1. State Immutability
Always use immutable data structures and update via `copy()`.

### 2. Single State Object
Keep all screen state in a single data class for easier management.

### 3. State Derivation
Derive computed values instead of storing them:
```kotlin
val sortedCoins = state.coins.sortedBy { it.rank }
```

### 4. Avoid State Duplication
Don't duplicate state between ViewModel and UI components.

### 5. Handle All State Cases
Always handle loading, success, and error states explicitly.

### 6. Use Stable Keys
Provide stable keys for list items to optimize recomposition:
```kotlin
LazyColumn {
    items(
        items = state.coins,
        key = { coin -> coin.id }
    ) { coin ->
        CoinItem(coin = coin)
    }
}
```