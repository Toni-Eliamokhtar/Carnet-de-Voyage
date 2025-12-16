package com.example.travail_pratique_synthese_toni_et_farid.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.travail_pratique_synthese_toni_et_farid.R
import androidx.core.content.edit
import android.annotation.SuppressLint


private const val LIEUX_NOTIFICATION_CHANNEL_ID = "lieux_notifications"
private const val LIEUX_NOTIFICATION_GROUP_KEY = "lieux_notification_group"

private const val LIEUX_NOTIFS_PREFS = "lieux_notifs_prefs"
private const val LIEUX_NOTIFS_COUNT_KEY = "lieux_notifs_count"
private const val LIEUX_NOTIFS_SUMMARY_ID = 8899

object NotificationsLieux {

    private fun hasPostNotificationsPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun creerCanalSiBesoin(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            LIEUX_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.lieux_notifications_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun nextNotificationId(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(LIEUX_NOTIFS_PREFS, Context.MODE_PRIVATE)
        val newCount = prefs.getInt(LIEUX_NOTIFS_COUNT_KEY, 0) + 1
        prefs.edit { putInt(LIEUX_NOTIFS_COUNT_KEY, newCount) }
        val notifId = 3000 + newCount
        return notifId to newCount
    }

    private fun envoyer(context: Context, titre: String, texte: String) {
        if (!hasPostNotificationsPermission(context)) return

        creerCanalSiBesoin(context)

        val (notifId, total) = nextNotificationId(context)

        val notif = NotificationCompat.Builder(context, LIEUX_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titre)
            .setContentText(texte)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(LIEUX_NOTIFICATION_GROUP_KEY)
            .build()

        val summaryTitre = context.getString(R.string.lieux_notifications_summary_title)
        val summaryTexte = context.getString(R.string.lieux_notifications_summary_text, total)

        val summary = NotificationCompat.Builder(context, LIEUX_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(summaryTitre)
            .setContentText(summaryTexte)
            .setStyle(NotificationCompat.InboxStyle().setSummaryText(summaryTexte))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(LIEUX_NOTIFICATION_GROUP_KEY)
            .setGroupSummary(true)
            .build()

        @SuppressLint("MissingPermission")
        with(NotificationManagerCompat.from(context)) {
            notify(notifId, notif)
            notify(LIEUX_NOTIFS_SUMMARY_ID, summary)
        }

    }

    fun notifierAjout(context: Context, nomLieu: String) {
        envoyer(
            context,
            context.getString(R.string.lieu_notification_added_title),
            context.getString(R.string.lieu_notification_added_text, nomLieu)
        )
    }

    fun notifierModification(context: Context, nomLieu: String) {
        envoyer(
            context,
            context.getString(R.string.lieu_notification_updated_title),
            context.getString(R.string.lieu_notification_updated_text, nomLieu)
        )
    }

    fun notifierSuppression(context: Context, nomLieu: String) {
        envoyer(
            context,
            context.getString(R.string.lieu_notification_deleted_title),
            context.getString(R.string.lieu_notification_deleted_text, nomLieu)
        )
    }
}
