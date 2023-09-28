package com.timi.seulseul

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object Utils {

    // 네트워크 상태 체크
     fun isNetworkAvailable(context: Context): Boolean {
        // [API 23이상] ConnectivityManager, NetworkCapabilities
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {

            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                // 셀룰러 체크
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                }
                // WIFI 체크
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                }
                // 이더넷 체크
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }

    // 권한 상태 체크
    fun checkPermission(context: Context): Boolean {
        // FINE_LOCATION 체크
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        // COARSE_LOCATION 체크때
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        // BACKGROUND_LOCATION 체크
        val hasBackgroundLocationPermission =
            // API 26(Q) 이상일 때
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                // API 26미만이면 백그라운드가 포그라운드 권한에 종속됨
                true
            }


        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // POST_NOTIFICATIONS 체크, API33 알림 퍼미션
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Notification 허용 여부 체크 (모든 알림(채널) 차단만 체크해줌)
            NotificationManagerCompat.from(context).areNotificationsEnabled()
            // 특정 채널 알림 차단여부는 체크따로 해야함
        }

        // 전부다 true일 때 checkPermission을 true로 반환, 하나라도 false가 있으면 false로 반환
        return hasFineLocationPermission && hasCoarseLocationPermission && hasBackgroundLocationPermission && hasNotificationPermission
    }
}