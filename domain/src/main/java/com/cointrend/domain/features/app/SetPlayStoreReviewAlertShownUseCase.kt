package com.cointrend.domain.features.app

import javax.inject.Inject

class SetPlayStoreReviewAlertShownUseCase @Inject constructor(
    private val repository: AppRepository
) {

    suspend operator fun invoke() {
        return repository.setPlayStoreReviewAlertShown()
    }

}