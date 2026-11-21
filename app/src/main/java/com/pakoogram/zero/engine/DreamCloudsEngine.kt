package com.pakoogram.zero.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

object DreamCloudsEngine {
    private const val BLUR_SCALE = 8

    var frameTick by mutableLongStateOf(0L)
        private set

    var phase: Float = 0f
        private set

    private val skyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cloudPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blurDrawPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    private val skyRect = RectF()
    private val ovalRect = RectF()
    private val srcRect = Rect()
    private val dstRect = RectF()

    private var skyGradient: LinearGradient? = null
    private var cachedSkyW = -1f
    private var cachedSkyH = -1f
    private var cachedWarmth = -1f

    private var blurBitmap: Bitmap? = null
    private var blurCanvas: Canvas? = null
    private var blurPixels: IntArray = IntArray(0)
    private var blurTemp: IntArray = IntArray(0)
    private var viewportW = 0
    private var viewportH = 0
    private var lastBlurPhase = -1f
    private var lastBlurScroll = Float.NaN
    private var lastBlurRadius = -1
    private var lastBlurWarmth = -1f
    private var lastBlurOpacity = -1f

    private val farClouds = createLayer(5, 0x11A8C7E8L, 0.36f, 0.56f, 0.14f, 0.24f, 0.14f, 0.72f)
    private val midClouds = createLayer(7, 0x22E5B8CEL, 0.28f, 0.48f, 0.11f, 0.20f, 0.28f, 0.92f)
    private val nearClouds = createLayer(5, 0x33B8D8F8L, 0.24f, 0.42f, 0.09f, 0.16f, 0.45f, 1.02f)

    fun advanceFrame(dtSeconds: Float) {
        val dt = dtSeconds.coerceIn(0.001f, 0.05f)
        phase = (phase + dt * 0.012f * DreamConfig.cloudSpeed) % 1000f
        DreamMotion.stepPhysics(dt)
        frameTick++
    }

    fun drawAndRefreshBackdrop(canvas: Canvas, width: Float, height: Float) {
        if (width <= 1f || height <= 1f) return
        val scrollPx = DreamMotion.scrollOffsetPx * DreamConfig.cloudParallax
        val warmth = DreamConfig.skyWarmth
        val opacity = DreamConfig.cloudOpacity

        drawSkyAndClouds(canvas, width, height, phase, scrollPx, warmth, opacity)

        val w = width.roundToInt()
        val h = height.roundToInt()
        val blurR = DreamConfig.glassBlurRadius.roundToInt().coerceIn(1, 22)
        val needBlurUpdate = blurBitmap == null ||
            viewportW != w || viewportH != h ||
            abs(phase - lastBlurPhase) > 0.0018f ||
            lastBlurScroll.isNaN() || abs(scrollPx - lastBlurScroll) > 1.5f ||
            lastBlurRadius != blurR ||
            abs(warmth - lastBlurWarmth) > 0.01f ||
            abs(opacity - lastBlurOpacity) > 0.01f

        if (needBlurUpdate) {
            updateBlurBuffer(width, height, phase, scrollPx, warmth, opacity, w, h, blurR)
            lastBlurPhase = phase
            lastBlurScroll = scrollPx
            lastBlurRadius = blurR
            lastBlurWarmth = warmth
            lastBlurOpacity = opacity
        }
    }

    fun drawBlurredSlice(
        canvas: Canvas,
        rootLeft: Float,
        rootTop: Float,
        rootRight: Float,
        rootBottom: Float,
        localW: Float,
        localH: Float
    ) {
        val bmp = blurBitmap ?: return
        if (bmp.isRecycled || viewportW <= 0 || viewportH <= 0 || localW <= 1f || localH <= 1f) return

        val bw = bmp.width
        val bh = bmp.height
        val nL = (rootLeft / viewportW).coerceIn(0f, 1f)
        val nT = (rootTop / viewportH).coerceIn(0f, 1f)
        val nR = (rootRight / viewportW).coerceIn(0f, 1f)
        val nB = (rootBottom / viewportH).coerceIn(0f, 1f)

        val sL = (nL * bw).roundToInt().coerceIn(0, bw - 1)
        val sT = (nT * bh).roundToInt().coerceIn(0, bh - 1)
        val sR = max(sL + 1, (nR * bw).roundToInt().coerceIn(1, bw))
        val sB = max(sT + 1, (nB * bh).roundToInt().coerceIn(1, bh))

        srcRect.set(sL, sT, sR, sB)
        dstRect.set(0f, 0f, localW, localH)
        canvas.drawBitmap(bmp, srcRect, dstRect, blurDrawPaint)
    }

