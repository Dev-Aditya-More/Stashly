package nodomain.aditya1875more.stashly.WorkManager

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import nodomain.aditya1875more.stashly.MainActivity
import nodomain.aditya1875more.stashly.R
import nodomain.aditya1875more.stashly.utils.NotificationUtils
import kotlin.random.Random

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val timeOfDay = inputData.getString("timeOfDay")
        val (title, message) = getNotificationContent(timeOfDay)
        showNotification(title, message)
        return Result.success()
    }

    private fun getNotificationContent(timeOfDay: String?): Pair<String, String> {
        return when (timeOfDay) {
            "morning" -> "🌅 Good Morning" to morningMessages.random()
            "evening" -> "🌙 Quick Reminder" to eveningMessages.random()
            else -> "Stashly" to "Save and organize what matters."
        }
    }

    private val morningMessages = listOf(
        "Save important links before your day gets busy.",
        "Got something to remember today? Stash it now.",
        "Start your day organized — stash notes, links, or files."
    )

    private val eveningMessages = listOf(
        "Review what you saved today in Stashly.",
        "Stash anything you don’t want to forget tomorrow.",
        "Clean up your saved items in a few taps."
    )

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.stashlylogo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(Random.nextInt(), notification)
    }
}