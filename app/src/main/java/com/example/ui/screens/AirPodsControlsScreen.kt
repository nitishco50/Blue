package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Earbuds
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AncMode
import com.example.ui.theme.DarkGrayTrack
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioCardSurfaceVariant
import com.example.ui.theme.StudioDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun AirPodsControlsScreen(
    ancMode: AncMode,
    isEarDetection: Boolean,
    isGamingMode: Boolean,
    isConversational: Boolean,
    isWindNoiseReduction: Boolean,
    stemHoldAction: String,
    channelBalance: Float,
    onSelectAncMode: (AncMode) -> Unit,
    onToggleEarDetection: (Boolean) -> Unit,
    onToggleGamingMode: (Boolean) -> Unit,
    onToggleConversational: (Boolean) -> Unit,
    onToggleWindNoiseReduction: (Boolean) -> Unit,
    onSelectStemHoldAction: (String) -> Unit,
    onChannelBalanceChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var stemDropdownExpanded by remember { mutableStateOf(false) }

    val stemActions = listOf(
        "Noise Control (Cycle ANC)",
        "Voice Assistant (Google / Siri)",
        "Volume Up / Down Swipe"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("airpods_controls_screen_content")
    ) {
        // Title
        Text(
            text = "AirPods Hardware Controls",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Full sensor, noise control, and gesture configuration",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Noise Control Hub
        Text(
            text = "Active Noise Control Modes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AncOptionRow(
                    mode = AncMode.NOISE_CANCELLATION,
                    title = "Active Noise Cancellation",
                    description = "Eliminates plane engines, air conditioning, and traffic rumble.",
                    selected = ancMode == AncMode.NOISE_CANCELLATION,
                    onSelect = { onSelectAncMode(AncMode.NOISE_CANCELLATION) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkGrayTrack))
                Spacer(modifier = Modifier.height(8.dp))

                AncOptionRow(
                    mode = AncMode.TRANSPARENCY,
                    title = "Transparency Mode",
                    description = "Passes ambient sound naturally through for conversations & outdoor safety.",
                    selected = ancMode == AncMode.TRANSPARENCY,
                    onSelect = { onSelectAncMode(AncMode.TRANSPARENCY) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkGrayTrack))
                Spacer(modifier = Modifier.height(8.dp))

                AncOptionRow(
                    mode = AncMode.ADAPTIVE,
                    title = "Adaptive Audio",
                    description = "Blends ANC and Transparency automatically as surrounding noise changes.",
                    selected = ancMode == AncMode.ADAPTIVE,
                    onSelect = { onSelectAncMode(AncMode.ADAPTIVE) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkGrayTrack))
                Spacer(modifier = Modifier.height(8.dp))

                AncOptionRow(
                    mode = AncMode.OFF,
                    title = "Noise Control Off",
                    description = "Turns off all microphones & active DSP for longest battery life.",
                    selected = ancMode == AncMode.OFF,
                    onSelect = { onSelectAncMode(AncMode.OFF) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stem Gestures & Click Action
        Text(
            text = "Stem Touch & Gestures",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Press and hold customization
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { stemDropdownExpanded = true }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Press and Hold Sensor",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = stemHoldAction,
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                    }

                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(StudioCardSurfaceVariant)
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("stem_dropdown_trigger")
                        ) {
                            Text("Change", fontSize = 12.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }

                        DropdownMenu(
                            expanded = stemDropdownExpanded,
                            onDismissRequest = { stemDropdownExpanded = false },
                            modifier = Modifier.background(StudioCardSurface)
                        ) {
                            stemActions.forEach { action ->
                                DropdownMenuItem(
                                    text = { Text(action, color = TextPrimary) },
                                    onClick = {
                                        onSelectStemHoldAction(action)
                                        stemDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gesture info bullets
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(StudioCardSurfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GestureGuideItem(gesture = "1x Press", effect = "Play / Pause media or answer call")
                    GestureGuideItem(gesture = "2x Press", effect = "Skip track forward")
                    GestureGuideItem(gesture = "3x Press", effect = "Skip back to previous track")
                    GestureGuideItem(gesture = "Swipe Up/Down", effect = "Adjust volume incrementally")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Advanced Audio & Intelligent Sensors
        Text(
            text = "Smart Audio & Sensors",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingSwitchRow(
                    icon = Icons.Default.Earbuds,
                    title = "Automatic Ear Detection",
                    subtitle = "Pauses playback when an earbud is removed, resumes when reinserted.",
                    checked = isEarDetection,
                    onCheckedChange = onToggleEarDetection,
                    tag = "ear_detection_switch"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkGrayTrack))
                Spacer(modifier = Modifier.height(12.dp))

                SettingSwitchRow(
                    icon = Icons.Default.RecordVoiceOver,
                    title = "Conversational Awareness",
                    subtitle = "Automatically lowers volume and turns on Transparency when you speak.",
                    checked = isConversational,
                    onCheckedChange = onToggleConversational,
                    tag = "conversational_switch"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkGrayTrack))
                Spacer(modifier = Modifier.height(12.dp))

                SettingSwitchRow(
                    icon = Icons.Default.Air,
                    title = "Wind Noise Reduction",
                    subtitle = "Optimizes beamforming microphones to suppress howling wind during runs.",
                    checked = isWindNoiseReduction,
                    onCheckedChange = onToggleWindNoiseReduction,
                    tag = "wind_noise_switch"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkGrayTrack))
                Spacer(modifier = Modifier.height(12.dp))

                SettingSwitchRow(
                    icon = Icons.Default.SportsEsports,
                    title = "Low-Latency Gaming Mode",
                    subtitle = "Minimizes audio buffer latency for gaming, FPS shooters, and streaming.",
                    checked = isGamingMode,
                    onCheckedChange = onToggleGamingMode,
                    tag = "gaming_mode_switch"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stereo Balance Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stereo Audio Balance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = when {
                            channelBalance < -0.05f -> "Left +${(-channelBalance * 100).toInt()}%"
                            channelBalance > 0.05f -> "Right +${(channelBalance * 100).toInt()}%"
                            else -> "Balanced (Center)"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Adjust volume distribution between Left and Right earbud for asymmetrical hearing.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("L", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = channelBalance,
                        onValueChange = onChannelBalanceChange,
                        valueRange = -1.0f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = DarkGrayTrack
                        ),
                        modifier = Modifier.weight(1f).testTag("channel_balance_slider")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("R", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AncOptionRow(
    mode: AncMode,
    title: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(8.dp)
            .testTag("anc_option_row_${mode.name}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) ElectricCyan else TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) ElectricCyan else StudioCardBorder, CircleShape)
                .background(if (selected) ElectricCyan else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF0B0E14),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(StudioCardSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ElectricCyan,
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = DarkGrayTrack
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun GestureGuideItem(gesture: String, effect: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$gesture :",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricCyan,
            modifier = Modifier.width(105.dp)
        )
        Text(
            text = effect,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}
