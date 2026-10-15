package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StudyContentRepository
import com.example.data.local.AppDatabase
import com.example.data.repository.LessonRepository
import com.example.data.repository.StreakRepository
import com.example.model.AppThemeMode
import com.example.model.DailyQuest
import com.example.model.LandmarkTheme
import com.example.model.LeaderboardRankingMetric
import com.example.model.LeaderboardScope
import com.example.model.LeaderboardUserEntry
import com.example.model.LessonPlanItem
import com.example.model.PomodoroMode
import com.example.model.PomodoroState
import com.example.model.ProficiencyLevel
import com.example.model.ProficiencyTimePoint
import com.example.model.QuizQuestion
import com.example.model.StreakBadge
import com.example.model.SubjectCategory
import com.example.model.UserProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class Screen {
    data object OnboardingProficiency : Screen()
    data object Dashboard : Screen()
    data class LessonDetail(val lessonId: String) : Screen()
    data class QuizPlayer(val subject: SubjectCategory, val difficulty: ProficiencyLevel) : Screen()
    data object Leaderboard : Screen()
    data object ResourceLibrary : Screen()
    data object PomodoroTimer : Screen()
    data object ProfileAndProficiency : Screen()
}

data class QuizSessionState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val score: Int = 0,
    val earnedXp: Int = 0,
    val streakCount: Int = 0,
    val isFinished: Boolean = false,
    val showHint: Boolean = false,
    val recordedUserAnswers: Map<Int, Int> = emptyMap()
)

