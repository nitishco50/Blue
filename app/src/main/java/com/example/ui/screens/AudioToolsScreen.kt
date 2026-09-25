package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HearingTestFrequency
import com.example.ui.components.AudiogramChart
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
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.EarTipTestState
import com.example.viewmodel.HearingTestState

@Composable
fun AudioToolsScreen(
    hearingState: HearingTestState,
    testFrequencies: List<HearingTestFrequency>,
    earTipState: EarTipTestState,
    currentDecibels: Float,
    findMyActiveEar: String?,
    proximityRssi: Int,
    onStartHearingTest: () -> Unit,
    onRecordHearingThreshold: (Boolean) -> Unit,
    onCancelHearingTest: () -> Unit,
    onStartEarTipFitTest: () -> Unit,
    onTriggerFindMyChirp: (String) -> Unit,
    onStopFindMyChirp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("audio_tools_screen_content")
    ) {
        // Title
        Text(
            text = "Audio Lab & Sound Tools",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Clinical audiometry, fit analysis, dB safety & beacon locator",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Hearing Test & Audiogram Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Hearing, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Personalized Hearing Test",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Audiogram-calibrated EQ response",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    if (hearingState.isComplete) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF064E3B))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Score: ${hearingState.overallScore}%", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!hearingState.isActive && !hearingState.isComplete) {
                    Text(
                        text = "Take a 2-minute diagnostic hearing test playing calibrated tones across 6 frequencies (250Hz - 8kHz). We will generate a customized EQ sound curve tailored to your ear sensitivity.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onStartHearingTest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("start_hearing_test_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Hearing Test", fontWeight = FontWeight.Bold)
                    }
                } else if (hearingState.isActive) {
                    // Active test UI
                    val currentFreq = testFrequencies[hearingState.currentFrequencyIndex]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(StudioCardSurfaceVariant)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Testing ${hearingState.currentEar} Ear",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hearingState.currentEar == "LEFT") ElectricCyan else NeonPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tone Frequency: ${currentFreq.label}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tone Volume Level: ${hearingState.currentVolumeDb} dB",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { onRecordHearingThreshold(true) },
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("hearing_heard_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = Color(0xFF0B0E14))
                                ) {
                                    Text("I Hear It", fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onRecordHearingThreshold(false) },
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("hearing_cant_hear_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                                ) {
                                    Text("Can't Hear", color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(onClick = onCancelHearingTest) {
                                Text("Cancel Test", color = Color(0xFFEF4444), fontSize = 12.sp)
                            }
                        }
                    }
                } else if (hearingState.isComplete) {
                    // Result Audiogram
                    val leftLoss = testFrequencies.map { hearingState.leftResults[it.frequencyHz] ?: 20 }
                    val rightLoss = testFrequencies.map { hearingState.rightResults[it.frequencyHz] ?: 20 }

                    AudiogramChart(
                        leftEarLoss = leftLoss,
                        rightEarLoss = rightLoss
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF064E3B).copy(alpha = 0.4f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Personalized Audiogram Profile created & automatically applied to Equalizer!",
                            fontSize = 11.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onStartHearingTest,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
                    ) {
                        Text("Retake Hearing Test", color = NeonPurple, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Ear Tip Fit Test
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ear Tip Fit Acoustic Test",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Acoustic seal detection for silicone tips",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Plays a diagnostic acoustic sweep tone and measures microphone resonance to ensure your silicone ear tips create a proper seal for rich bass and maximum noise cancellation.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (earTipState.isTesting) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Analyzing acoustic seal... ${(earTipState.progress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { earTipState.progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = ElectricCyan,
                            trackColor = DarkGrayTrack
                        )
                    }
                } else if (earTipState.result != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StudioCardSurfaceVariant)
                                .border(1.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Left Earbud", fontSize = 11.sp, color = TextSecondary)
                                Text("Good Seal (${earTipState.result.leftScore}%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StudioCardSurfaceVariant)
                                .border(1.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Right Earbud", fontSize = 11.sp, color = TextSecondary)
                                Text("Good Seal (${earTipState.result.rightScore}%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = earTipState.result.recommendation,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onStartEarTipFitTest,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                    ) {
                        Text("Retest Fit", color = ElectricCyan, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onStartEarTipFitTest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("start_fit_test_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color(0xFF0B0E14))
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Ear Tip Seal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Real-time Decibel Sound Pressure Meter
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
                            text = "Live Decibel & Hearing Guard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "WHO standard sound exposure monitoring",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    val levelColor = when {
                        currentDecibels > 85f -> Color(0xFFEF4444)
                        currentDecibels > 75f -> WarningAmber
                        else -> EmeraldGreen
                    }
                    val statusText = when {
                        currentDecibels > 85f -> "HAZARDOUS"
                        currentDecibels > 75f -> "MODERATE"
                        else -> "SAFE"
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(levelColor.copy(alpha = 0.2f))
                            .border(1.dp, levelColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(statusText, fontSize = 11.sp, color = levelColor, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${currentDecibels.toInt()}",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = " dB SPL",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // dB visual meter bar
                val meterProgress = (currentDecibels / 110f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(DarkGrayTrack)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(meterProgress)
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(EmeraldGreen, WarningAmber, Color(0xFFEF4444))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (currentDecibels > 85f)
                        "Warning: Prolonged exposure to levels above 85 dB can cause permanent hearing damage. Volume reduction recommended."
                    else
                        "Ambient noise level is well within safe thresholds. Comfortable listening environment.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Find My AirPods Locator & Proximity Radar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(StudioCardBorder, Color.Transparent)))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(WarningAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Radar, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Find My AirPods Radar & Chirp",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Play loud beacon alarm or track signal proximity",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Proximity Radar Gauge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(StudioCardSurfaceVariant)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Bluetooth Signal Proximity", fontSize = 12.sp, color = TextSecondary)
                            Text("Nearby (Approx 1.5 meters)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        }
                        Text("$proximityRssi% Signal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Play a high-pitch chirping tone to locate earbuds trapped between couch cushions or in another room:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isLeftPlaying = findMyActiveEar == "LEFT"
                    Button(
                        onClick = {
                            if (isLeftPlaying) onStopFindMyChirp() else onTriggerFindMyChirp("LEFT")
                        },
                        modifier = Modifier.weight(1f).height(44.dp).testTag("chirp_left_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLeftPlaying) Color(0xFFEF4444) else StudioCardSurfaceVariant,
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isLeftPlaying) Icons.Default.Stop else Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isLeftPlaying) Color.White else ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isLeftPlaying) "Stop Left" else "Chirp Left")
                    }

                    val isRightPlaying = findMyActiveEar == "RIGHT"
                    Button(
                        onClick = {
                            if (isRightPlaying) onStopFindMyChirp() else onTriggerFindMyChirp("RIGHT")
                        },
                        modifier = Modifier.weight(1f).height(44.dp).testTag("chirp_right_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRightPlaying) Color(0xFFEF4444) else StudioCardSurfaceVariant,
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isRightPlaying) Icons.Default.Stop else Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isRightPlaying) Color.White else NeonPurple
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRightPlaying) "Stop Right" else "Chirp Right")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
