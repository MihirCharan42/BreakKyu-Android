package com.example.breakkyuapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.type.DateTime
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date

class BreakService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return START_STICKY
    }

    fun start() {
        val formatter = SimpleDateFormat("HH:MM aaa")
        val date = Date()
        val time = formatter.format(date)
        val notification = NotificationCompat.Builder(this, "Break_Service")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("You are on break!")
            .setContentText("Since $time")
            .build()
        notification.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    enum class Actions {
        START, STOP
    }
}