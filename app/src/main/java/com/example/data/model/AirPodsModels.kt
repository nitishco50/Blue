package com.example.data.model

enum class AncMode(val title: String, val description: String) {
    NOISE_CANCELLATION("Active Noise Cancellation", "Blocks out external ambient noise using dual inward/outward microphones"),
    TRANSPARENCY("Transparency", "Lets outside sound in so you can hear what's happening around you"),
    ADAPTIVE("Adaptive Audio", "Dynamically blends ANC and Transparency based on your changing environment"),
    OFF("Off", "All active noise control processing is disabled to maximize battery life")
}

enum class SpatialAudioMode(val title: String, val description: String) {
    OFF("Off", "Standard stereo audio output"),
    FIXED("Fixed 3D", "Immersive 3D sound stage fixed in front of you"),
    HEAD_TRACKED("Head Tracked", "Acoustic audio anchors to your device as your head turns")
}

data class AirPodsBattery(
    val leftPercent: Int = 88,
    val rightPercent: Int = 92,
    val casePercent: Int = 74,
    val isLeftCharging: Boolean = false,
    val isRightCharging: Boolean = false,
    val isCaseCharging: Boolean = true
)

data class HearingTestFrequency(
    val frequencyHz: Int,
    val label: String,
    val leftThresholdDb: Int = 0,
    val rightThresholdDb: Int = 0
)

data class EarTipFitResult(
    val leftStatus: String = "Good Seal",
    val rightStatus: String = "Good Seal",
    val leftScore: Int = 95,
    val rightScore: Int = 92,
    val recommendation: String = "Optimal acoustic isolation achieved with current Medium tips."
)

data class DeviceHardwareInfo(
    val modelName: String = "AirPods Pro (2nd Generation)",
    val modelNumber: String = "A2968",
    val serialNumber: String = "H7J2L8N9K1",
    val firmwareVersion: String = "7A305",
    val latestFirmwareVersion: String = "7A305",
    val caseFirmware: String = "66.5.0",
    val hardwareRevision: String = "Rev 2.4",
    val bluetoothAddress: String = "64:A5:C3:98:21:BE",
    val isUpdateAvailable: Boolean = false
)
