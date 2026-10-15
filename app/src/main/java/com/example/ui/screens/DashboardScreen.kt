package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppThemeMode
import com.example.model.DailyQuest
import com.example.model.LandmarkTheme
import com.example.model.LessonPlanItem
import com.example.model.PomodoroState
import com.example.model.ProficiencyLevel
import com.example.model.SubjectCategory
import com.example.model.UserProfile
import com.example.ui.components.AdaptiveLevelBadge
import com.example.ui.components.GamificationStatsBar
import com.example.ui.components.KolkataLandmarkBanner
import com.example.ui.components.LeaderboardSnippetCard
import com.example.ui.components.OfflineCacheStatusBadge
import com.example.ui.components.PomodoroLauncherBanner
import com.example.ui.components.ProficiencyGrowthChart
import com.example.ui.components.RoomStreakBadgesCard
import com.example.ui.components.ShareStreakDialog
import com.example.ui.components.ThemeQuickToggleButton
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceCard
import com.example.ui.theme.NavySurfaceVariant
import com.example.viewmodel.Screen
import com.example.viewmodel.StudyViewModel

@Composable
fun DashboardScreen(
    viewModel: StudyViewModel,
    userProfile: UserProfile,
    dailyQuests: List<DailyQuest>,
    cachedLessonCount: Int,
    isOfflineReady: Boolean,
    isShareStreakDialogOpen: Boolean,
    pomodoroState: PomodoroState,
    selectedFilter: SubjectCategory?,
    selectedChartSubject: SubjectCategory?,
    activeLandmarkTheme: LandmarkTheme,
    roomStreakDays: Int,
    roomUnlockedBadgeIds: Set<String>,
    appThemeMode: AppThemeMode,
    celebrationMessage: String?,
    modifier: Modifier = Modifier
) {
    if (isShareStreakDialogOpen) {
        ShareStreakDialog(
            userName = userProfile.userName,
            streakDays = roomStreakDays,
            xp = userProfile.xp,
            unlockedBadgeTitles = userProfile.unlockedBadges,
            onDismiss = { viewModel.setShareStreakDialogOpen(false) }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SparkAi.com",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Namaste, ${userProfile.userName}! 👋",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThemeQuickToggleButton(
                        currentMode = appThemeMode,
                        onToggle = { viewModel.toggleThemeMode() }
                    )

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.navigateTo(Screen.ProfileAndProficiency) }
                            .testTag("nav_adjust_proficiency_button"),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Adjust Levels",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Levels",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Feature Banner: Kolkata, Howrah Bridge, Mountain Photo of Bengal
        item {
            KolkataLandmarkBanner(
                activeTheme = activeLandmarkTheme,
                onSelectTheme = { viewModel.setLandmarkTheme(it) }
            )
        }

        // Leaderboard Quick Shortcut Banner
        item {
            LeaderboardSnippetCard(
                userRank = 5,
                streakDays = roomStreakDays,
                onViewLeaderboard = { viewModel.navigateTo(Screen.Leaderboard) }
            )
        }

        // Pomodoro Focus Sprint Launcher
        item {
            PomodoroLauncherBanner(
                pomodoroState = pomodoroState,
                onLaunchTimer = { viewModel.navigateTo(Screen.PomodoroTimer) }
            )
        }

        // Room Offline Storage Indicator
        item {
            OfflineCacheStatusBadge(
                cachedCount = cachedLessonCount,
                isOfflineReady = isOfflineReady,
                onForceSync = { viewModel.forceSyncOfflineLessons() }
            )
        }

        if (celebrationMessage != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("celebration_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                    border = BorderStroke(1.dp, EmeraldSuccess)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎉", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = celebrationMessage,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(onClick = { viewModel.dismissCelebration() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Gamified Stats Bar (Streak, XP, Gems)
        item {
            GamificationStatsBar(userProfile = userProfile)
        }

        // Room Database Persistent Streak & Badges System with Social Share
        item {
            RoomStreakBadgesCard(
                currentStreakDays = roomStreakDays,
                unlockedBadgeIds = roomUnlockedBadgeIds,
                onAdvanceStreakTest = { viewModel.advanceStreakDayForTesting() },
                onOpenShareDialog = { viewModel.setShareStreakDialogOpen(true) }
            )
        }

        // Recharts / D3 Proficiency Growth Over Time Visualization
        item {
            ProficiencyGrowthChart(
                history = userProfile.proficiencyHistory,
                selectedSubject = selectedChartSubject,
                onSubjectToggled = { viewModel.toggleChartSubject(it) }
            )
        }

        // Daily Quests Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Quests & Challenges",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Resets in 10h",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    dailyQuests.forEach { quest ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = quest.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = quest.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = {
                                        (quest.current.toFloat() / quest.goal.toFloat()).coerceIn(0f, 1f)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (quest.isCompleted) EmeraldSuccess else CyanBright,
                                    trackColor = Color(0xFF1E293B)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (quest.isCompleted) EmeraldSuccess.copy(alpha = 0.2f)
                                        else AmberStreak.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (quest.isCompleted) "CLAIMED" else "+${quest.xpReward} XP",
                                    color = if (quest.isCompleted) EmeraldSuccess else AmberStreak,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subject Filter Carousel
        item {
            Column {
                Text(
                    text = "Personalized Subject Tracks",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { viewModel.filterSubject(null) },
                            label = { Text("All Tracks") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanBright,
                                selectedLabelColor = NavyDark,
                                containerColor = NavySurfaceCard,
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                    items(SubjectCategory.entries) { subject ->
                        val userProficiency = userProfile.subjectProficiency[subject] ?: ProficiencyLevel.NOVICE
                        FilterChip(
                            selected = selectedFilter == subject,
                            onClick = { viewModel.filterSubject(subject) },
                            label = {
                                Text("${subject.iconEmoji} ${subject.shortName} • ${userProficiency.badge}")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanBright,
                                selectedLabelColor = NavyDark,
                                containerColor = NavySurfaceCard,
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }
        }

        // Adaptive Gamified Quizzes Launcher Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("adaptive_quiz_launcher_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ElectricIndigo.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, ElectricIndigo)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ElectricIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = "Quiz",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Adaptive Gamified Quizzes",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Complexity and score points dynamically match your chosen proficiency levels.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Personalized Lesson Plans list (rendered from Room cached lessons)
        val displayedSubjects = if (selectedFilter != null) listOf(selectedFilter) else SubjectCategory.entries

        displayedSubjects.forEach { subject ->
            val userLevel = userProfile.subjectProficiency[subject] ?: ProficiencyLevel.NOVICE
            val subjectLessons = viewModel.getAdaptiveLessonsForSubject(subject)

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = subject.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = subject.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    AdaptiveLevelBadge(level = userLevel)
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.startQuizForSubject(subject) }
                        .testTag("play_quiz_button_${subject.id}"),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, CyanBright.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = "Play Quiz",
                                tint = CyanBright,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Play ${userLevel.badge} Quiz for ${subject.shortName}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Go",
                            tint = CyanBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            items(subjectLessons) { lesson ->
                val isCompleted = userProfile.completedLessonIds.contains(lesson.id)
                val isRecommended = viewModel.isLessonRecommended(lesson)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateTo(Screen.LessonDetail(lesson.id)) }
                        .testTag("lesson_card_${lesson.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
                    border = BorderStroke(
                        if (isRecommended) 1.5.dp else 1.dp,
                        if (isRecommended) CyanBright else Color(0xFF334155)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isRecommended) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyanBright.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "🎯 YOUR LEVEL",
                                            color = CyanBright,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                AdaptiveLevelBadge(level = lesson.minLevel)
                            }

                            if (isCompleted) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "DONE",
                                        color = EmeraldSuccess,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${lesson.orderNumber}. ${lesson.title}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lesson.summary,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Duration",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${lesson.durationMinutes} min",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = "+${lesson.xpReward} XP",
                                color = AmberStreak,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
