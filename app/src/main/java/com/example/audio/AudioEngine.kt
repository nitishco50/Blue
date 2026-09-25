package com.example.audio

import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.util.Log
import com.example.data.model.AudioPreset

class AudioEngine(private val context: Context) {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    private var isInitialized = false

    fun initAudioEffects(audioSessionId: Int = 0) {
        try {
            release()
            // Session 0 corresponds to the global audio mix output
            equalizer = Equalizer(1000, audioSessionId).apply {
                enabled = true
            }

            try {
                bassBoost = BassBoost(1000, audioSessionId).apply {
                    enabled = true
                }
            } catch (e: Exception) {
                Log.w("AudioEngine", "BassBoost not supported: ${e.message}")
            }

            try {
                virtualizer = Virtualizer(1000, audioSessionId).apply {
                    enabled = true
                }
            } catch (e: Exception) {
                Log.w("AudioEngine", "Virtualizer not supported: ${e.message}")
            }

            try {
                loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                    enabled = true
                }
            } catch (e: Exception) {
                Log.w("AudioEngine", "LoudnessEnhancer not supported: ${e.message}")
            }

            isInitialized = true
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error initializing audio effects: ${e.message}")
            isInitialized = false
        }
    }

    fun applyPreset(preset: AudioPreset, isEqEnabled: Boolean = true) {
        if (!isInitialized) {
            initAudioEffects()
        }

        try {
            equalizer?.enabled = isEqEnabled
            if (isEqEnabled && equalizer != null) {
                val numBands = equalizer?.numberOfBands?.toInt() ?: 0
                val minLevel = equalizer?.bandLevelRange?.get(0) ?: -1500
                val maxLevel = equalizer?.bandLevelRange?.get(1) ?: 1500

                val bands = preset.bandLevels
                for (i in 0 until minOf(numBands, bands.size)) {
                    val targetLevel = bands[i].toShort().coerceIn(minLevel, maxLevel)
                    equalizer?.setBandLevel(i.toShort(), targetLevel)
                }
            }

            bassBoost?.let {
                it.enabled = isEqEnabled && preset.bassBoost > 0
                if (it.strengthSupported) {
                    it.setStrength(preset.bassBoost.toShort().coerceIn(0, 1000))
                }
            }

            virtualizer?.let {
                it.enabled = isEqEnabled && preset.virtualizer > 0
                if (it.strengthSupported) {
                    it.setStrength(preset.virtualizer.toShort().coerceIn(0, 1000))
                }
            }

            loudnessEnhancer?.let {
                it.enabled = isEqEnabled && preset.loudnessGain > 0
                it.setTargetGain(preset.loudnessGain)
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Failed to apply preset: ${e.message}")
        }
    }

    fun setBandLevel(bandIndex: Short, millibels: Short) {
        try {
            equalizer?.setBandLevel(bandIndex, millibels)
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error setting band level: ${e.message}")
        }
    }

    fun setBassBoost(strength: Int) {
        try {
            bassBoost?.let {
                it.enabled = strength > 0
                it.setStrength(strength.toShort().coerceIn(0, 1000))
            }
        } catch (_: Exception) {}
    }

    fun setVirtualizer(strength: Int) {
        try {
            virtualizer?.let {
                it.enabled = strength > 0
                it.setStrength(strength.toShort().coerceIn(0, 1000))
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
            loudnessEnhancer?.release()
        } catch (_: Exception) {}
        equalizer = null
        bassBoost = null
        virtualizer = null
        loudnessEnhancer = null
        isInitialized = false
    }
}
