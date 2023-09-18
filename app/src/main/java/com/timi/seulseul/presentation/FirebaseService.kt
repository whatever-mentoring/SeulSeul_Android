package com.timi.seulseul.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.timi.seulseul.R
import timber.log.Timber

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // FCM 토큰이 갱신 될 경우 서버에 해당 토큰을 갱신됐다고 알려주는 콜백함수
        Timber.d("FCM 토큰 갱신 : $token")
    }

    // Notification 수신부
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // TODO: Data message 확인 이후 지울 예정
        //FCM 메시지 유형 : Notification message 수신 (pendingIntent, 알림 여러개 쌓이는 지 확인 용)
        if (remoteMessage.notification != null) {
            Timber.d("notification message : ${remoteMessage.notification?.title} / ${remoteMessage.notification?.body}")
            sendNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!)
        }

        // FCM 메시지 유형 : Data message 수신
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"].toString()
            val body = remoteMessage.data["body"].toString()
            Timber.d("title: $title / body: $body")

            sendNotification(title, body)
        } else {
            Timber.d("data가 비어있어 메시지를 수신하지 못하였습니다.")
        }
    }

    private fun sendNotification(title: String, body: String) {
        // 알림 클릭 시 화면 이동을 위한 Intent 설정
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, (System.currentTimeMillis()).toInt(), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        ) // FLAG_ONE_SHOT : 생성된 PendingIntent 단 한번만 사용

        // Notification Channel 생성 (안드로이드 8.0 이상에서 채널을 무조건 만들어 줘야 함)
        val mChannel = NotificationChannel(
            getString(R.string.default_notification_channel_id), // channel ID
            getString(R.string.channel_name), // channel name
            NotificationManager.IMPORTANCE_HIGH // importance(알림음 + head-up alarm 표시)
        ).apply {
            setShowBadge(true)
            enableLights(true)
            lightColor = android.graphics.Color.BLUE
            enableVibration(true)
            description = "notification"
        }

        // Notification Channel을 Notification Manager에 연결
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        // Notification의 id를 시간으로 주어 알림 여러 개 쌓일 수 있도록 설정
        val notification = getNotificationBuilder(title, body, pendingIntent)
        notificationManager.notify((System.currentTimeMillis()).toInt(), notification.build())
    }

    private fun getNotificationBuilder(
        title: String,
        body: String,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(this, getString(R.string.default_notification_channel_id))
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true) // 알람 터치 시 자동으로 삭제
            .setSmallIcon(R.drawable.ic_splash_app)
            .setShowWhen(true) // 알람 시간
            .setNumber(999) // 확인하지 않은 알림 갯수를 설정
    }
}