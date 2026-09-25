package com.example.data.model

data class AudioPreset(
    val id: Long = 0,
    val name: String,
    val isCustom: Boolean = false,
    val bandLevels: List<Int>, // in millibels, e.g. -1000 to +1000 (-10dB to +10dB)
    val bassBoost: Int = 0,     // 0 to 1000
    val virtualizer: Int = 0,   // 0 to 1000 (3D Spatial surround)
    val loudnessGain: Int = 0,  // 0 to 1000 mB
    val trebleBoost: Int = 0    // extra high frequency emphasis
) {
    companion object {
        val DEFAULT_BANDS = listOf(60, 230, 910, 3600, 14000)

        val BUILT_IN_PRESETS = listOf(
            AudioPreset(
                id = -1,
                name = "Flat / Balanced",
                isCustom = false,
                bandLevels = listOf(0, 0, 0, 0, 0),
                bassBoost = 0,
                virtualizer = 0,
                loudnessGain = 0
            ),
            AudioPreset(
                id = -2,
                name = "AirPods Pro Max Bass",
                isCustom = false,
                bandLevels = listOf(700, 450, 0, 200, 400),
                bassBoost = 650,
                virtualizer = 250,
                loudnessGain = 200
            ),
            AudioPreset(
                id = -3,
                name = "Vocal & Podcast Clarity",
                isCustom = false,
                bandLevels = listOf(-300, 100, 600, 750, 200),
                bassBoost = 50,
                virtualizer = 100,
                loudnessGain = 300
            ),
            AudioPreset(
                id = -4,
                name = "Electronic & Dance",
                isCustom = false,
                bandLevels = listOf(600, 300, -100, 400, 600),
                bassBoost = 700,
                virtualizer = 500,
                loudnessGain = 250
            ),
            AudioPreset(
                id = -5,
                name = "Rock & Metal Punch",
                isCustom = false,
                bandLevels = listOf(500, 200, -150, 350, 550),
                bassBoost = 450,
                virtualizer = 300,
                loudnessGain = 200
            ),
            AudioPreset(
                id = -6,
                name = "Hip-Hop & R&B Sub-Bass",
                isCustom = false,
                bandLevels = listOf(800, 500, 50, 200, 300),
                bassBoost = 800,
                virtualizer = 200,
                loudnessGain = 350
            ),
            AudioPreset(
                id = -7,
                name = "Audiophile Acoustic",
                isCustom = false,
                bandLevels = listOf(200, 350, 200, 300, 500),
                bassBoost = 200,
                virtualizer = 400,
                loudnessGain = 100
            ),
            AudioPreset(
                id = -8,
                name = "Spatial Cinema 3D",
                isCustom = false,
                bandLevels = listOf(350, 100, 200, 400, 650),
                bassBoost = 500,
                virtualizer = 900,
                loudnessGain = 400
            )
        )
    }
}
