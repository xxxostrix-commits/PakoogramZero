package com.pakoogram.zero.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Reply
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pakoogram.zero.engine.AmbilightAvatarAura
import com.pakoogram.zero.engine.DreamConfig
import com.pakoogram.zero.engine.DreamMotion
import com.pakoogram.zero.engine.DreamPalette
import com.pakoogram.zero.engine.DreamSkyBackground
import com.pakoogram.zero.engine.dreamGlass
import com.pakoogram.zero.engine.lerpF
import kotlinx.coroutines.launch

data class DemoMessage(
    val id: Long,
    val text: String,
    val time: String,
    val outgoing: Boolean,
    val replyTo: String? = null
)

data class DemoChat(
    val id: Long,
    val name: String,
    val username: String,
    val phone: String,
    val bio: String,
    val status: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val avatarColors: List<Color>,
    val initialMessages: List<DemoMessage>
)

private val sampleChats = listOf(
    DemoChat(
        id = 1,
        name = "Pakoogram Lab",
        username = "@pakoogram_zero",
        phone = "+7 (999) 420-12-00",
        bio = "Р§РёСЃС‚С‹Р№ РєР»РёРµРЅС‚ РЅР° Kotlin + Compose. 120 FPS, РѕР±Р»Р°РєР° Рё Р¶РёРІРѕРµ СЃС‚РµРєР»Рѕ.",
        status = "РІ СЃРµС‚Рё",
        lastMessage = "РџРѕРїСЂРѕР±СѓР№ СЃРІР°Р№РїРЅСѓС‚СЊ С‡Р°С‚ СЃ СЂР°Р·РЅРѕР№ СЃРєРѕСЂРѕСЃС‚СЊСЋ вњЁ",
        time = "15:42",
        unreadCount = 2,
        avatarColors = listOf(Color(0xFF8BA4FF), Color(0xFFF3A7D8), Color(0xFFB594FF)),
        initialMessages = listOf(
            DemoMessage(1, "Р”РѕР±СЂРѕ РїРѕР¶Р°Р»РѕРІР°С‚СЊ РІ Pakoogram Zero! Р­С‚Рѕ СѓР¶Рµ РЅРµ Р»РµРіР°СЃРё-РјРѕРґ, Р° С‡РёСЃС‚С‹Р№ РґРІРёР¶РѕРє СЃ РЅСѓР»СЏ.", "15:40", false),
            DemoMessage(2, "РЎС‚РµРєР»Рѕ СЂРµР°Р»СЊРЅРѕ СЂР°Р·РјС‹РІР°РµС‚ РїР»С‹РІСѓС‰РёРµ РѕР±Р»Р°РєР° РїРѕР·Р°РґРё РєР°СЂС‚РѕС‡РµРє, Р° С‚РµРєСЃС‚ РѕСЃС‚Р°С‘С‚СЃСЏ С‡С‘С‚РєРёРј.", "15:41", false),
            DemoMessage(3, "Р’С‹РіР»СЏРґРёС‚ РЅРµСЂРµР°Р»СЊРЅРѕ РїР»Р°РІРЅРѕ! РЎРµР№С‡Р°СЃ РѕС‚РєСЂРѕСЋ РїРѕР»Р·СѓРЅРєРё СЃРІРµСЂС…Сѓ Рё РїРѕРєСЂСѓС‡Сѓ РїСЂСѓР¶РёРЅС‹.", "15:41", true),
            DemoMessage(4, "РџРѕРїСЂРѕР±СѓР№ СЃРІР°Р№РїРЅСѓС‚СЊ Р»СЋР±РѕРµ СЃРѕРѕР±С‰РµРЅРёРµ РІР»РµРІРѕ (РѕС‚РІРµС‚) РёР»Рё СЃР°Рј С‡Р°С‚ РІРїСЂР°РІРѕ СЃ СЂР°Р·РЅРѕР№ СЃРєРѕСЂРѕСЃС‚СЊСЋ вњЁ", "15:42", false)
        )
    ),
    DemoChat(
        id = 2,
        name = "РђР»РёРЅР°",
        username = "@alina_sky",
        phone = "+7 (917) 310-44-18",
        bio = "Dreamcore, РїР»С‘РЅРєР° Рё СЂРѕР·РѕРІС‹Рµ Р·Р°РєР°С‚С‹",
        status = "Р±С‹Р»Р° 3 РјРёРЅ РЅР°Р·Р°Рґ",
        lastMessage = "РЎРєРёРЅСЊ РїРѕС‚РѕРј СЌС‚Сѓ РЅРѕРІСѓСЋ СЃР±РѕСЂРєСѓ, РѕР±Р»Р°РєР° РєР°Р№С„",
        time = "15:18",
        unreadCount = 1,
        avatarColors = listOf(Color(0xFFFF9EC9), Color(0xFFFFC599), Color(0xFFE09EFF)),
        initialMessages = listOf(
            DemoMessage(10, "РўС‹ СЂРµР°Р»СЊРЅРѕ СЂРµС€РёР» РЅР°РїРёСЃР°С‚СЊ СЃРІРѕР№ РєР»РёРµРЅС‚ СЃ РЅСѓР»СЏ?", "15:15", false),
            DemoMessage(11, "Р”Р°, РЅР° СЃС‚Р°СЂРѕРј РєРѕРґРµ РўР“ РєР°СЂС‚РѕС‡РєРё СѓР»РµС‚Р°Р»Рё РїСЂРё СЃРєСЂРѕР»Р»Рµ, Р° С‚СѓС‚ РІСЃС‘ СЃРІРѕС‘.", "15:17", true),
            DemoMessage(12, "РЎРєРёРЅСЊ РїРѕС‚РѕРј СЌС‚Сѓ РЅРѕРІСѓСЋ СЃР±РѕСЂРєСѓ, РѕР±Р»Р°РєР° РєР°Р№С„", "15:18", false)
        )
    ),
    DemoChat(
        id = 3,
        name = "РњР°РєСЃ (Terraria & GD)",
        username = "@max_infernum",
        phone = "+7 (927) 881-09-52",
        bio = "Calamity Infernum вЂў KOCMOC progress",
        status = "РІ СЃРµС‚Рё",
        lastMessage = "Р“Рѕ РІРµС‡РµСЂРѕРј РІ РјР°Р№РЅ РёР»Рё С‚РµСЃС‚РёС‚СЊ РЅРѕРІС‹Р№ Р±РёР»Рґ",
        time = "14:55",
        unreadCount = 0,
        avatarColors = listOf(Color(0xFF6DD5FA), Color(0xFF8E9EFF), Color(0xFF68E1C8)),
        initialMessages = listOf(
            DemoMessage(20, "Р“Рѕ РІРµС‡РµСЂРѕРј РІ РјР°Р№РЅ РёР»Рё С‚РµСЃС‚РёС‚СЊ РЅРѕРІС‹Р№ Р±РёР»Рґ", "14:55", false),
            DemoMessage(21, "Р©Р°СЃ РґРѕРєСЂСѓС‡Сѓ С„РёР·РёРєСѓ СЃРІР°Р№РїР° РІ Р›Р°Р±РѕСЂР°С‚РѕСЂРёРё Рё Р·Р°Р»РµС‡Сѓ!", "14:56", true)
        )
    ),
    DemoChat(
        id = 4,
        name = "РР·Р±СЂР°РЅРЅРѕРµ",
        username = "@stlma",
        phone = "РЎРєСЂС‹С‚Рѕ",
        bio = "Р—Р°РјРµС‚РєРё, РёРґРµРё Рё С€РµР№РґРµСЂС‹ РґР»СЏ Pakoogram",
        status = "РѕР±Р»Р°С‡РЅРѕРµ С…СЂР°РЅРёР»РёС‰Рµ",
        lastMessage = "Р­С‚Р°Рї 2: РїРѕРґРєР»СЋС‡РёС‚СЊ СЂРµР°Р»СЊРЅС‹Р№ TDLib + РІС…РѕРґ",
        time = "12:10",
        unreadCount = 0,
        avatarColors = listOf(Color(0xFF7D8FE0), Color(0xFF9AC5FF), Color(0xFFD8A7F5)),
        initialMessages = listOf(
            DemoMessage(30, "РџР°Р»РёС‚СЂР° РёР· СЂРµС„РµСЂРµРЅСЃР°: #5A6DB8 -> #7F92D6 -> #EBCBE2", "12:08", true),
            DemoMessage(31, "Р­С‚Р°Рї 2: РїРѕРґРєР»СЋС‡РёС‚СЊ СЂРµР°Р»СЊРЅС‹Р№ TDLib + РІС…РѕРґ", "12:10", true)
        )
    ),
    DemoChat(
        id = 5,
        name = "Dreamcore FM",
        username = "@dreamcore_fm",
        phone = "РљР°РЅР°Р»",
        bio = "Slowed + reverb С‚СЂРµРєРё РґР»СЏ РЅРѕС‡РЅРѕРіРѕ РєРѕРґРёРЅРіР°",
        status = "14.2K РїРѕРґРїРёСЃС‡РёРєРѕРІ",
        lastMessage = "РќРѕРІС‹Р№ РїР»РµР№Р»РёСЃС‚: Clouds Above Everything",
        time = "Р’С‡РµСЂР°",
        unreadCount = 4,
        avatarColors = listOf(Color(0xFFC79BFF), Color(0xFFFFA8DC), Color(0xFF8AA8FF)),
        initialMessages = listOf(
            DemoMessage(40, "РќРѕРІС‹Р№ РїР»РµР№Р»РёСЃС‚: Clouds Above Everything", "Р’С‡РµСЂР°", false)
        )
    )
)

