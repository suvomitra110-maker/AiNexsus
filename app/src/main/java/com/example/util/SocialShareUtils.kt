package com.example.util

import android.content.Context
import android.content.Intent
import com.example.model.StreakBadge

object SocialShareUtils {

    fun shareStreakAndBadges(
        context: Context,
        userName: String,
        streakDays: Int,
        xp: Int,
        unlockedBadgeTitles: List<String>
    ) {
        val badgesSummary = if (unlockedBadgeTitles.isNotEmpty()) {
            unlockedBadgeTitles.take(3).joinToString(separator = ", ")
        } else {
            "First Spark 🌱"
        }

        val shareText = """
            🔥 I'm on a $streakDays-day AI study streak with $xp XP on SparkAi.com!
            
            🎖️ Badges Unlocked: $badgesSummary
            🌉 Learning AI, Prompting & Ethics with Kolkata & Bengal heritage visuals!
            
            Join me on SparkAi.com and build your AI study streak! 🚀
            #SparkAi #AIStudy #MachineLearning #100DaysOfCode #Kolkata
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share your AI study streak via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareSingleBadge(
        context: Context,
        userName: String,
        badge: StreakBadge,
        streakDays: Int
    ) {
        val shareText = """
            🏆 Achievement Unlocked on SparkAi.com!
            ${badge.iconEmoji} "${badge.title}" Badge (+${badge.xpBonus} XP)
            "${badge.description}"
            
            🔥 Current Study Streak: $streakDays Days
            Study AI with personalized tracks and adaptive quizzes on SparkAi.com!
            
            #SparkAi #AIEducation #StreakMaster #StudyGoals
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share your badge on")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
