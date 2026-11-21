package com.pakoogram.zero.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AmbilightAvatarAura(
    sectorColors: List<Color>,
    avatarSize: Dp,
    modifier: Modifier = Modifier
) {
    val auraSize = avatarSize * 1.92f
    Canvas(modifier = modifier.size(auraSize)) {
        @Suppress("UNUSED_VARIABLE")
        val tick = DreamCloudsEngine.frameTick
        val intensity = DreamConfig.auraIntensity
        if (intensity <= 0.01f || sectorColors.isEmpty()) return@Canvas

        val phase = DreamCloudsEngine.phase * 14f * DreamConfig.auraPulseSpeed
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val baseRing = avatarSize.toPx() * 0.52f
        val puffRadius = avatarSize.toPx() * 0.44f
        val sectors = 24

        for (i in 0 until sectors) {
            val angle = (i / sectors.toFloat()) * (PI * 2.0) + phase * 0.18
            val breathe = 0.88f + 0.14f * sin(phase * 1.4f + i * 0.55f).toFloat()
            val color = sectorColors[i % sectorColors.size]
            val sx = cx + cos(angle).toFloat() * baseRing * 0.86f
            val sy = cy + sin(angle).toFloat() * baseRing * 0.86f
            val r = puffRadius * breathe

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = (0.36f * intensity).coerceIn(0f, 0.72f)),
                        color.copy(alpha = (0.14f * intensity).coerceIn(0f, 0.35f)),
                        Color.Transparent
                    ),
                    center = Offset(sx, sy),
                    radius = r
                ),
                radius = r,
                center = Offset(sx, sy)
            )
        }
    }
}