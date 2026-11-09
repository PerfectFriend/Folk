package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.utils.Loc
import com.example.ui.viewmodel.AppViewModel

/**
 * Dedicated persistent RadioPlayer UI component.
 * Provides play, pause, stop, station switching, seek-to-live, and volume controls,
 * backed by an Android MediaPlayer background service for continuous live streaming.
 */
@Composable
fun RadioPlayer(
    viewModel: AppViewModel,
    lang: String,
    modifier: Modifier = Modifier
) {
    val isPlaying by viewModel.isRadioPlaying.collectAsState()
    val isLoading by viewModel.isRadioLoading.collectAsState()
    val playingStation by viewModel.selectedStation.collectAsState()
    val radioStations by viewModel.radioStations.collectAsState()
    val positionMs by viewModel.radioPositionMs.collectAsState()
    val durationMs by viewModel.radioDurationMs.collectAsState()
    val isLiveEdge by viewModel.isLiveEdge.collectAsState()
    val volume by viewModel.radioVolume.collectAsState()
    val sleepTimerSeconds by viewModel.sleepTimerSeconds.collectAsState()

    val isRussian = lang == "Russian" || lang == "Русский"

    // Active station fallback
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

    // Animated pulse for LIVE stream indicator
    val infiniteTransition = rememberInfiniteTransition(label = "radio_player_pulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("persistent_radio_player"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF140727).copy(alpha = 0.96f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFFFD700).copy(alpha = if (isPlaying) 0.6f else 0.3f),
                    Color(0xFF00FFCC).copy(alpha = if (isPlaying) 0.5f else 0.2f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // --- TOP HEADER: Station Info, Animated Visualizer, Live Badge, and Action Buttons ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Station Avatar / Equalizer Icon
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
                            // Animated mini visualizer bars
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
                                        label = "eq_bar_$barIdx"
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
                            modifier = Modifier.testTag("radio_player_station_name")
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pulsing live stream indicator dot
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
                                    .testTag("radio_player_live_indicator")
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = when {
                                    isPlaying -> if (isRussian) "🔴 В ЭФИРЕ • MediaPlayer Service" else "🔴 LIVE • MediaPlayer Service"
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

                // Quick Header Actions: Sleep Timer badge, Favorite toggle, Expand Drawer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (sleepTimerSeconds > 0) {
                        Surface(
                            onClick = { viewModel.cancelSleepTimer() },
                            color = Color(0xFF00FFCC).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF00FFCC).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("radio_player_sleep_timer_badge")
                        ) {
                            Text(
                                text = "🌙 ${sleepTimerSeconds / 60}:${String.format("%02d", sleepTimerSeconds % 60)}",
                                color = Color(0xFF00FFCC),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    activeStation?.let { st ->
                        IconButton(
                            onClick = { viewModel.toggleRadioFavorite(st) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("radio_player_favorite_toggle")
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
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("radio_player_expand_toggle")
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

            // --- STREAM BUFFER & LIVE POSITION BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Elapsed session streaming time
                Text(
                    text = formatPlayerStreamTime(if (isDraggingSlider) (userSliderPos * effectiveMaxMs).toLong() else positionMs),
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
                        .testTag("radio_player_seek_slider"),
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
                        .testTag("radio_player_jump_to_live_button")
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

            // --- PLAYBACK CONTROLS: Prev, Play/Pause, Next, Stop ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .testTag("radio_player_control_bar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Stream info hint
                Text(
                    text = if (activeStation?.isDefault == true) "Live Radio Service Stream" else "Custom Stream",
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
                            .size(36.dp)
                            .testTag("radio_player_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = Loc.get("prev_station", lang),
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Main Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                            .testTag("radio_player_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color(0xFF140727),
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF140727),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Next Station button
                    IconButton(
                        onClick = { viewModel.playNextStation() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("radio_player_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = Loc.get("next_station", lang),
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Stop button
                    if (isPlaying || isLoading) {
                        IconButton(
                            onClick = { viewModel.stopRadio() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("radio_player_stop_button")
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

            // --- VOLUME CONTROLS BAR: Mute Button, Volume Slider, Percentage ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .testTag("radio_player_volume_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.toggleRadioMute() },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("radio_player_mute_button")
                ) {
                    val volumeIcon = when {
                        volume <= 0.01f -> Icons.AutoMirrored.Filled.VolumeOff
                        volume < 0.5f -> Icons.AutoMirrored.Filled.VolumeDown
                        else -> Icons.AutoMirrored.Filled.VolumeUp
                    }
                    Icon(
                        imageVector = volumeIcon,
                        contentDescription = "Mute / Unmute",
                        tint = if (volume > 0.01f) Color(0xFFFFD700) else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = Loc.get("volume_title", lang),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Slider(
                    value = volume,
                    onValueChange = { viewModel.setRadioVolume(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .testTag("radio_player_volume_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFD700),
                        activeTrackColor = Color(0xFFFFD700),
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )

                Text(
                    text = "${(volume * 100).toInt()}%",
                    color = Color(0xFFFFD700),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .width(34.dp)
                        .testTag("radio_player_volume_text"),
                    textAlign = TextAlign.End
                )
            }

            // --- EXPANDABLE DRAWER: Sleep Timer & Quick Station Carousel ---
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    // Sleep Timer row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Loc.get("sleep_timer_title", lang),
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (sleepTimerSeconds > 0) {
                            TextButton(
                                onClick = { viewModel.cancelSleepTimer() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = Loc.get("cancel", lang),
                                    color = Color(0xFFFF5252),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val timerPresets = listOf(15, 30, 45, 60)
                        timerPresets.forEach { minutes ->
                            val isSelected = sleepTimerSeconds in ((minutes - 1) * 60)..(minutes * 60)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSleepTimerMinutes(minutes) },
                                label = {
                                    Text(
                                        text = "$minutes ${Loc.get("minutes_short", lang)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00FFCC),
                                    selectedLabelColor = Color(0xFF140727),
                                    containerColor = Color.White.copy(alpha = 0.08f),
                                    labelColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = Color(0xFF00FFCC),
                                    borderColor = Color.White.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("radio_player_timer_chip_$minutes")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Stations Carousel
                    Text(
                        text = if (isRussian) "Быстрое переключение станций:" else "Quick Station Switch:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(radioStations) { st ->
                            val isCurrent = activeStation?.id == st.id
                            Surface(
                                onClick = { viewModel.playRadio(st) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) Color(0xFFFFD700).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isCurrent) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isCurrent && isPlaying) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF00FFCC))
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = st.name,
                                        color = if (isCurrent) Color(0xFFFFD700) else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
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

private fun formatPlayerStreamTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
