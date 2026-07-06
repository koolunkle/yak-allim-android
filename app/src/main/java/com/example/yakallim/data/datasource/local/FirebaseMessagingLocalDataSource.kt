package com.example.yakallim.data.datasource.local

interface FirebaseMessagingLocalDataSource {
    suspend fun getFcmToken(): String?
    suspend fun saveFcmToken(token: String)
}