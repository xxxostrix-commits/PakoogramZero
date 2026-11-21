package com.pakoogram.zero.engine

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs
import kotlin.math.exp

object DreamMotion {
    var scrollOffsetPx by mutableFloatStateOf(0f)
        private set

    // РџСЂСѓР¶РёРЅР° Р±Р»РёРєРѕРІ РЅР° СЃС‚РµРєР»СЏРЅРЅС‹С… РіСЂР°РЅСЏС…
    var rimOffsetPos by mutableFloatStateOf(0f)
        private set
    var rimBoostPos by mutableFloatStateOf(0f)
        private set

    private var rimOffsetTarget = 0f
    private var rimOffsetVel = 0f
    private var rimBoostTarget = 0f
    private var rimBoostVel = 0f

    private const val RIM_OMEGA = 14.5f
    private const val RIM_ZETA = 0.72f

    fun <T> transitionSpring(): SpringSpec<T> = spring(
        dampingRatio = DreamConfig.springDamping.coerceIn(0.45f, 1.2f),
        stiffness = DreamConfig.springStiffness.coerceIn(100f, 1200f)
    )

    fun pressSpring(): SpringSpec<Float> = spring(
        dampingRatio = 0.72f,
        stiffness = 620f
    )

    fun onScrollDelta(deltaY: Float) {
        if (deltaY.isNaN() || deltaY.isInfinite()) return
        scrollOffsetPx += deltaY
        val norm = (deltaY / 42f).coerceIn(-1.6f, 1.6f)
        rimOffsetTarget = (rimOffsetTarget + norm * 0.55f).coerceIn(-1.1f, 1.1f)
        rimBoostTarget = maxOf(rimBoostTarget, abs(norm)).coerceIn(0f, 1.1f)
    }

    fun stepPhysics(dtSeconds: Float) {
        val dt = dtSeconds.coerceIn(0.001f, 0.04f)
        rimOffsetTarget *= exp(-8.5f * dt)
        rimBoostTarget *= exp(-7.2f * dt)

        // РџСЂСѓР¶РёРЅР° СѓРіР»Р° Р±Р»РёРєР°
        val disp1 = rimOffsetPos - rimOffsetTarget
        val accel1 = -RIM_OMEGA * RIM_OMEGA * disp1 - 2f * RIM_ZETA * RIM_OMEGA * rimOffsetVel
        rimOffsetVel += accel1 * dt
        rimOffsetPos += rimOffsetVel * dt

        // РџСЂСѓР¶РёРЅР° СЏСЂРєРѕСЃС‚Рё РіСЂР°РЅРё
        val omega2 = RIM_OMEGA * 0.9f
        val disp2 = rimBoostPos - rimBoostTarget
        val accel2 = -omega2 * omega2 * disp2 - 2f * RIM_ZETA * omega2 * rimBoostVel
        rimBoostVel += accel2 * dt
        rimBoostPos = (rimBoostPos + rimBoostVel * dt).coerceIn(0f, 1.25f)
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y != 0f) {
                onScrollDelta(-available.y * 0.65f)
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (consumed.y != 0f) {
                onScrollDelta(-consumed.y * 0.35f)
            }
            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (abs(available.y) > 80f) {
                onScrollDelta((-available.y / 65f).coerceIn(-48f, 48f))
            }
            return Velocity.Zero
        }
    }
}

fun lerpF(start: Float, end: Float, t: Float): Float = start + (end - start) * t