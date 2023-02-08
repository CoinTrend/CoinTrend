package com.cointrend.domain.features.favouritecoins

import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowRepository
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsRefreshParams
import com.cointrend.domain.models.Coin
import kotlinx.coroutines.flow.Flow

interface FavouriteCoinsRepository :
    BaseAutomaticRefreshDataFlowRepository<FavouriteCoinsData, Unit, FavouriteCoinsRefreshParams> {

    override fun getDataFlow(inputParams: Unit): Flow<FavouriteCoinsData>
    override suspend fun refreshData(params: FavouriteCoinsRefreshParams): Result<Unit>
    suspend fun addFavouriteCoin(coin: Coin): Result<Unit>
    suspend fun removeFavouriteCoin(coinId: String): Result<Unit>
    suspend fun getFavouriteCoinsIds(): Result<List<String>>
    suspend fun reorderFavouriteCoin(coinId: String, toIndex: Int): Result<Unit>

}