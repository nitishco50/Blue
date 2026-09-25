package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPreset
import com.example.ui.components.EqualizerSliders
import com.example.ui.components.RotaryKnob
import com.example.ui.theme.DarkGrayTrack
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioCardSurfaceVariant
import com.example.ui.theme.StudioDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun EqualizerScreen(
    activePreset: AudioPreset,
    allPresets: List<AudioPreset>,
    isEqEnabled: Boolean,
    onSelectPreset: (AudioPreset) -> Unit,
    onBandChange: (Int, Int) -> Unit,
    onBassChange: (Int) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onLoudnessChange: (Int) -> Unit,
    onToggleEq: (Boolean) -> Unit,
    onSaveNewPreset: (String) -> Unit,
    onDeletePreset: (AudioPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val presetsScrollState = rememberScrollState()
    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("equalizer_screen_content")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Sound Profiles & EQ",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Hardware DSP Equalizer • AirPods Studio Tuning",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isEqEnabled) "Active" else "Bypass",
                    fontSize = 12.sp,
                    color = if (isEqEnabled) ElectricCyan else TextTertiary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isEqEnabled,
                    onCheckedChange = onToggleEq,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ElectricCyan,
                        uncheckedThumbColor = TextTertiary,
                        uncheckedTrackColor = DarkGrayTrack
                    ),
                    modifier = Modifier.testTag("eq_master_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(presetsScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // New preset button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudioCardSurfaceVariant)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                    .clickable { showSaveDialog = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("create_custom_preset_chip"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "New Preset", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                }
            }

            allPresets.forEach { preset ->
                val isSelected = activePreset.id == preset.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ElectricCyan else StudioCardSurfaceVariant)
                        .border(1.dp, if (isSelected) ElectricCyan else StudioCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectPreset(preset) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("preset_chip_${preset.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = preset.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF0B0E14) else TextPrimary
                        )
                        if (preset.isCustom) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFF0B0E14) else NeonPurple)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Equalizer Sliders Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = activePreset.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (activePreset.isCustom) "User Sound Preset" else "Studio Factory Preset",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Row {
                        if (activePreset.isCustom) {
                            IconButton(
                                onClick = { onDeletePreset(activePreset) },
                                modifier = Modifier.size(36.dp).testTag("delete_preset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                activePreset.bandLevels.indices.forEach { idx ->
                                    onBandChange(idx, 0)
                                }
                            },
                            modifier = Modifier.size(36.dp).testTag("reset_bands_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Flat",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                EqualizerSliders(
                    bandLevels = activePreset.bandLevels,
                    onBandLevelChange = onBandChange
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Rotary Knobs: Bass Boost & 3D Spatial Virtualizer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Acoustic Effects & Depth",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    RotaryKnob(
                        label = "AirPods Bass Boost",
                        value = activePreset.bassBoost,
                        onValueChange = onBassChange,
                        accentColor = ElectricCyan,
                        tag = "bass_boost_knob"
                    )

                    RotaryKnob(
                        label = "3D Virtualizer Stage",
                        value = activePreset.virtualizer,
                        onValueChange = onVirtualizerChange,
                        accentColor = NeonPurple,
                        tag = "virtualizer_knob"
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Loudness Enhancer Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Loudness & Dynamics Gain",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Text(
                            text = "+${activePreset.loudnessGain / 10} dB",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }

                    Slider(
                        value = activePreset.loudnessGain.toFloat(),
                        onValueChange = { onLoudnessChange(it.toInt()) },
                        valueRange = 0f..1000f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = DarkGrayTrack
                        ),
                        modifier = Modifier.testTag("loudness_slider")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    newPresetName = "${activePreset.name} (Custom)"
                    showSaveDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("save_as_new_button"),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Preset", color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Save Preset Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text("Save Custom Sound Profile", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Give your personalized EQ and acoustic settings a memorable name:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPresetName,
                        onValueChange = { newPresetName = it },
                        placeholder = { Text("e.g. My Workout Bass Curve") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedPlaceholderColor = TextTertiary,
                            unfocusedPlaceholderColor = TextTertiary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("preset_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveNewPreset(newPresetName)
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color(0xFF0B0E14)),
                    modifier = Modifier.testTag("confirm_save_preset_button")
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false },
                    modifier = Modifier.testTag("cancel_save_preset_button")
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = StudioCardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
