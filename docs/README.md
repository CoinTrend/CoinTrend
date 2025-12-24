# CoinTrend Technical Documentation

Welcome to the CoinTrend technical documentation. This comprehensive guide will help you understand the architecture, implementation details, and best practices used in the CoinTrend cryptocurrency tracking application.

## 📚 Documentation Structure

### Core Documentation

1. **[Tech Stack](01-TechStack.md)**
   - Complete list of technologies, frameworks, and libraries
   - Version information and dependencies
   - Module structure overview

2. **[Architecture](02-Architecture.md)**
   - Clean Architecture implementation
   - MVVM pattern with MVI-style state management
   - Data flow and layer responsibilities
   - Dependency injection setup

3. **[API Integration](03-API-Integration.md)**
   - CoinGecko API endpoints and usage
   - Network configuration and rate limiting
   - Data mapping and caching strategies
   - Error handling approaches

4. **[UI Components](04-UI-Components.md)**
   - Jetpack Compose implementation
   - Design system and theming
   - Custom components and animations
   - Screen compositions and navigation

5. **[State Management](05-StateManagement.md)**
   - Unidirectional data flow
   - ViewModel state patterns
   - Flow and coroutines usage
   - Side effects handling

6. **[Data Layer](06-DataLayer.md)**
   - Repository pattern implementation
   - Room database configuration
   - DataStore for preferences
   - Local and remote data sources

7. **[Project Setup](07-ProjectSetup.md)**
   - Development environment requirements
   - Build configuration
   - Running and debugging the app
   - Common issues and solutions

8. **[Best Practices](08-BestPractices.md)**
   - Architecture best practices
   - Compose performance optimization
   - Testing strategies
   - Code organization guidelines

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│         Jetpack Compose UI + ViewModels + State         │
├─────────────────────────────────────────────────────────┤
│                      Domain Layer                        │
│          Use Cases + Repository Interfaces              │
├─────────────────────────────────────────────────────────┤
│                       Data Layer                         │
│      Repository Impl + API + Database + DataStore       │
└─────────────────────────────────────────────────────────┘
```

## 🚀 Key Features

### Technical Features
- **Clean Architecture** with clear separation of concerns
- **MVVM Pattern** with reactive state management
- **Jetpack Compose** for modern declarative UI
- **Kotlin Coroutines & Flow** for asynchronous operations
- **Dagger Hilt** for dependency injection
- **Room Database** for local data persistence
- **Retrofit** for network operations

### App Features
- Real-time cryptocurrency prices and market data
- Top coins by market capitalization
- Trending coins discovery
- Favorites/watchlist management
- Detailed coin information with charts
- Search functionality
- Pull-to-refresh updates
- Offline support with caching

## 🛠️ Technology Stack

### Core
- **Language:** Kotlin 1.9.25
- **Min SDK:** 23 (Android 6.0)
- **Target SDK:** 35 (Android 14+)
- **JVM Target:** Java 21

### UI Framework
- **Jetpack Compose:** 1.6.8
- **Material Design 3:** 1.1.2
- **Navigation:** Reimagined 1.5.0

### Architecture Components
- **Dagger Hilt:** 2.52
- **ViewModel & LiveData:** 2.7.0
- **Room:** 2.6.1
- **DataStore:** 1.0.0

### Networking
- **Retrofit:** 2.10.0
- **OkHttp:** Included with Retrofit
- **Gson:** 2.10.0

### Testing
- **JUnit:** 4.13.2
- **MockK:** 1.13.11
- **Turbine:** 0.12.0 (Flow testing)

## 📱 Module Structure

```
CoinTrend/
├── app/           # Application entry point
├── presentation/  # UI layer (Compose, ViewModels)
├── domain/        # Business logic (Use Cases)
└── data/          # Data sources (API, Database)
```

### Module Dependencies
- `app` → `presentation`, `domain`, `data`
- `presentation` → `domain`
- `data` → `domain`
- `domain` → No Android dependencies (pure Kotlin)

## 🔑 API Information

### CoinGecko API
- **Base URL:** `https://api.coingecko.com/api/v3/`
- **Authentication:** None required (public API)
- **Rate Limiting:** Handled automatically
- **Caching:** 10MB HTTP cache + Room database

### Main Endpoints
- `/coins/markets` - Top coins by market cap
- `/coins/{id}/market_chart` - Price charts
- `/search/trending` - Trending coins
- `/search` - Search functionality

## 🎨 Design System

### Theme
- **Primary:** Dark theme optimized for financial data
- **Colors:** High contrast for readability
- **Typography:** Material 3 type system

### Key UI Components
- Coin list items with market data
- Interactive price charts
- Pull-to-refresh lists
- Reorderable favorites
- Search with instant results

## 📊 State Management

### Pattern
- Single immutable state per screen
- Unidirectional data flow
- Flow-based reactive updates

### Implementation
```kotlin
@HiltViewModel
class ViewModel : ViewModel() {
    var state by mutableStateOf(ScreenState())
        private set
}
```

## 🧪 Testing Strategy

### Unit Tests
- ViewModels with mocked dependencies
- Use Cases with fake repositories
- Mappers with sample data

### UI Tests
- Compose testing with ComposeTestRule
- Screen navigation tests
- User interaction flows

## 🚦 Getting Started

1. **Clone the repository**
2. **Open in Android Studio**
3. **Sync Gradle files**
4. **Run the app**

For detailed setup instructions, see [Project Setup](07-ProjectSetup.md).

## 📝 Development Guidelines

### Adding New Features
1. Start with domain layer (use cases, models)
2. Implement data layer (repository, data sources)
3. Build presentation layer (ViewModel, UI)
4. Write tests for each layer

### Code Style
- Follow Kotlin official style guide
- Use meaningful names
- Keep functions small and focused
- Write self-documenting code

## 🔧 Troubleshooting

Common issues and solutions are documented in [Project Setup](07-ProjectSetup.md#common-issues--solutions).

## 📖 Learning Path

### For Beginners
1. Start with [Tech Stack](01-TechStack.md) to understand technologies
2. Study [Architecture](02-Architecture.md) for overall structure
3. Explore [UI Components](04-UI-Components.md) for Compose basics

### For Intermediate Developers
1. Deep dive into [State Management](05-StateManagement.md)
2. Understand [Data Layer](06-DataLayer.md) implementation
3. Learn [API Integration](03-API-Integration.md) patterns

### For Advanced Developers
1. Review [Best Practices](08-BestPractices.md)
2. Analyze architecture decisions
3. Contribute improvements

## 🤝 Contributing

When contributing to the project:
1. Follow the established architecture patterns
2. Write tests for new features
3. Update documentation as needed
4. Ensure code passes lint checks

## 📌 Important Notes

- **No API key required** for CoinGecko basic endpoints
- **Rate limiting** is handled automatically
- **Offline support** through local caching
- **Dark theme only** (optimized for financial data)

## 🎯 Project Goals

This project demonstrates:
- Modern Android development with Kotlin
- Clean Architecture principles
- Jetpack Compose UI
- Reactive programming with Coroutines and Flow
- Professional app structure and patterns

---

For questions or clarifications about the documentation, please refer to the specific documentation files or create an issue in the repository.