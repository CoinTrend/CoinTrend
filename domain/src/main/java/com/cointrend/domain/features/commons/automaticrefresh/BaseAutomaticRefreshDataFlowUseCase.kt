package com.cointrend.domain.features.commons.automaticrefresh

import com.cointrend.domain.exceptions.EmptyDatabaseException
import com.cointrend.domain.features.commons.automaticrefresh.models.BaseDataWithLastUpdateDate
import com.github.davidepanidev.kotlinextensions.minutesBetween
import fr.haan.resultat.Resultat
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime

/**
 * This UseCase provides the single source of truth of features' data.
 * When the flow starts to be collected it also automatically handles the refresh of data
 * if the minutes elapsed from the last update date of stored data is greater than the value defined by [minutesRequiredToRefreshData].
 * In case of refresh the flow emits a Resultat.Loading value so that the consumer can observe its state.
 * However, in case of failures the flow doesn't emit a Failure Result but throws directly an exception
 * causing its interruption. This forces the consumer to catch the exceptions and decide if collecting
 * again the flow or not; this prevents a stuck flow situation.
 */
abstract class BaseAutomaticRefreshDataFlowUseCase<OutputData : BaseDataWithLastUpdateDate, InputParams, RefreshParams>(
    private val refreshDataUseCase: BaseRefreshDataUseCase<OutputData, InputParams, RefreshParams>,
    private val repository: BaseAutomaticRefreshDataFlowRepository<OutputData, InputParams, RefreshParams>,
    private val minutesRequiredToRefreshData: Long
) {

    operator fun invoke(inputParams: InputParams): Flow<Resultat<OutputData>> {
        // The entire flow collection is surrounded by a try catch block so that every exception
        // will always be rethrown inside the flow
        return try {
            getDataFlow(inputParams).catch {
                // In case of empty database it triggers the refresh of the coins list
                // and then it emits again the flow once the database is filled
                if (it is EmptyDatabaseException) {
                    emitAll(refreshData(inputParams))
                    emitAll(getDataFlow(inputParams))
                } else {
                    throw it
                }
            }
        } catch (e: Exception) {
            flow {
                throw e
            }
        }

    }


    protected open fun getDataFlow(inputParams: InputParams): Flow<Resultat<OutputData>> {
        val flow = repository.getDataFlow(inputParams)

        return flow.map {
            Resultat.success(it)
        }.onStart {
            // When the flow starts to be collected checks the last update date of stored data
            // to decide if it has to be refreshed or not. Before doing that it collects
            // stored data and emits it so that it is possible to display it while new data is fetched.
            val storedData = flow.first()
            emit(Resultat.success(storedData))

            if (shouldRefresh(lastUpdateDate = storedData.lastUpdate)) {
                emitAll(refreshData(inputParams))
            }
        }.distinctUntilChanged()
    }

    private fun shouldRefresh(lastUpdateDate: LocalDateTime): Boolean {
        val minutesFromLastUpdate = lastUpdateDate.minutesBetween(LocalDateTime.now())
        return minutesFromLastUpdate > minutesRequiredToRefreshData
    }

    protected fun refreshData(inputParams: InputParams): Flow<Resultat<OutputData>> {
        return flow {
            emit(Resultat.loading())
            refreshDataUseCase(inputParams).onFailure {
                throw it
            }
        }
    }


}