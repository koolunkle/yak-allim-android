package com.example.yakallim.data.datasource.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val activeAlarmMedicinesKey = stringSetPreferencesKey("active_alarm_medicines")

    private fun alarmCountKey(medicineName: String) =
        intPreferencesKey("alarm_count_${medicineName}")

    suspend fun saveAlarmCount(medicineName: String, count: Int) {
        dataStore.edit { prefs ->
            prefs[alarmCountKey(medicineName)] = count
        }
    }

    suspend fun getAlarmCount(medicineName: String): Int {
        return dataStore.data.map { prefs ->
            prefs[alarmCountKey(medicineName)] ?: 0
        }.first()
    }

    suspend fun removeAlarmCount(medicineName: String) {
        dataStore.edit { prefs ->
            prefs.remove(alarmCountKey(medicineName))
        }
    }

    suspend fun addActiveAlarmMedicine(medicineName: String) {
        dataStore.edit { prefs ->
            val current = prefs[activeAlarmMedicinesKey] ?: emptySet()
            prefs[activeAlarmMedicinesKey] = current + medicineName
        }
    }

    suspend fun removeActiveAlarmMedicine(medicineName: String) {
        dataStore.edit { prefs ->
            val current = prefs[activeAlarmMedicinesKey] ?: emptySet()
            prefs[activeAlarmMedicinesKey] = current - medicineName
        }
    }

    suspend fun getActiveAlarmMedicines(): Set<String> {
        return dataStore.data.map { prefs ->
            prefs[activeAlarmMedicinesKey] ?: emptySet()
        }.first()
    }
}
