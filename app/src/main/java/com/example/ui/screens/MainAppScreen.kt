package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.vector.ImageVector
import android.annotation.SuppressLint
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.database.NewsDigest
import com.example.data.database.RadioStation
import com.example.data.database.SavedJoke
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.utils.Loc
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: AppViewModel,
    onNavigateBackToLanguages: () -> Unit
) {
    val context = LocalContext.current
    val activeLanguage by viewModel.selectedLanguage.collectAsState()
    val activeStyle by viewModel.backgroundStyle.collectAsState()

    var currentTab by remember { mutableStateOf(0) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showStyleDialog by remember { mutableStateOf(false) }

    val languagesList = listOf(
        "English" to "🇬🇧",
        "Russian" to "🇷🇺",
        "Spanish" to "🇪🇸",
        "Chinese" to "🇨🇳",
        "Hindi" to "🇮🇳",
        "Arabic" to "🇦🇪",
        "French" to "🇫🇷",
        "Portuguese" to "🇵🇹",
        "German" to "🇩🇪",
        "Japanese" to "🇯🇵"
    )

    val tabTitles = listOf(
        Loc.get("tab_storyteller", activeLanguage),
        Loc.get("tab_news", activeLanguage),
        Loc.get("tab_radio", activeLanguage),
        Loc.get("tab_saved", activeLanguage)
    )

    if (showStyleDialog) {
        StyleSettingsDialog(
            viewModel = viewModel,
            lang = activeLanguage,
            onDismiss = { showStyleDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StyledBackground(activeStyle)

        Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "App Logo",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "Folklore",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (activeLanguage == "Russian" || activeLanguage == "Русский") "Анекдоты & Радио" else "Anecdotes & Radio",
                                fontSize = 11.sp,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showStyleDialog = true },
                        modifier = Modifier.testTag("style_switch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Switch Visual Style",
                            tint = Color(0xFFFFD700)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showLanguageMenu = true },
                            modifier = Modifier.testTag("language_switch_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Switch Language",
                                tint = Color(0xFFFFD700)
                            )
                        }
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false },
                            modifier = Modifier.background(Color(0xFF1A1A2E))
                        ) {
                            languagesList.forEach { (lang, flag) ->
                                DropdownMenuItem(
                                    text = {
                                        Row {
                                            Text(text = flag, modifier = Modifier.padding(end = 8.dp))
                                            Text(text = lang, color = Color.White)
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectLanguage(lang)
                                        showLanguageMenu = false
                                        Toast.makeText(context, "Language: $lang", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F061E)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F061E),
                tonalElevation = 8.dp
            ) {
                val icons = listOf(
                    Icons.Default.Forum,
                    Icons.AutoMirrored.Filled.TrendingUp,
                    Icons.Default.Radio,
                    Icons.Default.Star
                )

                tabTitles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = title
                            )
                        },
                        label = { Text(text = title, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1035),
                            selectedTextColor = Color(0xFFFFD700),
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f),
                            indicatorColor = Color(0xFFFFD700)
                        ),
                        modifier = Modifier.testTag("nav_tab_$index")
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Global HTML5 Audio Streaming Player (hidden WebView background engine)
            Html5AudioPlayer(viewModel)

            // Main content tabs
            when (currentTab) {
                0 -> StorytellerTab(viewModel, activeLanguage)
                1 -> NewsTrendsTab(viewModel, activeLanguage)
                2 -> RadioTab(viewModel, activeLanguage)
                3 -> FavoritesTab(viewModel, activeLanguage)
            }

            // Persistent Bottom Media Player above navigation bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                PersistentBottomMediaPlayer(viewModel, activeLanguage)
            }
        }
    }
    }
}

