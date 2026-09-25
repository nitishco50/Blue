package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AirPodsBattery
import com.example.data.model.AncMode
import com.example.data.model.AudioPreset
import com.example.data.model.SpatialAudioMode
import com.example.ui.components.BatteryCard
import com.example.ui.theme.DarkGrayTrack
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanGlow
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
fun HubScreen(
    battery: AirPodsBattery,
    activePreset: AudioPreset,
    ancMode: AncMode,
    spatialMode: SpatialAudioMode,
    isEqEnabled: Boolean,
    is80GuardActive: Boolean,
    isBgServiceActive: Boolean,
    onSelectAncMode: (AncMode) -> Unit,
    onSelectSpatialMode: (SpatialAudioMode) -> Unit,
    onToggleEq: (Boolean) -> Unit,
    onNavigateToEqualizer: () -> Unit,
    onShowConnectionPopup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("hub_screen_content")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PodEQ Studio",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Connected • AirPods Pro (2nd Gen)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudioCardSurfaceVariant)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                    .clickable { onShowConnectionPopup() }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .testTag("trigger_popup_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.BluetoothConnected,
                        contentDescription = "Bluetooth Card",
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Card",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hero Image Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.airpods_hero_1790305295887),
                    contentDescription = "AirPods Pro",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, StudioCardSurface.copy(alpha = 0.9f))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBgServiceActive) "Background Audio Engine Running" else "Audio Engine Ready",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Sound profile persists even after app closes",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Battery Section
        BatteryCard(
            battery = battery,
            is80GuardActive = is80GuardActive
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Noise Control Section
        Text(
            text = "Noise Control",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AncModePill(
                        mode = AncMode.NOISE_CANCELLATION,
                        selected = ancMode == AncMode.NOISE_CANCELLATION,
                        label = "ANC",
                        onClick = { onSelectAncMode(AncMode.NOISE_CANCELLATION) },
                        modifier = Modifier.weight(1f)
                    )
                    AncModePill(
                        mode = AncMode.ADAPTIVE,
                        selected = ancMode == AncMode.ADAPTIVE,
                        label = "Adaptive",
                        onClick = { onSelectAncMode(AncMode.ADAPTIVE) },
                        modifier = Modifier.weight(1f)
                    )
                    AncModePill(
                        mode = AncMode.TRANSPARENCY,
                        selected = ancMode == AncMode.TRANSPARENCY,
                        label = "Transparency",
                        onClick = { onSelectAncMode(AncMode.TRANSPARENCY) },
                        modifier = Modifier.weight(1.2f)
                    )
                    AncModePill(
                        mode = AncMode.OFF,
                        selected = ancMode == AncMode.OFF,
                        label = "Off",
                        onClick = { onSelectAncMode(AncMode.OFF) },
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = ancMode.description,
                    fontSize = 12.sp,
                    color = TextTertiary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spatial Audio Section
        Text(
            text = "Spatial Audio & 3D Stage",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpatialModePill(
                    title = "Off",
                    selected = spatialMode == SpatialAudioMode.OFF,
                    onClick = { onSelectSpatialMode(SpatialAudioMode.OFF) },
                    modifier = Modifier.weight(1f)
                )
                SpatialModePill(
                    title = "Fixed 3D",
                    selected = spatialMode == SpatialAudioMode.FIXED,
                    onClick = { onSelectSpatialMode(SpatialAudioMode.FIXED) },
                    modifier = Modifier.weight(1f)
                )
                SpatialModePill(
                    title = "Head Tracked",
                    selected = spatialMode == SpatialAudioMode.HEAD_TRACKED,
                    onClick = { onSelectSpatialMode(SpatialAudioMode.HEAD_TRACKED) },
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Active Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToEqualizer() }
                .testTag("hub_active_preset_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ElectricCyan.copy(alpha = 0.5f), NeonPurple.copy(alpha = 0.3f))))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(ElectricCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = activePreset.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Bass +${activePreset.bassBoost / 10}% • 3D ${activePreset.virtualizer / 10}%",
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isEqEnabled,
                        onCheckedChange = { onToggleEq(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ElectricCyan,
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = DarkGrayTrack
                        ),
                        modifier = Modifier.testTag("hub_eq_toggle_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AncModePill(
    mode: AncMode,
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) ElectricCyan else StudioCardSurfaceVariant)
            .border(
                1.dp,
                if (selected) ElectricCyan else StudioCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .testTag("anc_pill_${mode.name}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color(0xFF0B0E14) else TextSecondary
        )
    }
}

@Composable
private fun SpatialModePill(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) NeonPurple else StudioCardSurfaceVariant)
            .border(
                1.dp,
                if (selected) NeonPurple else StudioCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .testTag("spatial_pill_$title"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else TextSecondary
        )
    }
}
