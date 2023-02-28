<div align="center">

<p><img src="https://github.com/CoinTrend/CoinTrend/blob/develop/metadata/en-US/images/icon.png" width="200"></p>
 
# CoinTrend

### Lightweight Open-Source Crypto Monitor

[![Android](https://img.shields.io/badge/Android-grey?logo=android&style=flat)](https://www.android.com/)
[![AndroidAPI](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![JetpackCompose](https://img.shields.io/badge/Jetpack%20Compose-1.3.0-yellow)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/github/license/CoinTrend/CoinTrend?color=orange)](./LICENSE)
[![Release](https://badgen.net/github/release/CoinTrend/CoinTrend?color=red)](https://github.com/CoinTrend/CoinTrend/releases)


<p align="center"> 
  <a href='https://play.google.com/store/apps/details?id=com.cointrend&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
    <img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height=75/>
  </a>

  <a href='https://f-droid.org/packages/com.cointrend/'>
    <img alt='Get it on F-Droid' src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="75"/>
  </a>
</p>
 

</div>
 
 ----
 
Lightweight, fast and open-source cryptocurrencies market monitor for Android 📱📈 No Ads, no trackers, just coins 💎

## Features

- **Market**: real-time updates of the Trending Coins (the most searched coins in the last 24h) and the Top Coins (the top 250 cryptocurrencies by market cap).
- **Favourites**: track your favourite coins at a glance by adding them to the favourite list and sorting them as you wish.
- **Search**: search over 10.000 coins by name or symbol.

<br>

<p float="left">
  <img src="https://github.com/CoinTrend/CoinTrend/blob/develop/metadata/en-US/images/phoneScreenshots/1.jpg" width="23%" />
  <img src="https://github.com/CoinTrend/CoinTrend/blob/develop/metadata/en-US/images/phoneScreenshots/2.jpg" width="23%" />
  <img src="https://github.com/CoinTrend/CoinTrend/blob/develop/metadata/en-US/images/phoneScreenshots/3.jpg" width="23%" />
  <img src="https://github.com/CoinTrend/CoinTrend/blob/develop/metadata/en-US/images/phoneScreenshots/4.jpg" width="23%" />
</p>


### Lightweight
CoinTrend stores coins' data locally and updates them automatically only when needed, thus minimising network data consumption and battery consumption. You can still manually trigger a data update whenever you need it by simply swiping down on each screen!

### 100% free and open-source
Don't trust, verify! CoinTrend is 100% free and does not include any ads nor trackers. Check the code out yourself and don't hesitate to reach out if you have any suggestions!

### Designed for Android
The User Interface has been designed by following the latest Google's Material Design guidelines and by using only native Android components and animations.


## Technical Details

- **100% Jetpack Compose** 🚀

- **Material Design 3** 💎

- **Multimodule Clean Architecture** 🏛 as [davidepanidev](https://github.com/davidepanidev)'s [Clean Architecture Compose Concept](https://github.com/davidepanidev/android-multimodule-architecture-concepts/tree/clean-architecture-compose-concept) which consists of 4 separate modules:
  -  _app_: Android module that contains the Android Application component and all the framework specific configurations. It has visibility over all the other modules and defines the global dependency injection configurations.
  -  _presentation_: Android module **MVVM**-based. It contains the Android UI framework components (Activities, Composables, ViewModels...) and the related resources (e.g. images, strings...). This module just observes data coming from the undelying modules through Kotlin Flows and displays it. 
  -  _domain_: Kotlin module that contains Use Cases (platform-independent business logic), the Entities (platform-independent business models) and the Repository interfaces. It contains the `BaseAutomaticRefreshDataFlowUseCase` which handles the logic to refresh the persisted data when it becomes outdated.
  -  _data_: Android module that acts as the **Single-Source-Of-Truth (SSOT)** of the App. It contains Repositories implementation, the Room Entities for persistence, the data source Api implementations and the corresponding api-specific models.
  
- **Unit Testing** ⚙️ of the domain logic using MockK, Strickt and Turbine to test Kotlin Flows.


## Powered By

- [CoinGecko API](https://www.coingecko.com/api/)


## Credits


### Contributors

- [ZineeEddine](https://github.com/ZineeEddine) & [beastboym](https://github.com/beastboym): thanks for working on the AboutScreen.


### Libraries

- [Philipp Lackner](https://github.com/philipplackner): he inspired the LineChart with his [StockChart](https://github.com/philipplackner/StockMarketApp/blob/final/app/src/main/java/com/plcoding/stockmarketapp/presentation/company_info/StockChart.kt). Check out his YouTube channel for great videos about Android Development!

- [Zach Klippenstein](https://github.com/zach-klippenstein): he inspired the SegmentedControl with his [Composable](https://gist.github.com/zach-klippenstein/7ae8874db304f957d6bb91263e292117).

- [nicolashaan](https://github.com/nicolashaan): for the [Resultat](https://github.com/nicolashaan/resultat) library.

- [olshevski](https://github.com/olshevski): for the [Compose Navigation Reimagined](https://github.com/olshevski/compose-navigation-reimagined) library.

- [mxalbert1996](https://github.com/mxalbert1996): for the [Compose Shared Element](https://github.com/mxalbert1996/compose-shared-elements) library.

- [aclassen](https://github.com/aclassen): for the [ComposeReorderable](https://github.com/aclassen/ComposeReorderable) library.


## Support

CoinTrend as a FOSS project does not generate any revenue. If you wish to support the developers you can donate some sats at the Bitcoin address below:

### Bitcoin

```
bc1qszr4jv77n737569vhsdwgq3zc2x47n39mlq82f
```  

<p><img src="https://raw.githubusercontent.com/CoinTrend/.github/main/support/bitcoin.png" width="200"></p>

### Monero

```
47h4uVziTTNgWVmorhoJ5YAvC3pbjnLUb6t9v9BcRyWY4koyuFf8dQE41caPSVy5pcfh5WvMAwzSZ27omunS2AxHDhJbeeJ
```

<p><img src="https://raw.githubusercontent.com/CoinTrend/.github/main/support/monero.png" width="200"></p>


## License

CoinTrend is released under the [GPL-3.0 License](./LICENSE). Unless explicitly 
stated otherwise all files in this repository are so licensed.

All projects must properly attribute [the original source](https://github.com/CoinTrend/CoinTrend)
and must include an unmodified copy of the license text below in all forks.

```
CoinTrend: lightweight, fast and open-source cryptocurrencies monitor
Copyright (C) 2022  CoinTrend

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```   
