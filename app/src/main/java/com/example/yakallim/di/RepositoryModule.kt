package com.example.yakallim.di

import android.content.Context
import com.example.yakallim.data.datasource.local.OcrLocalDataSource
import com.example.yakallim.data.datasource.remote.OcrRemoteDataSource
import com.example.yakallim.data.infrastructure.image.ImageProcessor
import com.example.yakallim.data.repository.OcrRepositoryImpl
import com.example.yakallim.domain.notification.PushTokenProvider
import com.example.yakallim.domain.repository.OcrRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideOcrRepository(
        ocrRemoteDataSource: OcrRemoteDataSource,
        imageProcessor: ImageProcessor,
        pushTokenProvider: PushTokenProvider,
        ocrLocalDataSource: OcrLocalDataSource,
        moshi: Moshi,
        @ApplicationContext context: Context
    ): OcrRepository {
        return OcrRepositoryImpl(
            ocrRemoteDataSource,
            imageProcessor,
            pushTokenProvider,
            ocrLocalDataSource,
            moshi,
            context
        )
    }
}
