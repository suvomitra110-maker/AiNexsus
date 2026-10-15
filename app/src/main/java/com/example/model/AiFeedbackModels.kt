package com.example.model

data class QuestionReviewItem(
    val question: QuizQuestion,
    val selectedIndex: Int?,
    val isCorrect: Boolean
)

data class AiTopicRecommendation(
    val topicTitle: String,
    val subject: SubjectCategory,
    val reason: String,
    val suggestedLessonId: String? = null,
    val actionText: String = "Review Module"
)

data class AiQuizFeedbackAnalysis(
    val masterySummary: String,
    val keyStrengths: List<String>,
    val growthAreas: List<String>,
    val missedQuestionsReview: List<QuestionReviewItem>,
    val recommendedTopics: List<AiTopicRecommendation>
)
