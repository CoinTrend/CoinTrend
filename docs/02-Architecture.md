# CoinTrend Architecture Documentation

## Architecture Overview

The app follows **Clean Architecture** principles with **MVVM** pattern for the presentation layer. The architecture ensures separation of concerns, testability, and maintainability.

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  (Compose UI, ViewModels, UI States, Mappers)           │
├─────────────────────────────────────────────────────────┤
│                      Domain Layer                        │
│  (Use Cases, Repository Interfaces, Domain Models)      │
├─────────────────────────────────────────────────────────┤
│                       Data Layer                         │
│  (Repository Impl, Data Sources, DTOs, API)             │
└─────────────────────────────────────────────────────────┘
```

## Layer Details

### 1. Presentation Layer

#### ViewModels
- **Pattern:** MVVM with MVI-style state management
- **State Management:** Single immutable state object per ViewModel
- **Side Effects:** Handled through Coroutines and Flows

Example ViewModel Structure:
```kotlin
@HiltViewModel
class CoinsListViewModel @Inject constructor(
    private val getTopCoinsFlowUseCase: GetTopCoinsFlowUseCase,
    private val refreshTopCoinsUseCase: RefreshTopCoinsUseCase,
    private val mapper: UiMapper,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    var state by mutableStateOf(CoinsListState(...))
    private set

    // State updates through use cases
}
```

#### UI State Management
- **State Holder:** Single data class per screen
- **State Updates:** Immutable updates via `copy()`
- **UI States:** Idle, Loading, Success, Error, Refreshing

#### Screens
Main screens in the app:
- `CoinsListScreen` - Top and trending coins
- `FavouriteCoinsScreen` - User's favorite coins
- `CoinDetailScreen` - Detailed coin information
- `SearchScreen` - Coin search functionality
- `SettingsScreen` - App settings
- `AboutScreen` - App information

### 2. Domain Layer

#### Use Cases
- Single responsibility principle
- One use case per business operation
- Flow-based for reactive data
- Suspend functions for one-time operations

Types of Use Cases:
```kotlin
// Flow-based (reactive)
class GetTopCoinsFlowUseCase
class GetTrendingCoinsFlowUseCase
class GetFavouriteCoinsFlowUseCase

// Suspend functions (one-time)
class RefreshTopCoinsUseCase
class AddFavouriteCoinUseCase
class RemoveFavouriteCoinUseCase
class ReorderFavouriteCoinUseCase
```

#### Repository Interfaces
Clean interfaces defining data operations:
- `TopCoinsRepository`
- `TrendingCoinsRepository`
- `FavouriteCoinsRepository`
- `MarketChartRepository`
- `SearchRepository`
- `SettingsRepository`

### 3. Data Layer

#### Repository Implementations
Concrete implementations managing data sources:
```kotlin
class TopCoinsRepositoryImpl @Inject constructor(
    private val remoteDataSource: CoinGeckoTopCoinsRemoteDataSource,
    private val localDataSource: TopCoinsLocalDataSource,
    private val mapper: DataMapper
) : TopCoinsRepository
```

#### Data Sources

**Remote Data Sources:**
- `CoinGeckoTopCoinsRemoteDataSource`
- `CoinGeckoTrendingCoinsRemoteDataSource`
- `CoinGeckoSearchRemoteDataSource`
- `CoinGeckoCoinMarketDataRemoteDataSource`

**Local Data Sources:**
- Room Database for caching
- DataStore for preferences
- In-memory caching

#### API Service
```kotlin
interface CoinGeckoApiService {
    @GET("coins/markets")
    suspend fun getCoinsMarkets(...): List<CoinGeckoMarketsDto>

    @GET("coins/{id}/market_chart")
    suspend fun getCoinMarketChart(...): CoinGeckoMarketChartDto

    @GET("search/trending")
    suspend fun getSearchTrending(): CoinGeckoSearchTrendingDto

    @GET("search")
    suspend fun getSearch(query: String): CoinGeckoSearchDto
}
```

## Data Flow

### Unidirectional Data Flow (UDF)
```
User Action → ViewModel → Use Case → Repository → Data Source
                ↓
            State Update
                ↓
            UI Recomposition
```

### Example: Loading Top Coins
1. **UI Layer:** User opens app or pulls to refresh
2. **ViewModel:** Calls `getTopCoinsFlowUseCase`
3. **Use Case:** Invokes repository method
4. **Repository:**
   - Checks local cache first
   - Fetches from API if needed
   - Updates local cache
   - Returns Flow of data
5. **ViewModel:** Updates state with new data
6. **UI:** Recomposes with updated state

## Dependency Injection

### Hilt Modules

#### ApiModule
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideCoinGeckoApiService(): CoinGeckoApiService
}
```

#### DatabaseModule
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase
}
```

#### RepositoryModule
Binds repository implementations to interfaces

#### UseCaseModule
Provides use case instances

## Error Handling

### Custom Exceptions
- `TemporarilyUnavailableNetworkServiceException`
- Network errors wrapped in Result types
- UI-friendly error messages

### Error States
```kotlin
sealed class CoinsListUiState {
    object Idle : CoinsListUiState()
    object Loading : CoinsListUiState()
    object Refreshing : CoinsListUiState()
    data class Error(val message: String) : CoinsListUiState()
}
```

## Threading Strategy

### Coroutines & Dispatchers
- **UI Operations:** Main dispatcher
- **Network Calls:** IO dispatcher
- **Heavy Computations:** Default dispatcher
- **Database Operations:** IO dispatcher

### DispatcherProvider
Injected for testability:
```kotlin
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
```

## Caching Strategy

### Multi-Level Caching
1. **Memory Cache:** In ViewModel state
2. **Local Database:** Room for persistent storage
3. **HTTP Cache:** OkHttp 10MB cache

### Cache Invalidation
- Pull-to-refresh clears and reloads
- Time-based expiration
- Manual refresh via use cases

## Navigation

### Reimagined Navigation
Type-safe navigation with sealed classes:
```kotlin
sealed class Screen : Parcelable {
    @Parcelize
    object CoinsList : Screen()

    @Parcelize
    data class CoinDetail(val coinId: String) : Screen()

    @Parcelize
    object Search : Screen()
}
```

## Testing Strategy

### Unit Tests
- ViewModels with MockK
- Use Cases with mocked repositories
- Repositories with mocked data sources
- Mappers with sample data

### Integration Tests
- Repository with real Room database
- API integration tests

### UI Tests
- Compose UI tests
- Screen navigation tests
- User interaction flows