package com.cointrend.domain.features.marketdata

import javax.inject.Inject

/**
 * This UseCase triggers the deletion of all the market data stored locally on the device.
 */
class DeleteAllMarketDataUseCase @Inject constructor(
    private val coinMarketDataRepository: CoinMarketDataRepository
) {

    internal suspend operator fun invoke(): Result<Unit> {
        return coinMarketDataRepository.deleteAllData()
    }

}