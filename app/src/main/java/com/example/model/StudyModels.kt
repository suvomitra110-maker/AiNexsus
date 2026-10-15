package com.example.model

enum class ProficiencyLevel(
    val title: String,
    val subtitle: String,
    val description: String,
    val badge: String,
    val defaultXpBonus: Int,
    val scoreValue: Float // 0f to 100f numerical level
) {
    NOVICE(
        title = "Absolute Beginner",
        subtitle = "Zero prior background",
        description = "Starts with gentle visual analogies, no jargon, step-by-step intuitive concepts.",
        badge = "🌱 Starter",
        defaultXpBonus = 100,
        scoreValue = 30f
    ),
    EXPLORER(
        title = "Curious Explorer",
        subtitle = "Heard of basics / used tools",
        description = "Covers how systems connect, introductory terminology, and practical real-world usages.",
        badge = "⚡ Explorer",
        defaultXpBonus = 250,
        scoreValue = 65f
    ),
    INTERMEDIATE(
        title = "Aspiring Builder",
        subtitle = "Some coding / math intuition",
        description = "Dives into architecture details, prompting techniques, model parameters, and edge cases.",
        badge = "🚀 Builder",
        defaultXpBonus = 500,
        scoreValue = 95f
    );

    fun next(): ProficiencyLevel {
        return when (this) {
            NOVICE -> EXPLORER
            EXPLORER -> INTERMEDIATE
            INTERMEDIATE -> INTERMEDIATE
        }
    }
}

enum class SubjectCategory(
    val id: String,
    val title: String,
    val shortName: String,
    val tagLine: String,
    val iconEmoji: String,
    val chartColorHex: Long
) {
    AI_FUNDAMENTALS(
        id = "ai_fundamentals",
        title = "AI & Machine Learning Foundations",
        shortName = "AI Foundations",
        tagLine = "Neural nets, training, inference & patterns",
        iconEmoji = "🧠",
        chartColorHex = 0xFF22D3EE // CyanBright
    ),
    PROMPT_ENGINEERING(
        id = "prompt_eng",
        title = "Prompt Engineering & LLMs",
        shortName = "Prompting",
        tagLine = "Mastering inputs, system prompts & zero-shot",
        iconEmoji = "💬",
        chartColorHex = 0xFF818CF8 // Indigo
    ),
    ETHICS_SAFETY(
        id = "ethics_safety",
        title = "AI Ethics, Bias & Safe AI",
        shortName = "AI Ethics",
        tagLine = "Hallucinations, privacy, fairness & guardrails",
        iconEmoji = "🛡️",
        chartColorHex = 0xFF34D399 // Emerald
    ),
    PYTHON_FOR_AI(
        id = "python_ai",
        title = "Python & Math for AI",
        shortName = "Code & Math",
        tagLine = "Vectors, embeddings, tokens & basic scripts",
        iconEmoji = "🐍",
        chartColorHex = 0xFFFBBF24 // Amber
    )
}

data class ProficiencyTimePoint(
    val dayLabel: String,
    val scores: Map<SubjectCategory, Float> // 0f to 100f
)

data class UserProfile(
    val userName: String = "AI Student",
    val isSetupCompleted: Boolean = false,
    val subjectProficiency: Map<SubjectCategory, ProficiencyLevel> = SubjectCategory.entries.associateWith { ProficiencyLevel.NOVICE },
    val xp: Int = 120,
    val streakDays: Int = 3,
    val gems: Int = 45,
    val completedLessonIds: Set<String> = emptySet(),
    val completedQuizIds: Set<String> = emptySet(),
    val highScores: Map<String, Int> = emptyMap(),
    val unlockedBadges: List<String> = listOf("Curious Mind", "Genesis Scholar"),
    val proficiencyHistory: List<ProficiencyTimePoint> = emptyList()
)

data class LessonPlanItem(
    val id: String,
    val subject: SubjectCategory,
    val minLevel: ProficiencyLevel,
    val orderNumber: Int,
    val title: String,
    val summary: String,
    val durationMinutes: Int,
    val xpReward: Int,
    val keyTakeaways: List<String>,
    val studyContentMarkdown: String,
    val interactiveTip: String,
    val formulaOrSnippet: String? = null
)

data class QuizQuestion(
    val id: String,
    val subject: SubjectCategory,
    val difficulty: ProficiencyLevel,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val hint: String,
    val xpValue: Int
)

data class DailyQuest(
    val id: String,
    val title: String,
    val goal: Int,
    val current: Int,
    val xpReward: Int,
    val iconEmoji: String,
    val isCompleted: Boolean = false
)

data class AiResourceItem(
    val id: String,
    val title: String,
    val type: String, // "Cheatsheet", "Glossary", "Interactive Tool", "Playground"
    val subject: SubjectCategory,
    val description: String,
    val readTime: String,
    val previewSnippet: String
)
