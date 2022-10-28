package com.cointrend.domain.features.search

import com.cointrend.domain.models.Coin
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(query: String): Result<List<Coin>> {
        return searchRepository.search(query = query)
    }

}