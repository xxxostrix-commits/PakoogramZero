package com.pakoogram.zero.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pakoogram.zero.engine.DreamConfig
import com.pakoogram.zero.engine.DreamPalette
import com.pakoogram.zero.engine.dreamGlass
import java.util.Locale

@Composable
fun DreamLabSheet(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Р¤РёР·РёРєР°", "РЎС‚РµРєР»Рѕ", "РќРµР±Рѕ", "РћСЂРµРѕР»")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .dreamGlass(cornerRadius = 28.dp, interactive = false, opacityScale = 1.15f)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Р›Р°Р±РѕСЂР°С‚РѕСЂРёСЏ Pakoogram",
                    color = DreamPalette.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Р’СЃРµ РёР·РјРµРЅРµРЅРёСЏ РїСЂРёРјРµРЅСЏСЋС‚СЃСЏ РјРіРЅРѕРІРµРЅРЅРѕ",
                    color = DreamPalette.TextSubtitle,
                    fontSize = 12.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .dreamGlass(cornerRadius = 18.dp, interactive = true)
                        .clip(CircleShape)
                        .clickable { DreamConfig.reset(context) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "РЎР±СЂРѕСЃ",
                        tint = DreamPalette.IconPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .dreamGlass(cornerRadius = 18.dp, interactive = true)
                        .clip(CircleShape)
                        .clickable {
                            DreamConfig.save(context)
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Р—Р°РєСЂС‹С‚СЊ",
                        tint = DreamPalette.IconPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabs.forEachIndexed { idx, label ->
                val selected = activeTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .dreamGlass(
                            cornerRadius = 16.dp,
                            accentTint = if (selected) DreamPalette.AccentLavender else null,
                            accentTintAlpha = if (selected) 0.28f else 0f,
                            interactive = true
                        )
                        .clickable { activeTab = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) DreamPalette.TextPrimary else DreamPalette.TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when (activeTab) {
                0 -> {
                    LabSlider("Р–С‘СЃС‚РєРѕСЃС‚СЊ РїСЂСѓР¶РёРЅС‹", DreamConfig.springStiffness, 150f..900f, "%.0f") {
                        DreamConfig.springStiffness = it
                        DreamConfig.save(context)
                    }
                    LabSlider("Р”РµРјРїС„РёСЂРѕРІР°РЅРёРµ (РѕС‚СЃРєРѕРє)", DreamConfig.springDamping, 0.55f..1.05f, "%.2f") {
                        DreamConfig.springDamping = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РЎР¶Р°С‚РёРµ РїСЂРё РЅР°Р¶Р°С‚РёРё", DreamConfig.pressScale, 0.92f..0.995f, "%.3f") {
                        DreamConfig.pressScale = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РРЅРµСЂС†РёСЏ СЃРІР°Р№РїР°", DreamConfig.swipeSensitivity, 0.5f..2.2f, "%.2fx") {
                        DreamConfig.swipeSensitivity = it
                        DreamConfig.save(context)
                    }
                    LabSlider("Р“Р»СѓР±РёРЅР° Р·Р°РґРЅРµРіРѕ СЌРєСЂР°РЅР°", DreamConfig.backDepthScale, 0.88f..0.99f, "%.3f") {
                        DreamConfig.backDepthScale = it
                        DreamConfig.save(context)
                    }
                }
                1 -> {
                    LabSlider("РЎРёР»Р° СЂР°Р·РјС‹С‚РёСЏ С„РѕРЅР°", DreamConfig.glassBlurRadius, 2f..22f, "%.0f") {
                        DreamConfig.glassBlurRadius = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РњРѕР»РѕС‡РЅР°СЏ РІСѓР°Р»СЊ", DreamConfig.glassVeilAlpha, 0.15f..0.85f, "%.2f") {
                        DreamConfig.glassVeilAlpha = it
                        DreamConfig.save(context)
                    }
                    LabSlider("Р РѕР·РѕРІС‹Р№ РїРѕРґС‚РѕРЅ", DreamConfig.glassPinkTint, 0.0f..0.55f, "%.2f") {
                        DreamConfig.glassPinkTint = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РЇСЂРєРѕСЃС‚СЊ Р±Р»РёРєРѕРІ РіСЂР°РЅРµР№", DreamConfig.glassRimIntensity, 0.0f..1.2f, "%.2f") {
                        DreamConfig.glassRimIntensity = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РџСЂСѓР¶РёРЅР° Р±Р»РёРєРѕРІ СЃРєСЂРѕР»Р»Р°", DreamConfig.glassRimSpringStrength, 0.0f..2.0f, "%.2f") {
                        DreamConfig.glassRimSpringStrength = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РЎРєСЂСѓРіР»РµРЅРёРµ РєР°СЂС‚РѕС‡РµРє", DreamConfig.glassCornerRadiusDp, 12f..34f, "%.0f dp") {
                        DreamConfig.glassCornerRadiusDp = it
                        DreamConfig.save(context)
                    }
                }
                2 -> {
                    LabSlider("РЎРєРѕСЂРѕСЃС‚СЊ РѕР±Р»Р°РєРѕРІ", DreamConfig.cloudSpeed, 0.0f..3.0f, "%.2fx") {
                        DreamConfig.cloudSpeed = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РџР°СЂР°Р»Р»Р°РєСЃ СЃРєСЂРѕР»Р»Р°", DreamConfig.cloudParallax, 0.0f..2.2f, "%.2fx") {
                        DreamConfig.cloudParallax = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РџР»РѕС‚РЅРѕСЃС‚СЊ РѕР±Р»Р°РєРѕРІ", DreamConfig.cloudOpacity, 0.3f..1.0f, "%.2f") {
                        DreamConfig.cloudOpacity = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РўРµРїР»РѕС‚Р° РЅРµР±Р° (СЂРѕР·РѕРІС‹Р№)", DreamConfig.skyWarmth, 0.0f..1.0f, "%.2f") {
                        DreamConfig.skyWarmth = it
                        DreamConfig.save(context)
                    }
                }
                else -> {
                    LabSlider("РЇСЂРєРѕСЃС‚СЊ 24-СЃРµРєС‚РѕСЂРЅРѕРіРѕ РѕСЂРµРѕР»Р°", DreamConfig.auraIntensity, 0.0f..1.6f, "%.2f") {
                        DreamConfig.auraIntensity = it
                        DreamConfig.save(context)
                    }
                    LabSlider("РЎРєРѕСЂРѕСЃС‚СЊ РґС‹С…Р°РЅРёСЏ РѕСЂРµРѕР»Р°", DreamConfig.auraPulseSpeed, 0.0f..2.5f, "%.2fx") {
                        DreamConfig.auraPulseSpeed = it
                        DreamConfig.save(context)
                    }
                }
            }
        }
    }
}

@Composable
private fun LabSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    format: String,
    onChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = DreamPalette.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
                text = String.format(Locale.US, format, value),
                color = DreamPalette.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = DreamPalette.AccentLavender,
                activeTrackColor = DreamPalette.AccentPink,
                inactiveTrackColor = DreamPalette.Divider
            )
        )
    }
}