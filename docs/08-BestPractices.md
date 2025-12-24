# CoinTrend Best Practices & Implementation Guide

## Architecture Best Practices

### 1. Clean Architecture Principles

#### Separation of Concerns
```kotlin
// ❌ Bad: Business logic in UI
@Composable
fun CoinScreen() {
    val coins = remember { mutableStateOf<List<Coin>>(emptyList()) }

    LaunchedEffect(Unit) {
        val response = apiService.getCoins() // Direct API call in UI
        coins.value = response.map { it.toDomain() }
    }
}

// ✅ Good: Proper separation
@Composable
fun CoinScreen(
    viewModel: CoinViewModel = hiltViewModel()
) {
    val state = viewModel.state
    // UI only handles display logic
}
```

#### Dependency Rule
```kotlin
// Domain layer should not depend on external layers
// ❌ Bad: Domain depending on data layer
package com.cointrend.domain.usecase

import com.cointrend.data.api.CoinGeckoApiService // Wrong!

// ✅ Good: Domain using abstractions
package com.cointrend.domain.usecase

import com.cointrend.domain.repository.CoinRepository // Correct!
```

### 2. MVVM with MVI Pattern

#### Single State Object
```kotlin
// ✅ Good: Single immutable state
data class ScreenState(
    val coins: ImmutableList<Coin> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCoin: Coin? = null
)

@HiltViewModel
class ScreenViewModel : ViewModel() {
    var state by mutableStateOf(ScreenState())
        private set

    fun onCoinSelected(coin: Coin) {
        state = state.copy(selectedCoin = coin)
    }
}
```

#### Unidirectional Data Flow
```kotlin
// Events flow up, state flows down
@Composable
fun Screen(viewModel: ViewModel) {
    val state = viewModel.state // State flows down

    CoinList(
        coins = state.coins,
        onCoinClick = viewModel::onCoinSelected // Events flow up
    )
}
```

## Compose Best Practices

### 1. Performance Optimization

#### Use Stable Parameters
```kotlin
// ✅ Good: Stable parameters prevent unnecessary recomposition
@Stable
data class CoinUiModel(
    val id: String,
    val name: String,
    val price: String
)

@Composable
fun CoinItem(
    coin: CoinUiModel, // Stable parameter
    onClick: () -> Unit // Stable lambda
)
```

#### Remember Expensive Operations
```kotlin
@Composable
fun ExpensiveComputationScreen(data: List<Item>) {
    // ✅ Good: Remember expensive computation
    val processedData = remember(data) {
        data.filter { it.isValid }
            .sortedBy { it.priority }
            .take(100)
    }

    LazyColumn {
        items(processedData, key = { it.id }) { item ->
            ItemRow(item)
        }
    }
}
```

### 2. State Management

#### State Hoisting
```kotlin
// ✅ Good: State hoisted to parent
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }

    SearchBar(
        query = query,
        onQueryChange = { query = it }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange
    )
}
```

### 3. Reusability

#### Compose Previews
```kotlin
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CoinItemPreview() {
    CoinTrendTheme {
        CoinItem(
            coin = CoinUiModel(
                id = "bitcoin",
                name = "Bitcoin",
                price = "$50,000"
            ),
            onClick = {}
        )
    }
}
```

## Coroutines & Flow Best Practices

### 1. Structured Concurrency

```kotlin
@HiltViewModel
class ViewModel : ViewModel() {
    // ✅ Good: Use viewModelScope for automatic cancellation
    fun loadData() {
        viewModelScope.launch {
            val data = repository.getData()
            state = state.copy(data = data)
        }
    }

    // ✅ Good: Proper error handling
    fun loadDataSafely() {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true)
                val data = repository.getData()
                state = state.copy(data = data, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}
```

### 2. Flow Operations

```kotlin
class Repository {
    // ✅ Good: Proper Flow usage
    fun observeData(): Flow<List<Data>> = flow {
        emit(getLocalData()) // Emit cached data first
        emit(getRemoteData()) // Then fetch fresh data
    }
    .catch { e ->
        Timber.e(e, "Error fetching data")
        emit(emptyList()) // Fallback value
    }
    .flowOn(Dispatchers.IO)
}
```

### 3. Hot vs Cold Flows

```kotlin
// Cold Flow - starts fresh for each collector
fun coldFlow(): Flow<Int> = flow {
    repeat(5) { emit(it) }
}

// Hot Flow - shares data between collectors
class HotFlowExample {
    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()
}
```

## Dependency Injection Best Practices

### 1. Module Organization

