package com.example.data.repository

import com.example.data.db.PresetDao
import com.example.data.db.PresetEntity
import com.example.data.model.AudioPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PresetRepository(private val presetDao: PresetDao) {

    val customPresets: Flow<List<AudioPreset>> = presetDao.getAllPresets().map { list ->
        list.map { it.toAudioPreset() }
    }

    suspend fun savePreset(preset: AudioPreset): Long {
        val entity = PresetEntity.fromAudioPreset(preset)
        return presetDao.insertPreset(entity)
    }

    suspend fun updatePreset(preset: AudioPreset) {
        val entity = PresetEntity.fromAudioPreset(preset)
        presetDao.updatePreset(entity)
    }

    suspend fun deletePreset(id: Long) {
        if (id > 0) {
            presetDao.deletePresetById(id)
        }
    }
}
