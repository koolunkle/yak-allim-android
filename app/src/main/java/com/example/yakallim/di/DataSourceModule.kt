package com.example.yakallim.di

import com.example.yakallim.data.datasource.local.FirebaseMessagingLocalDataSource
import com.example.yakallim.data.datasource.local.FirebaseMessagingLocalDataSourceImpl
import com.example.yakallim.data.datasource.local.OcrLocalDataSource
import com.example.yakallim.data.datasource.local.OcrLocalDataSourceImpl
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
    abstract fun bindFirebaseMessagingLocalDataSource(
        firebaseMessagingLocalDataSourceImpl: FirebaseMessagingLocalDataSourceImpl
    ): FirebaseMessagingLocalDataSource

    @Binds
    @Singleton
    abstract fun bindOcrLocalDataSource(
        ocrLocalDataSourceImpl: OcrLocalDataSourceImpl
    ): OcrLocalDataSource
}
