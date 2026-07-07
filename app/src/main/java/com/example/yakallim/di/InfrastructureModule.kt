package com.example.yakallim.di

import com.example.yakallim.data.infrastructure.alarm.AlarmSchedulerImpl
import com.example.yakallim.data.infrastructure.alarm.AlarmDispatcherImpl
import com.example.yakallim.data.infrastructure.fcm.FirebaseMessagingObserverImpl
import com.example.yakallim.data.infrastructure.fcm.FirebaseMessagingTokenProviderImpl
import com.example.yakallim.data.infrastructure.image.ImageProcessorImpl
import com.example.yakallim.domain.infrastructure.alarm.AlarmScheduler
import com.example.yakallim.domain.infrastructure.alarm.AlarmDispatcher
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingObserver
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingTokenProvider
import com.example.yakallim.domain.infrastructure.image.ImageProcessor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InfrastructureModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseMessageObserver(firebaseMessagingObserverImpl: FirebaseMessagingObserverImpl): FirebaseMessagingObserver

    @Binds
    @Singleton
    abstract fun bindFirebaseMessagingTokenProvider(firebaseMessagingTokenProviderImpl: FirebaseMessagingTokenProviderImpl): FirebaseMessagingTokenProvider

    @Binds
    @Singleton
    abstract fun bindAlarmDispatcher(
        alarmDispatcherImpl: AlarmDispatcherImpl
    ): AlarmDispatcher

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        alarmSchedulerImpl: AlarmSchedulerImpl
    ): AlarmScheduler

    @Binds
    @Singleton
    abstract fun bindImageProcessor(
        imageProcessorImpl: ImageProcessorImpl
    ): ImageProcessor
}
