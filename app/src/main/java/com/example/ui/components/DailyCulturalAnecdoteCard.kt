package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FallbackData
import com.example.ui.utils.Loc
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data structure representing a regional cultural folk anecdote.
 */
data class RegionalStory(
    val id: String,
    val cultureKey: String,
    val cultureName: String,
    val flagEmoji: String,
    val regionTag: String,
    val text: String,
    val moral: String = "",
    val accentColor: Color = Color(0xFFFFD700),
    val gradientColors: List<Color> = listOf(Color(0xFF26123D), Color(0xFF140727))
)

/**
 * Interactive Daily Cultural Folk Anecdote Card component.
 * Allows users to swipe horizontally (via HorizontalPager or gesture controls) between
 * authentic stories from diverse world cultures & regions, complete with TTS narration,
 * bookmarking, copy/share, and visual indicator dots.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DailyCulturalAnecdoteCard(
    viewModel: AppViewModel,
    lang: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isRussian = lang == "Russian" || lang == "Русский"

    // Today's formatted date string
    val todayFormatted = remember(lang) {
        val locale = if (isRussian) Locale("ru", "RU") else Locale.getDefault()
        SimpleDateFormat("d MMMM", locale).format(Date())
    }

    // Build curated regional cultural stories
    val regionalStories = remember(isRussian, lang) {
        listOf(
            RegionalStory(
                id = "uzbek_daily",
                cultureKey = "Uzbek",
                cultureName = Loc.getCultureName("Uzbek", lang),
                flagEmoji = "🇺🇿",
                regionTag = if (isRussian) "Шелковый путь • Бухара" else "Silk Road • Bukhara",
                text = FallbackData.getFallbackJokes("Uzbek", isRussian).firstOrNull() ?: "",
                moral = if (isRussian) "Мудрость мысли ценнее тысячи пустых слов." else "The wisdom of thought is worth a thousand empty words.",
                accentColor = Color(0xFFFFD700),
                gradientColors = listOf(Color(0xFF2E1948), Color(0xFF160927))
            ),
            RegionalStory(
                id = "armenian_daily",
                cultureKey = "Armenian",
                cultureName = Loc.getCultureName("Armenian", lang),
                flagEmoji = "🇦🇲",
                regionTag = if (isRussian) "Кавказ • Ереван" else "Caucasus • Yerevan",
                text = FallbackData.getFallbackJokes("Armenian", isRussian).getOrNull(1) ?: "",
                moral = if (isRussian) "Истинное гостеприимство измеряется теплом сердца." else "True hospitality is measured by the warmth of one's heart.",
                accentColor = Color(0xFFFF9E00),
                gradientColors = listOf(Color(0xFF33162A), Color(0xFF1A0A1C))
            ),
            RegionalStory(
                id = "jewish_daily",
                cultureKey = "Jewish",
                cultureName = Loc.getCultureName("Jewish", lang),
                flagEmoji = "✡️",
                regionTag = if (isRussian) "Одесса • Привоз" else "Odessa • Seaside Humour",
                text = FallbackData.getFallbackJokes("Jewish", isRussian).getOrNull(2) ?: "",
                moral = if (isRussian) "Счастье познается в сравнении и умении радоваться малому." else "Happiness is realized when you learn to appreciate quiet simplicity.",
                accentColor = Color(0xFF00FFCC),
                gradientColors = listOf(Color(0xFF112938), Color(0xFF0A1420))
            ),
            RegionalStory(
                id = "chukchi_daily",
                cultureKey = "Chukchi",
                cultureName = Loc.getCultureName("Chukchi", lang),
                flagEmoji = "🏹",
                regionTag = if (isRussian) "Крайний Север • Берингов пролив" else "Arctic Tundra • Bering Strait",
                text = FallbackData.getFallbackJokes("Chukchi", isRussian).firstOrNull() ?: "",
                moral = if (isRussian) "Улыбка и радио согреют даже в сорокаградусный мороз." else "Humor and good broadcast bring warmth even in bitter cold.",
                accentColor = Color(0xFF64B5F6),
                gradientColors = listOf(Color(0xFF132238), Color(0xFF0B1220))
            ),
            RegionalStory(
                id = "yakut_daily",
                cultureKey = "Yakut",
                cultureName = Loc.getCultureName("Yakut", lang),
                flagEmoji = "❄️",
                regionTag = if (isRussian) "Сибирь • Полюс холода" else "Siberia • Taiga Depths",
                text = FallbackData.getFallbackJokes("Yakut", isRussian).getOrNull(1) ?: "",
                moral = if (isRussian) "Пока в избе горит огонь и кипит чай — зима бессильна." else "As long as the hearth is warm, winter holds no dominion.",
                accentColor = Color(0xFF80DEEA),
                gradientColors = listOf(Color(0xFF102830), Color(0xFF08151C))
            ),
            RegionalStory(
                id = "kamchatka_daily",
                cultureKey = "Kamchatka",
                cultureName = Loc.getCultureName("Kamchatka", lang),
                flagEmoji = "🌋",
                regionTag = if (isRussian) "Тихий океан • Долина Гейзеров" else "Pacific Rim • Geyser Valley",
                text = FallbackData.getFallbackJokes("Kamchatka", isRussian).firstOrNull() ?: "",
                moral = if (isRussian) "Хорошая песня отгонит любые тревоги в пути." else "A good song repels wild worries on every path.",
                accentColor = Color(0xFFFF5252),
                gradientColors = listOf(Color(0xFF38141C), Color(0xFF1C090F))
            ),
            RegionalStory(
                id = "american_daily",
                cultureKey = "American",
                cultureName = Loc.getCultureName("American", lang),
                flagEmoji = "🤠",
                regionTag = if (isRussian) "Дикий Запад • Техасские прерии" else "Wild West • Prairie Saloon",
                text = FallbackData.getFallbackJokes("American", isRussian).firstOrNull() ?: "",
                moral = if (isRussian) "Самоирония — лучший щит в любой потасовке." else "Quick self-humor disarms the roughest brawls.",
                accentColor = Color(0xFFFFB300),
                gradientColors = listOf(Color(0xFF2E2211), Color(0xFF1A1308))
            ),
            RegionalStory(
                id = "african_daily",
                cultureKey = "African",
                cultureName = Loc.getCultureName("African", lang),
                flagEmoji = "🦁",
                regionTag = if (isRussian) "Саванна • Баобаб Мудрости" else "Savannah • Baobab Shade",
                text = FallbackData.getFallbackJokes("African", isRussian).firstOrNull() ?: "",
                moral = if (isRussian) "Тот, кто умеет смеяться над собой, непобедим." else "Those who laugh at themselves can never be humiliated.",
                accentColor = Color(0xFFFF7043),
                gradientColors = listOf(Color(0xFF331A10), Color(0xFF1C0D07))
            )
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { regionalStories.size })
    val currentStory = regionalStories.getOrNull(pagerState.currentPage) ?: regionalStories.first()

    val ttsSpeaking by viewModel.ttsSpeaking.collectAsState()
    val ttsCurrentId by viewModel.ttsCurrentSpokenId.collectAsState()
    val isPlayingCurrent = ttsSpeaking && ttsCurrentId == "daily_${currentStory.id}"

    // Saved state tracking map
    val savedStoriesMap = remember { mutableStateMapOf<String, Boolean>() }
    val isCurrentSaved = savedStoriesMap[currentStory.id] == true

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_cultural_anecdote_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(
                    currentStory.accentColor.copy(alpha = 0.8f),
                    Color(0xFF00FFCC).copy(alpha = 0.4f),
                    currentStory.accentColor.copy(alpha = 0.8f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .background(Brush.verticalGradient(currentStory.gradientColors))
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Header: Date badge, Daily label, Page indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = currentStory.accentColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "📜", fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isRussian) "Фольклор дня • $todayFormatted" else "Daily Anecdote • $todayFormatted",
                                    color = currentStory.accentColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x3300FFCC)
                                ) {
                                    Text(
                                        text = "SWIPE ⇄",
                                        color = Color(0xFF00FFCC),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = currentStory.regionTag,
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Region Index Counter chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1}/${regionalStories.size}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Swipeable Horizontal Pager for regional stories
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("regional_stories_pager")
                ) { page ->
                    val story = regionalStories[page]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Region Title Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = story.flagEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = story.cultureName,
                                color = story.accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Anecdote / Story Body Text
                        Text(
                            text = story.text,
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.testTag("daily_anecdote_text")
                        )

                        if (story.moral.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, story.accentColor.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "💡", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = story.moral,
                                        color = story.accentColor,
                                        fontSize = 12.sp,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dots indicator and manual prev/next arrow controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (pagerState.currentPage > 0) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage > 0,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("daily_anecdote_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Region",
                            tint = if (pagerState.currentPage > 0) Color.White else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Dot indicators for all regional stories
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        regionalStories.forEachIndexed { index, story ->
                            val isSelected = pagerState.currentPage == index
                            val targetWidth = if (isSelected) 18.dp else 6.dp
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(targetWidth)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) story.accentColor else Color.White.copy(alpha = 0.25f)
                                    )
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (pagerState.currentPage < regionalStories.size - 1) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage < regionalStories.size - 1,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("daily_anecdote_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Region",
                            tint = if (pagerState.currentPage < regionalStories.size - 1) Color.White else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Action Buttons: Listen to Elder (TTS), Save to Favorites, Copy, Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play TTS Narration Button
                    Button(
                        onClick = {
                            val title = if (isRussian) "Фольклор дня (${currentStory.cultureName})" else "Daily Anecdote (${currentStory.cultureName})"
                            viewModel.ttsManager.speakSingle("daily_${currentStory.id}", currentStory.text, title, lang)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlayingCurrent) Color(0xFF00FFCC) else currentStory.accentColor,
                            contentColor = Color(0xFF140727)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("daily_anecdote_listen_button")
                    ) {
                        Icon(
                            imageVector = if (isPlayingCurrent) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Listen",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Loc.get("listen_elder_btn", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Bookmark / Save to TOP
                        IconButton(
                            onClick = {
                                viewModel.toggleSaveJoke(currentStory.text, currentStory.cultureKey, "Daily Cultural Anecdote")
                                val newState = !isCurrentSaved
                                savedStoriesMap[currentStory.id] = newState
                                Toast.makeText(
                                    context,
                                    if (newState) Loc.get("added_favorites", lang) else Loc.get("removed_favorites", lang),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("daily_anecdote_save_button")
                        ) {
                            Icon(
                                imageVector = if (isCurrentSaved) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Save",
                                tint = if (isCurrentSaved) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Copy to clipboard
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Daily Anecdote", "${currentStory.cultureName}:\n${currentStory.text}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    if (isRussian) "Анекдот скопирован в буфер!" else "Story copied to clipboard!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("daily_anecdote_copy_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Share
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Фольклорный анекдот дня (${currentStory.cultureName})")
                                    putExtra(Intent.EXTRA_TEXT, "✨ ${currentStory.cultureName} [${currentStory.regionTag}]:\n\n${currentStory.text}\n\n— Из приложения Folklore Anecdotes & Radio 📻")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Поделиться анекдотом"))
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("daily_anecdote_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
