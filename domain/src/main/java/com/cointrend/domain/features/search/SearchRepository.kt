package com.cointrend.domain.features.search

import com.cointrend.domain.models.Coin

interface SearchRepository {

    suspend fun search(query: String): Result<List<Coin>>

}