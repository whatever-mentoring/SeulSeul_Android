package com.timi.seulseul.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.timi.seulseul.R
import timber.log.Timber

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d(token)
    }

    // Notification 수신부
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH

        // Notification Channel 생성 (안드로이드 8.0 이상에서 채널을 무조건 만들어 줘야 함)
        val mChannel = NotificationChannel(
            getString(R.string.default_notification_channel_id),
            name,
            importance
        ).apply {
            setShowBadge(true)
            enableLights(true)
            lightColor = android.graphics.Color.BLUE
            enableVibration(true)
            description = "notification"
        }

        mChannel.description = descriptionText

        // Notification Channel을 Notification Manager에 연결
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        val body = message.notification?.body ?: ""

        val notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_splash_app)
            .setContentTitle(getString(R.string.channel_name))
            .setContentText(body)

        // 여기서 id == 이 Notification의 id (구분자 역할)
        notificationManager.notify(0, notificationBuilder.build())
    }
}