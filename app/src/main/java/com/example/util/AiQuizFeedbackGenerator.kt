package com.example.util

import com.example.data.StudyContentRepository
import com.example.model.AiQuizFeedbackAnalysis
import com.example.model.AiTopicRecommendation
import com.example.model.ProficiencyLevel
import com.example.model.QuestionReviewItem
import com.example.model.QuizQuestion
import com.example.model.SubjectCategory

object AiQuizFeedbackGenerator {

    fun generateFeedback(
        subject: SubjectCategory,
        difficulty: ProficiencyLevel,
        questions: List<QuizQuestion>,
        userAnswers: Map<Int, Int> // questionIndex -> selectedOptionIndex
    ): AiQuizFeedbackAnalysis {
        val reviewList = questions.mapIndexed { idx, q ->
            val chosen = userAnswers[idx]
            val isCorrect = chosen == q.correctIndex
            QuestionReviewItem(
                question = q,
                selectedIndex = chosen,
                isCorrect = isCorrect
            )
        }

        val total = questions.size
        val correctCount = reviewList.count { it.isCorrect }
        val missedList = reviewList.filter { !it.isCorrect }
        val accuracy = if (total > 0) (correctCount.toFloat() / total * 100).toInt() else 0

        val masterySummary = when {
            accuracy == 100 -> "Flawless Performance! You demonstrated complete conceptual mastery of ${subject.shortName} at the ${difficulty.title} tier."
            accuracy >= 70 -> "Solid Progress! You have strong core intuition in ${subject.shortName}, with just a few fine-grained technical nuances to refine."
            accuracy >= 40 -> "Good Foundation! You understand high-level concepts, but review of foundational formulas and edge cases will accelerate your score."
            else -> "Growth Opportunity Detected! Re-centering on core analogies and step-by-step mental models will dramatically improve your next run."
        }

        val strengths = mutableListOf<String>()
        val growthAreas = mutableListOf<String>()
        val recommendations = mutableListOf<AiTopicRecommendation>()

        if (correctCount > 0) {
            strengths.add("Accurately identified ${subject.shortName} pattern relationships under test conditions.")
            strengths.add("Demonstrated solid grasp of primary definitions and use-cases.")
        } else {
            strengths.add("Showed resilience by completing the full adaptive assessment.")
        }

        if (missedList.isEmpty()) {
            growthAreas.add("Ready for next difficulty level: ${difficulty.next().title}.")
            val nextLesson = StudyContentRepository.allLessons.find { it.subject == subject && it.minLevel == difficulty.next() }
            recommendations.add(
                AiTopicRecommendation(
                    topicTitle = "Advanced ${subject.shortName} Foundations",
                    subject = subject,
                    reason = "You achieved 100% accuracy. Advance to the next level of complexity.",
                    suggestedLessonId = nextLesson?.id,
                    actionText = "Explore Next Tier"
                )
            )
        } else {
            missedList.forEach { missed ->
                growthAreas.add("Revisit concept: \"${missed.question.questionText.take(48)}...\"")
            }

            // Find matching review lesson for the subject
            val reviewLesson = StudyContentRepository.allLessons.find { it.subject == subject }
            recommendations.add(
                AiTopicRecommendation(
                    topicTitle = "${subject.shortName} Core Refresher",
                    subject = subject,
                    reason = "Reviewing fundamental principles will clarify why the correct answer is \"${missedList.first().question.options[missedList.first().question.correctIndex]}\".",
                    suggestedLessonId = reviewLesson?.id,
                    actionText = "Read Review Module"
                )
            )

            // Related cross-discipline recommendation
            val altSubject = when (subject) {
                SubjectCategory.AI_FUNDAMENTALS -> SubjectCategory.PYTHON_FOR_AI
                SubjectCategory.PROMPT_ENGINEERING -> SubjectCategory.ETHICS_SAFETY
                SubjectCategory.ETHICS_SAFETY -> SubjectCategory.PROMPT_ENGINEERING
                SubjectCategory.PYTHON_FOR_AI -> SubjectCategory.AI_FUNDAMENTALS
            }
            val crossLesson = StudyContentRepository.allLessons.find { it.subject == altSubject }
            recommendations.add(
                AiTopicRecommendation(
                    topicTitle = "Complementary Track: ${altSubject.shortName}",
                    subject = altSubject,
                    reason = "Strengthening cross-domain knowledge in ${altSubject.shortName} builds stronger intuition for ${subject.shortName}.",
                    suggestedLessonId = crossLesson?.id,
                    actionText = "Open Track"
                )
            )
        }

        return AiQuizFeedbackAnalysis(
            masterySummary = masterySummary,
            keyStrengths = strengths,
            growthAreas = growthAreas,
            missedQuestionsReview = reviewList,
            recommendedTopics = recommendations
        )
    }
}