```kotlin
// ✅ Good: Separate modules by layer/feature
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCoinRepository(
        impl: CoinRepositoryImpl
    ): CoinRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
    }
}
```

### 2. Scoping

```kotlin
// ✅ Good: Appropriate scoping
@Singleton // App-wide singleton
class ApiService

@ViewModelScoped // Lives as long as ViewModel
class ViewModelScopedDependency

@ActivityScoped // Lives as long as Activity
class ActivityScopedDependency
```

## Testing Best Practices

### 1. Unit Testing

```kotlin
class UseCaseTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val repository = mockk<Repository>()
    private val useCase = GetDataUseCase(repository)

    @Test
    fun `test successful data fetch`() = runTest {
        // Given
        val expectedData = listOf(testData)
        coEvery { repository.getData() } returns expectedData

        // When
        val result = useCase()

        // Then
        assertEquals(expectedData, result)
        coVerify { repository.getData() }
    }
}
```

### 2. UI Testing

```kotlin
class ScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testScreenContent() {
        composeTestRule.setContent {
            CoinScreen(
                state = CoinScreenState(
                    coins = listOf(testCoin)
                )
            )
        }

        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertIsDisplayed()
    }
}
```

## Error Handling Best Practices

### 1. Sealed Classes for Errors

```kotlin
sealed class AppError : Exception() {
    data class Network(override val message: String) : AppError()
    data class Api(val code: Int, override val message: String) : AppError()
    data class Database(override val message: String) : AppError()
    object Unknown : AppError()
}
```

### 2. Result Wrapper

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: AppError) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

fun <T> Result<T>.fold(
    onSuccess: (T) -> Unit,
    onError: (AppError) -> Unit,
    onLoading: () -> Unit = {}
) {
    when (this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError(exception)
        is Result.Loading -> onLoading()
    }
}
```

## Performance Best Practices

### 1. Lazy Loading

```kotlin
@Composable
fun CoinList(coins: List<Coin>) {
    LazyColumn {
        items(
            items = coins,
            key = { it.id } // Stable keys for better performance
        ) { coin ->
            CoinItem(coin)
        }
    }
}
```

### 2. Image Loading

```kotlin
@Composable
fun CoinImage(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .build(),
        contentDescription = null,
        modifier = Modifier.size(48.dp)
    )
}
```

### 3. Database Optimization

```kotlin
@Dao
interface CoinDao {
    // ✅ Good: Use transactions for multiple operations
    @Transaction
    suspend fun updateCoins(coins: List<CoinEntity>) {
        deleteAll()
        insertAll(coins)
    }

    // ✅ Good: Use LIMIT for pagination
    @Query("SELECT * FROM coins ORDER BY rank LIMIT :limit OFFSET :offset")
    fun getCoinsPage(limit: Int, offset: Int): Flow<List<CoinEntity>>
}
```

## Code Organization Best Practices

### 1. Package Structure

```
feature/
├── data/
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/
    ├── components/
    ├── screen/
    └── viewmodel/
```

### 2. Naming Conventions

```kotlin
// ViewModels: [Feature]ViewModel
class CoinDetailViewModel

// Use Cases: [Action][Entity]UseCase
class GetCoinDetailsUseCase
class UpdateFavoriteCoinUseCase

// Repositories: [Entity]Repository
interface CoinRepository
class CoinRepositoryImpl

// Composables: Descriptive names
@Composable
fun CoinPriceChart()

@Composable
fun FavoriteButton()
```

## Security Best Practices

### 1. API Key Management

```kotlin
// ❌ Bad: Hardcoded API key
const val API_KEY = "abc123xyz"

// ✅ Good: Use BuildConfig or secure storage
BuildConfig.API_KEY // Set in build.gradle from environment
```

### 2. ProGuard/R8 Rules

```proguard
# Keep data classes
-keep class com.cointrend.data.api.models.** { *; }

# Keep Retrofit interfaces
-keep interface com.cointrend.data.api.** { *; }

# Obfuscate sensitive classes
-repackageclasses ''
```

## Documentation Best Practices

### 1. KDoc Comments

```kotlin
/**
 * Fetches the list of top coins by market cap.
 *
 * @param limit Maximum number of coins to fetch
 * @param currency Currency for price values (e.g., "USD", "EUR")
 * @return Flow emitting list of coins or error
 * @throws NetworkException if network request fails
 */
fun getTopCoins(
    limit: Int = 100,
    currency: String = "USD"
): Flow<Result<List<Coin>>>
```

### 2. README Files

Include in each module:
- Purpose and responsibilities
- Key classes and their roles
- How to add new features
- Testing guidelines