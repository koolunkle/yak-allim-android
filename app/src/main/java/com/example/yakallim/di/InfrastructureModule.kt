package com.example.yakallim.di

import android.content.Context
import com.example.yakallim.data.datasource.local.preference.AlarmPreference
import com.example.yakallim.data.infrastructure.alarm.AlarmSchedulerImpl
import com.example.yakallim.data.infrastructure.fcm.FirebaseMessagingObserverImpl
import com.example.yakallim.data.infrastructure.fcm.FirebaseMessagingTokenProviderImpl
import com.example.yakallim.data.infrastructure.image.ImageProcessorImpl
import com.example.yakallim.domain.infrastructure.alarm.AlarmScheduler
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingObserver
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingTokenProvider
import com.example.yakallim.domain.infrastructure.image.ImageProcessor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    companion object {
        @Provides
        @Singleton
        fun provideAlarmScheduler(
            @ApplicationContext context: Context,
            alarmPreference: AlarmPreference
        ): AlarmScheduler {
            return AlarmSchedulerImpl(context, alarmPreference)
        }

        @Provides
        @Singleton
        fun provideImageProcessor(@ApplicationContext context: Context): ImageProcessor {
            return ImageProcessorImpl(context)
        }
    }
}