@Composable
fun PakoogramApp() {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    var rootSize by remember { mutableStateOf(IntSize.Zero) }
    var activeChat by remember { mutableStateOf<DemoChat?>(null) }
    var sourceCardRect by remember { mutableStateOf<Rect?>(null) }
    var showLab by remember { mutableStateOf(false) }

    // 0f = РЎРїРёСЃРѕРє С‡Р°С‚РѕРІ, 1f = Р§Р°С‚ РѕС‚РєСЂС‹С‚
    val chatProgress = remember { Animatable(0f) }
    // 0f = Р§Р°С‚, 1f = РџСЂРѕС„РёР»СЊ РѕС‚РєСЂС‹С‚
    val profileProgress = remember { Animatable(0f) }

    val messagesByChat = remember {
        mutableMapOf<Long, MutableList<DemoMessage>>().apply {
            sampleChats.forEach { put(it.id, mutableStateListOf(*it.initialMessages.toTypedArray())) }
        }
    }

    fun closeProfile(velocity: Float = 0f) {
        scope.launch {
            profileProgress.animateTo(
                targetValue = 0f,
                animationSpec = DreamMotion.transitionSpring(),
                initialVelocity = velocity
            )
        }
    }

    fun closeChat(velocity: Float = 0f) {
        scope.launch {
            chatProgress.animateTo(
                targetValue = 0f,
                animationSpec = DreamMotion.transitionSpring(),
                initialVelocity = velocity
            )
            if (chatProgress.value < 0.02f) {
                activeChat = null
            }
        }
    }

    BackHandler(enabled = showLab || profileProgress.value > 0.05f || activeChat != null) {
        when {
            showLab -> showLab = false
            profileProgress.value > 0.05f -> closeProfile()
            activeChat != null -> closeChat()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { rootSize = it }
    ) {
        // Р•РґРёРЅРѕРµ Р¶РёРІРѕРµ РЅРµР±Рѕ Рё 3 СЃР»РѕСЏ РѕР±СЉС‘РјРЅС‹С… РѕР±Р»Р°РєРѕРІ РґР»СЏ РІСЃРµРіРѕ РїСЂРёР»РѕР¶РµРЅРёСЏ
        DreamSkyBackground()

        val wPx = rootSize.width.toFloat().coerceAtLeast(1f)
        val hPx = rootSize.height.toFloat().coerceAtLeast(1f)
        val cProg = chatProgress.value.coerceIn(0f, 1.15f)
        val pProg = profileProgress.value.coerceIn(0f, 1.15f)

        // 1. Р“Р»Р°РІРЅС‹Р№ СЌРєСЂР°РЅ вЂ” РЎРїРёСЃРѕРє С‡Р°С‚РѕРІ (СѓС…РѕРґРёС‚ РІ РіР»СѓР±РёРЅСѓ РїСЂРё РѕС‚РєСЂС‹С‚РёРё С‡Р°С‚Р°)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val depthT = cProg.coerceIn(0f, 1f)
                    val s = lerpF(1f, DreamConfig.backDepthScale, depthT)
                    scaleX = s
                    scaleY = s
                    translationX = -wPx * 0.06f * depthT
                    alpha = lerpF(1f, 0.78f, depthT)
                }
        ) {
            ChatListScreen(
                chats = sampleChats,
                onOpenLab = { showLab = !showLab },
                onChatClick = { chat, rect ->
                    activeChat = chat
                    sourceCardRect = rect
                    scope.launch {
                        chatProgress.animateTo(1f, DreamMotion.transitionSpring())
                    }
                }
            )
        }

        // 2. Р­РєСЂР°РЅ С‡Р°С‚Р° СЃ РЅР°СЃС‚РѕСЏС‰РёРј Container Transform + СЃРІР°Р№РїРѕРј РїРѕ СЃРєРѕСЂРѕСЃС‚Рё РїР°Р»СЊС†Р°
        val currentChat = activeChat
        if (currentChat != null && (cProg > 0.002f || chatProgress.isRunning)) {
            val startRect = sourceCardRect ?: Rect(24f, hPx * 0.35f, wPx - 24f, hPx * 0.48f)
            val t = cProg.coerceIn(0f, 1f)
            val curLeft = lerpF(startRect.left, 0f, t)
            val curTop = lerpF(startRect.top, 0f, t)
            val curRight = lerpF(startRect.right, wPx, t)
            val curBottom = lerpF(startRect.bottom, hPx, t)
            val curW = (curRight - curLeft).coerceAtLeast(1f)
            val uniformScale = (curW / wPx).coerceAtLeast(0.1f)
            val cornerDp = lerpF(DreamConfig.glassCornerRadiusDp, 0f, t)

            val chatDepthScale = lerpF(1f, DreamConfig.backDepthScale, pProg.coerceIn(0f, 1f))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0f, 0f)
                        translationX = curLeft - wPx * 0.05f * pProg.coerceIn(0f, 1f)
                        translationY = curTop
                        scaleX = uniformScale * chatDepthScale
                        scaleY = uniformScale * chatDepthScale
                        shape = RoundedCornerShape((cornerDp / uniformScale).dp)
                        clip = t < 0.995f
                        alpha = ((cProg - 0.02f) / 0.22f).coerceIn(0f, 1f)
                    }
                    .pointerInput(currentChat.id) {
                        val velocityTracker = VelocityTracker()
                        detectHorizontalDragGestures(
                            onDragStart = { velocityTracker.resetTracking() },
                            onHorizontalDrag = { change, dragAmount ->
                                if (profileProgress.value > 0.05f) return@detectHorizontalDragGestures
                                velocityTracker.addPosition(change.uptimeMillis, change.position)
                                val deltaProgress = -dragAmount / wPx
                                scope.launch {
                                    chatProgress.snapTo((chatProgress.value + deltaProgress).coerceIn(0f, 1f))
                                }
                            },
                            onDragEnd = {
                                if (profileProgress.value > 0.05f) return@detectHorizontalDragGestures
                                val vx = velocityTracker.calculateVelocity().x
                                val normVel = (-vx / wPx) * DreamConfig.swipeSensitivity
                                val shouldClose = chatProgress.value < 0.68f || vx > 750f
                                if (shouldClose) {
                                    closeChat(velocity = normVel)
                                } else {
                                    scope.launch {
                                        chatProgress.animateTo(
                                            targetValue = 1f,
                                            animationSpec = DreamMotion.transitionSpring(),
                                            initialVelocity = normVel
                                        )
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch {
                                    chatProgress.animateTo(1f, DreamMotion.transitionSpring())
                                }
                            }
                        )
                    }
            ) {
                // РџР»РѕС‚РЅР°СЏ РЅРµР±РµСЃРЅР°СЏ РїРѕРґР»РѕР¶РєР°, С‡С‚РѕР±С‹ СЃРїРёСЃРѕРє С‡Р°С‚РѕРІ РЅРµ РїСЂРѕСЃРІРµС‡РёРІР°Р» СЃРєРІРѕР·СЊ РїРµСЂРµРїРёСЃРєСѓ
                if (t > 0.01f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(DreamPalette.SKY_TOP_ARGB).copy(alpha = 0.25f * t),
                                        Color(DreamPalette.SKY_HORIZON_ARGB).copy(alpha = 0.22f * t)
                                    )
                                )
                            )
                    )
                }

                val msgList = messagesByChat[currentChat.id] ?: remember { mutableStateListOf() }
                ChatScreen(
                    chat = currentChat,
                    messages = msgList,
                    onBack = { closeChat() },
                    onOpenProfile = {
                        scope.launch {
                            profileProgress.animateTo(1f, DreamMotion.transitionSpring())
                        }
                    },
                    onOpenLab = { showLab = !showLab },
                    onSendMessage = { text, reply ->
                        msgList.add(
                            DemoMessage(
                                id = System.currentTimeMillis(),
                                text = text,
                                time = "РЎРµР№С‡Р°СЃ",
                                outgoing = true,
                                replyTo = reply
                            )
                        )
                    }
                )
            }
        }

        // 3. Р­РєСЂР°РЅ РїСЂРѕС„РёР»СЏ (С‡РёСЃС‚РѕРµ РЅРµР±Рѕ + 24-СЃРµРєС‚РѕСЂРЅС‹Р№ РѕСЂРµРѕР» + РЅР°С‚РёРІРЅРѕРµ СЃС‚РµРєР»Рѕ, СЃРІР°Р№Рї РїРѕ СЃРєРѕСЂРѕСЃС‚Рё)
        if (currentChat != null && (pProg > 0.002f || profileProgress.isRunning)) {
            val pt = pProg.coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = wPx * (1f - pt)
                        val r = with(density) { lerpF(DreamConfig.glassCornerRadiusDp, 0f, pt).dp }
                        shape = RoundedCornerShape(r)
                        clip = pt < 0.995f
                    }
                    .pointerInput(currentChat.id) {
                        val velocityTracker = VelocityTracker()
                        detectHorizontalDragGestures(
                            onDragStart = { velocityTracker.resetTracking() },
                            onHorizontalDrag = { change, dragAmount ->
                                velocityTracker.addPosition(change.uptimeMillis, change.position)
                                val delta = -dragAmount / wPx
                                scope.launch {
                                    profileProgress.snapTo((profileProgress.value + delta).coerceIn(0f, 1f))
                                }
                            },
                            onDragEnd = {
                                val vx = velocityTracker.calculateVelocity().x
                                val normVel = (-vx / wPx) * DreamConfig.swipeSensitivity
                                if (profileProgress.value < 0.7f || vx > 700f) {
                                    closeProfile(velocity = normVel)
                                } else {
                                    scope.launch {
                                        profileProgress.animateTo(
                                            targetValue = 1f,
                                            animationSpec = DreamMotion.transitionSpring(),
                                            initialVelocity = normVel
                                        )
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch {
                                    profileProgress.animateTo(1f, DreamMotion.transitionSpring())
                                }
                            }
                        )
                    }
            ) {
                // РќРµРїСЂРѕР·СЂР°С‡РЅС‹Р№ РЅРµР±РµСЃРЅС‹Р№ С„РѕРЅ РїСЂРѕС„РёР»СЏ, С‡С‚РѕР±С‹ С‡Р°С‚ РЅРёРєРѕРіРґР° РЅРµ РїСЂРѕСЃРІРµС‡РёРІР°Р» СЃРєРІРѕР·СЊ РЅРµРіРѕ
                DreamSkyBackground()
                ProfileScreen(
                    chat = currentChat,
                    onBack = { closeProfile() },
                    onOpenLab = { showLab = !showLab }
                )
            }
        }

        // 4. РџР»Р°РІР°СЋС‰Р°СЏ СЃС‚РµРєР»СЏРЅРЅР°СЏ В«Р›Р°Р±РѕСЂР°С‚РѕСЂРёСЏ PakoogramВ» РґР»СЏ live-РЅР°СЃС‚СЂРѕР№РєРё РІСЃРµРіРѕ
        AnimatedVisibility(
            visible = showLab,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            DreamLabSheet(onClose = { showLab = false })
        }
    }
}

