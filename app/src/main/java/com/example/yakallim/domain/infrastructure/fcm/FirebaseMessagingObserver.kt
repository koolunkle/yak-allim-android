package com.example.yakallim.domain.infrastructure.fcm

import kotlinx.coroutines.flow.Flow

interface FirebaseMessagingObserver {
    val messages: Flow<FirebaseMessage>

    suspend fun emitMessage(message: FirebaseMessage)
}
