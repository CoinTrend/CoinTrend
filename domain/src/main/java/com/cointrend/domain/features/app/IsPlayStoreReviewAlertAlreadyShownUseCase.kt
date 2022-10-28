package com.cointrend.domain.features.app

import javax.inject.Inject

class IsPlayStoreReviewAlertAlreadyShownUseCase @Inject constructor(
    private val repository: AppRepository
) {

    suspend operator fun invoke(): Boolean {
        return repository.isPlayStoreReviewAlertAlreadyShown()
    }

}