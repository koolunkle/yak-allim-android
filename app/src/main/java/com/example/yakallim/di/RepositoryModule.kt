package com.example.yakallim.di

import android.content.Context
import com.example.yakallim.data.datasource.local.OcrLocalDataSource
import com.example.yakallim.data.datasource.remote.api.OcrApiService
import com.example.yakallim.data.repository.OcrRepositoryImpl
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingTokenProvider
import com.example.yakallim.domain.infrastructure.image.ImageProcessor
import com.example.yakallim.domain.repository.OcrRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

import com.example.yakallim.data.datasource.remote.OcrRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideOcrRepository(
        ocrRemoteDataSource: OcrRemoteDataSource,
        imageProcessor: ImageProcessor,
        firebaseMessagingTokenProvider: FirebaseMessagingTokenProvider,
        ocrLocalDataSource: OcrLocalDataSource,
        moshi: Moshi,
        @ApplicationContext context: Context
    ): OcrRepository {
        return OcrRepositoryImpl(
            ocrRemoteDataSource,
            imageProcessor,
            firebaseMessagingTokenProvider,
            ocrLocalDataSource,
            moshi,
            context
        )
    }
}
