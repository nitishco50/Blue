package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AncMode
import com.example.data.model.SpatialAudioMode

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("podeq_preferences", Context.MODE_PRIVATE)

    var ancMode: AncMode
        get() {
            val name = prefs.getString(KEY_ANC_MODE, AncMode.NOISE_CANCELLATION.name)
            return try {
                AncMode.valueOf(name ?: AncMode.NOISE_CANCELLATION.name)
            } catch (_: Exception) {
                AncMode.NOISE_CANCELLATION
            }
        }
        set(value) = prefs.edit().putString(KEY_ANC_MODE, value.name).apply()

    var spatialAudioMode: SpatialAudioMode
        get() {
            val name = prefs.getString(KEY_SPATIAL_MODE, SpatialAudioMode.FIXED.name)
            return try {
                SpatialAudioMode.valueOf(name ?: SpatialAudioMode.FIXED.name)
            } catch (_: Exception) {
                SpatialAudioMode.FIXED
            }
        }
        set(value) = prefs.edit().putString(KEY_SPATIAL_MODE, value.name).apply()

    var isBackgroundServiceEnabled: Boolean
        get() = prefs.getBoolean(KEY_BG_SERVICE, true)
        set(value) = prefs.edit().putBoolean(KEY_BG_SERVICE, value).apply()

    var isAutoConnectEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CONNECT, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_CONNECT, value).apply()

    var isBattery80LimitGuardEnabled: Boolean
        get() = prefs.getBoolean(KEY_BATTERY_GUARD, true)
        set(value) = prefs.edit().putBoolean(KEY_BATTERY_GUARD, value).apply()

    var isEarDetectionEnabled: Boolean
        get() = prefs.getBoolean(KEY_EAR_DETECTION, true)
        set(value) = prefs.edit().putBoolean(KEY_EAR_DETECTION, value).apply()

    var isGamingModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_GAMING_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_GAMING_MODE, value).apply()

    var isConversationalAwarenessEnabled: Boolean
        get() = prefs.getBoolean(KEY_CONVERSATIONAL, true)
        set(value) = prefs.edit().putBoolean(KEY_CONVERSATIONAL, value).apply()

    var isWindNoiseReductionEnabled: Boolean
        get() = prefs.getBoolean(KEY_WIND_NOISE, true)
        set(value) = prefs.edit().putBoolean(KEY_WIND_NOISE, value).apply()

    var stemPressAndHoldAction: String
        get() = prefs.getString(KEY_STEM_HOLD, "Noise Control (Cycle ANC)") ?: "Noise Control (Cycle ANC)"
        set(value) = prefs.edit().putString(KEY_STEM_HOLD, value).apply()

    var channelBalance: Float // -1.0 (all Left) to +1.0 (all Right), 0.0 is center
        get() = prefs.getFloat(KEY_CHANNEL_BALANCE, 0.0f)
        set(value) = prefs.edit().putFloat(KEY_CHANNEL_BALANCE, value).apply()

    var selectedPresetId: Long
        get() = prefs.getLong(KEY_SELECTED_PRESET_ID, -2) // Default to AirPods Pro Max Bass
        set(value) = prefs.edit().putLong(KEY_SELECTED_PRESET_ID, value).apply()

    var personalizedProfileEnabled: Boolean
        get() = prefs.getBoolean(KEY_PERSONALIZED_PROFILE, false)
        set(value) = prefs.edit().putBoolean(KEY_PERSONALIZED_PROFILE, value).apply()

    var hearingTestScore: Int
        get() = prefs.getInt(KEY_HEARING_SCORE, 94)
        set(value) = prefs.edit().putInt(KEY_HEARING_SCORE, value).apply()

    companion object {
        private const val KEY_ANC_MODE = "key_anc_mode"
        private const val KEY_SPATIAL_MODE = "key_spatial_mode"
        private const val KEY_BG_SERVICE = "key_bg_service"
        private const val KEY_AUTO_CONNECT = "key_auto_connect"
        private const val KEY_BATTERY_GUARD = "key_battery_guard"
        private const val KEY_EAR_DETECTION = "key_ear_detection"
        private const val KEY_GAMING_MODE = "key_gaming_mode"
        private const val KEY_CONVERSATIONAL = "key_conversational"
        private const val KEY_WIND_NOISE = "key_wind_noise"
        private const val KEY_STEM_HOLD = "key_stem_hold"
        private const val KEY_CHANNEL_BALANCE = "key_channel_balance"
        private const val KEY_SELECTED_PRESET_ID = "key_selected_preset_id"
        private const val KEY_PERSONALIZED_PROFILE = "key_personalized_profile"
        private const val KEY_HEARING_SCORE = "key_hearing_score"
    }
}
