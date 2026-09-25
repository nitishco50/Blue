package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkGrayTrack
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EqualizerSliders(
    bandLevels: List<Int>, // in millibels (-1200 to +1200)
    bandFrequencies: List<String> = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz"),
    onBandLevelChange: (index: Int, newMillibels: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxDb = 12
    val minDb = -12
    val rangeDb = maxDb - minDb // 24

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("equalizer_sliders_container")
            .padding(vertical = 12.dp)
    ) {
        // Frequency response curve canvas behind or above sliders
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F141C))
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stepX = size.width / (bandLevels.size - 1)
                val path = Path()
                val fillPath = Path()

                fillPath.moveTo(0f, size.height)

                bandLevels.forEachIndexed { i, mb ->
                    val db = (mb / 100f).coerceIn(minDb.toFloat(), maxDb.toFloat())
                    // Top is maxDb, bottom is minDb
                    val normalized = 1f - ((db - minDb) / rangeDb.toFloat())
                    val x = i * stepX
                    val y = normalized * (size.height - 16f) + 8f

                    if (i == 0) {
                        path.moveTo(x, y)
                        fillPath.lineTo(x, y)
                    } else {
                        val prevX = (i - 1) * stepX
                        val prevDb = (bandLevels[i - 1] / 100f).coerceIn(minDb.toFloat(), maxDb.toFloat())
                        val prevNorm = 1f - ((prevDb - minDb) / rangeDb.toFloat())
                        val prevY = prevNorm * (size.height - 16f) + 8f

                        val cx = (prevX + x) / 2f
                        path.cubicTo(cx, prevY, cx, y, x, y)
                        fillPath.cubicTo(cx, prevY, cx, y, x, y)
                    }
                }

                fillPath.lineTo(size.width, size.height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(ElectricCyan.copy(alpha = 0.25f), Color.Transparent)
                    )
                )

                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple)),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Zero line
                val zeroY = 0.5f * (size.height - 16f) + 8f
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = Offset(0f, zeroY),
                    end = Offset(size.width, zeroY),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sliders row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bandLevels.forEachIndexed { index, millibels ->
                val freqLabel = bandFrequencies.getOrElse(index) { "${index + 1}" }
                VerticalBandSlider(
                    bandIndex = index,
                    freqLabel = freqLabel,
                    millibels = millibels,
                    onValueChange = { onBandLevelChange(index, it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun VerticalBandSlider(
    bandIndex: Int,
    freqLabel: String,
    millibels: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var trackHeightPx by remember { mutableFloatStateOf(1f) }
    val maxDb = 1200f
    val minDb = -1200f

    val currentDb = (millibels / 100f)
    val dbText = if (currentDb > 0) "+${currentDb.toInt()} dB" else if (currentDb < 0) "${currentDb.toInt()} dB" else "0 dB"

    Column(
        modifier = modifier
            .fillMaxHeight()
            .testTag("eq_band_slider_$bandIndex"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // dB display
        Text(
            text = dbText,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (millibels != 0) ElectricCyan else TextSecondary
        )

        // Custom vertical drag track
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(140.dp)
                .onGloballyPositioned { trackHeightPx = it.size.height.toFloat() }
                .pointerInput(trackHeightPx) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        // Drag down increases Y, which means lower dB
                        val deltaDb = -(dragAmount / trackHeightPx) * (maxDb - minDb)
                        val newMb = (millibels + deltaDb).toInt().coerceIn(minDb.toInt(), maxDb.toInt())
                        onValueChange(newMb)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Background rail
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(DarkGrayTrack)
            )

            // Center notch indicator (0 dB)
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .background(StudioCardBorder)
            )

            // Thumb calculation: top is maxDb (+1), bottom is minDb (-1)
            val fractionFromBottom = ((millibels - minDb) / (maxDb - minDb)).coerceIn(0f, 1f)
            val verticalBias = (1f - fractionFromBottom) * 2f - 1f // -1f (top) to +1f (bottom)

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(BiasAlignment(0f, verticalBias))
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ElectricCyan, Color(0xFF0284C7))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }

        // Frequency Label
        Text(
            text = freqLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }
}
