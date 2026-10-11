package com.cointrend.presentation

/** Best practice Maybe for future, pass with MVVM archi UiState & Event
 *
private val _mainEventChannel = Channel<MainEvent>(Channel.BUFFERED)
val mainEventFlow = _mainEventChannel.receiveAsFlow()

 for screen redirection launch event NavigateTo (Screen)
 **/
/*
sealed interface MainEvent {
    // Market
    data object NavigateToCoinsList : MainEvent
    // Favorite
    data object NavigateToFavouriteCoinsList : MainEvent
}
*/