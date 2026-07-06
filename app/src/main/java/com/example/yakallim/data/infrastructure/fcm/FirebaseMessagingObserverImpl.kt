package com.example.yakallim.data.infrastructure.fcm

import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingObserver
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessagingObserverImpl @Inject constructor() : FirebaseMessagingObserver {

    private val _messages = MutableSharedFlow<FirebaseMessage>(replay = 1, extraBufferCapacity = 64)
    override val messages: Flow<FirebaseMessage> = _messages

    override suspend fun emitMessage(message: FirebaseMessage) {
        _messages.emit(message)
    }
}
