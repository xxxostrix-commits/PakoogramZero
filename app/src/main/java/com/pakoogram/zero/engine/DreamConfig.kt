package com.pakoogram.zero.engine

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue

object DreamConfig {
    private const val PREFS_NAME = "pakoogram_dream_config_v1"

    // Р¤РёР·РёРєР° РїРµСЂРµС…РѕРґРѕРІ Рё РЅР°Р¶Р°С‚РёР№
    var springStiffness by mutableFloatStateOf(380f)
    var springDamping by mutableFloatStateOf(0.86f)
    var pressScale by mutableFloatStateOf(0.968f)
    var swipeSensitivity by mutableFloatStateOf(1.1f)
    var backDepthScale by mutableFloatStateOf(0.955f)

    // РњР°С‚РѕРІРѕРµ СЃС‚РµРєР»Рѕ
    var glassBlurRadius by mutableFloatStateOf(12f)
    var glassVeilAlpha by mutableFloatStateOf(0.56f)
    var glassPinkTint by mutableFloatStateOf(0.20f)
    var glassRimIntensity by mutableFloatStateOf(0.88f)
    var glassRimSpringStrength by mutableFloatStateOf(1.0f)
    var glassCornerRadiusDp by mutableFloatStateOf(26f)

    // РќРµР±Рѕ Рё РѕР±Р»Р°РєР°
    var cloudSpeed by mutableFloatStateOf(1.0f)
    var cloudParallax by mutableFloatStateOf(1.0f)
    var cloudOpacity by mutableFloatStateOf(0.94f)
    var skyWarmth by mutableFloatStateOf(0.35f)

    // 24-СЃРµРєС‚РѕСЂРЅС‹Р№ РѕСЂРµРѕР» РїСЂРѕС„РёР»СЏ
    var auraIntensity by mutableFloatStateOf(1.05f)
    var auraPulseSpeed by mutableFloatStateOf(1.0f)

    fun load(context: Context) {
        val p = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        springStiffness = p.getFloat("springStiffness", 380f)
        springDamping = p.getFloat("springDamping", 0.86f)
        pressScale = p.getFloat("pressScale", 0.968f)
        swipeSensitivity = p.getFloat("swipeSensitivity", 1.1f)
        backDepthScale = p.getFloat("backDepthScale", 0.955f)

        glassBlurRadius = p.getFloat("glassBlurRadius", 12f)
        glassVeilAlpha = p.getFloat("glassVeilAlpha", 0.56f)
        glassPinkTint = p.getFloat("glassPinkTint", 0.20f)
        glassRimIntensity = p.getFloat("glassRimIntensity", 0.88f)
        glassRimSpringStrength = p.getFloat("glassRimSpringStrength", 1.0f)
        glassCornerRadiusDp = p.getFloat("glassCornerRadiusDp", 26f)

        cloudSpeed = p.getFloat("cloudSpeed", 1.0f)
        cloudParallax = p.getFloat("cloudParallax", 1.0f)
        cloudOpacity = p.getFloat("cloudOpacity", 0.94f)
        skyWarmth = p.getFloat("skyWarmth", 0.35f)

        auraIntensity = p.getFloat("auraIntensity", 1.05f)
        auraPulseSpeed = p.getFloat("auraPulseSpeed", 1.0f)
    }

    fun save(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putFloat("springStiffness", springStiffness)
            .putFloat("springDamping", springDamping)
            .putFloat("pressScale", pressScale)
            .putFloat("swipeSensitivity", swipeSensitivity)
            .putFloat("backDepthScale", backDepthScale)
            .putFloat("glassBlurRadius", glassBlurRadius)
            .putFloat("glassVeilAlpha", glassVeilAlpha)
            .putFloat("glassPinkTint", glassPinkTint)
            .putFloat("glassRimIntensity", glassRimIntensity)
            .putFloat("glassRimSpringStrength", glassRimSpringStrength)
            .putFloat("glassCornerRadiusDp", glassCornerRadiusDp)
            .putFloat("cloudSpeed", cloudSpeed)
            .putFloat("cloudParallax", cloudParallax)
            .putFloat("cloudOpacity", cloudOpacity)
            .putFloat("skyWarmth", skyWarmth)
            .putFloat("auraIntensity", auraIntensity)
            .putFloat("auraPulseSpeed", auraPulseSpeed)
            .apply()
    }

    fun reset(context: Context) {
        springStiffness = 380f
        springDamping = 0.86f
        pressScale = 0.968f
        swipeSensitivity = 1.1f
        backDepthScale = 0.955f
        glassBlurRadius = 12f
        glassVeilAlpha = 0.56f
        glassPinkTint = 0.20f
        glassRimIntensity = 0.88f
        glassRimSpringStrength = 1.0f
        glassCornerRadiusDp = 26f
        cloudSpeed = 1.0f
        cloudParallax = 1.0f
        cloudOpacity = 0.94f
        skyWarmth = 0.35f
        auraIntensity = 1.05f
        auraPulseSpeed = 1.0f
        save(context)
    }
}