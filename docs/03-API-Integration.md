# CoinTrend API Integration Documentation

## API Provider

### CoinGecko API
- **Base URL:** `https://api.coingecko.com/api/v3/`
- **Authentication:** None required (public API)
- **Rate Limiting:** Handled automatically with 429 status detection
- **Documentation:** https://www.coingecko.com/api/documentation

## API Endpoints

### 1. Get Coins Markets
```
GET /coins/markets
```

**Parameters:**
- `vs_currency`: Currency for prices (default: "usd")
- `page`: Page number (default: 1)
- `per_page`: Results per page (default: 100)
- `order`: Sort order (default: "market_cap_desc")
- `sparkline`: Include 7-day sparkline data
- `price_change_percentage`: Price change intervals
- `ids`: Filter by coin IDs

**Response Model:**
```kotlin
data class CoinGeckoMarketsDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Long,
    val market_cap_rank: Int,
    val price_change_percentage_24h: Double,
    // ... more fields
)
```

### 2. Get Coin Market Chart
```
GET /coins/{id}/market_chart
```

**Parameters:**
- `id`: Coin ID (path parameter)
- `vs_currency`: Currency (default: "usd")
- `days`: Number of days (1, 7, 30, etc.)

**Response Model:**
```kotlin
data class CoinGeckoMarketChartDto(
    val prices: List<List<Double>>,
    val market_caps: List<List<Double>>,
    val total_volumes: List<List<Double>>
)
```

### 3. Get Trending Search
```
GET /search/trending
```

**Response Model:**
```kotlin
data class CoinGeckoSearchTrendingDto(
    val coins: List<TrendingCoinItem>
)
```

### 4. Search Coins
```
GET /search
```

**Parameters:**
- `query`: Search query string

**Response Model:**
```kotlin
data class CoinGeckoSearchDto(
    val coins: List<SearchCoinItem>
)
```

## Network Configuration

### Retrofit Setup
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideCoinGeckoApiService(
        @ApplicationContext context: Context
    ): CoinGeckoApiService {
        val cacheSize = 10 * 1024 * 1024L // 10MB
        val cache = Cache(context.cacheDir, cacheSize)

        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                // Rate limiting handler
                // Request logging
                // Response handling
            }
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CoinGeckoApiService.BASE_URL)
            .client(okHttpClient)
            .build()
            .create(CoinGeckoApiService::class.java)
    }
}
```

## Rate Limiting Strategy

### Implementation
1. **Detection:** Monitor for 429 (Too Many Requests) status
2. **Blocking:** Temporarily block requests for 1 minute
3. **Recovery:** Automatically resume after cooldown period

```kotlin
private const val MINUTES_TO_WAIT_WHEN_REACHED_MAX_REQUESTS = 1
private var timeWhenMaxRequestsLimitIsReached: LocalDateTime? = null

// In interceptor
when(response.code()) {
    429 -> {
        setMaxRequestsLimitReached()
        throw TemporarilyUnavailableNetworkServiceException(
            serviceName = "CoinGecko"
        )
    }
}
```

## Error Handling

### Network Exceptions
```kotlin
sealed class NetworkException : Exception() {
    data class TemporarilyUnavailable(
        val serviceName: String
    ) : NetworkException()

    data class ApiError(
        val code: Int,
        val message: String
    ) : NetworkException()

    object NoInternet : NetworkException()
}
```

### Repository Error Handling
```kotlin
try {
    val response = apiService.getCoinsMarkets()
    // Success handling
} catch (e: Exception) {
    when (e) {
        is TemporarilyUnavailableNetworkServiceException -> {
            // Handle rate limiting
        }
        is IOException -> {
            // Handle network errors
        }
        else -> {
            // Handle other errors
        }
    }
}
```

## Data Mapping

### DTO to Domain Model
```kotlin
@Singleton
class CoinGeckoDataMapper @Inject constructor() {
    fun mapToDomainModel(dto: CoinGeckoMarketsDto): Coin {
        return Coin(
            id = dto.id,
            symbol = dto.symbol,
            name = dto.name,
            imageUrl = dto.image,
            currentPrice = dto.current_price,
            marketCap = dto.market_cap,
            rank = dto.market_cap_rank,
            priceChangePercentage24h = dto.price_change_percentage_24h
        )
    }
}
```

## Caching Strategy

### HTTP Cache
- **Size:** 10MB
- **Location:** App cache directory
- **Strategy:** Cache-first with network fallback

### Local Database Cache
- **Implementation:** Room Database
- **Update Strategy:**
  - On app launch
  - Pull-to-refresh
  - Background sync

### Cache Invalidation
```kotlin
class TopCoinsRepositoryImpl {
    override suspend fun refreshTopCoins() {
        // Clear local cache
        localDataSource.clear()

        // Fetch fresh data
        val freshData = remoteDataSource.getTopCoins()

        // Update cache
        localDataSource.insert(freshData)
    }
}
```

## Usage Examples

### Fetching Top Coins
```kotlin
class GetTopCoinsFlowUseCase @Inject constructor(
    private val repository: TopCoinsRepository
) {
    operator fun invoke(params: Unit): Flow<Resultat<TopCoinsData>> {
        return repository.getTopCoinsFlow()
            .map { coins ->
                Resultat.success(
                    TopCoinsData(
                        coins = coins,
                        lastUpdate = System.currentTimeMillis()
                    )
                )
            }
            .catch { emit(Resultat.failure(it)) }
    }
}
```

### Search Implementation
```kotlin
class SearchCoinsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Resultat<List<Coin>> {
        return try {
            val results = repository.searchCoins(query)
            Resultat.success(results)
        } catch (e: Exception) {
            Resultat.failure(e)
        }
    }
}
```

## Important Notes

### API Key
- **No API key required** for basic endpoints
- Public API with generous rate limits
- Consider paid plans for production apps with high traffic

### Best Practices
1. Always implement proper error handling
2. Cache responses to minimize API calls
3. Respect rate limits
4. Use appropriate update intervals
5. Implement offline support
6. Show loading states during network operations

### Testing
- Mock API responses for unit tests
- Use test fixtures for consistent data
- Test error scenarios (network failure, 429 status, etc.)
- Verify caching behavior