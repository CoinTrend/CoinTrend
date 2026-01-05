# CoinTrend Project Setup Guide

## Prerequisites

### Development Environment
- **Android Studio:** Arctic Fox or later (recommended: latest stable)
- **JDK:** Version 21
- **Android SDK:** API 35 (Android 14)
- **Gradle:** 8.0+ (handled by wrapper)

### System Requirements
- **OS:** Windows 10/11, macOS 10.14+, or Linux
- **RAM:** Minimum 8GB (16GB recommended)
- **Disk Space:** 10GB+ for Android SDK and tools

## Initial Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-repo/CoinTrend.git
cd CoinTrend
```

### 2. Configure local.properties
Create `local.properties` in the root directory:
```properties
sdk.dir=/path/to/your/Android/Sdk
```

### 3. Sync Project
Open the project in Android Studio and sync Gradle files.

## Project Structure

```
CoinTrend/
├── app/                    # Application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/cointrend/
│   │   │   │   ├── CoinTrendApplication.kt
│   │   │   │   └── MainActivity.kt
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle
│
├── presentation/          # UI layer module
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/cointrend/presentation/
│   │   │       ├── ui/          # Screens
│   │   │       ├── theme/       # Design system
│   │   │       ├── models/      # UI models
│   │   │       └── mappers/     # Domain to UI mappers
│   │   └── test/
│   └── build.gradle
│
├── domain/               # Business logic module
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/cointrend/domain/
│   │   │       ├── features/    # Use cases by feature
│   │   │       ├── models/      # Domain models
│   │   │       └── repositories/ # Repository interfaces
│   │   └── test/
│   └── build.gradle
│
├── data/                 # Data layer module
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/cointrend/data/
│   │   │       ├── api/         # API services
│   │   │       ├── database/    # Room database
│   │   │       ├── features/    # Repository implementations
│   │   │       └── mappers/     # DTO to Domain mappers
│   │   └── test/
│   └── build.gradle
│
├── build.gradle          # Root build configuration
├── settings.gradle       # Module configuration
└── gradle.properties     # Gradle properties
```

## Build Configuration

### Gradle Properties
Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true
kotlin.code.style=official
android.useAndroidX=true
android.nonTransitiveRClass=true
```

### Build Variants
The app supports two build types:
- **Debug:** Development build with debugging enabled
- **Release:** Production build with ProGuard/R8 optimization

## API Configuration

### CoinGecko API
The app uses the free CoinGecko API:
- No API key required for basic endpoints
- Rate limiting is handled automatically
- Base URL: `https://api.coingecko.com/api/v3/`

## Running the App

### Debug Build
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
# Unit tests
./gradlew test

# Android instrumentation tests
./gradlew connectedAndroidTest

# All tests
./gradlew testDebugUnitTest connectedDebugAndroidTest
```

## Code Quality

### Lint Checks
```bash
./gradlew lint
```

### Code Formatting
The project uses Kotlin's official code style. Configure in Android Studio:
1. Settings → Editor → Code Style → Kotlin
2. Set from → Predefined style → Kotlin style guide

## Common Issues & Solutions

### Issue 1: Gradle Sync Failed
**Solution:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

### Issue 2: API Rate Limiting
**Solution:**
The app handles rate limiting automatically. If you encounter issues:
- Wait 1 minute before retrying
- Check network interceptor logs

### Issue 3: Room Schema Changes
**Solution:**
When modifying database entities:
1. Increment database version in `AppDatabase`
2. Add migration or use `.fallbackToDestructiveMigration()`

### Issue 4: Compose Preview Not Working
**Solution:**
1. Invalidate caches: File → Invalidate Caches
2. Ensure preview dependencies are included
3. Check `@Preview` annotations

## Development Workflow

### Feature Development
1. Create feature branch from `develop`
2. Implement in appropriate module:
   - Domain: Business logic
   - Data: API/Database operations
   - Presentation: UI components
3. Write tests
4. Run lint and tests
5. Create pull request

### Module Dependencies
```
app → presentation, domain, data
presentation → domain
data → domain
domain → (no Android dependencies)
```

### Adding New Features

#### 1. Domain Layer
```kotlin
// Create use case
class GetNewFeatureUseCase @Inject constructor(
    private val repository: NewFeatureRepository
)

// Define repository interface
interface NewFeatureRepository {
    fun getData(): Flow<Data>
}
```

#### 2. Data Layer
```kotlin
// Implement repository
@Singleton
class NewFeatureRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: NewFeatureDao
) : NewFeatureRepository
```

#### 3. Presentation Layer
```kotlin
// Create ViewModel
@HiltViewModel
class NewFeatureViewModel @Inject constructor(
    private val useCase: GetNewFeatureUseCase
) : ViewModel()

// Create Composable screen
@Composable
fun NewFeatureScreen(
    viewModel: NewFeatureViewModel = hiltViewModel()
)
```

## Debugging

### Enable Logging
```kotlin
// In Application class
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}
```

### Network Inspection
Use Android Studio's Network Profiler or add logging interceptor:
```kotlin
OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
```

### Database Inspection
Use Android Studio's Database Inspector:
1. View → Tool Windows → Database Inspector
2. Select running app process
3. Browse database tables

## Performance Optimization

### ProGuard/R8 Rules
Check `app/proguard-rules.pro` for:
- Retrofit models
- Gson serialization
- Compose rules

### Build Speed
```bash
# Enable configuration cache
./gradlew --configuration-cache

# Enable build cache
./gradlew --build-cache
```

## CI/CD Integration

### GitHub Actions Example
```yaml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - uses: actions/setup-java@v2
      with:
        java-version: '21'
    - run: ./gradlew build
    - run: ./gradlew test
```

## Resources

### Documentation
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [CoinGecko API](https://www.coingecko.com/api/documentation)

### Libraries
- [Hilt](https://dagger.dev/hilt/)
- [Retrofit](https://square.github.io/retrofit/)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)