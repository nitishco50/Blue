package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.audio.AudioEngine
import com.example.data.model.AncMode
import com.example.data.model.AudioPreset
import com.example.data.preferences.AppPreferences

class AudioFxService : Service() {

    private lateinit var audioEngine: AudioEngine
    private lateinit var appPreferences: AppPreferences
    private var currentPreset: AudioPreset = AudioPreset.BUILT_IN_PRESETS[1] // Default
    private var isEqEnabled: Boolean = true

    override fun onCreate() {
        super.onCreate()
        audioEngine = AudioEngine(this)
        appPreferences = AppPreferences(this)
        audioEngine.initAudioEffects()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START

        when (action) {
            ACTION_START -> {
                startForegroundServiceNotification()
                audioEngine.applyPreset(currentPreset, isEqEnabled)
            }
            ACTION_UPDATE_PRESET -> {
                val presetName = intent?.getStringExtra(EXTRA_PRESET_NAME) ?: currentPreset.name
                val bandLevels = intent?.getIntArrayExtra(EXTRA_BAND_LEVELS)?.toList() ?: currentPreset.bandLevels
                val bass = intent?.getIntExtra(EXTRA_BASS, currentPreset.bassBoost) ?: currentPreset.bassBoost
                val virt = intent?.getIntExtra(EXTRA_VIRTUALIZER, currentPreset.virtualizer) ?: currentPreset.virtualizer
                val loud = intent?.getIntExtra(EXTRA_LOUDNESS, currentPreset.loudnessGain) ?: currentPreset.loudnessGain
                isEqEnabled = intent?.getBooleanExtra(EXTRA_EQ_ENABLED, true) ?: true

                currentPreset = AudioPreset(
                    id = currentPreset.id,
                    name = presetName,
                    isCustom = currentPreset.isCustom,
                    bandLevels = bandLevels,
                    bassBoost = bass,
                    virtualizer = virt,
                    loudnessGain = loud
                )
                audioEngine.applyPreset(currentPreset, isEqEnabled)
                updateNotification()
            }
            ACTION_TOGGLE_ANC -> {
                val current = appPreferences.ancMode
                val next = when (current) {
                    AncMode.NOISE_CANCELLATION -> AncMode.TRANSPARENCY
                    AncMode.TRANSPARENCY -> AncMode.ADAPTIVE
                    AncMode.ADAPTIVE -> AncMode.OFF
                    AncMode.OFF -> AncMode.NOISE_CANCELLATION
                }
                appPreferences.ancMode = next
                updateNotification()

                // Broadcast change back to activity if alive
                val broadcastIntent = Intent(ACTION_ANC_CHANGED).apply {
                    putExtra(EXTRA_NEW_ANC, next.name)
                    setPackage(packageName)
                }
                sendBroadcast(broadcastIntent)
            }
            ACTION_TOGGLE_EQ -> {
                isEqEnabled = !isEqEnabled
                audioEngine.applyPreset(currentPreset, isEqEnabled)
                updateNotification()

                val broadcastIntent = Intent(ACTION_EQ_TOGGLED).apply {
                    putExtra(EXTRA_EQ_ENABLED, isEqEnabled)
                    setPackage(packageName)
                }
                sendBroadcast(broadcastIntent)
            }
            ACTION_STOP -> {
                audioEngine.release()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    private fun startForegroundServiceNotification() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification() {
        val notification = buildNotification()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleAncIntent = Intent(this, AudioFxService::class.java).apply {
            action = ACTION_TOGGLE_ANC
        }
        val pendingAnc = PendingIntent.getService(
            this,
            1,
            toggleAncIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleEqIntent = Intent(this, AudioFxService::class.java).apply {
            action = ACTION_TOGGLE_EQ
        }
        val pendingEq = PendingIntent.getService(
            this,
            2,
            toggleEqIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, AudioFxService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStop = PendingIntent.getService(
            this,
            3,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val ancText = when (appPreferences.ancMode) {
            AncMode.NOISE_CANCELLATION -> "ANC Active"
            AncMode.TRANSPARENCY -> "Transparency"
            AncMode.ADAPTIVE -> "Adaptive"
            AncMode.OFF -> "Noise Ctrl Off"
        }
        val eqStatus = if (isEqEnabled) "EQ ON (${currentPreset.name})" else "EQ Bypassed"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PodEQ Active — AirPods Pro")
            .setContentText("$eqStatus • $ancText")
            .setSubText("Background Sound Profile")
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                android.R.drawable.ic_menu_rotate,
                "Cycle ANC",
                pendingAnc
            )
            .addAction(
                android.R.drawable.ic_media_play,
                if (isEqEnabled) "Disable EQ" else "Enable EQ",
                pendingEq
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Exit",
                pendingStop
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "PodEQ Audio Engine",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps AirPods equalizer and sound profile active in background"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "podeq_audio_fx_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.podeq.START"
        const val ACTION_STOP = "com.example.podeq.STOP"
        const val ACTION_UPDATE_PRESET = "com.example.podeq.UPDATE_PRESET"
        const val ACTION_TOGGLE_ANC = "com.example.podeq.TOGGLE_ANC"
        const val ACTION_TOGGLE_EQ = "com.example.podeq.TOGGLE_EQ"

        const val ACTION_ANC_CHANGED = "com.example.podeq.ANC_CHANGED"
        const val ACTION_EQ_TOGGLED = "com.example.podeq.EQ_TOGGLED"

        const val EXTRA_PRESET_NAME = "extra_preset_name"
        const val EXTRA_BAND_LEVELS = "extra_band_levels"
        const val EXTRA_BASS = "extra_bass"
        const val EXTRA_VIRTUALIZER = "extra_virtualizer"
        const val EXTRA_LOUDNESS = "extra_loudness"
        const val EXTRA_EQ_ENABLED = "extra_eq_enabled"
        const val EXTRA_NEW_ANC = "extra_new_anc"

        fun startService(context: Context, preset: AudioPreset, isEqEnabled: Boolean = true) {
            val intent = Intent(context, AudioFxService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_PRESET_NAME, preset.name)
                putExtra(EXTRA_BAND_LEVELS, preset.bandLevels.toIntArray())
                putExtra(EXTRA_BASS, preset.bassBoost)
                putExtra(EXTRA_VIRTUALIZER, preset.virtualizer)
                putExtra(EXTRA_LOUDNESS, preset.loudnessGain)
                putExtra(EXTRA_EQ_ENABLED, isEqEnabled)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateService(context: Context, preset: AudioPreset, isEqEnabled: Boolean = true) {
            val intent = Intent(context, AudioFxService::class.java).apply {
                action = ACTION_UPDATE_PRESET
                putExtra(EXTRA_PRESET_NAME, preset.name)
                putExtra(EXTRA_BAND_LEVELS, preset.bandLevels.toIntArray())
                putExtra(EXTRA_BASS, preset.bassBoost)
                putExtra(EXTRA_VIRTUALIZER, preset.virtualizer)
                putExtra(EXTRA_LOUDNESS, preset.loudnessGain)
                putExtra(EXTRA_EQ_ENABLED, isEqEnabled)
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, AudioFxService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
