package com.puneet.marketplace.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.puneet.marketplace.domain.usecase.UploadPendingAnalyticsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AnalyticsUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val uploadPendingAnalyticsUseCase: UploadPendingAnalyticsUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        uploadPendingAnalyticsUseCase()
            .fold(
                onSuccess = { Result.success() },
                onFailure = { Result.retry() },
            )

    companion object {
        const val WORK_NAME = "analytics_upload"
    }
}
