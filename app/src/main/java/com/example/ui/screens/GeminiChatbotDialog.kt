package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.chat.ChatMessage
import com.example.data.chat.GeminiChatModel
import com.example.ui.utils.Loc
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatbotDialog(
    viewModel: AppViewModel,
    lang: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages by viewModel.chatMessages.collectAsState()
    val loading by viewModel.chatLoading.collectAsState()
    val currentModel by viewModel.chatModel.collectAsState()
    val useSearchGrounding by viewModel.chatUseSearchGrounding.collectAsState()
    val activeCulture by viewModel.storytellerCulture.collectAsState()
    val ttsSpeaking by viewModel.ttsSpeaking.collectAsState()
    val ttsCurrentId by viewModel.ttsCurrentSpokenId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var inputText by remember { mutableStateOf("") }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputText = spoken
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("gemini_chatbot_dialog"),
            color = Color(0xFF140727)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar: Merry Akyn
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B0E2E))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎸", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (lang == "Russian" || lang == "Русский") "Весёлый Акын" else "Merry Akyn",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (lang == "Russian" || lang == "Русский")
                                    "«Что вижу — о том и пою!» • ${Loc.getCultureName(activeCulture, lang)}"
                                else
                                    "\"What I see — of that I sing!\" • ${Loc.getCultureName(activeCulture, lang)}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (messages.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.clearChat() },
                                modifier = Modifier.size(32.dp).testTag("clear_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Clear",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("close_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Controls Row: Model Picker & Search Grounding Toggle
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.03f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Models Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GeminiChatModel.values().forEach { modelOption ->
                            val isSelected = currentModel == modelOption
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setChatModel(modelOption) },
                                label = {
                                    Text(
                                        text = when (modelOption) {
                                            GeminiChatModel.FLASH -> "⚡ 3.8 Flash"
                                            GeminiChatModel.ARGON -> "🧠 4 Argon"
                                            GeminiChatModel.FLASH_LITE -> "🚀 3.1 Lite"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF1E1035) else Color.White
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFD700),
                                    containerColor = Color.White.copy(alpha = 0.05f)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = Color(0xFFFFD700),
                                    borderColor = Color.White.copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.testTag("model_chip_${modelOption.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Google Search Grounding Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { viewModel.toggleChatSearchGrounding() }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Search Grounding",
                                tint = if (useSearchGrounding) Color(0xFF00FFCC) else Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == "Russian" || lang == "Русский")
                                    "Google Search Grounding (Актуальные данные)"
                                else
                                    "Google Search Grounding (Live Web Data)",
                                color = if (useSearchGrounding) Color(0xFF00FFCC) else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = if (useSearchGrounding) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Switch(
                            checked = useSearchGrounding,
                            onCheckedChange = { viewModel.toggleChatSearchGrounding() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF140727),
                                checkedTrackColor = Color(0xFF00FFCC),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.testTag("search_grounding_switch")
                        )
                    }
                }

                // Auth Account Status Banner
                val user = currentUser
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF24143D))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (user != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Authorized",
                                tint = Color(0xFF00FFCC),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == "Russian" || lang == "Русский")
                                    "Вход: ${user.displayName ?: user.email} (персональные байки)"
                                else
                                    "User: ${user.displayName ?: user.email} (personalized)",
                                color = Color(0xFF00FFCC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Guest",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == "Russian" || lang == "Русский")
                                    "Гостевой режим"
                                else
                                    "Guest mode",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                        TextButton(
                            onClick = {
                                val activity = context as? Activity
                                if (activity != null) {
                                    viewModel.signInWithGoogle(
                                        activity = activity,
                                        onSuccess = {
                                            Toast.makeText(context, "Google вход успешен!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { err ->
                                            Toast.makeText(context, "Ошибка входа: $err", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.testTag("dialog_google_signin_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Login,
                                    contentDescription = "Login",
                                    tint = Color(0xFF4285F4),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (lang == "Russian" || lang == "Русский") "Войти через Google" else "Sign in with Google",
                                    color = Color(0xFF4285F4),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Chat Messages Thread
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (messages.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD700).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎸", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (lang == "Russian" || lang == "Русский")
                                    "«Что вижу — о том и пою!»"
                                else
                                    "\"What I see — of that I sing!\"",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (lang == "Russian" || lang == "Русский")
                                    "Я — весёлый акын и народный сказитель! Спроси меня о чём угодно — на каждый запрос я отвечу максимально смешной житейской историей и народной байкой!"
                                else
                                    "I am a cheerful Akyn folk storyteller! Ask me anything — I will reply with a hilarious true-to-life story and cultural joke!",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                val isUser = msg.sender == "user"
                                val isSpeakingThis = ttsSpeaking && ttsCurrentId == "chat_${msg.id}"

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 16.dp
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isUser) Color(0xFFFFD700)
                                            else if (isSpeakingThis) Color(0xFF2C194D)
                                            else Color(0xFF1E1035)
                                        ),
                                        border = if (!isUser) BorderStroke(
                                            1.dp,
                                            if (isSpeakingThis) Color(0xFFFFD700) else Color.White.copy(alpha = 0.12f)
                                        ) else null,
                                        modifier = Modifier.widthIn(max = 300.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (isUser) (if (lang == "Russian" || lang == "Русский") "Вы" else "You")
                                                    else "👳‍♂️ " + Loc.getCultureName(activeCulture, lang),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (isUser) Color(0xFF1E1035) else Color(0xFFFFD700)
                                                )

                                                if (!isUser) {
                                                    IconButton(
                                                        onClick = { viewModel.narrateChatMessage(msg) },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isSpeakingThis) Icons.Default.Pause else Icons.Default.VolumeUp,
                                                            contentDescription = "Listen",
                                                            tint = Color(0xFFFFD700),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = msg.text,
                                                color = if (isUser) Color(0xFF140727) else Color.White,
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp
                                            )

                                            // Grounding Sources Badge if search was used
                                            if (!isUser && msg.isSearchGrounded && msg.searchSources.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .background(Color(0xFF00FFCC).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Grounded", tint = Color(0xFF00FFCC), modifier = Modifier.size(10.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Google Search: ${msg.searchSources.firstOrNull() ?: "Verified"}",
                                                        color = Color(0xFF00FFCC),
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (loading) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color(0xFFFFD700),
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (lang == "Russian" || lang == "Русский")
                                                "Старец подбирает мудрое слово..."
                                            else
                                                "The elder is pondering folk wisdom...",
                                            color = Color(0xFFFFD700),
                                            fontSize = 12.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B0E2E))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (lang == "Russian" || lang == "Русский") "Спросить у сказителя..." else "Ask the storyteller...",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (inputText.isNotBlank()) {
                                keyboardController?.hide()
                                viewModel.sendMessageToElder(inputText)
                                inputText = ""
                            }
                        })
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Voice Input Button
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, viewModel.ttsManager.getLocaleForLanguage(lang).toLanguageTag())
                                }
                                speechLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Voice input not available", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White.copy(alpha = 0.06f), CircleShape)
                            .testTag("chat_voice_mic_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice", tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                keyboardController?.hide()
                                viewModel.sendMessageToElder(inputText)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && !loading,
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (inputText.isNotBlank() && !loading) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f),
                                CircleShape
                            )
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() && !loading) Color(0xFF140727) else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
