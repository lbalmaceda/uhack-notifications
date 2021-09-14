package com.auth0.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random


/*
 * Requirements and disclaimers:
 * - Requires API 18 and UP
 * - Notification listeners cannot get notification access or be bound by the system on low-RAM devices running Android Q (and below).
 * - The system also ignores notification listeners running in a work profile. A DevicePolicyManager might block notifications originating from a work profile.
 */
class NotificationListener : NotificationListenerService() {
    private val TAG = this.javaClass.simpleName
    private val TARGET_APP = "com.auth0.notifications"

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.i(TAG, "**********  onNotificationPosted")
        logNotification(sbn)

        if (sbn.packageName == TARGET_APP) {
            // Change to the app we want to listen notifications from
            onNewTargetAppNotification(sbn)
        }
    }

    @SuppressLint("LogConditional")
    private fun logNotification(sbn: StatusBarNotification) {
        val pack: String = sbn.packageName
        val extras: Bundle = sbn.notification.extras
        val title = extras.getString("android.title").orEmpty()
        val text = extras.getCharSequence("android.text").toString()

        Log.i(
            TAG, """
        $pack - "$title": "$text"
        """.trimIndent()
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i(TAG, "********** onNotificationRemoved")
        logNotification(sbn)
    }

    private fun onNewTargetAppNotification(sbn: StatusBarNotification) {
        /*
         * TODO: Play "It's that you??" w/ Text to Voice
         *  and save this notification's intents (Parcelable) for later actioning
         */
        Log.i(TAG, "Received a notification from our TARGET_APP package")
    }

    companion object {
        private const val CHANNEL_ID = "sample_channel"

        fun isNotificationAccessGranted(context: Context): Boolean {
            return try {
                Settings.Secure.getString(
                    context.contentResolver,
                    "enabled_notification_listeners"
                ).contains(context.packageName)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun showNotificationsAccessPreference(context: Context) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            context.startActivity(intent)
        }

        fun registerNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_channel_name)
                val descriptionText = context.getString(R.string.notification_channel_description)
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = descriptionText
                }
                val nManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
                nManager.createNotificationChannel(channel)
            }
        }

        fun createSampleNotification(context: Context) {
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, 0)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Notification #${Random.nextInt(100)}")
                .setContentText("Sample notification")
                .setTicker("Sample notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(openAppPendingIntent)
                .setAutoCancel(true)
                .build()

            val nManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
            nManager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}