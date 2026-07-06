package com.example.yakallim.data.datasource.local

import com.example.yakallim.data.datasource.local.preference.FirebaseMessagingTokenPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessagingLocalDataSourceImpl @Inject constructor(
    private val firebaseMessagingTokenPreference: FirebaseMessagingTokenPreference
) : FirebaseMessagingLocalDataSource {

    override suspend fun getFcmToken(): String? {
        return firebaseMessagingTokenPreference.getFcmToken()
    }

    override suspend fun saveFcmToken(token: String) {
        firebaseMessagingTokenPreference.saveFcmToken(token)
    }
}