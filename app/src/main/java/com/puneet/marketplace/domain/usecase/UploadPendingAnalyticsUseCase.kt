package com.puneet.marketplace.domain.usecase

import com.puneet.marketplace.domain.repository.ProductActionLogRepository
import javax.inject.Inject

class UploadPendingAnalyticsUseCase @Inject constructor(
    private val repository: ProductActionLogRepository,
) {

    suspend operator fun invoke(): Result<Int> = repository.uploadPendingLogs()
}
