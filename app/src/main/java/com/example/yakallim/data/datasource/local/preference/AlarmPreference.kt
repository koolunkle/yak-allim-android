package com.example.yakallim.data.datasource.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.yakallim.domain.model.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val activeAlarmMedicinesKey = stringSetPreferencesKey("active_alarm_medicines")

    private fun alarmCountKey(medicineName: String) =
        intPreferencesKey("alarm_count_${medicineName}")

    private fun alarmDetailKey(medicineName: String) =
        stringPreferencesKey("alarm_detail_${medicineName}")

    suspend fun saveAlarmCount(medicineName: String, count: Int) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs[alarmCountKey(medicineName)] = count
        }
    }

    suspend fun getAlarmCount(medicineName: String): Int = withContext(Dispatchers.IO) {
        dataStore.data.map { prefs ->
            prefs[alarmCountKey(medicineName)] ?: 0
        }.first()
    }

    suspend fun removeAlarmCount(medicineName: String) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs.remove(alarmCountKey(medicineName))
        }
    }

    suspend fun addActiveAlarmMedicine(medicineName: String) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            val current = prefs[activeAlarmMedicinesKey] ?: emptySet()
            prefs[activeAlarmMedicinesKey] = current + medicineName
        }
    }

    suspend fun removeActiveAlarmMedicine(medicineName: String) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            val current = prefs[activeAlarmMedicinesKey] ?: emptySet()
            prefs[activeAlarmMedicinesKey] = current - medicineName
        }
    }

    suspend fun getActiveAlarmMedicines(): Set<String> = withContext(Dispatchers.IO) {
        dataStore.data.map { prefs ->
            prefs[activeAlarmMedicinesKey] ?: emptySet()
        }.first()
    }

    suspend fun saveDetailAlarm(medicineName: String, times: List<String>, soundUri: String?) = withContext(Dispatchers.IO) {
        val timesStr = times.joinToString(",")
        val uriStr = soundUri ?: ""
        val serialized = "$timesStr|$uriStr"
        dataStore.edit { prefs ->
            prefs[alarmDetailKey(medicineName)] = serialized
        }
    }

    suspend fun getDetailAlarm(medicineName: String): Alarm? = withContext(Dispatchers.IO) {
        val serialized = dataStore.data.map { prefs ->
            prefs[alarmDetailKey(medicineName)]
        }.first() ?: return@withContext null

        val parts = serialized.split("|")
        val timesStr = parts.getOrNull(0) ?: ""
        val times = timesStr.split(",").filter { it.isNotBlank() }
        
        val soundUriRaw = parts.getOrNull(1)
        val soundUri = if (soundUriRaw.isNullOrBlank()) null else soundUriRaw

        Alarm(times, soundUri)
    }

    suspend fun removeDetailAlarm(medicineName: String) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs.remove(alarmDetailKey(medicineName))
        }
    }
}
