package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkGrayTrack
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RotaryKnob(
    label: String,
    value: Int, // 0 to 1000
    onValueChange: (Int) -> Unit,
    accentColor: Color = ElectricCyan,
    modifier: Modifier = Modifier,
    tag: String = "rotary_knob"
) {
    val percentage = (value / 1000f).coerceIn(0f, 1f)

    Column(
        modifier = modifier.testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Drag up/right increases, drag down/left decreases
                        val delta = (dragAmount.x - dragAmount.y) * 4f
                        val newValue = (value + delta).toInt().coerceIn(0, 1000)
                        onValueChange(newValue)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(86.dp)) {
                val strokeWidth = 8.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Background track arc (from 135 deg to 405 deg = 270 deg span)
                drawArc(
                    color = DarkGrayTrack,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active glowing value arc
                val activeSweep = 270f * percentage
                if (activeSweep > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(ElectricCyan, accentColor, NeonPurple),
                            center = center
                        ),
                        startAngle = 135f,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Dial center circle
                drawCircle(
                    color = Color(0xFF131924),
                    radius = radius - 8.dp.toPx()
                )

                // Indicator needle/dot
                val angleRad = Math.toRadians((135.0 + activeSweep))
                val dotRadius = radius - 14.dp.toPx()
                val dotX = center.x + dotRadius * cos(angleRad).toFloat()
                val dotY = center.y + dotRadius * sin(angleRad).toFloat()

                drawCircle(
                    color = accentColor,
                    radius = 4.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }
}