    private fun drawSkyAndClouds(
        canvas: Canvas,
        width: Float,
        height: Float,
        p: Float,
        scrollPx: Float,
        warmth: Float,
        opacity: Float
    ) {
        ensureSkyGradient(width, height, warmth)
        skyRect.set(0f, 0f, width, height)
        skyPaint.shader = skyGradient
        canvas.drawRect(skyRect, skyPaint)
        skyPaint.shader = null

        drawMistLayer(canvas, farClouds, width, height, p, scrollPx, 0.18f, 0.14f, opacity)
        drawVolumetricLayer(canvas, midClouds, width, height, p, scrollPx, 0.42f, 0.30f, opacity)
        drawNearLayer(canvas, nearClouds, width, height, p, scrollPx, 0.78f, 0.48f, opacity)
    }

    private fun ensureSkyGradient(width: Float, height: Float, warmth: Float) {
        if (skyGradient != null && cachedSkyW == width && cachedSkyH == height && abs(cachedWarmth - warmth) < 0.005f) {
            return
        }
        cachedSkyW = width
        cachedSkyH = height
        cachedWarmth = warmth
        val top = lerpArgb(DreamPalette.SKY_TOP_ARGB, DreamPalette.SKY_TOP_WARM_ARGB, warmth)
        val mid = lerpArgb(DreamPalette.SKY_MID_ARGB, DreamPalette.SKY_MID_WARM_ARGB, warmth)
        val horizon = lerpArgb(DreamPalette.SKY_HORIZON_ARGB, DreamPalette.SKY_HORIZON_WARM_ARGB, warmth)
        skyGradient = LinearGradient(
            0f, 0f, 0f, height,
            intArrayOf(top, mid, horizon),
            floatArrayOf(0f, 0.44f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    private fun drawMistLayer(
        canvas: Canvas,
        layer: Array<CloudNode>,
        width: Float,
        height: Float,
        p: Float,
        scrollPx: Float,
        speedMult: Float,
        parallax: Float,
        opacity: Float
    ) {
        val parallaxY = (-scrollPx * parallax) % (height * 0.6f)
        for (c in layer) {
            val cw = width * c.wFactor
            val ch = height * c.hFactor
            val span = width + cw * 1.8f
            val driftX = wrapDrift(p * c.driftSpeed * speedMult + c.phaseOffset * span, span) - cw * 0.9f
            val swayY = sin((p * 6f + c.phaseOffset) * PI * 2.0).toFloat() * (height * 0.006f)
            val baseY = c.anchorY * height + swayY + parallaxY

            for (i in 0 until c.puffCount) {
                val px = driftX + c.ox[i] * cw
                val py = baseY + c.oy[i] * ch
                val r = c.rad[i] * ch

                cloudPaint.color = withAlpha(DreamPalette.CLOUD_MIDTONE_ARGB, (0x45 * opacity).roundToInt())
                canvas.drawCircle(px, py + r * 0.2f, r * 1.18f, cloudPaint)
                cloudPaint.color = withAlpha(DreamPalette.CLOUD_HIGHLIGHT_ARGB, (0x52 * opacity).roundToInt())
                canvas.drawCircle(px, py - r * 0.14f, r * 1.08f, cloudPaint)
            }
        }
    }

    private fun drawVolumetricLayer(
        canvas: Canvas,
        layer: Array<CloudNode>,
        width: Float,
        height: Float,
        p: Float,
        scrollPx: Float,
        speedMult: Float,
        parallax: Float,
        opacity: Float
    ) {
        val parallaxY = (-scrollPx * parallax) % (height * 0.8f)
        for (c in layer) {
            val cw = width * c.wFactor
            val ch = height * c.hFactor
            val span = width + cw * 1.9f
            val driftX = wrapDrift(p * c.driftSpeed * speedMult + c.phaseOffset * span, span) - cw * 0.95f
            val swayY = sin((p * 7.5f + c.phaseOffset * 1.3f) * PI * 2.0).toFloat() * (height * 0.009f)
            val baseY = c.anchorY * height + swayY + parallaxY

            for (i in 0 until c.puffCount) {
                val px = driftX + c.ox[i] * cw
                val py = baseY + c.oy[i] * ch
                val r = c.rad[i] * ch

                // РќРёР¶РЅСЏСЏ СЃРёСЂРµРЅРµРІР°СЏ С‚РµРЅСЊ (РєР°Рє РІ СЂРµС„РµСЂРµРЅСЃРµ 4887.jpg)
                cloudPaint.color = withAlpha(DreamPalette.CLOUD_DEEP_SHADOW_ARGB, (0x62 * opacity).roundToInt())
                canvas.drawCircle(px + r * 0.12f, py + r * 0.44f, r * 0.92f, cloudPaint)

                cloudPaint.color = withAlpha(DreamPalette.CLOUD_SHADOW_ARGB, (0x82 * opacity).roundToInt())
                canvas.drawCircle(px, py + r * 0.30f, r * 0.98f, cloudPaint)

                // РџР°СЃС‚РµР»СЊРЅРѕ-СЂРѕР·РѕРІРѕРµ С‚РµР»Рѕ РѕР±Р»Р°РєР°
                cloudPaint.color = withAlpha(DreamPalette.CLOUD_BODY_ARGB, (0xCC * opacity).roundToInt())
                canvas.drawCircle(px, py + r * 0.02f, r * 0.95f, cloudPaint)

                // РЇСЂРєРёР№ СЂРѕР·РѕРІР°С‚Рѕ-Р±РµР»С‹Р№ РІРµСЂС…РЅРёР№ Р±Р»РёРє
                cloudPaint.color = withAlpha(DreamPalette.CLOUD_HIGHLIGHT_ARGB, (0xEE * opacity).roundToInt())
                canvas.drawCircle(px - r * 0.14f, py - r * 0.26f, r * 0.74f, cloudPaint)
            }
        }
    }

    private fun drawNearLayer(
        canvas: Canvas,
        layer: Array<CloudNode>,
        width: Float,
        height: Float,
        p: Float,
        scrollPx: Float,
        speedMult: Float,
        parallax: Float,
        opacity: Float
    ) {
        val parallaxY = (-scrollPx * parallax) % (height * 0.9f)
        for (c in layer) {
            val cw = width * c.wFactor
            val ch = height * c.hFactor
            val span = width + cw * 2.0f
            val driftX = wrapDrift(p * c.driftSpeed * speedMult + c.phaseOffset * span, span) - cw
            val swayY = sin((p * 9.5f + c.phaseOffset * 1.7f) * PI * 2.0).toFloat() * (height * 0.012f)
            val baseY = c.anchorY * height + swayY + parallaxY

            for (i in 0 until c.puffCount) {
                val px = driftX + c.ox[i] * cw
                val py = baseY + c.oy[i] * ch
                val rx = c.rad[i] * ch * 1.25f
                val ry = c.rad[i] * ch * 0.76f

                ovalRect.set(px - rx * 0.8f, py + ry * 0.14f, px + rx * 0.85f, py + ry * 0.96f)
                cloudPaint.color = withAlpha(DreamPalette.CLOUD_SHADOW_ARGB, (0x50 * opacity).roundToInt())
                canvas.drawOval(ovalRect, cloudPaint)

                ovalRect.set(px - rx, py - ry, px + rx, py + ry)
                cloudPaint.color = withAlpha(DreamPalette.CLOUD_HIGHLIGHT_ARGB, (0x92 * opacity).roundToInt())
                canvas.drawOval(ovalRect, cloudPaint)
            }
        }
    }

    private fun updateBlurBuffer(
        width: Float,
        height: Float,
        p: Float,
        scrollPx: Float,
        warmth: Float,
        opacity: Float,
        vpW: Int,
        vpH: Int,
        blurRadius: Int
    ) {
        viewportW = vpW
        viewportH = vpH
        val bw = max(2, vpW / BLUR_SCALE)
        val bh = max(2, vpH / BLUR_SCALE)
        val size = bw * bh

        if (blurBitmap == null || blurBitmap!!.isRecycled || blurBitmap!!.width != bw || blurBitmap!!.height != bh) {
            // GC releases old bitmap safely
            blurBitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888)
            blurCanvas = Canvas(blurBitmap!!)
            blurPixels = IntArray(size)
            blurTemp = IntArray(size)
        }

        val bmp = blurBitmap!!
        val bc = blurCanvas!!
        bmp.eraseColor(Color.TRANSPARENT)
        val save = bc.save()
        bc.scale(1f / BLUR_SCALE, 1f / BLUR_SCALE)
        drawSkyAndClouds(bc, width, height, p, scrollPx, warmth, opacity)
        bc.restoreToCount(save)

        // Р‘С‹СЃС‚СЂС‹Р№ 2-РїСЂРѕС…РѕРґРЅС‹Р№ Р»РёРЅРµР№РЅС‹Р№ Box Blur O(N) Р±РµР· Р°Р»Р»РѕРєР°С†РёР№ (Р°РїРїСЂРѕРєСЃРёРјР°С†РёСЏ Р“Р°СѓСЃСЃР°)
        val r = (blurRadius / 2).coerceIn(1, 14)
        bmp.getPixels(blurPixels, 0, bw, 0, 0, bw, bh)
        boxBlurHorizontal(blurPixels, blurTemp, bw, bh, r)
        boxBlurVertical(blurTemp, blurPixels, bw, bh, r)
        boxBlurHorizontal(blurPixels, blurTemp, bw, bh, max(1, r - 1))
        boxBlurVertical(blurTemp, blurPixels, bw, bh, max(1, r - 1))
        bmp.setPixels(blurPixels, 0, bw, 0, 0, bw, bh)
    }

    private fun boxBlurHorizontal(src: IntArray, dst: IntArray, w: Int, h: Int, r: Int) {
        val div = r * 2 + 1
        for (y in 0 until h) {
            val row = y * w
            var rSum = 0
            var gSum = 0
            var bSum = 0
            for (i in -r..r) {
                val c = src[row + i.coerceIn(0, w - 1)]
                rSum += (c shr 16) and 0xFF
                gSum += (c shr 8) and 0xFF
                bSum += c and 0xFF
            }
            for (x in 0 until w) {
                dst[row + x] = (0xFF shl 24) or ((rSum / div) shl 16) or ((gSum / div) shl 8) or (bSum / div)
                val cAdd = src[row + min(w - 1, x + r + 1)]
                val cRem = src[row + max(0, x - r)]
                rSum += ((cAdd shr 16) and 0xFF) - ((cRem shr 16) and 0xFF)
                gSum += ((cAdd shr 8) and 0xFF) - ((cRem shr 8) and 0xFF)
                bSum += (cAdd and 0xFF) - (cRem and 0xFF)
            }
        }
    }

    private fun boxBlurVertical(src: IntArray, dst: IntArray, w: Int, h: Int, r: Int) {
        val div = r * 2 + 1
        for (x in 0 until w) {
            var rSum = 0
            var gSum = 0
            var bSum = 0
            for (i in -r..r) {
                val c = src[i.coerceIn(0, h - 1) * w + x]
                rSum += (c shr 16) and 0xFF
                gSum += (c shr 8) and 0xFF
                bSum += c and 0xFF
            }
            for (y in 0 until h) {
                dst[y * w + x] = (0xFF shl 24) or ((rSum / div) shl 16) or ((gSum / div) shl 8) or (bSum / div)
                val cAdd = src[min(h - 1, y + r + 1) * w + x]
                val cRem = src[max(0, y - r) * w + x]
                rSum += ((cAdd shr 16) and 0xFF) - ((cRem shr 16) and 0xFF)
                gSum += ((cAdd shr 8) and 0xFF) - ((cRem shr 8) and 0xFF)
                bSum += (cAdd and 0xFF) - (cRem and 0xFF)
            }
        }
    }

    private fun createLayer(
        count: Int,
        seed: Long,
        minW: Float,
        maxW: Float,
        minH: Float,
        maxH: Float,
        minY: Float,
        maxY: Float
    ): Array<CloudNode> {
        val rnd = Random(seed)
        return Array(count) { idx ->
            val puffs = 5 + rnd.nextInt(3)
            val ox = FloatArray(puffs)
            val oy = FloatArray(puffs)
            val rad = FloatArray(puffs)
            for (p in 0 until puffs) {
                val t = if (puffs > 1) p / (puffs - 1f) else 0.5f
                val dome = sin(t * PI).toFloat()
                ox[p] = -0.38f + t * 0.76f + (rnd.nextFloat() - 0.5f) * 0.06f
                oy[p] = 0.08f - dome * 0.24f + (rnd.nextFloat() - 0.5f) * 0.06f
                rad[p] = (0.34f + 0.18f * dome) * (0.85f + 0.30f * rnd.nextFloat())
            }
            val frac = idx / max(1, count - 1).toFloat()
            val anchorY = lerpF(minY, maxY, frac) + (rnd.nextFloat() - 0.5f) * 0.05f
            CloudNode(
                wFactor = lerpF(minW, maxW, rnd.nextFloat()),
                hFactor = lerpF(minH, maxH, rnd.nextFloat()),
                anchorY = anchorY.coerceIn(0.08f, 1.05f),
                phaseOffset = idx / count.toFloat() + rnd.nextFloat() * 0.12f,
                driftSpeed = 900f + rnd.nextFloat() * 550f,
                puffCount = puffs,
                ox = ox,
                oy = oy,
                rad = rad
            )
        }
    }

    private fun wrapDrift(distance: Float, span: Float): Float {
        if (span <= 1f) return 0f
        var m = distance % span
        if (m < 0f) m += span
        return m
    }

    private fun withAlpha(argb: Int, alpha: Int): Int {
        val a = alpha.coerceIn(0, 255)
        return (a shl 24) or (argb and 0x00FFFFFF)
    }

    private fun lerpArgb(c1: Int, c2: Int, t: Float): Int {
        val k = t.coerceIn(0f, 1f)
        val r = lerpF(((c1 shr 16) and 0xFF).toFloat(), ((c2 shr 16) and 0xFF).toFloat(), k).roundToInt()
        val g = lerpF(((c1 shr 8) and 0xFF).toFloat(), ((c2 shr 8) and 0xFF).toFloat(), k).roundToInt()
        val b = lerpF((c1 and 0xFF).toFloat(), (c2 and 0xFF).toFloat(), k).roundToInt()
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }

    private class CloudNode(
        val wFactor: Float,
        val hFactor: Float,
        val anchorY: Float,
        val phaseOffset: Float,
        val driftSpeed: Float,
        val puffCount: Int,
        val ox: FloatArray,
        val oy: FloatArray,
        val rad: FloatArray
    )
}

@Composable
fun DreamSkyBackground(modifier: Modifier = Modifier, driveAnimation: Boolean = true) {
    if (driveAnimation) LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { now ->
                if (lastNanos != 0L) {
                    val dt = (now - lastNanos) / 1_000_000_000f
                    DreamCloudsEngine.advanceFrame(dt)
                }
                lastNanos = now
            }
        }
    }

    ComposeCanvas(modifier = modifier.fillMaxSize()) {
        // Р§РёС‚Р°РµРј frameTick РІРЅСѓС‚СЂРё С„Р°Р·С‹ РѕС‚СЂРёСЃРѕРІРєРё -> 120 FPS Р±РµР· СЂРµРєРѕРјРїРѕР·РёС†РёР№
        @Suppress("UNUSED_VARIABLE")
        val tick = DreamCloudsEngine.frameTick
        drawIntoCanvas { canvas ->
            DreamCloudsEngine.drawAndRefreshBackdrop(canvas.nativeCanvas, size.width, size.height)
        }
    }
}