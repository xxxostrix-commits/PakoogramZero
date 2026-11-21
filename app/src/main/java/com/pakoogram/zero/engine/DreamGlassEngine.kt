package com.pakoogram.zero.engine

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun Modifier.dreamGlass(
    cornerRadius: Dp = DreamConfig.glassCornerRadiusDp.dp,
    accentTint: Color? = null,
    accentTintAlpha: Float = 0.16f,
    interactive: Boolean = true,
    opacityScale: Float = 1f
): Modifier {
    var rootLeft by remember { mutableFloatStateOf(0f) }
    var rootTop by remember { mutableFloatStateOf(0f) }
    var rootRight by remember { mutableFloatStateOf(0f) }
    var rootBottom by remember { mutableFloatStateOf(0f) }

    var isPressed by remember { mutableStateOf(false) }
    var touchLocal by remember { mutableStateOf(Offset.Zero) }
    val clipPath = remember { Path() }

    val scale by animateFloatAsState(
        targetValue = if (interactive && isPressed) DreamConfig.pressScale else 1f,
        animationSpec = DreamMotion.pressSpring(),
        label = "dreamGlassPressScale"
    )

    val touchGlowAlpha by animateFloatAsState(
        targetValue = if (interactive && isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = if (isPressed) 80 else 280),
        label = "dreamGlassTouchGlow"
    )

    val touchModifier = if (interactive) {
        Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                var downPos = Offset.Zero
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull() ?: continue
                    if (change.pressed && !change.previousPressed) {
                        isPressed = true
                        downPos = change.position
                        touchLocal = change.position
                    } else if (change.pressed) {
                        touchLocal = change.position
                        if ((change.position - downPos).getDistance() > 28f) {
                            isPressed = false
                        }
                    } else {
                        isPressed = false
                    }
                }
            }
        }
    } else {
        Modifier
    }

    return this
        .onGloballyPositioned { coords ->
            val pos = coords.positionInRoot()
            rootLeft = pos.x
            rootTop = pos.y
            rootRight = pos.x + coords.size.width
            rootBottom = pos.y + coords.size.height
        }
        .then(touchModifier)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .drawWithContent {
            // РџСЂРёРІСЏР·С‹РІР°РµРј РїРµСЂРµСЂРёСЃРѕРІРєСѓ Рє 120 Р“С† С†РёРєР»Сѓ РЅРµР±Р° Р±РµР· СЂРµРєРѕРјРїРѕР·РёС†РёР№
            @Suppress("UNUSED_VARIABLE")
            val tick = DreamCloudsEngine.frameTick

            val rPx = min(cornerRadius.toPx(), min(size.width, size.height) * 0.5f)
            clipPath.reset()
            clipPath.addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, Size(size.width, size.height)),
                    cornerRadius = CornerRadius(rPx, rPx)
                )
            )

            val veilAlpha = (DreamConfig.glassVeilAlpha * opacityScale).coerceIn(0.1f, 0.92f)
            val pinkAlpha = (DreamConfig.glassPinkTint * opacityScale).coerceIn(0f, 0.6f)

            clipPath(clipPath) {
                // 1. РќР°СЃС‚РѕСЏС‰РёР№ СЂР°Р·РјС‹С‚С‹Р№ С„СЂР°РіРјРµРЅС‚ РїР»С‹РІСѓС‰РёС… РѕР±Р»Р°РєРѕРІ СЂРѕРІРЅРѕ РїРѕРґ РєР°СЂС‚РѕС‡РєРѕР№
                drawIntoCanvas { canvas ->
                    DreamCloudsEngine.drawBlurredSlice(
                        canvas = canvas.nativeCanvas,
                        rootLeft = rootLeft,
                        rootTop = rootTop,
                        rootRight = rootRight,
                        rootBottom = rootBottom,
                        localW = size.width,
                        localH = size.height
                    )
                }

                // 2. Р§РёСЃС‚Р°СЏ РјРѕР»РѕС‡РЅРѕ-РїР°СЃС‚РµР»СЊРЅР°СЏ РІСѓР°Р»СЊ (Р±РµР· С€СѓРјР°/Р·РµСЂРЅР°)
                drawRect(DreamPalette.GlassVeilWhite.copy(alpha = veilAlpha))
                drawRect(DreamPalette.GlassVeilPink.copy(alpha = pinkAlpha))
                drawRect(DreamPalette.GlassVeilBlue.copy(alpha = pinkAlpha * 0.45f))

                // 3. РќРµР¶РЅС‹Р№ С†РІРµС‚РЅРѕР№ РїРѕРґС‚РѕРЅ (С‚РѕР»СЊРєРѕ РґР»СЏ РєР°СЂС‚РѕС‡РµРє РІ СЃРїРёСЃРєРµ С‡Р°С‚РѕРІ)
                if (accentTint != null && accentTintAlpha > 0.005f) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accentTint.copy(alpha = accentTintAlpha * 1.15f),
                                accentTint.copy(alpha = accentTintAlpha * 0.35f),
                                Color.Transparent
                            )
                        )
                    )
                }

                // 4. РЎРІРµС‚РѕРІРѕР№ Р±Р»РёРє РІ С‚РѕС‡РєРµ РєР°СЃР°РЅРёСЏ РїР°Р»СЊС†Р°
                if (touchGlowAlpha > 0.01f) {
                    val glowRadius = max(140f, max(size.width, size.height) * 0.58f)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.48f * touchGlowAlpha),
                                DreamPalette.RimPinkWhite.copy(alpha = 0.18f * touchGlowAlpha),
                                Color.Transparent
                            ),
                            center = touchLocal,
                            radius = glowRadius
                        )
                    )
                }

                // 5. РЎР°Рј РєРѕРЅС‚РµРЅС‚ РєР°СЂС‚РѕС‡РєРё вЂ” 100% С‡С‘С‚РєРёР№, РїРѕРІРµСЂС… СЃС‚РµРєР»Р°
                this@drawWithContent.drawContent()
            }

            // 6. РџСЂСѓР¶РёРЅРЅС‹Р№ СЃРІРµС‚РѕРІРѕР№ Р±Р»РёРє РїРѕ РєСЂР°СЋ РіСЂР°РЅРё (1.15вЂ“1.55 dp)
            val rimStrength = DreamConfig.glassRimIntensity
            if (rimStrength > 0.01f) {
                val boost = DreamMotion.rimBoostPos.coerceIn(0f, 1.2f)
                val angleDeg = 32f + DreamMotion.rimOffsetPos * 32f * DreamConfig.glassRimSpringStrength
                val rad = angleDeg * (PI / 180.0)
                val cosA = cos(rad).toFloat()
                val sinA = sin(rad).toFloat()
                val halfSpan = (size.width + size.height) * 0.42f
                val cx = size.width * 0.5f
                val cy = size.height * 0.5f

                val rimAlpha = ((0.62f + 0.34f * min(1f, boost)) * rimStrength * opacityScale).coerceIn(0f, 1f)
                val strokePx = (1.15f + 0.4f * min(1f, boost)).dp.toPx()

                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            DreamPalette.RimPinkWhite.copy(alpha = 0.92f * rimAlpha),
                            DreamPalette.RimPureWhite.copy(alpha = 0.72f * rimAlpha),
                            DreamPalette.RimSkyLavender.copy(alpha = 0.86f * rimAlpha)
                        ),
                        start = Offset(cx - cosA * halfSpan, cy - sinA * halfSpan),
                        end = Offset(cx + cosA * halfSpan, cy + sinA * halfSpan)
                    ),
                    cornerRadius = CornerRadius(rPx, rPx),
                    style = Stroke(width = strokePx)
                )
            }
        }
}