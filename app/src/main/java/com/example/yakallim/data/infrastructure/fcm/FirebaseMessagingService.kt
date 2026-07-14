package com.example.yakallim.data.infrastructure.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.yakallim.MainActivity
import com.example.yakallim.R
import com.example.yakallim.data.datasource.local.FirebaseMessagingLocalDataSource
import com.example.yakallim.data.datasource.local.OcrLocalDataSource
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingObserver
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingTokenProvider
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessage
import com.example.yakallim.domain.model.JobStatus
import com.example.yakallim.util.notificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMessagingService"
        private const val CHANNEL_ID = "OCR_FCM_CHANNEL"
    }

    @Inject
    lateinit var firebaseMessagingObserver: FirebaseMessagingObserver

    @Inject
    lateinit var firebaseMessagingTokenProvider: FirebaseMessagingTokenProvider

    @Inject
    lateinit var firebaseMessagingLocalDataSource: FirebaseMessagingLocalDataSource

    @Inject
    lateinit var ocrLocalDataSource: OcrLocalDataSource

    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)

    override fun onRegistered(token: String) {
        super.onRegistered(token)
        coroutineScope.launch {
            firebaseMessagingLocalDataSource.saveFcmToken(token)
            firebaseMessagingTokenProvider.emitToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val jobId = message.data[FcmPayloadSpec.KEY_JOB_ID]
        val status = message.data[FcmPayloadSpec.KEY_STATUS]

        if (jobId == null) {
            Log.w(TAG, "FCM 수신 에러: 작업 ID 누락")
            return
        }

        coroutineScope.launch {
            if (ocrLocalDataSource.isAnalysisCancelled(jobId)) {
                Log.i(TAG, "FCM 수신 제외: 이미 취소된 작업 (작업 ID: $jobId)")
                ocrLocalDataSource.removeAnalysisCancelled(jobId)
                return@launch
            }

            val isSuccess = status?.equals(FcmPayloadSpec.STATUS_COMPLETED, ignoreCase = true) == true
            val errorMessage = message.data[FcmPayloadSpec.KEY_ERROR]

            firebaseMessagingObserver.emitMessage(
                FirebaseMessage(
                    jobId = jobId,
                    status = if (isSuccess) JobStatus.COMPLETED else JobStatus.FAILED,
                    errorMessage = errorMessage
                )
            )

            val title = message.notification?.title
                ?: if (isSuccess) getString(R.string.notification_fcm_completed_title) else getString(R.string.notification_fcm_failed_title)

            val body = message.notification?.body
                ?: if (isSuccess) getString(R.string.notification_fcm_completed_body)
                else errorMessage ?: getString(R.string.notification_fcm_failed_body)

            showNotification(title, body, jobId)
        }
    }

    private fun showNotification(title: String, body: String, jobId: String?) {
        val notificationManager = notificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_fcm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_fcm_channel_name)
                enableLights(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            setPackage(packageName)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(FcmPayloadSpec.KEY_JOB_ID, jobId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            jobId?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(jobId?.hashCode() ?: 0, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        supervisorJob.cancel()
    }
}
