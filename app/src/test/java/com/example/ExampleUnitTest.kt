package com.example

import com.example.data.db.PresetEntity
import com.example.data.model.AudioPreset
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPresetConversion() {
    val preset = AudioPreset(
      id = 12,
      name = "Sub-Bass Club",
      isCustom = true,
      bandLevels = listOf(500, 300, 0, 200, 400),
      bassBoost = 750,
      virtualizer = 300,
      loudnessGain = 200
    )
    val entity = PresetEntity.fromAudioPreset(preset)
    assertEquals("Sub-Bass Club", entity.name)
    assertEquals("500,300,0,200,400", entity.bandLevelsCsv)
    val back = entity.toAudioPreset()
    assertEquals(preset.name, back.name)
    assertEquals(preset.bandLevels, back.bandLevels)
    assertEquals(preset.bassBoost, back.bassBoost)
  }
}

