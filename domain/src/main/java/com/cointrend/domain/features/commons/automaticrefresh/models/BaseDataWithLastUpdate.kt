package com.cointrend.domain.features.commons.automaticrefresh.models

import java.time.LocalDateTime

abstract class BaseDataWithLastUpdateDate(
    open val lastUpdate: LocalDateTime
)