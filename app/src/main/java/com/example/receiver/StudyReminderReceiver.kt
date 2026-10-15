package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class StudyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE) ?: TYPE_POMODORO
        val title = intent.getStringExtra(EXTRA_NOTIFICATION_TITLE) ?: "SparkAi Study Alert"
        val message = intent.getStringExtra(EXTRA_NOTIFICATION_MESSAGE) ?: "Your focus session is ready."

        showLocalNotification(context, type, title, message)
    }

    private fun showLocalNotification(
        context: Context,
        type: String,
        title: String,
        message: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = if (type == TYPE_POMODORO) CHANNEL_ID_POMODORO else CHANNEL_ID_STREAK
        val channelName = if (type == TYPE_POMODORO) "Pomodoro Focus Timer" else "Daily Study Streak Reminders"
        val notificationId = if (type == TYPE_POMODORO) 1001 else 1002

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Offline background alerts for SparkAi focus timer and study streak"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID_POMODORO = "sparkai_pomodoro_channel"
        const val CHANNEL_ID_STREAK = "sparkai_streak_channel"

        const val EXTRA_NOTIFICATION_TYPE = "extra_type"
        const val EXTRA_NOTIFICATION_TITLE = "extra_title"
        const val EXTRA_NOTIFICATION_MESSAGE = "extra_message"

        const val TYPE_POMODORO = "type_pomodoro"
        const val TYPE_STREAK = "type_streak"
    }
}
