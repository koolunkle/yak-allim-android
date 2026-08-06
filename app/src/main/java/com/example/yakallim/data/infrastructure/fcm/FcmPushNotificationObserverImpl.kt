package com.example.yakallim.data.infrastructure.fcm

import com.example.yakallim.domain.notification.PushNotificationObserver
import com.example.yakallim.domain.notification.PushMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmPushNotificationObserverImpl @Inject constructor() : PushNotificationObserver {

    private val _messages = MutableSharedFlow<PushMessage>(replay = 1, extraBufferCapacity = 64)
    override val messages: Flow<PushMessage> = _messages

    override suspend fun emitMessage(message: PushMessage) {
        _messages.emit(message)
    }
}
