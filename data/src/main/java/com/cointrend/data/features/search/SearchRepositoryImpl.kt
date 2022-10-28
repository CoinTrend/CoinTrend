package com.cointrend.data.features.search

import com.cointrend.domain.features.search.SearchRepository
import com.cointrend.domain.models.Coin
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteSource: SearchRemoteDataSource
) : SearchRepository {

    override suspend fun search(query: String): Result<List<Coin>> {
        return Result.runCatching {
            remoteSource.search(query = query).getOrThrow()
        }
    }

}


interface SearchRemoteDataSource {

    suspend fun search(query: String): Result<List<Coin>>

}