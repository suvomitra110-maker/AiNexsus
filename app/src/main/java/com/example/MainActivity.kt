package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PomodoroTimerScreen
import com.example.ui.screens.ProfileAndProficiencyScreen
import com.example.ui.screens.QuizPlayerScreen
import com.example.ui.screens.ResourceLibraryScreen
import com.example.ui.theme.CyanBright
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val studyViewModel: StudyViewModel = viewModel()
            val uiState by studyViewModel.uiState.collectAsState()

            MyApplicationTheme(themeMode = uiState.appThemeMode) {
                MainStudyApp(studyViewModel = studyViewModel)
            }
        }
    }
}

@Composable
fun MainStudyApp(
    studyViewModel: StudyViewModel = viewModel()
) {
    val uiState by studyViewModel.uiState.collectAsState()

    val showBottomBar = uiState.currentScreen is Screen.Dashboard ||
            uiState.currentScreen is Screen.Leaderboard ||
            uiState.currentScreen is Screen.PomodoroTimer ||
            uiState.currentScreen is Screen.ResourceLibrary ||
            uiState.currentScreen is Screen.ProfileAndProficiency

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val currentScreen = uiState.currentScreen

                    NavigationBarItem(
                        selected = currentScreen is Screen.Dashboard,
                        onClick = { studyViewModel.navigateTo(Screen.Dashboard) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.Dashboard) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Study") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Leaderboard,
                        onClick = { studyViewModel.navigateTo(Screen.Leaderboard) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.Leaderboard) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                contentDescription = "Leaderboard"
                            )
                        },
                        label = { Text("Ranks") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_leaderboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.PomodoroTimer,
                        onClick = { studyViewModel.navigateTo(Screen.PomodoroTimer) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.PomodoroTimer) Icons.Filled.Timer else Icons.Outlined.Timer,
                                contentDescription = "Pomodoro"
                            )
                        },
                        label = { Text("Focus") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_pomodoro")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.ResourceLibrary,
                        onClick = { studyViewModel.navigateTo(Screen.ResourceLibrary) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.ResourceLibrary) Icons.Filled.Book else Icons.Outlined.Book,
                                contentDescription = "Resources"
                            )
                        },
                        label = { Text("Vault") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_resources")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.ProfileAndProficiency,
                        onClick = { studyViewModel.navigateTo(Screen.ProfileAndProficiency) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.ProfileAndProficiency) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (val screen = uiState.currentScreen) {
                is Screen.OnboardingProficiency -> {
                    OnboardingScreen(
                        viewModel = studyViewModel,
                        userProficiencies = uiState.userProfile.subjectProficiency
                    )
                }

                is Screen.Dashboard -> {
                    DashboardScreen(
                        viewModel = studyViewModel,
                        userProfile = uiState.userProfile,
                        dailyQuests = uiState.dailyQuests,
                        cachedLessonCount = uiState.cachedLessonCount,
                        isOfflineReady = uiState.isOfflineReady,
                        isShareStreakDialogOpen = uiState.isShareStreakDialogOpen,
                        pomodoroState = uiState.pomodoroState,
                        selectedFilter = uiState.selectedFilterSubject,
                        selectedChartSubject = uiState.selectedChartSubject,
                        activeLandmarkTheme = uiState.activeLandmarkTheme,
                        roomStreakDays = uiState.roomStreakDays,
                        roomUnlockedBadgeIds = uiState.roomUnlockedBadgeIds,
                        appThemeMode = uiState.appThemeMode,
                        celebrationMessage = uiState.celebrationMessage
                    )
                }

                is Screen.Leaderboard -> {
                    LeaderboardScreen(
                        viewModel = studyViewModel,
                        scope = uiState.leaderboardScope,
                        metric = uiState.leaderboardMetric,
                        entries = studyViewModel.getLeaderboardEntries()
                    )
                }

                is Screen.PomodoroTimer -> {
                    PomodoroTimerScreen(
                        viewModel = studyViewModel,
                        pomodoroState = uiState.pomodoroState
                    )
                }

                is Screen.LessonDetail -> {
                    LessonDetailScreen(
                        viewModel = studyViewModel,
                        lessonId = screen.lessonId,
                        userProfile = uiState.userProfile
                    )
                }

                is Screen.QuizPlayer -> {
                    QuizPlayerScreen(
                        viewModel = studyViewModel,
                        subject = screen.subject,
                        difficulty = screen.difficulty,
                        quizSession = uiState.quizSession
                    )
                }

                is Screen.ResourceLibrary -> {
                    ResourceLibraryScreen(
                        viewModel = studyViewModel,
                        searchQuery = uiState.searchQuery
                    )
                }

                is Screen.ProfileAndProficiency -> {
                    ProfileAndProficiencyScreen(
                        viewModel = studyViewModel,
                        userProfile = uiState.userProfile,
                        appThemeMode = uiState.appThemeMode
                    )
                }
            }
        }
    }
}
