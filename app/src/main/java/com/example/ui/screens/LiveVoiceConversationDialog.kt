package com.example.ui.screens

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.chat.LiveVoiceState
import com.example.ui.viewmodel.AppViewModel
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun LiveVoiceConversationDialog(
    viewModel: AppViewModel,
    lang: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val liveState by viewModel.liveVoiceState.collectAsState()
    var typedInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val isRussian = lang == "Russian" || lang == "Русский"

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (!spokenText.isNullOrBlank()) {
            viewModel.sendLiveVoiceQuery(spokenText)
        } else {
            Toast.makeText(
                context,
                if (isRussian) "Речь не распознана" else "Speech not recognized",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startLiveVoiceSession()
    }

    Dialog(
        onDismissRequest = {
            viewModel.stopLiveVoiceSession()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 20.dp)
                .testTag("live_voice_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F061E),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFF00FFCC))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0x3300FFCC),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Live Voice Icon",
                                    tint = Color(0xFF00FFCC)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isRussian) "Живая Беседа со Старцем" else "Live Voice with Elder",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0x33FFD700)
                                ) {
                                    Text(
                                        text = "LIVE API • gemini-3.8-live",
                                        color = Color(0xFFFFD700),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            viewModel.stopLiveVoiceSession()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("live_voice_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Live Voice",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Scrollable content area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Animated Glowing Orb / Visualizer
                    LiveVoiceVisualizerOrb(
                        state = liveState,
                        modifier = Modifier
                            .size(190.dp)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status Indicator
                    val statusText = when {
                        liveState.isSpeaking -> if (isRussian) "Старец говорит..." else "Elder is speaking..."
                        liveState.isProcessing -> if (isRussian) "Старец размышляет (gemini-3.8-live)..." else "Elder is thinking (gemini-3.8-live)..."
                        liveState.isListening -> if (isRussian) "Старец слушает вас..." else "Elder is listening..."
                        else -> if (isRussian) "Нажмите на микрофон для беседы" else "Tap microphone to converse"
                    }

                    val statusColor = when {
                        liveState.isSpeaking -> Color(0xFF00FFCC)
                        liveState.isProcessing -> Color(0xFFFFD700)
                        liveState.isListening -> Color(0xFFFF5252)
                        else -> Color(0xFFB0B0C0)
                    }

                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Error notice if any
                    liveState.errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x33FF5252),
                            border = BorderStroke(1.dp, Color(0xFFFF5252))
                        ) {
                            Text(
                                text = err,
                                color = Color(0xFFFF8A80),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Last spoken user utterance
                    if (liveState.lastUserSpokenText.isNotBlank()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF23173A),
                            border = BorderStroke(1.dp, Color(0x3300FFCC))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = Color(0xFF00FFCC),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isRussian) "Вы сказали:" else "You said:",
                                        fontSize = 11.sp,
                                        color = Color(0xFF00FFCC),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = liveState.lastUserSpokenText,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Elder's spoken response
                    if (liveState.lastModelSpokenResponse.isNotBlank()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E1035),
                            border = BorderStroke(1.dp, Color(0x44FFD700))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.SelfImprovement,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isRussian) "Мудрый Старец (gemini-3.8-live)" else "Wise Elder (gemini-3.8-live)",
                                            fontSize = 12.sp,
                                            color = Color(0xFFFFD700),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Narration replay button
                                    IconButton(
                                        onClick = {
                                            viewModel.narrateChatMessage(liveState.lastModelSpokenResponse)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (liveState.isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                            contentDescription = "Replay Narration",
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = liveState.lastModelSpokenResponse,
                                    fontSize = 14.sp,
                                    color = Color(0xFFF0E6D2),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Voice Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big Mic Button
                    Button(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE,
                                    if (isRussian) "ru-RU" else "en-US"
                                )
                                putExtra(
                                    RecognizerIntent.EXTRA_PROMPT,
                                    if (isRussian) "Спросите Старца голосом..." else "Speak to the Elder..."
                                )
                            }
                            try {
                                speechLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    if (isRussian) "Голосовой ввод недоступен: ${e.message}" else "Voice input unavailable: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .testTag("live_voice_mic_button")
                            .height(56.dp)
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color(0xFF1E1035)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speak to Elder",
                            tint = Color(0xFF1E1035),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRussian) "Говорить со Старцем" else "Speak to Elder",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Stop/Interrupt Button
                    FilledTonalIconButton(
                        onClick = {
                            viewModel.stopLiveVoiceSession()
                        },
                        modifier = Modifier
                            .testTag("live_voice_stop_button")
                            .size(56.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0x33FF5252),
                            contentColor = Color(0xFFFF5252)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Alternative typed input bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = typedInput,
                        onValueChange = { typedInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("live_voice_text_input"),
                        placeholder = {
                            Text(
                                text = if (isRussian) "Или напишите старцу..." else "Or type to elder...",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FFCC),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00FFCC)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (typedInput.isNotBlank()) {
                                    val text = typedInput.trim()
                                    typedInput = ""
                                    keyboardController?.hide()
                                    viewModel.sendLiveVoiceQuery(text)
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (typedInput.isNotBlank()) {
                                val text = typedInput.trim()
                                typedInput = ""
                                keyboardController?.hide()
                                viewModel.sendLiveVoiceQuery(text)
                            }
                        },
                        modifier = Modifier
                            .testTag("live_voice_send_button")
                            .size(48.dp)
                            .background(Color(0xFF00FFCC), CircleShape),
                        enabled = typedInput.isNotBlank() && !liveState.isProcessing
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send to Live Elder",
                            tint = Color(0xFF0F061E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mystical glowing orb and soundwave visualizer responding to Live Voice states.
 */
@Composable
fun LiveVoiceVisualizerOrb(
    state: LiveVoiceState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    // Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Wave phase animation for soundbars
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    // Secondary ring alpha
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_alpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 3.4f

            val primaryColor = when {
                state.isSpeaking -> Color(0xFF00FFCC)
                state.isListening -> Color(0xFFFF5252)
                state.isProcessing -> Color(0xFFFFD700)
                else -> Color(0xFF7B1FA2)
            }

            val secondaryColor = when {
                state.isSpeaking -> Color(0xFFFFD700)
                state.isListening -> Color(0xFFFFB300)
                state.isProcessing -> Color(0xFF00FFCC)
                else -> Color(0xFF4A148C)
            }

            // Outer ethereal glow ring
            drawCircle(
                color = secondaryColor.copy(alpha = ringAlpha * 0.35f),
                radius = baseRadius * 1.55f * (if (state.isSpeaking || state.isListening) pulseScale else 1f),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Middle pulsed ring
            drawCircle(
                color = primaryColor.copy(alpha = ringAlpha * 0.65f),
                radius = baseRadius * 1.25f * (if (state.isSpeaking || state.isListening) pulseScale else 1f),
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Center glowing mystic orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor,
                        Color(0xFF0F061E)
                    ),
                    center = center,
                    radius = baseRadius * (if (state.isSpeaking || state.isListening) pulseScale else 1f)
                ),
                radius = baseRadius * (if (state.isSpeaking || state.isListening) pulseScale else 1f),
                center = center
            )

            // Soundwave bars if speaking or listening
            if (state.isSpeaking || state.isListening || state.isProcessing) {
                val barCount = 18
                val barWidth = 3.dp.toPx()
                val totalWidth = barCount * (barWidth + 4.dp.toPx())
                val startX = center.x - totalWidth / 2f

                for (i in 0 until barCount) {
                    val normalizedIndex = i.toFloat() / barCount
                    val sinVal = sin(normalizedIndex * 2 * PI + wavePhase).toFloat()
                    val barHeight = (baseRadius * 0.5f) * (0.3f + 0.7f * kotlin.math.abs(sinVal))
                    val x = startX + i * (barWidth + 4.dp.toPx())
                    val y1 = center.y - barHeight / 2f
                    val y2 = center.y + barHeight / 2f

                    drawLine(
                        color = Color.White.copy(alpha = 0.85f),
                        start = Offset(x, y1),
                        end = Offset(x, y2),
                        strokeWidth = barWidth
                    )
                }
            }
        }

        // Center icon if idle
        if (!state.isSpeaking && !state.isListening && !state.isProcessing) {
            Icon(
                imageVector = Icons.Default.RecordVoiceOver,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(44.dp)
                    .scale(pulseScale)
            )
        }
    }
}
