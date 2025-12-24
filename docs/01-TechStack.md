# CoinTrend Tech Stack Documentation

## Core Technologies

### Android SDK & Kotlin
- **Min SDK:** 23 (Android 6.0 Marshmallow)
- **Target SDK:** 35 (Android 14+)
- **Compile SDK:** 35
- **Kotlin Version:** 1.9.25
- **JVM Target:** Java 21

### Architecture Pattern
**MVVM with Clean Architecture**
- **Presentation Layer:** ViewModels + Compose UI
- **Domain Layer:** Use Cases + Repository Interfaces
- **Data Layer:** Repository Implementations + Remote/Local Data Sources

## Build Configuration

### Gradle
- **Android Gradle Plugin:** 8.10.0
- **Kotlin Gradle Plugin:** 1.9.25
- **Build Features:**
  - Compose enabled
  - BuildConfig enabled
  - Core Library Desugaring enabled

## UI Framework

### Jetpack Compose
- **Compose Version:** 1.6.8
- **Compose Compiler:** 1.5.15
- **Material Design 3:** 1.1.2
- **Activity Compose:** 1.8.2
- **ConstraintLayout Compose:** 1.0.1

### Compose Extensions
- **Accompanist:** 0.25.1
  - SwipeRefresh
  - Placeholder Material
- **Compose Reorderable:** 0.9.6 (for drag-and-drop lists)
- **Glide Compose:** 1.0.0-beta01 (image loading)

## Dependency Injection
- **Dagger Hilt:** 2.52
- **Hilt Navigation for Compose:** via reimagined-hilt 1.5.0

## Navigation
- **Reimagined Navigation:** 1.5.0 (Type-safe Compose navigation)

## Networking

### HTTP Client
- **Retrofit:** 2.10.0
- **OkHttp:** (bundled with Retrofit)
- **Gson Converter:** 2.10.0

### Network Features
- Request caching (10MB cache)
- Rate limiting protection (429 status handling)
- Network interceptor for request monitoring

## Data Persistence

### Local Storage
- **Room Database:** 2.6.1
  - Schema export enabled for migrations
  - Kotlin extensions (KTX) included

### Preferences
- **DataStore Preferences:** 1.0.0 (for app settings)

## Asynchronous Programming
- **Kotlin Coroutines:** 1.7.3
- **Coroutines Android:** 1.6.4
- **Flow:** Extensively used for reactive data streams

## State Management
- **Resultat:** 1.0.0 (Enhanced Result type with loading states)
- **Immutable Collections:** 0.3.7 (for state immutability)
- **Compose Runtime LiveData:** For LiveData integration

## Android Jetpack Components
- **Core KTX:** 1.12.0
- **Lifecycle:** 2.7.0
  - ViewModel KTX
  - LiveData KTX
  - Runtime KTX
  - ViewModel Compose
- **AppCompat:** 1.6.1
- **Core Splashscreen:** 1.0.1

## External Libraries
- **davidepanidev Extensions:**
  - android-extensions: 2.2.1
  - kotlin-extensions: 2.1
- **Timber:** 5.0.1 (Logging)

## Testing Dependencies

### Unit Testing
- **JUnit:** 4.13.2
- **MockK:** 1.13.11
- **Strikt:** 0.34.1 (Assertions)
- **Turbine:** 0.12.0 (Flow testing)
- **Coroutines Test:** 1.6.0
- **Arch Core Testing:** 2.1.0

### Android Testing
- **AndroidX Test:** 1.2.1
- **Espresso:** 3.6.1
- **Compose UI Test:** Included in Compose

## Build Optimization
- **R8/ProGuard:** Enabled for release builds
  - Minification enabled
  - Resource shrinking enabled
  - Optimized ProGuard rules
- **Core Library Desugaring:** 1.1.5 (Java 8+ API support on older devices)

## Module Structure

### App Module
- Application entry point
- Dependency injection setup
- App-level configurations

### Presentation Module
- ViewModels
- Compose UI screens
- UI state management
- Mappers (Domain to UI models)
- Custom composables
- Theme and styling

### Domain Module
- Use cases
- Repository interfaces
- Domain models
- Business logic
- Pure Kotlin module (no Android dependencies)

### Data Module
- Repository implementations
- Remote data sources (API)
- Local data sources (Database, DataStore)
- Data mappers (DTO to Domain models)
- Network configuration