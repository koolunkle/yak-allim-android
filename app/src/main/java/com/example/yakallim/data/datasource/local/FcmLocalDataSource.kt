package com.example.yakallim.data.datasource.local

interface FcmLocalDataSource {
    suspend fun getFcmToken(): String?
    suspend fun saveFcmToken(token: String)
}
