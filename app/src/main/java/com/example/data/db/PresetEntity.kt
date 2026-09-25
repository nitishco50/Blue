package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AudioPreset

@Entity(tableName = "custom_presets")
data class PresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val bandLevelsCsv: String,
    val bassBoost: Int,
    val virtualizer: Int,
    val loudnessGain: Int,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toAudioPreset(): AudioPreset {
        val bands = try {
            bandLevelsCsv.split(",").map { it.trim().toInt() }
        } catch (_: Exception) {
            listOf(0, 0, 0, 0, 0)
        }
        return AudioPreset(
            id = id,
            name = name,
            isCustom = true,
            bandLevels = if (bands.size == 5) bands else listOf(0, 0, 0, 0, 0),
            bassBoost = bassBoost,
            virtualizer = virtualizer,
            loudnessGain = loudnessGain
        )
    }

    companion object {
        fun fromAudioPreset(preset: AudioPreset): PresetEntity {
            return PresetEntity(
                id = if (preset.id > 0) preset.id else 0,
                name = preset.name,
                bandLevelsCsv = preset.bandLevels.joinToString(","),
                bassBoost = preset.bassBoost,
                virtualizer = preset.virtualizer,
                loudnessGain = preset.loudnessGain
            )
        }
    }
}
