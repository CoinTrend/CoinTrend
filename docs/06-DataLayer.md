# CoinTrend Data Layer Documentation

## Overview

The Data Layer implements the Repository pattern and manages all data operations including API calls, local caching, and data transformations.

## Data Sources

### Remote Data Sources

#### CoinGeckoTopCoinsRemoteDataSource
```kotlin
@Singleton
class CoinGeckoTopCoinsRemoteDataSource @Inject constructor(
    private val apiService: CoinGeckoApiService,
    private val mapper: CoinGeckoDataMapper
) {
    suspend fun getTopCoins(
        page: Int = 1,
        perPage: Int = 100
    ): List<Coin> {
        return apiService.getCoinsMarkets(
            page = page,
            numCoinsPerPage = perPage,
            includeSparkline7dData = false
        ).map { dto ->
            mapper.mapToDomainModel(dto)
        }
    }
}
```

#### CoinGeckoTrendingCoinsRemoteDataSource
```kotlin
@Singleton
class CoinGeckoTrendingCoinsRemoteDataSource @Inject constructor(
    private val apiService: CoinGeckoApiService,
    private val mapper: CoinGeckoDataMapper
) {
    suspend fun getTrendingCoins(): List<TrendingCoin> {
        return apiService.getSearchTrending().coins.map { item ->
            mapper.mapToTrendingCoin(item)
        }
    }
}
```

### Local Data Sources

#### Room Database Configuration

```kotlin
@Database(
    entities = [
        CoinEntity::class,
        FavoriteCoinEntity::class,
        TrendingCoinEntity::class,
        MarketDataEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun trendingDao(): TrendingDao
    abstract fun marketDataDao(): MarketDataDao
}
```

#### Data Access Objects (DAOs)

```kotlin
@Dao
interface CoinDao {
    @Query("SELECT * FROM coins ORDER BY rank ASC")
    fun getAllCoins(): Flow<List<CoinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)

    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()

    @Transaction
    suspend fun refreshCoins(coins: List<CoinEntity>) {
        deleteAllCoins()
        insertCoins(coins)
    }
}
```

```kotlin
@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_coins ORDER BY position ASC")
    fun getFavoriteCoins(): Flow<List<FavoriteCoinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteCoinEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoriteCoinEntity)

    @Update
    suspend fun updatePosition(favorite: FavoriteCoinEntity)
}
```

### DataStore for Preferences

```kotlin
@Singleton
class PreferencesDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )

    private val dataStore = context.dataStore

    companion object {
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val REFRESH_INTERVAL_KEY = intPreferencesKey("refresh_interval")
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
    }

    val settingsFlow: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            currency = preferences[CURRENCY_KEY] ?: "USD",
            refreshInterval = preferences[REFRESH_INTERVAL_KEY] ?: 30,
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
        )
    }

    suspend fun updateCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
}
```

## Repository Implementations

### TopCoinsRepositoryImpl

```kotlin
@Singleton
class TopCoinsRepositoryImpl @Inject constructor(
    private val remoteDataSource: CoinGeckoTopCoinsRemoteDataSource,
    private val localDataSource: CoinDao,
    private val mapper: DataMapper,
    private val dispatcherProvider: DispatcherProvider
) : TopCoinsRepository {

    override fun getTopCoinsFlow(): Flow<List<Coin>> {
        return localDataSource.getAllCoins()
            .map { entities ->
                entities.map { mapper.mapToDomain(it) }
            }
            .onStart {
                // Refresh if cache is empty
                if (localDataSource.getAllCoins().first().isEmpty()) {
                    refreshTopCoins()
                }
            }
            .flowOn(dispatcherProvider.io)
    }

    override suspend fun refreshTopCoins() = withContext(dispatcherProvider.io) {
        try {
            val remoteCoins = remoteDataSource.getTopCoins()
            val entities = remoteCoins.map { mapper.mapToEntity(it) }
            localDataSource.refreshCoins(entities)
        } catch (e: Exception) {
            // Log error but don't crash - use cached data
            Timber.e(e, "Failed to refresh top coins")
            throw e
        }
    }
}
```

### FavoriteCoinsRepositoryImpl

```kotlin
@Singleton
class FavoriteCoinsRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val coinDao: CoinDao,
    private val remoteDataSource: CoinGeckoTopCoinsRemoteDataSource,
    private val mapper: DataMapper
) : FavoriteCoinsRepository {

    override fun getFavoriteCoinsFlow(): Flow<List<FavoriteCoin>> {
        return favoriteDao.getFavoriteCoins()
            .map { entities ->
                entities.map { favoriteEntity ->
                    val coinData = coinDao.getCoinById(favoriteEntity.coinId)
                    mapper.mapToFavoriteCoin(favoriteEntity, coinData)
                }
            }
    }

    override suspend fun addFavorite(coinId: String) {
        val position = favoriteDao.getMaxPosition() + 1
        favoriteDao.addFavorite(
            FavoriteCoinEntity(
                coinId = coinId,
                position = position,
                addedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeFavorite(coinId: String) {
        favoriteDao.removeFavorite(coinId)
    }

    override suspend fun reorderFavorites(fromPosition: Int, toPosition: Int) {
        // Complex reordering logic
    }
}
```

## Data Models

### Entity Models (Room)

