package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AudiogramChart(
    frequencies: List<String> = listOf("250", "500", "1k", "2k", "4k", "8k"),
    leftEarLoss: List<Int>, // in dB (e.g. 15, 20, 25, 30, 20, 25)
    rightEarLoss: List<Int>, // in dB
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F141C))
            .border(1.dp, StudioCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("audiogram_chart")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Audiometric Response Curve",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Left Ear legend
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricCyan))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Left Ear", fontSize = 11.sp, color = TextSecondary)

                Spacer(modifier = Modifier.width(12.dp))

                // Right Ear legend
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonPurple))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Right Ear", fontSize = 11.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxLoss = 60f
                val minLoss = 0f
                val range = maxLoss - minLoss

                // Draw horizontal guide lines (0dB, 25dB Normal cutoff, 50dB)
                val normalY = (25f / range) * size.height
                drawLine(
                    color = EmeraldGreen.copy(alpha = 0.35f),
                    start = Offset(0f, normalY),
                    end = Offset(size.width, normalY),
                    strokeWidth = 1.dp.toPx()
                )

                val stepX = size.width / (frequencies.size - 1)

                // Left Ear Path
                val leftPath = Path()
                leftEarLoss.forEachIndexed { i, loss ->
                    val x = i * stepX
                    val y = (loss.toFloat() / range) * size.height
                    if (i == 0) leftPath.moveTo(x, y) else leftPath.lineTo(x, y)
                    drawCircle(color = ElectricCyan, radius = 4.dp.toPx(), center = Offset(x, y))
                }
                drawPath(
                    path = leftPath,
                    color = ElectricCyan,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Right Ear Path
                val rightPath = Path()
                rightEarLoss.forEachIndexed { i, loss ->
                    val x = i * stepX
                    val y = (loss.toFloat() / range) * size.height
                    if (i == 0) rightPath.moveTo(x, y) else rightPath.lineTo(x, y)
                    drawCircle(color = NeonPurple, radius = 4.dp.toPx(), center = Offset(x, y))
                }
                drawPath(
                    path = rightPath,
                    color = NeonPurple,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Frequency axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            frequencies.forEach { freq ->
                Text(
                    text = "$freq Hz",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