@Composable
private fun ChatListScreen(
    chats: List<DemoChat>,
    onOpenLab: () -> Unit,
    onChatClick: (DemoChat, Rect) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // РЎС‚РµРєР»СЏРЅРЅР°СЏ С€Р°РїРєР° СЃРїРёСЃРєР° С‡Р°С‚РѕРІ
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .dreamGlass(cornerRadius = 24.dp, interactive = false)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Pakoogram Zero",
                    color = DreamPalette.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "120 FPS вЂў Dream Glass Engine",
                    color = DreamPalette.TextSubtitle,
                    fontSize = 12.sp
                )
            }
            GlassIconButton(icon = Icons.Rounded.Tune, contentDesc = "РќР°СЃС‚СЂРѕР№РєРё РґРІРёР¶РєР°", onClick = onOpenLab)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(DreamMotion.nestedScrollConnection),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chats, key = { it.id }) { chat ->
                var cardBounds by remember { mutableStateOf(Rect.Zero) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            val p = coords.positionInRoot()
                            cardBounds = Rect(p.x, p.y, p.x + coords.size.width, p.y + coords.size.height)
                        }
                        .dreamGlass(
                            cornerRadius = DreamConfig.glassCornerRadiusDp.dp,
                            accentTint = chat.avatarColors.first(),
                            accentTintAlpha = 0.18f,
                            interactive = true
                        )
                        .clickable { onChatClick(chat, cardBounds) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarCircle(name = chat.name, colors = chat.avatarColors, size = 54.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = chat.name,
                                color = DreamPalette.TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = chat.time,
                                color = DreamPalette.TextSubtitle,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = chat.lastMessage,
                                color = DreamPalette.TextSecondary,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (chat.unreadCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(DreamPalette.AccentLavender, DreamPalette.AccentPink)
                                            )
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = chat.unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(
    chat: DemoChat,
    messages: List<DemoMessage>,
    onBack: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLab: () -> Unit,
    onSendMessage: (String, String?) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var replyQuote by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // РЎС‚РµРєР»СЏРЅРЅР°СЏ С€Р°РїРєР° С‡Р°С‚Р° (РЅР°Р¶Р°С‚РёРµ РѕС‚РєСЂС‹РІР°РµС‚ РџСЂРѕС„РёР»СЊ)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .dreamGlass(cornerRadius = 24.dp, interactive = false)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(icon = Icons.AutoMirrored.Rounded.ArrowBack, contentDesc = "РќР°Р·Р°Рґ", onClick = onBack)
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenProfile() }
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(name = chat.name, colors = chat.avatarColors, size = 42.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = chat.name,
                        color = DreamPalette.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${chat.status} вЂў РЅР°Р¶РјРё РґР»СЏ РїСЂРѕС„РёР»СЏ",
                        color = DreamPalette.TextSubtitle,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
            GlassIconButton(icon = Icons.Rounded.Tune, contentDesc = "РќР°СЃС‚СЂРѕР№РєРё", onClick = onOpenLab)
        }

        // Р›РµРЅС‚Р° СЃРѕРѕР±С‰РµРЅРёР№ СЃРѕ СЃРІР°Р№Рї-РѕС‚РІРµС‚РѕРј РЅР° РїСЂСѓР¶РёРЅРµ
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .nestedScroll(DreamMotion.nestedScrollConnection),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                SwipeableMessageBubble(
                    message = msg,
                    onReply = { replyQuote = msg.text }
                )
            }
        }

        // РџР»Р°С€РєР° РѕС‚РІРµС‚Р° (РµСЃР»Рё СЃРІР°Р№РїРЅСѓР»Рё СЃРѕРѕР±С‰РµРЅРёРµ)
        if (replyQuote != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .dreamGlass(cornerRadius = 18.dp, accentTint = DreamPalette.AccentLavender, accentTintAlpha = 0.16f)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Reply,
                    contentDescription = null,
                    tint = DreamPalette.AccentLavender,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = replyQuote ?: "",
                    color = DreamPalette.TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "вњ•",
                    color = DreamPalette.TextSubtitle,
                    modifier = Modifier
                        .clickable { replyQuote = null }
                        .padding(horizontal = 6.dp)
                )
            }
        }

        // РЎС‚РµРєР»СЏРЅРЅРѕРµ РїРѕР»Рµ РІРІРѕРґР°
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .dreamGlass(cornerRadius = 26.dp, interactive = false)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (inputText.isEmpty()) {
                    Text(
                        text = "РЎРѕРѕР±С‰РµРЅРёРµ...",
                        color = DreamPalette.TextSubtitle,
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = TextStyle(color = DreamPalette.TextPrimary, fontSize = 15.sp),
                    cursorBrush = SolidColor(DreamPalette.AccentLavender),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(DreamPalette.AccentLavender, DreamPalette.AccentPink)
                        )
                    )
                    .clickable {
                        val clean = inputText.trim()
                        if (clean.isNotEmpty()) {
                            onSendMessage(clean, replyQuote)
                            inputText = ""
                            replyQuote = null
                            scope.launch {
                                listState.animateScrollToItem(messages.size)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "РћС‚РїСЂР°РІРёС‚СЊ",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SwipeableMessageBubble(
    message: DemoMessage,
    onReply: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val swipeX = remember { Animatable(0f) }
    var triggered by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(message.id) {
                detectHorizontalDragGestures(
                    onDragStart = { triggered = false },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount < 0f || swipeX.value < 0f) {
                            val next = (swipeX.value + dragAmount * 0.55f).coerceIn(-160f, 0f)
                            scope.launch { swipeX.snapTo(next) }
                            if (next < -95f && !triggered) {
                                triggered = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                    },
                    onDragEnd = {
                        if (triggered) onReply()
                        scope.launch { swipeX.animateTo(0f, DreamMotion.transitionSpring()) }
                    },
                    onDragCancel = {
                        scope.launch { swipeX.animateTo(0f, DreamMotion.transitionSpring()) }
                    }
                )
            }
    ) {
        if (swipeX.value < -12f) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Reply,
                contentDescription = null,
                tint = DreamPalette.IconPrimary.copy(alpha = (-swipeX.value / 110f).coerceIn(0f, 1f)),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
                    .size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(if (message.outgoing) Alignment.CenterEnd else Alignment.CenterStart)
                .graphicsLayer { translationX = swipeX.value }
                .fillMaxWidth(0.82f)
                .dreamGlass(
                    cornerRadius = 22.dp,
                    accentTint = if (message.outgoing) DreamPalette.AccentLavender else DreamPalette.AccentPink,
                    accentTintAlpha = if (message.outgoing) 0.16f else 0.08f,
                    interactive = true
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (message.replyTo != null) {
                Text(
                    text = "в†Є ${message.replyTo}",
                    color = DreamPalette.AccentLavender,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
            }
            Text(
                text = message.text,
                color = DreamPalette.TextPrimary,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = message.time, color = DreamPalette.TextSubtitle, fontSize = 11.sp)
                if (message.outgoing) {
                    Icon(
                        imageVector = Icons.Rounded.DoneAll,
                        contentDescription = null,
                        tint = DreamPalette.AccentLavender,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    chat: DemoChat,
    onBack: () -> Unit,
    onOpenLab: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(icon = Icons.AutoMirrored.Rounded.ArrowBack, contentDesc = "РќР°Р·Р°Рґ", onClick = onBack)
            GlassIconButton(icon = Icons.Rounded.Tune, contentDesc = "РќР°СЃС‚СЂРѕР№РєРё", onClick = onOpenLab)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(DreamMotion.nestedScrollConnection),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // РђРІР°С‚Р°СЂРєР° СЃ 24-СЃРµРєС‚РѕСЂРЅС‹Рј РѕСЂРµРѕР»РѕРј (С„РѕРЅ РїСЂРѕС„РёР»СЏ РѕСЃС‚Р°С‘С‚СЃСЏ С‡РёСЃС‚С‹Рј РЅРµР±РѕРј)
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        AmbilightAvatarAura(sectorColors = chat.avatarColors, avatarSize = 104.dp)
                        AvatarCircle(name = chat.name, colors = chat.avatarColors, size = 104.dp)
                    }
                    Text(
                        text = chat.name,
                        color = DreamPalette.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = chat.status,
                        color = DreamPalette.TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            // 4 РѕС‚РґРµР»СЊРЅС‹Рµ СЃС‚РµРєР»СЏРЅРЅС‹Рµ РєРЅРѕРїРєРё РґРµР№СЃС‚РІРёР№ (РЅР°РјРµСЂС‚РІРѕ РїСЂРёРІСЏР·Р°РЅС‹ Рє РІС‘СЂСЃС‚РєРµ!)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileActionGlassButton(Icons.Rounded.Call, "РџРѕР·РІРѕРЅРёС‚СЊ", Modifier.weight(1f))
                    ProfileActionGlassButton(Icons.Rounded.Videocam, "Р’РёРґРµРѕ", Modifier.weight(1f))
                    ProfileActionGlassButton(Icons.Rounded.NotificationsOff, "Р—РІСѓРє", Modifier.weight(1f))
                    ProfileActionGlassButton(Icons.Rounded.MoreHoriz, "Р•С‰С‘", Modifier.weight(1f))
                }
            }

            // РЎС‚РµРєР»СЏРЅРЅР°СЏ РєР°СЂС‚РѕС‡РєР° СЃ РёРЅС„РѕР№ (С‚РµР»РµС„РѕРЅ, СЋР·РµСЂРЅРµР№Рј, Р±РёРѕ)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .dreamGlass(cornerRadius = DreamConfig.glassCornerRadiusDp.dp, interactive = false)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileInfoRow(Icons.Rounded.Phone, chat.phone, "РўРµР»РµС„РѕРЅ")
                    HorizontalDivider(color = DreamPalette.Divider)
                    ProfileInfoRow(Icons.Rounded.AlternateEmail, chat.username, "РРјСЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ")
                    HorizontalDivider(color = DreamPalette.Divider)
                    ProfileInfoRow(Icons.Rounded.Info, chat.bio, "Рћ СЃРµР±Рµ")
                }
            }

            // РЎС‚РµРєР»СЏРЅРЅС‹Р№ Р±Р»РѕРє РјРµРґРёР°
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .dreamGlass(cornerRadius = DreamConfig.glassCornerRadiusDp.dp, interactive = false)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "РњРµРґРёР° Рё РІР»РѕР¶РµРЅРёСЏ",
                        color = DreamPalette.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(3) { idx ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(86.dp)
                                    .dreamGlass(
                                        cornerRadius = 18.dp,
                                        accentTint = chat.avatarColors[idx % chat.avatarColors.size],
                                        accentTintAlpha = 0.22f,
                                        interactive = true
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Image,
                                    contentDescription = null,
                                    tint = DreamPalette.IconPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileActionGlassButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .dreamGlass(cornerRadius = 20.dp, interactive = true)
            .clickable { }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = DreamPalette.IconPrimary,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = DreamPalette.TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DreamPalette.AccentLavender,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = value, color = DreamPalette.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = label, color = DreamPalette.TextSubtitle, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: ImageVector,
    contentDesc: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .dreamGlass(cornerRadius = 21.dp, interactive = true)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = DreamPalette.IconPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AvatarCircle(name: String, colors: List<Color>, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            color = Color.White,
            fontSize = (size.value * 0.38f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}