```kotlin
@Entity(tableName = "coins")
data class CoinEntity(
    @PrimaryKey
    val id: String,
    val symbol: String,
    val name: String,
    val imageUrl: String,
    val rank: Int,
    val currentPrice: Double,
    val marketCap: Long,
    val priceChangePercentage24h: Double,
    val lastUpdated: Long
)

@Entity(
    tableName = "favorite_coins",
    indices = [Index(value = ["coinId"], unique = true)]
)
data class FavoriteCoinEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val coinId: String,
    val position: Int,
    val addedAt: Long
)
```

### DTO Models (API)

```kotlin
data class CoinGeckoMarketsDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("current_price")
    val currentPrice: Double?,

    @SerializedName("market_cap")
    val marketCap: Long?,

    @SerializedName("market_cap_rank")
    val marketCapRank: Int?,

    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,

    @SerializedName("sparkline_in_7d")
    val sparklineIn7d: SparklineDto?
)
```

## Data Mappers

### CoinGeckoDataMapper

```kotlin
@Singleton
class CoinGeckoDataMapper @Inject constructor() {

    fun mapToDomainModel(dto: CoinGeckoMarketsDto): Coin {
        return Coin(
            id = dto.id,
            symbol = dto.symbol.uppercase(),
            name = dto.name,
            imageUrl = dto.image,
            rank = dto.marketCapRank ?: 0,
            currentPrice = dto.currentPrice ?: 0.0,
            marketCap = dto.marketCap ?: 0L,
            priceChangePercentage24h = dto.priceChangePercentage24h ?: 0.0
        )
    }

    fun mapToEntity(coin: Coin): CoinEntity {
        return CoinEntity(
            id = coin.id,
            symbol = coin.symbol,
            name = coin.name,
            imageUrl = coin.imageUrl,
            rank = coin.rank,
            currentPrice = coin.currentPrice,
            marketCap = coin.marketCap,
            priceChangePercentage24h = coin.priceChangePercentage24h,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun mapEntityToDomain(entity: CoinEntity): Coin {
        return Coin(
            id = entity.id,
            symbol = entity.symbol,
            name = entity.name,
            imageUrl = entity.imageUrl,
            rank = entity.rank,
            currentPrice = entity.currentPrice,
            marketCap = entity.marketCap,
            priceChangePercentage24h = entity.priceChangePercentage24h
        )
    }
}
```

## Caching Strategy

### Cache-First Strategy
1. Return cached data immediately
2. Fetch fresh data in background
3. Update cache and emit new data

```kotlin
fun getDataFlow(): Flow<List<Data>> = flow {
    // Emit cached data first
    val cachedData = localDataSource.getData()
    emit(cachedData)

    // Fetch and emit fresh data
    try {
        val freshData = remoteDataSource.getData()
        localDataSource.saveData(freshData)
        emit(freshData)
    } catch (e: Exception) {
        // Continue using cached data on error
        if (cachedData.isEmpty()) {
            throw e // Re-throw if no cache
        }
    }
}
```

### Cache Invalidation

```kotlin
class CacheManager @Inject constructor(
    private val preferences: DataStore<Preferences>
) {
    companion object {
        private const val CACHE_DURATION_MS = 5 * 60 * 1000 // 5 minutes
    }

    suspend fun shouldRefresh(lastUpdate: Long): Boolean {
        val now = System.currentTimeMillis()
        return (now - lastUpdate) > CACHE_DURATION_MS
    }

    suspend fun markAsUpdated(key: String) {
        preferences.edit { prefs ->
            prefs[longPreferencesKey(key)] = System.currentTimeMillis()
        }
    }
}
```

## Error Handling

### Repository Error Handling

```kotlin
sealed class DataError : Exception() {
    object NetworkError : DataError()
    object CacheError : DataError()
    data class ApiError(val code: Int, val message: String) : DataError()
    object UnknownError : DataError()
}

suspend fun safeApiCall<T>(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: IOException) {
        Result.failure(DataError.NetworkError)
    } catch (e: HttpException) {
        Result.failure(DataError.ApiError(e.code(), e.message()))
    } catch (e: Exception) {
        Result.failure(DataError.UnknownError)
    }
}
```

## Dependency Injection

### Data Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindTopCoinsRepository(
        impl: TopCoinsRepositoryImpl
    ): TopCoinsRepository

    @Binds
    abstract fun bindFavoriteCoinsRepository(
        impl: FavoriteCoinsRepositoryImpl
    ): FavoriteCoinsRepository

    @Binds
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
```

### Database Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cointrend_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideCoinDao(database: AppDatabase): CoinDao {
        return database.coinDao()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}
```

## Testing

### Repository Testing

```kotlin
@Test
fun `getTopCoins returns cached data when available`() = runTest {
    // Given
    val cachedCoins = listOf(testCoin1, testCoin2)
    coEvery { localDataSource.getAllCoins() } returns flowOf(cachedCoins)

    // When
    val result = repository.getTopCoinsFlow().first()

    // Then
    assertEquals(cachedCoins, result)
    coVerify(exactly = 0) { remoteDataSource.getTopCoins() }
}
```

### Data Source Testing

```kotlin
@Test
fun `remote data source maps DTOs correctly`() = runTest {
    // Given
    val dto = CoinGeckoMarketsDto(id = "bitcoin", ...)
    coEvery { apiService.getCoinsMarkets() } returns listOf(dto)

    // When
    val result = remoteDataSource.getTopCoins()

    // Then
    assertEquals("bitcoin", result.first().id)
}
```