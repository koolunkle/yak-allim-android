package com.example.yakallim.domain.infrastructure.fcm

import kotlinx.coroutines.flow.Flow

interface FirebaseMessagingTokenProvider {
    val fcmToken: Flow<String>

    suspend fun getFcmToken(): String?
    suspend fun emitToken(token: String)
    fun registerFcmToken()
}