data class UiState(
    val currentScreen: Screen = Screen.OnboardingProficiency,
    val userProfile: UserProfile = UserProfile(),
    val dailyQuests: List<DailyQuest> = StudyContentRepository.sampleDailyQuests,
    val quizSession: QuizSessionState = QuizSessionState(),
    val cachedLessons: List<LessonPlanItem> = StudyContentRepository.allLessons,
    val cachedLessonCount: Int = 0,
    val isOfflineReady: Boolean = false,
    val isShareStreakDialogOpen: Boolean = false,
    val pomodoroState: PomodoroState = PomodoroState(),
    val leaderboardScope: LeaderboardScope = LeaderboardScope.REGIONAL_BENGAL,
    val leaderboardMetric: LeaderboardRankingMetric = LeaderboardRankingMetric.STREAK,
    val selectedFilterSubject: SubjectCategory? = null,
    val selectedChartSubject: SubjectCategory? = null,
    val activeLandmarkTheme: LandmarkTheme = LandmarkTheme.HOWRAH_BRIDGE,
    val roomStreakDays: Int = 3,
    val roomUnlockedBadgeIds: Set<String> = setOf("badge_first_spark", "badge_3_day"),
    val appThemeMode: AppThemeMode = AppThemeMode.DARK,
    val searchQuery: String = "",
    val celebrationMessage: String? = null
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val streakRepository: StreakRepository = StreakRepository(db.userStreakDao())
    private val lessonRepository: LessonRepository = LessonRepository(db.cachedLessonDao())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        val initialHistory = generateGrowthHistory(
            mapOf(
                SubjectCategory.AI_FUNDAMENTALS to ProficiencyLevel.NOVICE,
                SubjectCategory.PROMPT_ENGINEERING to ProficiencyLevel.NOVICE,
                SubjectCategory.ETHICS_SAFETY to ProficiencyLevel.NOVICE,
                SubjectCategory.PYTHON_FOR_AI to ProficiencyLevel.NOVICE
            )
        )
        _uiState.update { it.copy(userProfile = it.userProfile.copy(proficiencyHistory = initialHistory)) }

        viewModelScope.launch {
            streakRepository.streakFlow.collectLatest { entity ->
                if (entity != null) {
                    val badgeIds = entity.earnedBadgeIdsCsv.split(",").filter { it.isNotBlank() }.toSet()
                    _uiState.update { state ->
                        state.copy(
                            roomStreakDays = entity.streakDays,
                            roomUnlockedBadgeIds = badgeIds,
                            userProfile = state.userProfile.copy(
                                streakDays = entity.streakDays,
                                unlockedBadges = badgeIds.mapNotNull { bId ->
                                    StreakBadge.entries.find { it.id == bId }?.title
                                }
                            )
                        )
                    }
                } else {
                    streakRepository.getInitialOrCurrentStreak()
                }
            }
        }

        viewModelScope.launch {
            val count = lessonRepository.ensureLessonsCachedLocally()
            _uiState.update { it.copy(cachedLessonCount = count, isOfflineReady = true) }

            lessonRepository.allCachedLessonsFlow.collectLatest { lessons ->
                if (lessons.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            cachedLessons = lessons,
                            cachedLessonCount = lessons.size,
                            isOfflineReady = true
                        )
                    }
                }
            }
        }
    }

    private fun generateGrowthHistory(currentLevels: Map<SubjectCategory, ProficiencyLevel>): List<ProficiencyTimePoint> {
        val days = listOf("Day 1", "Day 3", "Day 7", "Day 14", "Today")
        val factors = listOf(0.4f, 0.55f, 0.75f, 0.9f, 1.0f)

        return days.mapIndexed { idx, day ->
            val factor = factors[idx]
            val scores = SubjectCategory.entries.associateWith { subj ->
                val target = (currentLevels[subj] ?: ProficiencyLevel.NOVICE).scoreValue
                (target * factor).coerceIn(10f, 100f)
            }
            ProficiencyTimePoint(dayLabel = day, scores = scores)
        }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen, celebrationMessage = null) }
    }

    // --- LEADERBOARD LOGIC ---
    fun setLeaderboardScope(scope: LeaderboardScope) {
        _uiState.update { it.copy(leaderboardScope = scope) }
    }

    fun setLeaderboardMetric(metric: LeaderboardRankingMetric) {
        _uiState.update { it.copy(leaderboardMetric = metric) }
    }

    fun getLeaderboardEntries(): List<LeaderboardUserEntry> {
        val state = _uiState.value
        val isRegional = state.leaderboardScope == LeaderboardScope.REGIONAL_BENGAL
        val currentStreak = state.roomStreakDays
        val currentXp = state.userProfile.xp
        val currentUserName = state.userProfile.userName

        val peers = if (isRegional) {
            listOf(
                LeaderboardUserEntry(1, "Ananya Roy", "Kolkata, Salt Lake", "👩‍💻", 28, 3420, 96, "👑 Bengal Scholar"),
                LeaderboardUserEntry(2, "Debojyoti Sen", "Howrah", "🚀", 22, 2850, 94, "⛰️ Himalayan Peak"),
                LeaderboardUserEntry(3, "Priya Mukherjee", "Kolkata, Park Circus", "💡", 17, 2190, 92, "🏛️ Victoria Crest"),
                LeaderboardUserEntry(4, "Rahul Banerjee", "Darjeeling", "☕", 12, 1740, 89, "🌉 Howrah Pillar"),
                LeaderboardUserEntry(5, "Subham Ghosh", "Kolkata, New Town", "🐍", 8, 1260, 88, "🌊 Hooghly Current"),
                LeaderboardUserEntry(6, "Tanushree Das", "Siliguri", "🔬", 6, 920, 85, "🌊 Hooghly Current"),
                LeaderboardUserEntry(7, "Arindam Paul", "Durgapur", "🌱", 4, 680, 82, "🌱 First Spark")
            )
        } else {
            listOf(
                LeaderboardUserEntry(1, "Aiden Vance", "San Francisco, USA", "⚡", 45, 6200, 98, "👑 Global AI Lead"),
                LeaderboardUserEntry(2, "Mei-Ling Chen", "Singapore", "🧠", 39, 5450, 97, "👑 Global Scholar"),
                LeaderboardUserEntry(3, "Carlos Silva", "São Paulo, Brazil", "🔥", 31, 4600, 95, "⛰️ Himalayan Peak"),
                LeaderboardUserEntry(4, "Ananya Roy", "Kolkata, India", "👩‍💻", 28, 3420, 96, "👑 Bengal Scholar"),
                LeaderboardUserEntry(5, "Liam O'Connor", "Dublin, Ireland", "🎯", 20, 2710, 91, "🏛️ Victoria Crest"),
                LeaderboardUserEntry(6, "Kenji Sato", "Tokyo, Japan", "🤖", 15, 2050, 90, "🌉 Howrah Pillar"),
                LeaderboardUserEntry(7, "Zainab Al-Mansoor", "Dubai, UAE", "✨", 10, 1420, 87, "🌊 Hooghly Current")
            )
        }

        // Current User entry
        val topBadge = if (currentStreak >= 30) "👑 Bengal Scholar"
            else if (currentStreak >= 14) "⛰️ Himalayan Peak"
            else if (currentStreak >= 7) "🏛️ Victoria Crest"
            else if (currentStreak >= 5) "🌉 Howrah Pillar"
            else if (currentStreak >= 3) "🌊 Hooghly Current"
            else "🌱 First Spark"

        val currentUserEntry = LeaderboardUserEntry(
            rank = 0, // calculated below
            name = "$currentUserName (You)",
            location = if (isRegional) "Kolkata (Your Device)" else "India (Your Device)",
            avatarEmoji = "⭐",
            streakDays = currentStreak,
            xpSparks = currentXp,
            quizAccuracyPercent = 90,
            topBadge = topBadge,
            isCurrentUser = true
        )

        val combined = (peers + currentUserEntry)
        val sorted = if (state.leaderboardMetric == LeaderboardRankingMetric.STREAK) {
            combined.sortedByDescending { it.streakDays }
        } else {
            combined.sortedByDescending { it.xpSparks }
        }

        return sorted.mapIndexed { idx, item ->
            item.copy(rank = idx + 1)
        }
    }

    // --- POMODORO TIMER LOGIC ---
    fun setPomodoroMode(mode: PomodoroMode) {
        timerJob?.cancel()
        val secs = mode.defaultMinutes * 60
        _uiState.update { state ->
            state.copy(
                pomodoroState = state.pomodoroState.copy(
                    mode = mode,
                    totalSeconds = secs,
                    remainingSeconds = secs,
                    isRunning = false
                )
            )
        }
    }

    fun setPomodoroCustomMinutes(minutes: Int) {
        timerJob?.cancel()
        val secs = minutes.coerceIn(1, 120) * 60
        _uiState.update { state ->
            state.copy(
                pomodoroState = state.pomodoroState.copy(
                    totalSeconds = secs,
                    remainingSeconds = secs,
                    isRunning = false
                )
            )
        }
    }

    fun togglePomodoroTimer() {
        val isRunning = _uiState.value.pomodoroState.isRunning
        if (isRunning) {
            pausePomodoroTimer()
        } else {
            startPomodoroTimer()
        }
    }

    private fun startPomodoroTimer() {
        timerJob?.cancel()
        _uiState.update { state ->
            state.copy(pomodoroState = state.pomodoroState.copy(isRunning = true))
        }

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentRemaining = _uiState.value.pomodoroState.remainingSeconds
                if (currentRemaining > 1) {
                    _uiState.update { state ->
                        state.copy(
                            pomodoroState = state.pomodoroState.copy(
                                remainingSeconds = currentRemaining - 1
                            )
                        )
                    }
                } else {
                    onPomodoroCompleted()
                    break
                }
            }
        }
    }

    private fun pausePomodoroTimer() {
        timerJob?.cancel()
        _uiState.update { state ->
            state.copy(pomodoroState = state.pomodoroState.copy(isRunning = false))
        }
    }

    fun resetPomodoroTimer() {
        timerJob?.cancel()
        val totalSecs = _uiState.value.pomodoroState.totalSeconds
        _uiState.update { state ->
            state.copy(
                pomodoroState = state.pomodoroState.copy(
                    remainingSeconds = totalSecs,
                    isRunning = false
                )
            )
        }
    }

    private fun onPomodoroCompleted() {
        timerJob?.cancel()
        val currentMode = _uiState.value.pomodoroState.mode
        val focusedMins = _uiState.value.pomodoroState.totalSeconds / 60
        val isFocus = currentMode == PomodoroMode.FOCUS

        val earnedXp = if (isFocus) 40 else 10

        viewModelScope.launch {
            if (isFocus) {
                streakRepository.recordStudySession()
            }

            _uiState.update { state ->
                val nextSessions = state.pomodoroState.completedSessionsCount + 1
                val totalMins = state.pomodoroState.totalFocusedMinutes + (if (isFocus) focusedMins else 0)
                val newXp = state.userProfile.xp + earnedXp

                state.copy(
                    pomodoroState = state.pomodoroState.copy(
                        remainingSeconds = 0,
                        isRunning = false,
                        completedSessionsCount = nextSessions,
                        totalFocusedMinutes = totalMins
                    ),
                    userProfile = state.userProfile.copy(xp = newXp),
                    celebrationMessage = if (isFocus) {
                        "🎉 Pomodoro Focus Sprint Completed! +$earnedXp XP & Study Streak recorded."
                    } else "☕ Break finished! Ready for the next AI study session."
                )
            }
        }
    }

    // --- OTHER CORE ACTIONS ---
    fun setShareStreakDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isShareStreakDialogOpen = open) }
    }

    fun updateSubjectProficiency(subject: SubjectCategory, level: ProficiencyLevel) {
        _uiState.update { state ->
            val updatedMap = state.userProfile.subjectProficiency.toMutableMap()
            updatedMap[subject] = level
            val newHistory = generateGrowthHistory(updatedMap)
            val updatedProfile = state.userProfile.copy(
                subjectProficiency = updatedMap,
                proficiencyHistory = newHistory
            )
            state.copy(userProfile = updatedProfile)
        }
    }

    fun completeOnboarding(userName: String) {
        _uiState.update { state ->
            val cleanName = userName.ifBlank { "AI Student" }
            val startingXp = 100 + state.userProfile.subjectProficiency.values.sumOf { it.defaultXpBonus }
            val newHistory = generateGrowthHistory(state.userProfile.subjectProficiency)
            val updatedProfile = state.userProfile.copy(
                userName = cleanName,
                isSetupCompleted = true,
                xp = startingXp,
                proficiencyHistory = newHistory
            )
            state.copy(
                userProfile = updatedProfile,
                currentScreen = Screen.Dashboard,
                celebrationMessage = "Welcome $cleanName! Leaderboards and lesson plans active."
            )
        }
    }

    fun getAdaptiveLessonsForSubject(subject: SubjectCategory): List<LessonPlanItem> {
        val lessons = _uiState.value.cachedLessons
        val allSubjectLessons = lessons.filter { it.subject == subject }
        return allSubjectLessons.sortedBy { it.orderNumber }
    }

    fun isLessonRecommended(lesson: LessonPlanItem): Boolean {
        val userLevel = _uiState.value.userProfile.subjectProficiency[lesson.subject] ?: ProficiencyLevel.NOVICE
        return lesson.minLevel == userLevel
    }

    fun completeLesson(lessonId: String, xp: Int) {
        viewModelScope.launch {
            val (_, newBadges) = streakRepository.recordStudySession()

            _uiState.update { state ->
                val completed = state.userProfile.completedLessonIds + lessonId
                val newXp = state.userProfile.xp + xp + (newBadges.sumOf { it.xpBonus })
                val newGems = state.userProfile.gems + 5
                val updatedQuests = state.dailyQuests.map { q ->
                    if (q.id == "quest_1") q.copy(current = 1, isCompleted = true) else q
                }

                val targetLesson = state.cachedLessons.find { it.id == lessonId }
                val updatedHistory = if (targetLesson != null && state.userProfile.proficiencyHistory.isNotEmpty()) {
                    val lastPoint = state.userProfile.proficiencyHistory.last()
                    val currentScore = lastPoint.scores[targetLesson.subject] ?: 30f
                    val boostedScores = lastPoint.scores.toMutableMap()
                    boostedScores[targetLesson.subject] = (currentScore + 6f).coerceAtMost(100f)
                    state.userProfile.proficiencyHistory.dropLast(1) + lastPoint.copy(scores = boostedScores)
                } else state.userProfile.proficiencyHistory

                val celebration = if (newBadges.isNotEmpty()) {
                    "🏆 New Room Badge: ${newBadges.first().title}! +$xp XP"
                } else "+$xp XP earned! Room streak saved."

                state.copy(
                    userProfile = state.userProfile.copy(
                        completedLessonIds = completed,
                        xp = newXp,
                        gems = newGems,
                        proficiencyHistory = updatedHistory
                    ),
                    dailyQuests = updatedQuests,
                    celebrationMessage = celebration
                )
            }
        }
    }

    fun advanceStreakDayForTesting() {
        viewModelScope.launch {
            val (updatedRecord, newBadges) = streakRepository.advanceStreakDayForTesting()
            val badgeMessage = if (newBadges.isNotEmpty()) {
                "🎖️ UNLOCKED: ${newBadges.joinToString { it.title }}! Streak: ${updatedRecord.streakDays} Days"
            } else "Streak increased to ${updatedRecord.streakDays} Days in Room database!"

            _uiState.update { state ->
                state.copy(celebrationMessage = badgeMessage)
            }
        }
    }

    fun forceSyncOfflineLessons() {
        viewModelScope.launch {
            val count = lessonRepository.refreshCache()
            _uiState.update {
                it.copy(
                    cachedLessonCount = count,
                    celebrationMessage = "⚡ $count lessons successfully re-synced & cached into Room!"
                )
            }
        }
    }

    fun startQuizForSubject(subject: SubjectCategory) {
        val difficulty = _uiState.value.userProfile.subjectProficiency[subject] ?: ProficiencyLevel.NOVICE
        startQuizWithSettings(subject, difficulty)
    }

    fun startQuizWithSettings(subject: SubjectCategory, difficulty: ProficiencyLevel) {
        val filteredQuestions = StudyContentRepository.allQuizzes.filter {
            it.subject == subject && it.difficulty == difficulty
        }.ifEmpty {
            StudyContentRepository.allQuizzes.filter { it.subject == subject }
        }

        _uiState.update { state ->
            state.copy(
                currentScreen = Screen.QuizPlayer(subject, difficulty),
                quizSession = QuizSessionState(
                    questions = filteredQuestions,
                    currentIndex = 0,
                    selectedOptionIndex = null,
                    isAnswerSubmitted = false,
                    score = 0,
                    earnedXp = 0,
                    streakCount = 0,
                    isFinished = false,
                    showHint = false
                )
            )
        }
    }

    fun selectQuizOption(index: Int) {
        val session = _uiState.value.quizSession
        if (session.isAnswerSubmitted || session.isFinished) return
        _uiState.update { state ->
            state.copy(quizSession = session.copy(selectedOptionIndex = index))
        }
    }

    fun toggleHint() {
        _uiState.update { state ->
            val current = state.quizSession.showHint
            state.copy(quizSession = state.quizSession.copy(showHint = !current))
        }
    }

    fun submitAnswer() {
        val session = _uiState.value.quizSession
        if (session.selectedOptionIndex == null || session.isAnswerSubmitted) return

        val currentQuestion = session.questions[session.currentIndex]
        val isCorrect = session.selectedOptionIndex == currentQuestion.correctIndex
        val addedXp = if (isCorrect) currentQuestion.xpValue else 5
        val newStreak = if (isCorrect) session.streakCount + 1 else 0
        val updatedAnswers = session.recordedUserAnswers.toMutableMap()
        updatedAnswers[session.currentIndex] = session.selectedOptionIndex

        _uiState.update { state ->
            state.copy(
                quizSession = session.copy(
                    isAnswerSubmitted = true,
                    score = if (isCorrect) session.score + 1 else session.score,
                    earnedXp = session.earnedXp + addedXp,
                    streakCount = newStreak,
                    recordedUserAnswers = updatedAnswers
                )
            )
        }
    }

    fun nextQuizQuestion() {
        val session = _uiState.value.quizSession
        if (session.currentIndex + 1 < session.questions.size) {
            _uiState.update { state ->
                state.copy(
                    quizSession = session.copy(
                        currentIndex = session.currentIndex + 1,
                        selectedOptionIndex = null,
                        isAnswerSubmitted = false,
                        showHint = false
                    )
                )
            }
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val session = _uiState.value.quizSession
        viewModelScope.launch {
            streakRepository.recordStudySession()

            _uiState.update { state ->
                val user = state.userProfile
                val newXp = user.xp + session.earnedXp
                val newGems = user.gems + (session.score * 3)
                val updatedQuests = state.dailyQuests.map { q ->
                    if (q.id == "quest_2" && session.score > 0) q.copy(current = 1, isCompleted = true) else q
                }
                state.copy(
                    quizSession = session.copy(isFinished = true),
                    userProfile = user.copy(
                        xp = newXp,
                        gems = newGems
                    ),
                    dailyQuests = updatedQuests
                )
            }
        }
    }

    fun filterSubject(subject: SubjectCategory?) {
        _uiState.update { it.copy(selectedFilterSubject = subject) }
    }

    fun toggleChartSubject(subject: SubjectCategory?) {
        _uiState.update { it.copy(selectedChartSubject = subject) }
    }

    fun setLandmarkTheme(theme: LandmarkTheme) {
        _uiState.update { it.copy(activeLandmarkTheme = theme) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.update { it.copy(appThemeMode = mode) }
    }

    fun toggleThemeMode() {
        _uiState.update { current ->
            val nextMode = when (current.appThemeMode) {
                AppThemeMode.DARK -> AppThemeMode.LIGHT
                AppThemeMode.LIGHT -> AppThemeMode.DARK
                AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
            }
            current.copy(appThemeMode = nextMode)
        }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(celebrationMessage = null) }
    }
}