@Composable
fun StorytellerTab(viewModel: AppViewModel, lang: String) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val loading by viewModel.storytellerLoading.collectAsState()
    val intro by viewModel.storytellerIntro.collectAsState()
    val jokes by viewModel.storytellerJokes.collectAsState()
    val outro by viewModel.storytellerOutro.collectAsState()
    val activeCulture by viewModel.storytellerCulture.collectAsState()
    val situation by viewModel.storytellerSituation.collectAsState()
    val apiError by viewModel.storytellerError.collectAsState()

    val cultures = listOf(
        "Jewish" to "✡️",
        "Uzbek" to "🇺🇿",
        "Armenian" to "🇦🇲",
        "Chukchi" to "🏹",
        "Yakut" to "❄️",
        "Kamchatka" to "🌋",
        "American" to "🤠",
        "African" to "🦁"
    )

    // Funny loading texts to keep user entertained
    val loadingPhrases = Loc.getLoadingPhrases(lang)

    var loadingText by remember { mutableStateOf(loadingPhrases.firstOrNull() ?: "") }
    LaunchedEffect(loading) {
        if (loading) {
            var idx = 0
            while (loading) {
                loadingText = loadingPhrases[idx % loadingPhrases.size]
                delay(2500)
                idx++
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero campfire banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_storyteller_banner_1783385481811),
                    contentDescription = "Wise Storyteller Campfire",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )
                Text(
                    text = Loc.get("storyteller_quote", lang),
                    color = Color(0xFFFFD700),
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Culture selector title
        item {
            Text(
                text = Loc.get("select_folklore_style", lang),
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Horizontal scrolling cultures list
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cultures) { (culture, emoji) ->
                    val isSelected = activeCulture.lowercase() == culture.lowercase()
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStorytellerCulture(culture) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = emoji, modifier = Modifier.padding(end = 4.dp))
                                Text(
                                    text = Loc.getCultureName(culture, lang),
                                    color = if (isSelected) Color(0xFF1E1035) else Color.White
                                )
                            }
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
                        modifier = Modifier.testTag("culture_chip_$culture")
                    )
                }
            }
        }

        // Custom situation field
        item {
            OutlinedTextField(
                value = situation,
                onValueChange = { viewModel.setStorytellerSituation(it) },
                label = {
                    Text(
                        text = Loc.get("situation_label", lang),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                placeholder = {
                    Text(
                        text = Loc.get("situation_placeholder", lang),
                        color = Color.White.copy(alpha = 0.3f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("situation_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
        }

        // Tell Jokes Button
        item {
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.generate20Jokes()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_jokes_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color(0xFF1E1035)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color(0xFF1E1035), modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = "Tell")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Loc.get("tell_jokes_btn", lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Loading and results block
        if (loading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFD700), modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = loadingText,
                        color = Color(0xFFFFD700),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Notice if offline / fallback was triggered
            if (apiError != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1F1F)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.WifiOff, contentDescription = "Offline", tint = Color.Red)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = Loc.get("offline_notice", lang),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Introduction speech bubble
            if (intro.isNotBlank()) {
                item {
                    StorytellerSpeechBubble(text = intro, title = Loc.get("storyteller_says", lang))
                }
            }

            // The Jokes
            itemsIndexed(jokes) { index, joke ->
                JokeCard(
                    index = index + 1,
                    text = joke,
                    culture = activeCulture,
                    situation = situation,
                    viewModel = viewModel,
                    lang = lang
                )
            }

            // Outro speech bubble
            if (outro.isNotBlank()) {
                item {
                    StorytellerSpeechBubble(text = outro, title = Loc.get("parting_words", lang))
                }
            }
        }

        // Bottom space for continuous player
        item {
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

@Composable
fun StorytellerSpeechBubble(text: String, title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1035)),
        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp),
        border = BoxBorderDefaults(Color(0xFFFFD700).copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
fun JokeCard(
    index: Int,
    text: String,
    culture: String,
    situation: String,
    viewModel: AppViewModel,
    lang: String
) {
    val context = LocalContext.current
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        viewModel.isJokeSavedState(text) { saved ->
            isSaved = saved
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("joke_card_$index"),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFFFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = index.toString(),
                            color = Color(0xFF1E1035),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Loc.get("folk_saying_header", lang),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row {
                    // Copy action
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("joke", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, Loc.get("copied_toast", lang), Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White.copy(alpha = 0.6f))
                    }

                    // Share action
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Joke"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White.copy(alpha = 0.6f))
                    }

                    // Favorite action
                    IconButton(
                        onClick = {
                            viewModel.toggleSaveJoke(text, culture, situation)
                            isSaved = !isSaved
                            Toast.makeText(
                                context,
                                if (isSaved) Loc.get("added_favorites", lang)
                                else Loc.get("removed_favorites", lang),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.testTag("favorite_button_$index")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun NewsTrendsTab(viewModel: AppViewModel, lang: String) {
    val loading by viewModel.newsLoading.collectAsState()
    val digests by viewModel.newsDigests.collectAsState()
    val errorMsg by viewModel.newsError.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterChip by remember { mutableStateOf("All") }

    val filterChips = listOf(
        "All" to Loc.get("filter_all", lang),
        "Space" to Loc.get("filter_space", lang),
        "Tech" to Loc.get("filter_tech", lang),
        "Culture" to Loc.get("filter_culture", lang),
        "Society" to Loc.get("filter_society", lang)
    )

    // Interactive states for social metrics (persisted in composition)
    val likedStates = remember { mutableStateMapOf<String, Boolean>() }
    val retweetedStates = remember { mutableStateMapOf<String, Boolean>() }
    val likeCounts = remember { mutableStateMapOf<String, Int>() }
    val retweetCounts = remember { mutableStateMapOf<String, Int>() }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Local filtering based on selected chip and search queries
    val filteredDigests = digests.filter { digest ->
        val matchesChip = when (selectedFilterChip) {
            "All" -> true
            "Space" -> digest.title.lowercase().contains("space") || digest.title.lowercase().contains("mars") || digest.title.lowercase().contains("космос") || digest.title.lowercase().contains("марс")
            "Tech" -> digest.title.lowercase().contains("tech") || digest.title.lowercase().contains("ai") || digest.title.lowercase().contains("ии") || digest.title.lowercase().contains("техно")
            "Culture" -> digest.title.lowercase().contains("folk") || digest.title.lowercase().contains("culture") || digest.title.lowercase().contains("культур")
            "Society" -> !digest.title.lowercase().contains("space") && !digest.title.lowercase().contains("mars") && !digest.title.lowercase().contains("tech") && !digest.title.lowercase().contains("ai") && !digest.title.lowercase().contains("folk")
            else -> true
        }
        val matchesSearch = if (searchQuery.isNotBlank()) {
            digest.title.lowercase().contains(searchQuery.lowercase()) ||
            digest.content.lowercase().contains(searchQuery.lowercase()) ||
            digest.attachedJoke.lowercase().contains(searchQuery.lowercase())
        } else {
            true
        }
        matchesChip && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simulated X.com Monitoring Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1035).copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pulsing Online Signal
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val alphaScale by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(alphaScale)
                            .background(Color(0xFF00FFCC), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Loc.get("monitoring_x_header", lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = Loc.get("monitoring_x_subheader", lang),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // X Accounts Integration Panel
        item {
            val monitoredAccounts by viewModel.monitoredAccounts.collectAsState()
            val selectedAccounts by viewModel.selectedAccounts.collectAsState()
            var newAccountHandle by remember { mutableStateOf("") }
            val defaultAccounts = listOf("@NASA", "@TechCrunch", "@Reuters", "@folklore", "@OdessaHumor")

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Loc.get("x_integration", lang),
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${selectedAccounts.size} ${Loc.get("active_suffix", lang)}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(monitoredAccounts) { account ->
                            val isSelected = selectedAccounts.contains(account)
                            val isDefault = defaultAccounts.contains(account)
                            
                            Card(
                                shape = RoundedCornerShape(50),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.05f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, 
                                    if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.clickable { viewModel.toggleAccountSelection(account) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = account,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF1E1035) else Color.White
                                    )
                                    if (!isDefault) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { viewModel.removeCustomAccount(account) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = if (isSelected) Color(0xFF1E1035) else Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newAccountHandle,
                            onValueChange = { newAccountHandle = it },
                            placeholder = { Text(Loc.get("add_account_placeholder", lang), fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("add_account_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFFFD700)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newAccountHandle.isNotBlank()) {
                                    viewModel.addCustomAccount(newAccountHandle)
                                    newAccountHandle = ""
                                }
                            })
                        )

                        Button(
                            onClick = {
                                if (newAccountHandle.isNotBlank()) {
                                    viewModel.addCustomAccount(newAccountHandle)
                                    newAccountHandle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("add_account_button")
                        ) {
                            Text(
                                text = Loc.get("add_button", lang),
                                color = Color(0xFF1E1035),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Tweet Composer / Topic Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = {
                    Text(
                        text = Loc.get("search_placeholder", lang),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("news_search_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFFD700)
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Row(modifier = Modifier.padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.5f))
                            }
                        }
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                if (searchQuery.isNotBlank()) {
                                    viewModel.generateXDigest(searchQuery)
                                } else {
                                    viewModel.generateXDigest()
                                }
                            },
                            enabled = !loading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Query News API",
                                tint = Color(0xFFFFD700)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    if (searchQuery.isNotBlank()) {
                        viewModel.generateXDigest(searchQuery)
                    } else {
                        viewModel.generateXDigest()
                    }
                })
            )
        }

        // Horizontal Tag Chips Row
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterChips) { (id, label) ->
                    val selected = selectedFilterChip == id
                    FilterChip(
                        selected = selected,
                        onClick = { selectedFilterChip = id },
                        label = { Text(text = label, fontSize = 12.sp, color = if (selected) Color(0xFF1E1035) else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFD700),
                            containerColor = Color.White.copy(alpha = 0.05f),
                            selectedLabelColor = Color(0xFF1E1035)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f),
                            selectedBorderColor = Color(0xFFFFD700),
                            enabled = true,
                            selected = selected
                        )
                    )
                }
            }
        }

        // Loading and Feed List
        if (loading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = Loc.get("news_loading", lang),
                        color = Color(0xFFFFD700),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            if (errorMsg != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1F1F)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Alert", tint = Color.Red)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = Loc.get("news_cached", lang),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            if (filteredDigests.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Inbox, contentDescription = "Empty", tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Loc.get("no_posts", lang),
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                itemsIndexed(filteredDigests) { index, digest ->
                    NewsDigestCard(
                        digest = digest,
                        index = index,
                        viewModel = viewModel,
                        lang = lang,
                        likedStates = likedStates,
                        retweetedStates = retweetedStates,
                        likeCounts = likeCounts,
                        retweetCounts = retweetCounts
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

// X-styled Post Author Data Structure
data class TweetAuthor(
    val name: String,
    val handle: String,
    val avatarIcon: ImageVector,
    val avatarBg: Color
)

fun getTweetAuthor(title: String, lang: String): TweetAuthor {
    val lower = title.lowercase()
    return when {
        lower.contains("space") || lower.contains("mars") || lower.contains("космос") || lower.contains("марс") -> {
            TweetAuthor(
                name = Loc.getAuthorName("@space_horizons", lang),
                handle = "@space_horizons",
                avatarIcon = Icons.Default.Science,
                avatarBg = Color(0xFF9C27B0) // Purple
            )
        }
        lower.contains("tech") || lower.contains("ai") || lower.contains("ии") || lower.contains("техно") -> {
            TweetAuthor(
                name = Loc.getAuthorName("@tech_trends", lang),
                handle = "@tech_trends",
                avatarIcon = Icons.Default.Build,
                avatarBg = Color(0xFF00BCD4) // Cyan
            )
        }
        lower.contains("folk") || lower.contains("culture") || lower.contains("культур") -> {
            TweetAuthor(
                name = Loc.getAuthorName("@ethno_observer", lang),
                handle = "@ethno_observer",
                avatarIcon = Icons.Default.Book,
                avatarBg = Color(0xFFFF9800) // Orange
            )
        }
        else -> {
            TweetAuthor(
                name = Loc.getAuthorName("@global_pulse", lang),
                handle = "@global_pulse",
                avatarIcon = Icons.Default.Public,
                avatarBg = Color(0xFF4CAF50) // Green
            )
        }
    }
}

@Composable
fun NewsDigestCard(
    digest: NewsDigest,
    index: Int,
    viewModel: AppViewModel,
    lang: String,
    likedStates: MutableMap<String, Boolean>,
    retweetedStates: MutableMap<String, Boolean>,
    likeCounts: MutableMap<String, Int>,
    retweetCounts: MutableMap<String, Int>
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(digest.attachedJoke) {
        viewModel.isJokeSavedState(digest.attachedJoke) { saved ->
            isSaved = saved
        }
    }

    val author = getTweetAuthor(digest.title, lang)

    // Interactive social states
    val isLiked = likedStates[digest.title] ?: false
    val isRetweeted = retweetedStates[digest.title] ?: false

    // Unique deterministic base metrics derived from title string
    val baseHash = digest.title.hashCode()
    val baseHashAbs = if (baseHash < 0) -baseHash else baseHash
    val baseLikes = (baseHashAbs % 150) + 20
    val baseRetweets = (baseHashAbs % 60) + 5
    val baseComments = (baseHashAbs % 30) + 2

    val likesCount = likeCounts[digest.title] ?: (baseLikes + if (isLiked) 1 else 0)
    val retweetsCount = retweetCounts[digest.title] ?: (baseRetweets + if (isRetweeted) 1 else 0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("news_tweet_card_$index"),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = BoxBorderDefaults(Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Post Header (X Style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(author.avatarBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = author.avatarIcon,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Author Details
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = if (digest.authorName.isNotEmpty()) digest.authorName else author.name
                    val displayHandle = if (digest.authorHandle.isNotEmpty()) digest.authorHandle else author.handle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // Verified Blue Checkmark Badge
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "· 3h",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = displayHandle,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Headline (Bold)
            Text(
                text = digest.title,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Content Body with dynamic hashtags coloring
            val annotatedContent = remember(digest.content) {
                // To keep it simple and compile perfectly without imports, we do a basic highlighted text flow
                digest.content
            }

            Text(
                text = annotatedContent,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // X Community Note styled "Folk Note" (Folk Wisdom Anecdote Context)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { isExpanded = !isExpanded },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1035).copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Community Note",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${Loc.get("folk_context_header", lang)} (${Loc.getCultureName(digest.attachedJokeCulture, lang)})",
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand Note",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Small preview sentence
                    if (!isExpanded) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (digest.attachedJoke.length > 60) "${digest.attachedJoke.take(60).trim()}..." else digest.attachedJoke,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = digest.attachedJoke,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val context = LocalContext.current
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("joke", digest.attachedJoke)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, Loc.get("copied_toast", lang), Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = {
                                    viewModel.toggleSaveJoke(digest.attachedJoke, digest.attachedJokeCulture, "X.com News Digest")
                                    isSaved = !isSaved
                                    Toast.makeText(
                                        context,
                                        if (isSaved) Loc.get("added_favorites", lang)
                                        else Loc.get("removed_favorites", lang),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.size(32.dp).testTag("news_joke_favorite_$index")
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Save",
                                    tint = if (isSaved) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Social Interaction Counters Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reply/Comment Button
                val context = LocalContext.current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, Loc.get("loading_replies", lang), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Reply",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = baseComments.toString(),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }

                // Repost/Retweet Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val active = !isRetweeted
                        retweetedStates[digest.title] = active
                        retweetCounts[digest.title] = baseRetweets + if (active) 1 else 0
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Retweet",
                        tint = if (isRetweeted) Color(0xFF00BA7C) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = retweetsCount.toString(),
                        color = if (isRetweeted) Color(0xFF00BA7C) else Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = if (isRetweeted) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Like Button (Spring glowing heart)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val active = !isLiked
                        likedStates[digest.title] = active
                        likeCounts[digest.title] = baseLikes + if (active) 1 else 0
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFF91880) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = likesCount.toString(),
                        color = if (isLiked) Color(0xFFF91880) else Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Copy Link/Share Button
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "X.com Trend: ${digest.title}\n\n${digest.content}")
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Trend"))
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RadioTab(viewModel: AppViewModel, lang: String) {
    val stations by viewModel.radioStations.collectAsState()
    val playingStation by viewModel.selectedStation.collectAsState()
    val isPlaying by viewModel.isRadioPlaying.collectAsState()
    val isLoading by viewModel.isRadioLoading.collectAsState()

    var showCreator by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newUrl by remember { mutableStateOf("") }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player Engine Selection Card
        item {
            val useHtml5 by viewModel.useHtml5Player.collectAsState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .testTag("engine_selection_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                border = BoxBorderDefaults(Color(0xFFFFD700).copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Loc.get("streaming_engine", lang),
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (useHtml5) Loc.get("html5_player_desc", lang) else Loc.get("native_player_desc", lang),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                              )
                        }
                        
                        // Custom premium switch/chip selector
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF0F061E))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (!useHtml5) Color(0xFFFFD700) else Color.Transparent)
                                    .clickable { viewModel.setUseHtml5Player(false) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Native",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!useHtml5) Color(0xFF1E1035) else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (useHtml5) Color(0xFFFFD700) else Color.Transparent)
                                    .clickable { viewModel.setUseHtml5Player(true) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "HTML5",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (useHtml5) Color(0xFF1E1035) else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (useHtml5) Loc.get("html5_player_long", lang) else Loc.get("native_player_long", lang),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Equalizer / retro panel
        item {
            val useHtml5 by viewModel.useHtml5Player.collectAsState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF150A26), Color(0xFF07020E))
                        )
                    )
                    .border(2.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (useHtml5) Loc.get("html5_title", lang) else Loc.get("retro_title", lang),
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Retro Frequency screen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00FFCC).copy(alpha = 0.05f))
                            .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playingStation?.name ?: Loc.get("broadcaster_off", lang),
                                    color = Color(0xFF00FFCC),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isPlaying) {
                                        if (useHtml5) Loc.get("html5_active", lang) else Loc.get("live_streaming", lang)
                                    } else if (isLoading) {
                                        Loc.get("tuning", lang)
                                    } else {
                                        Loc.get("standby", lang)
                                    },
                                    color = Color(0xFF00FFCC).copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Dynamic equalizer visualizer
                            Row(
                                modifier = Modifier.height(30.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                repeat(5) { barIndex ->
                                    val infiniteTransition = rememberInfiniteTransition(label = "eq")
                                    val heightScale by infiniteTransition.animateFloat(
                                        initialValue = 0.2f,
                                        targetValue = if (isPlaying) 1f else 0.2f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(durationMillis = 300 + (barIndex * 150), easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "eq_anim"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .fillMaxHeight(heightScale)
                                            .background(Color(0xFF00FFCC))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleRadio() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFFD700), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "PlayPause",
                                tint = Color(0xFF1E1035)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.stopRadio() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Custom stream creator expander
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCreator = !showCreator },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Loc.get("add_custom_station", lang),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Icon(
                            imageVector = if (showCreator) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    if (showCreator) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text(Loc.get("station_name", lang), color = Color.White.copy(alpha = 0.6f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newUrl,
                            onValueChange = { newUrl = it },
                            label = { Text(Loc.get("stream_url", lang), color = Color.White.copy(alpha = 0.6f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (newName.isNotBlank() && newUrl.isNotBlank()) {
                                    viewModel.addCustomRadioStation(newName, newUrl)
                                    newName = ""
                                    newUrl = ""
                                    showCreator = false
                                    Toast.makeText(context, Loc.get("station_added", lang), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, Loc.get("fill_all_fields", lang), Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color(0xFF1E1035))
                        ) {
                            Text(Loc.get("add_station_btn", lang))
                        }
                    }
                }
            }
        }

        // Stations list header
        item {
            Text(
                text = Loc.get("available_broadcasts", lang),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Available stations
        items(stations) { station ->
            val isCurrent = playingStation?.streamUrl == station.streamUrl
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.playRadio(station) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color(0xFFFFD700).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.04f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = if (isCurrent) BoxBorderDefaults(Color(0xFFFFD700).copy(alpha = 0.4f)) else null
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { viewModel.playRadio(station) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (isCurrent && isPlaying) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = if (isCurrent && isPlaying) Color(0xFF1E1035) else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = station.name,
                                color = if (isCurrent) Color(0xFFFFD700) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (station.isDefault) "Official Stream" else "User Added Stream",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.toggleRadioFavorite(station) }
                        ) {
                            Icon(
                                imageVector = if (station.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Favorite",
                                tint = if (station.isFavorite) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f)
                            )
                        }

                        if (!station.isDefault) {
                            IconButton(
                                onClick = { viewModel.deleteRadioStation(station) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

@Composable
fun FavoritesTab(viewModel: AppViewModel, lang: String) {
    val savedJokes by viewModel.savedJokes.collectAsState()
    val radioStations by viewModel.radioStations.collectAsState()
    val favoritedStations = radioStations.filter { it.isFavorite }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Favorited Radio streams header
        item {
            Text(
                text = Loc.get("fav_stations_header", lang),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (favoritedStations.isEmpty()) {
            item {
                Text(
                    text = Loc.get("no_fav_stations", lang),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            items(favoritedStations) { station ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.playRadio(station) },
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Radio, contentDescription = "Radio", tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = station.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        IconButton(onClick = { viewModel.toggleRadioFavorite(station) }) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = "Unstar", tint = Color(0xFFFFD700))
                        }
                    }
                }
            }
        }

        // Saved Jokes header
        item {
            Text(
                text = Loc.get("saved_anecdotes_header", lang),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (savedJokes.isEmpty()) {
            item {
                Text(
                    text = Loc.get("no_saved_anecdotes", lang),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            items(savedJokes) { joke ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${Loc.get("style_prefix", lang)} ${Loc.getCultureName(joke.culture, lang)}",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (joke.situation.isNotBlank()) {
                                    Text(
                                        text = "${Loc.get("topic_prefix", lang)} ${joke.situation}",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row {
                                val context = LocalContext.current
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("joke", joke.jokeText)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, Loc.get("copied", lang), Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White.copy(alpha = 0.6f))
                                }
                                IconButton(onClick = { viewModel.deleteSavedJoke(joke) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = joke.jokeText,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

// Time formatting utility for media playback
fun formatStreamTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

// Persistent bottom media player component for live internet radio streaming
@Composable
fun PersistentBottomMediaPlayer(viewModel: AppViewModel, lang: String) {
    val isPlaying by viewModel.isRadioPlaying.collectAsState()
    val isLoading by viewModel.isRadioLoading.collectAsState()
    val playingStation by viewModel.selectedStation.collectAsState()
    val radioStations by viewModel.radioStations.collectAsState()
    val positionMs by viewModel.radioPositionMs.collectAsState()
    val durationMs by viewModel.radioDurationMs.collectAsState()
    val isLiveEdge by viewModel.isLiveEdge.collectAsState()
    val volume by viewModel.radioVolume.collectAsState()

    // Active station: either currently selected station or fallback to first station in library
    val activeStation = playingStation ?: radioStations.firstOrNull { it.isFavorite } ?: radioStations.firstOrNull()

    var isExpanded by remember { mutableStateOf(false) }
    var userSliderPos by remember { mutableFloatStateOf(0f) }
    var isDraggingSlider by remember { mutableStateOf(false) }

    val effectiveMaxMs = if (durationMs > 0L) durationMs.toFloat() else 60_000f
    val currentProgress = if (isDraggingSlider) {
        userSliderPos
    } else {
        (positionMs.toFloat() / effectiveMaxMs.coerceAtLeast(1f)).coerceIn(0f, 1f)
    }

    // Animated pulse for LIVE badge
    val infiniteTransition = rememberInfiniteTransition(label = "player_live_pulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_dot_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("persistent_bottom_media_player"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF140727).copy(alpha = 0.96f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BoxBorderDefaults(Color(0xFFFFD700).copy(alpha = if (isPlaying) 0.5f else 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Top Row: Station icon, Station name, status badges, and quick toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Station Icon & Details
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = if (isPlaying) listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                                    else listOf(Color(0xFF2C194D), Color(0xFF1E0E38))
                                )
                            )
                            .border(
                                1.dp,
                                if (isPlaying) Color(0xFFFFD700) else Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlaying) {
                            // Animated mini visualizer bars inside icon
                            Row(
                                modifier = Modifier.height(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                repeat(4) { barIdx ->
                                    val barHeight by infiniteTransition.animateFloat(
                                        initialValue = 0.25f,
                                        targetValue = 0.95f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(280 + barIdx * 140, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "bar_$barIdx"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .fillMaxHeight(barHeight)
                                            .background(Color(0xFF140727), RoundedCornerShape(1.dp))
                                    )
                                }
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = "Radio Station",
                                tint = if (isPlaying) Color(0xFF140727) else Color(0xFFFFD700),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeStation?.name ?: Loc.get("radio_stream", lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("media_station_name")
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Glowing live indicator dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPlaying -> Color(0xFF00FFCC).copy(alpha = liveAlpha)
                                            isLoading -> Color(0xFFFFD700).copy(alpha = liveAlpha)
                                            else -> Color.White.copy(alpha = 0.35f)
                                        }
                                    )
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = when {
                                    isPlaying -> Loc.get("live_badge", lang) + " • " + Loc.get("audio_quality", lang)
                                    isLoading -> Loc.get("tuning_in", lang)
                                    else -> Loc.get("ready_to_play", lang)
                                },
                                color = when {
                                    isPlaying -> Color(0xFF00FFCC)
                                    isLoading -> Color(0xFFFFD700)
                                    else -> Color.White.copy(alpha = 0.5f)
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Quick Header Action icons: Favorite toggle & Expand/Volume toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    activeStation?.let { st ->
                        IconButton(
                            onClick = { viewModel.toggleRadioFavorite(st) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (st.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Favorite Station",
                                tint = if (st.isFavorite) Color(0xFFFFD700) else Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.Tune,
                            contentDescription = "Audio Controls",
                            tint = if (isExpanded) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Seek Bar Section for Live Internet Radio Streaming
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Elapsed session streaming time
                Text(
                    text = formatStreamTime(if (isDraggingSlider) (userSliderPos * effectiveMaxMs).toLong() else positionMs),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.width(36.dp)
                )

                // Seek Bar Slider
                Slider(
                    value = currentProgress,
                    onValueChange = {
                        isDraggingSlider = true
                        userSliderPos = it
                    },
                    onValueChangeFinished = {
                        val targetMs = (userSliderPos * effectiveMaxMs).toLong()
                        viewModel.seekRadio(targetMs)
                        isDraggingSlider = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .padding(horizontal = 4.dp)
                        .testTag("media_seek_bar"),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFD700),
                        activeTrackColor = Color(0xFFFFD700),
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )

                // Live status badge or Jump to Live button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isLiveEdge) Color(0xFF00FFCC).copy(alpha = 0.12f)
                            else Color(0xFFFFD700).copy(alpha = 0.2f)
                        )
                        .border(
                            1.dp,
                            if (isLiveEdge) Color(0xFF00FFCC).copy(alpha = 0.35f)
                            else Color(0xFFFFD700).copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.seekRadioToLive() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (isLiveEdge) Color(0xFF00FFCC) else Color(0xFFFFD700))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLiveEdge) Loc.get("live_badge", lang) else Loc.get("seek_to_live", lang),
                            color = if (isLiveEdge) Color(0xFF00FFCC) else Color(0xFFFFD700),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Controls Row: Station skip buttons, Main Play/Pause, and Stop button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Stream info hint
                Text(
                    text = if (activeStation?.isDefault == true) "Official Global Broadcast" else "Custom Radio Stream",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Previous Station button
                    IconButton(
                        onClick = { viewModel.playPreviousStation() },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("media_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = Loc.get("prev_station", lang),
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Main Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFF9E00))
                                )
                            )
                            .clickable {
                                if (activeStation != null) {
                                    viewModel.toggleRadio()
                                }
                            }
                            .testTag("media_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color(0xFF140727),
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF140727),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Next Station button
                    IconButton(
                        onClick = { viewModel.playNextStation() },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("media_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = Loc.get("next_station", lang),
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Stop / Standby button
                    if (isPlaying || isLoading) {
                        IconButton(
                            onClick = { viewModel.stopRadio() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Expandable Audio settings drawer (Volume slider & technical info)
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (volume > 0.05f) viewModel.setRadioVolume(0f) else viewModel.setRadioVolume(1f)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (volume > 0.05f) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "Mute",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = Loc.get("volume_title", lang),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        Slider(
                            value = volume,
                            onValueChange = { viewModel.setRadioVolume(it) },
                            valueRange = 0f..1f,
                            modifier = Modifier
                                .weight(1f)
                                .height(24.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFFFFD700),
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )

                        Text(
                            text = "${(volume * 100).toInt()}%",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.width(32.dp)
                        )
                    }
                }
            }
        }
    }
}

// Delegate alias for backwards compatibility
@Composable
fun MiniRadioPlayer(viewModel: AppViewModel, lang: String) {
    PersistentBottomMediaPlayer(viewModel, lang)
}

// Utility to translate culture identifiers to readable Russian names
fun getRussianCultureName(culture: String): String {
    return when (culture.lowercase()) {
        "uzbek" -> "Узбекский 🇺🇿"
        "armenian" -> "Армянский 🇦🇲"
        "jewish" -> "Еврейский ✡️"
        "chukchi" -> "Чукотский 🏹"
        "yakut" -> "Якутский ❄️"
        "kamchatka" -> "Камчатский 🌋"
        "american" -> "Американский 🤠"
        "african" -> "Африканский 🦁"
        else -> culture
    }
}

// Border utility to keep syntax clean
fun BoxBorderDefaults(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Html5AudioPlayer(viewModel: AppViewModel) {
    val useHtml5 by viewModel.useHtml5Player.collectAsState()
    val currentUrl by viewModel.currentRadioUrl.collectAsState()
    val isPlaying by viewModel.isRadioPlaying.collectAsState()

    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // HTML5 Player Page content with custom retro neon visualization
    val htmlContent = remember {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    background: transparent;
                    color: white;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    overflow: hidden;
                }
                #audio-element {
                    display: none;
                }
            </style>
        </head>
        <body>
            <audio id="audio-element" preload="auto" crossorigin="anonymous"></audio>
            
            <script>
                var audio = document.getElementById('audio-element');
                var currentUrl = '';
                var isExplicitlyPlaying = false;

                function sendStateToAndroid(playing, loading, error) {
                    if (window.AndroidRadio) {
                        window.AndroidRadio.updateState(playing, loading, error);
                    }
                }

                function playStation(url) {
                    if (currentUrl !== url) {
                        currentUrl = url;
                        audio.src = url;
                        audio.load();
                    }
                    isExplicitlyPlaying = true;
                    audio.play().then(function() {
                        sendStateToAndroid(true, false, null);
                    }).catch(function(err) {
                        console.error("Play failed:", err);
                        sendStateToAndroid(false, true, null);
                    });
                }

                function pauseStation() {
                    isExplicitlyPlaying = false;
                    audio.pause();
                    sendStateToAndroid(false, false, null);
                }

                audio.addEventListener('play', function() {
                    sendStateToAndroid(true, false, null);
                });
                audio.addEventListener('playing', function() {
                    sendStateToAndroid(true, false, null);
                });
                audio.addEventListener('pause', function() {
                    sendStateToAndroid(false, false, null);
                });
                audio.addEventListener('waiting', function() {
                    if (isExplicitlyPlaying) {
                        sendStateToAndroid(false, true, null);
                    }
                });
                audio.addEventListener('loadstart', function() {
                    if (isExplicitlyPlaying) {
                        sendStateToAndroid(false, true, null);
                    }
                });
                audio.addEventListener('error', function(e) {
                    sendStateToAndroid(false, false, "HTML5 Audio connection error");
                });
                audio.addEventListener('stalled', function() {
                    if (isExplicitlyPlaying) {
                        sendStateToAndroid(false, true, null);
                    }
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    if (useHtml5) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.domStorageEnabled = true
                    
                    addJavascriptInterface(object {
                        @android.webkit.JavascriptInterface
                        fun updateState(playing: Boolean, loading: Boolean, error: String?) {
                            viewModel.setHtml5PlayerState(playing, loading, error)
                        }
                    }, "AndroidRadio")
                    
                    loadDataWithBaseURL("https://local-radio-player", htmlContent, "text/html", "UTF-8", null)
                    webViewRef = this
                }
            },
            modifier = Modifier.size(0.dp)
        )

        LaunchedEffect(currentUrl, isPlaying) {
            webViewRef?.let { webView ->
                if (currentUrl != null) {
                    if (isPlaying) {
                        webView.evaluateJavascript("playStation('$currentUrl')", null)
                    } else {
                        webView.evaluateJavascript("pauseStation()", null)
                    }
                } else {
                    webView.evaluateJavascript("pauseStation()", null)
                }
            }
        }
    }
}

@Composable
fun StyledBackground(style: String) {
    val brush = when (style) {
        "Registan" -> Brush.verticalGradient(colors = listOf(Color(0xFF010C1E), Color(0xFF03162D), Color(0xFF051D3A)))
        "Giza" -> Brush.verticalGradient(colors = listOf(Color(0xFF130903), Color(0xFF281308), Color(0xFF381B0C)))
        "India" -> Brush.verticalGradient(colors = listOf(Color(0xFF1B0316), Color(0xFF350723), Color(0xFF4A0A1F)))
        "Peru" -> Brush.verticalGradient(colors = listOf(Color(0xFF090808), Color(0xFF1A100B), Color(0xFF24120D)))
        "Gaudi" -> Brush.verticalGradient(colors = listOf(Color(0xFF051122), Color(0xFF0A223B), Color(0xFF003D55)))
        "VanGogh" -> Brush.verticalGradient(colors = listOf(Color(0xFF00091A), Color(0xFF02132D), Color(0xFF062244)))
        "AvantGarde" -> Brush.verticalGradient(colors = listOf(Color(0xFF060607), Color(0xFF121215), Color(0xFF1A1A1E)))
        else -> Brush.verticalGradient(colors = listOf(Color(0xFF05010B), Color(0xFF05010B)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (style) {
                "Registan" -> drawRegistanOrnaments(w, h)
                "Giza" -> drawGizaOrnaments(w, h)
                "India" -> drawIndiaOrnaments(w, h)
                "Peru" -> drawPeruOrnaments(w, h)
                "Gaudi" -> drawGaudiOrnaments(w, h)
                "VanGogh" -> drawVanGoghOrnaments(w, h)
                "AvantGarde" -> drawAvantGardeOrnaments(w, h)
            }
        }
    }
}

private fun DrawScope.drawRegistanOrnaments(w: Float, h: Float) {
    // Draw in top-right
    translate(w - 60.dp.toPx(), 80.dp.toPx()) {
        rotate(45f) {
            for (i in 0..3) {
                rotate(i * 15f) {
                    drawRect(
                        color = Color(0xFF00FFCC).copy(alpha = 0.08f),
                        size = androidx.compose.ui.geometry.Size(120.dp.toPx(), 120.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawRect(
                        color = Color(0xFFFFD700).copy(alpha = 0.05f),
                        size = androidx.compose.ui.geometry.Size(80.dp.toPx(), 80.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
        }
    }
    // Draw in bottom-left
    translate(60.dp.toPx(), h - 180.dp.toPx()) {
        rotate(15f) {
            for (i in 0..3) {
                rotate(i * 15f) {
                    drawRect(
                        color = Color(0xFF00FFCC).copy(alpha = 0.08f),
                        size = androidx.compose.ui.geometry.Size(160.dp.toPx(), 160.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawRect(
                        color = Color(0xFFFFD700).copy(alpha = 0.05f),
                        size = androidx.compose.ui.geometry.Size(100.dp.toPx(), 100.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawGizaOrnaments(w: Float, h: Float) {
    // Glowing sun in upper right
    drawCircle(
        color = Color(0xFFFFB300).copy(alpha = 0.1f),
        radius = 180.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(w - 20.dp.toPx(), 100.dp.toPx())
    )
    drawCircle(
        color = Color(0xFFFFCC80).copy(alpha = 0.05f),
        radius = 280.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(w - 20.dp.toPx(), 100.dp.toPx())
    )

    // Pyramids at the bottom
    val path1 = Path().apply {
        moveTo(w * 0.1f, h)
        lineTo(w * 0.4f, h - 140.dp.toPx())
        lineTo(w * 0.7f, h)
        close()
    }
    drawPath(
        path = path1,
        color = Color(0xFF4E2B15).copy(alpha = 0.3f),
        style = Fill
    )
    drawPath(
        path = path1,
        color = Color(0xFFFFD700).copy(alpha = 0.15f),
        style = Stroke(width = 1.5.dp.toPx())
    )

    val path2 = Path().apply {
        moveTo(w * 0.45f, h)
        lineTo(w * 0.7f, h - 110.dp.toPx())
        lineTo(w * 0.95f, h)
        close()
    }
    drawPath(
        path = path2,
        color = Color(0xFF3D1D0B).copy(alpha = 0.4f),
        style = Fill
    )
    drawPath(
        path = path2,
        color = Color(0xFFFFB300).copy(alpha = 0.12f),
        style = Stroke(width = 1.5.dp.toPx())
    )
}

private fun DrawScope.drawIndiaOrnaments(w: Float, h: Float) {
    // Radiating mandala ornament centered at bottom-right
    translate(w - 20.dp.toPx(), h - 50.dp.toPx()) {
        for (radius in listOf(60, 120, 180, 240)) {
            drawCircle(
                color = Color(0xFFFF5722).copy(alpha = 0.05f),
                radius = radius.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
        }
        // Radiating spokes
        val petals = 16
        for (i in 0 until petals) {
            rotate(i * (360f / petals)) {
                drawLine(
                    color = Color(0xFFFFD700).copy(alpha = 0.1f),
                    start = androidx.compose.ui.geometry.Offset(30.dp.toPx(), 0f),
                    end = androidx.compose.ui.geometry.Offset(230.dp.toPx(), 0f),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawCircle(
                    color = Color(0xFFE91E63).copy(alpha = 0.12f),
                    radius = 6.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(150.dp.toPx(), 0f)
                )
            }
        }
    }

    // Top-left small mandala
    translate(20.dp.toPx(), 60.dp.toPx()) {
        for (radius in listOf(40, 80, 120)) {
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = 0.06f),
                radius = radius.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
        }
        val petals = 12
        for (i in 0 until petals) {
            rotate(i * (360f / petals)) {
                drawLine(
                    color = Color(0xFFE91E63).copy(alpha = 0.08f),
                    start = androidx.compose.ui.geometry.Offset(20.dp.toPx(), 0f),
                    end = androidx.compose.ui.geometry.Offset(110.dp.toPx(), 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

private fun DrawScope.drawPeruOrnaments(w: Float, h: Float) {
    val colors = listOf(
        Color(0xFFE53935).copy(alpha = 0.12f), // Crimson Red
        Color(0xFFFFB300).copy(alpha = 0.12f), // Gold Yellow
        Color(0xFF00897B).copy(alpha = 0.1f),  // Teal Green
        Color(0xFF5D4037).copy(alpha = 0.15f)  // Dark Brown
    )

    val bandHeights = listOf(h * 0.15f, h * 0.5f, h * 0.8f)
    for (y in bandHeights) {
        // Thick colorful background band
        drawRect(
            color = colors[3],
            topLeft = androidx.compose.ui.geometry.Offset(0f, y),
            size = androidx.compose.ui.geometry.Size(w, 40.dp.toPx())
        )
        // Draw zigzag lines across screen width
        val step = 30.dp.toPx()
        val path = Path()
        path.moveTo(0f, y + 20.dp.toPx())
        var x = 0f
        var up = true
        while (x < w) {
            x += step
            val nextY = if (up) y + 5.dp.toPx() else y + 35.dp.toPx()
            path.lineTo(x, nextY)
            up = !up
        }
        drawPath(
            path = path,
            color = Color(0xFFFFD700).copy(alpha = 0.2f),
            style = Stroke(width = 2.dp.toPx())
        )

        // Repeat slightly offset
        val path2 = Path()
        path2.moveTo(0f, y + 15.dp.toPx())
        x = 0f
        up = true
        while (x < w) {
            x += step
            val nextY = if (up) y + 0.dp.toPx() else y + 30.dp.toPx()
            path2.lineTo(x, nextY)
            up = !up
        }
        drawPath(
            path = path2,
            color = Color(0xFFE53935).copy(alpha = 0.2f),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private fun DrawScope.drawGaudiOrnaments(w: Float, h: Float) {
    // Curved wave paths (organic flow)
    val path = Path().apply {
        moveTo(0f, h * 0.2f)
        cubicTo(w * 0.3f, h * 0.1f, w * 0.7f, h * 0.3f, w, h * 0.15f)
    }
    drawPath(
        path = path,
        color = Color(0xFFFF9800).copy(alpha = 0.12f),
        style = Stroke(width = 4.dp.toPx())
    )

    val path2 = Path().apply {
        moveTo(0f, h * 0.75f)
        cubicTo(w * 0.4f, h * 0.85f, w * 0.6f, h * 0.65f, w, h * 0.8f)
    }
    drawPath(
        path = path2,
        color = Color(0xFF00E676).copy(alpha = 0.1f),
        style = Stroke(width = 6.dp.toPx())
    )

    // Scattering mosaic tiles (trencadís)
    val mosaicColors = listOf(
        Color(0xFF00D2FF),
        Color(0xFFFFD700),
        Color(0xFFFF5722),
        Color(0xFF00E676),
        Color(0xFFE040FB)
    )

    // Cluster 1 (Top Left)
    for (i in 0..15) {
        val cx = (i % 4) * 25.dp.toPx() + 15.dp.toPx()
        val cy = (i / 4) * 25.dp.toPx() + 30.dp.toPx()
        val sizeVal = 15
        drawRoundRect(
            color = mosaicColors[i % mosaicColors.size].copy(alpha = 0.15f),
            topLeft = androidx.compose.ui.geometry.Offset(cx, cy),
            size = androidx.compose.ui.geometry.Size(sizeVal.dp.toPx(), sizeVal.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
    }

    // Cluster 2 (Bottom Right)
    for (i in 0..15) {
        val cx = w - (i % 4) * 25.dp.toPx() - 100.dp.toPx()
        val cy = h - (i / 4) * 25.dp.toPx() - 150.dp.toPx()
        val sizeVal = 16
        drawRoundRect(
            color = mosaicColors[(i + 2) % mosaicColors.size].copy(alpha = 0.15f),
            topLeft = androidx.compose.ui.geometry.Offset(cx, cy),
            size = androidx.compose.ui.geometry.Size(sizeVal.dp.toPx(), sizeVal.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
    }
}

private fun DrawScope.drawVanGoghOrnaments(w: Float, h: Float) {
    // Swirling starry night crescent moon in top right
    val moonX = w - 60.dp.toPx()
    val moonY = 80.dp.toPx()
    
    // Moon Halos
    drawCircle(
        color = Color(0xFFFFD700).copy(alpha = 0.05f),
        radius = 50.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(moonX, moonY)
    )
    drawCircle(
        color = Color(0xFFFFD700).copy(alpha = 0.02f),
        radius = 80.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(moonX, moonY)
    )
    
    // Crescent Moon shape
    drawCircle(
        color = Color(0xFFFFD700).copy(alpha = 0.25f),
        radius = 24.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(moonX, moonY)
    )
    drawCircle(
        color = Color(0xFF00091A), // Clips it
        radius = 22.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(moonX - 8.dp.toPx(), moonY - 2.dp.toPx())
    )

    // Swirling curves across center of background
    val path1 = Path().apply {
        moveTo(0f, h * 0.45f)
        cubicTo(w * 0.35f, h * 0.3f, w * 0.65f, h * 0.6f, w, h * 0.45f)
    }
    drawPath(
        path = path1,
        color = Color(0xFF00D2FF).copy(alpha = 0.08f),
        style = Stroke(width = 4.dp.toPx())
    )

    val path2 = Path().apply {
        moveTo(0f, h * 0.48f)
        cubicTo(w * 0.4f, h * 0.35f, w * 0.6f, h * 0.55f, w, h * 0.42f)
    }
    drawPath(
        path = path2,
        color = Color(0xFFFFD700).copy(alpha = 0.06f),
        style = Stroke(width = 2.dp.toPx())
    )

    // Star swirly circles
    val stars = listOf(
        androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.25f),
        androidx.compose.ui.geometry.Offset(w * 0.7f, h * 0.3f),
        androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.7f),
        androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.65f)
    )
    for (star in stars) {
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = 0.2f),
            radius = 6.dp.toPx(),
            center = star
        )
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = 0.06f),
            radius = 18.dp.toPx(),
            center = star,
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF00FFCC).copy(alpha = 0.04f),
            radius = 30.dp.toPx(),
            center = star,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private fun DrawScope.drawAvantGardeOrnaments(w: Float, h: Float) {
    // Bold construction diagonal bars
    drawLine(
        color = Color(0xFFD32F2F).copy(alpha = 0.15f), // Bold red
        start = androidx.compose.ui.geometry.Offset(0f, h * 0.2f),
        end = androidx.compose.ui.geometry.Offset(w, h * 0.4f),
        strokeWidth = 12.dp.toPx()
    )

    drawLine(
        color = Color(0xFFFFFFFF).copy(alpha = 0.05f), // Stark white
        start = androidx.compose.ui.geometry.Offset(w, h * 0.1f),
        end = androidx.compose.ui.geometry.Offset(0f, h * 0.5f),
        strokeWidth = 6.dp.toPx()
    )

    // Overlapping geometric shapes
    drawCircle(
        color = Color(0xFFD32F2F).copy(alpha = 0.18f),
        radius = 90.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(40.dp.toPx(), h * 0.22f)
    )

    // Solid black/charcoal triangle
    val path = Path().apply {
        moveTo(w - 20.dp.toPx(), h * 0.6f)
        lineTo(w - 140.dp.toPx(), h * 0.75f)
        lineTo(w - 10.dp.toPx(), h * 0.85f)
        close()
    }
    drawPath(
        path = path,
        color = Color(0xFF000000).copy(alpha = 0.4f),
        style = Fill
    )
    drawPath(
        path = path,
        color = Color(0xFFFFFFFF).copy(alpha = 0.1f),
        style = Stroke(width = 2.dp.toPx())
    )

    // Solid stark rectangle column on right
    drawRect(
        color = Color(0xFFD32F2F).copy(alpha = 0.08f),
        topLeft = androidx.compose.ui.geometry.Offset(w - 50.dp.toPx(), h * 0.3f),
        size = androidx.compose.ui.geometry.Size(30.dp.toPx(), 250.dp.toPx())
    )
}

@Composable
fun StyleSettingsDialog(
    viewModel: AppViewModel,
    lang: String,
    onDismiss: () -> Unit
) {
    val activeStyle by viewModel.backgroundStyle.collectAsState()
    val styles = listOf("Registan", "Giza", "India", "Peru", "Gaudi", "VanGogh", "AvantGarde")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Loc.get("select_bg_style", lang),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(styles) { styleId ->
                    val isSelected = activeStyle == styleId
                    val styleName = Loc.getDesignStyleDisplayName(styleId, lang)
                    val desc = Loc.getDesignStyleName(styleId, lang)
                    val gradient = when (styleId) {
                        "Registan" -> listOf(Color(0xFF020E26), Color(0xFF082F59))
                        "Giza" -> listOf(Color(0xFF1F0D02), Color(0xFF421D05))
                        "India" -> listOf(Color(0xFF2A0322), Color(0xFF5E0B3B))
                        "Peru" -> listOf(Color(0xFF0F0E0E), Color(0xFF241611))
                        "Gaudi" -> listOf(Color(0xFF0A192F), Color(0xFF0F3057))
                        "VanGogh" -> listOf(Color(0xFF010E28), Color(0xFF051C42))
                        "AvantGarde" -> listOf(Color(0xFF0C0C0D), Color(0xFF1D1D21))
                        else -> listOf(Color.Black, Color.DarkGray)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectBackgroundStyle(styleId) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Style Swatch Preview
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.verticalGradient(gradient))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = styleName,
                                color = if (isSelected) Color(0xFFFFD700) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = desc,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = Loc.get("done", lang),
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color(0xFF0F061E),
        shape = RoundedCornerShape(20.dp)
    )
}
