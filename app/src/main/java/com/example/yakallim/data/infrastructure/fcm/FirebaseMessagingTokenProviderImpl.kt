package com.example.yakallim.data.infrastructure.fcm

import android.util.Log
import com.example.yakallim.data.datasource.local.FirebaseMessagingLocalDataSource
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingTokenProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessagingTokenProviderImpl @Inject constructor(
    private val firebaseMessagingLocalDataSource: FirebaseMessagingLocalDataSource
) : FirebaseMessagingTokenProvider {

    private val _fcmToken = MutableSharedFlow<String>(extraBufferCapacity = 1)
    override val fcmToken: Flow<String> = _fcmToken

    override suspend fun getFcmToken(): String? {
        val cachedToken = firebaseMessagingLocalDataSource.getFcmToken()
        if (!cachedToken.isNullOrBlank()) {
            return cachedToken
        }

        registerFcmToken()

        return withTimeoutOrNull(5000L) { fcmToken.first() } ?: run {
            Log.w("FirebaseMessagingTokenProviderImpl", "FCM 토큰 등록 시간 초과")
            null
        }
    }

    override suspend fun emitToken(token: String) {
        _fcmToken.emit(token)
    }

    override fun registerFcmToken() {
        try {
            FirebaseMessaging.getInstance().register()
        } catch (e: Exception) {
            Log.e("FirebaseMessagingTokenProviderImpl", "FCM 토큰 등록 요청 실패", e)
        }
    }
}
