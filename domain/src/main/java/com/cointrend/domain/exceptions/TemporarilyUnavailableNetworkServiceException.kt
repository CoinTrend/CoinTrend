package com.cointrend.domain.exceptions

import java.io.IOException

/**
 * Exception to be used when the server responds with
 * a not available service error code, i.e. HTTP 429.
 */
class TemporarilyUnavailableNetworkServiceException(val serviceName: String) : IOException()