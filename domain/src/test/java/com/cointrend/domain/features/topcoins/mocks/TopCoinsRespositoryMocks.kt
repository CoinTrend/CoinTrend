package com.cointrend.domain.features.topcoins.mocks

import com.cointrend.domain.exceptions.EmptyDatabaseException
import com.cointrend.domain.features.topcoins.TopCoinsRepository
import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.features.topcoins.models.TopCoinsRefreshParams
import com.cointrend.domain.mocks.expectedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

internal abstract class BaseTopCoinsRepositoryTest : TopCoinsRepository {
    var numGetTopCoinsFlowCallsReceived = 0
        private set

    override fun getDataFlow(inputParams: Unit): Flow<TopCoinsData> {
        numGetTopCoinsFlowCallsReceived++
        return getMockedFlow()
    }

    open suspend fun emit(topCoinData: TopCoinsData) {

    }

    abstract fun getMockedFlow(): Flow<TopCoinsData>

    override suspend fun refreshData(params: TopCoinsRefreshParams): Result<Unit> {
        return Result.success(Unit)
    }


}

internal class TopCoinsRepositoryWithFailure : BaseTopCoinsRepositoryTest() {
    override fun getMockedFlow(): Flow<TopCoinsData> {
        throw expectedException
    }
}

internal class TopCoinsRepositoryWithInitialEmptyDatabase : BaseTopCoinsRepositoryTest() {
    override fun getMockedFlow(): Flow<TopCoinsData> {
        return flow {
            throw EmptyDatabaseException()
        }
    }
}

internal class TopCoinsRepositoryWithInitialEmptyDatabaseAndThenFailure :
    BaseTopCoinsRepositoryTest() {
    override fun getMockedFlow(): Flow<TopCoinsData> {
        return if (numGetTopCoinsFlowCallsReceived == 1) {
            flow { throw EmptyDatabaseException() }
        } else {
            flow { throw expectedException }
        }
    }
}

internal class TopCoinsRepositoryWithInitialEmptyDatabaseAndThenSuccessData :
    BaseTopCoinsRepositoryTest() {
    override fun getMockedFlow(): Flow<TopCoinsData> {
        return if (numGetTopCoinsFlowCallsReceived == 1) {
            flow { throw EmptyDatabaseException() }
        } else {
            flow {
                emit(expectedTopCoinDataThatDontRequireRefresh)
            }
        }
    }
}

internal class TopCoinsRepositoryWithSuccessRefreshedData : BaseTopCoinsRepositoryTest() {
    override fun getMockedFlow(): Flow<TopCoinsData> {
        return flow {
            emit(expectedTopCoinDataThatDontRequireRefresh)
        }
    }
}

internal class TopCoinsRepositoryWithManualEmitOfData : BaseTopCoinsRepositoryTest() {
    private val flow = MutableSharedFlow<TopCoinsData>()

    override fun getMockedFlow(): Flow<TopCoinsData> {
        return flow
    }

    override suspend fun emit(topCoinData: TopCoinsData) {
        flow.emit(topCoinData)
    }
}