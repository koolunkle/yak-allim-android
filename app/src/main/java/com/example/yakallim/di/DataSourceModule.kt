package com.example.yakallim.di

import com.example.yakallim.data.datasource.local.FcmLocalDataSource
import com.example.yakallim.data.datasource.local.FcmLocalDataSourceImpl
import com.example.yakallim.data.datasource.local.OcrLocalDataSource
import com.example.yakallim.data.datasource.local.OcrLocalDataSourceImpl
import com.example.yakallim.data.datasource.remote.OcrKtorRemoteDataSourceImpl
import com.example.yakallim.data.datasource.remote.OcrRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFcmLocalDataSource(
        fcmLocalDataSourceImpl: FcmLocalDataSourceImpl
    ): FcmLocalDataSource

    @Binds
    @Singleton
    abstract fun bindOcrLocalDataSource(
        ocrLocalDataSourceImpl: OcrLocalDataSourceImpl
    ): OcrLocalDataSource

    @Binds
    @Singleton
    abstract fun bindOcrRemoteDataSource(
        ocrKtorRemoteDataSourceImpl: OcrKtorRemoteDataSourceImpl
    ): OcrRemoteDataSource
}
