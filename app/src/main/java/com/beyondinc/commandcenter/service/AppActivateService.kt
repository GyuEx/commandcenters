package com.beyondinc.commandcenter.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

import com.beyondinc.commandcenter.BuildConfig
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.activity.Mains

class AppActivateService : Service() {

    companion object {
        const val ACTION_FOREGROUND = "${BuildConfig.APPLICATION_ID}.app.FOREGROUND"
        const val ACTION_BACKGROUND = "${BuildConfig.APPLICATION_ID}.app.BACKGROUND"
        const val ACTION_END_APP = "${BuildConfig.APPLICATION_ID}.app.ENDAPP"

        private const val NOTIFICATION_ID = 9999
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_FOREGROUND -> {
                    registerNotification()
                }
                ACTION_BACKGROUND, ACTION_END_APP -> {
                    stopForeground(true)
                }
            }
        }
        return START_STICKY
    }

    private fun createNotificationChannel(name: String, descriptionText: String, channelID: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_NONE
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun registerNotification() {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.desc_notification)

        val channelID = "$packageName-$name"
        val content = getString(R.string.message_activate_app)

        val intent = Intent(baseContext,NotificationBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        createNotificationChannel(name, descriptionText, channelID)

        val builder = NotificationCompat.Builder(this, channelID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.parseColor(getString(R.string.icon_color)))
                .setContentTitle(name)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)

        startForeground(NOTIFICATION_ID, builder.build())
    }
}