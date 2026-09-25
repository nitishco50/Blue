package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object ToneGenerator {
    private const val SAMPLE_RATE = 44100
    private var activeTrack: AudioTrack? = null

    /**
     * Plays a pure sine tone at [frequencyHz] for [durationMs] at given [volume] (0.0 to 1.0).
     * [channel]: "LEFT", "RIGHT", or "BOTH"
     */
    suspend fun playTone(
        frequencyHz: Int,
        durationMs: Int,
        volume: Float = 0.5f,
        channel: String = "BOTH"
    ) = withContext(Dispatchers.IO) {
        stopTone()
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        // Stereo: 2 shorts per sample (Left, Right)
        val buffer = ShortArray(numSamples * 2)

        val leftGain = if (channel == "RIGHT") 0.0f else volume
        val rightGain = if (channel == "LEFT") 0.0f else volume

        val twoPi = 2.0 * Math.PI
        val step = twoPi * frequencyHz / SAMPLE_RATE

        for (i in 0 until numSamples) {
            val angle = i * step
            val sample = sin(angle)
            // Apply fade-in and fade-out envelope to avoid clicks
            val envelope = when {
                i < 200 -> i / 200.0f
                i > numSamples - 200 -> (numSamples - i) / 200.0f
                else -> 1.0f
            }
            val leftVal = (sample * Short.MAX_VALUE * leftGain * envelope).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            val rightVal = (sample * Short.MAX_VALUE * rightGain * envelope).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())

            buffer[i * 2] = leftVal.toShort()
            buffer[i * 2 + 1] = rightVal.toShort()
        }

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            activeTrack = track
        } catch (_: Exception) {
            // Ignore track initialization errors on restricted environments
        }
    }

    /**
     * Plays a loud alternating locator chirping alarm for Find My AirPods.
     */
    suspend fun playFindMyChirp(channel: String, repetitions: Int = 4) = withContext(Dispatchers.IO) {
        val frequencies = intArrayOf(2400, 3200, 2400, 3600)
        for (rep in 0 until repetitions) {
            for (freq in frequencies) {
                playTone(frequencyHz = freq, durationMs = 180, volume = 0.95f, channel = channel)
                kotlinx.coroutines.delay(220)
            }
            kotlinx.coroutines.delay(300)
        }
    }

    fun stopTone() {
        try {
            activeTrack?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.release()
            }
            activeTrack = null
        } catch (_: Exception) {}
    }
}
