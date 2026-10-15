package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.receiver.StudyReminderReceiver
import java.util.Calendar

object NotificationSchedulerHelper {

    private const val REQUEST_CODE_POMODORO = 2001
    private const val REQUEST_CODE_STREAK = 2002

    fun schedulePomodoroCompletion(
        context: Context,
        secondsFromNow: Int,
        modeTitle: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + (secondsFromNow * 1000L)

        val intent = Intent(context, StudyReminderReceiver::class.java).apply {
            putExtra(StudyReminderReceiver.EXTRA_NOTIFICATION_TYPE, StudyReminderReceiver.TYPE_POMODORO)
            putExtra(StudyReminderReceiver.EXTRA_NOTIFICATION_TITLE, "⏰ $modeTitle Finished!")
            putExtra(StudyReminderReceiver.EXTRA_NOTIFICATION_MESSAGE, "Great job! Your focus sprint is complete. Claim your XP sparks on SparkAi.com.")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_POMODORO,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelPomodoroAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StudyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_POMODORO,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleDailyStreakReminder(
        context: Context,
        streakDays: Int,
        hourOfDay: Int = 19, // 7:00 PM evening study reminder
        minute: Int = 0
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(context, StudyReminderReceiver::class.java).apply {
            putExtra(StudyReminderReceiver.EXTRA_NOTIFICATION_TYPE, StudyReminderReceiver.TYPE_STREAK)
            putExtra(StudyReminderReceiver.EXTRA_NOTIFICATION_TITLE, "🔥 Keep Your $streakDays-Day Streak Alive!")
            putExtra(StudyReminderReceiver.EXTRA_NOTIFICATION_MESSAGE, "You're building great AI study momentum. Spend 5 mins on today's quick quiz to keep your Room badge progress.")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_STREAK,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelDailyStreakReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StudyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_STREAK,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
