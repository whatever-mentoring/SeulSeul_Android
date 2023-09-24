package com.timi.seulseul.data.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.timi.seulseul.data.model.Location
import com.timi.seulseul.data.repository.LocationRepo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject lateinit var locationRepo: LocationRepo


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    // Service 클래스를 상속받는 모든 클래스는 onBind(Intent) 메서드를 구현해야 합니다.
    // 이 메서드는 다른 컴포넌트가 서비스에 바인딩할 때 호출됩니다.
    // 그러나 위치 업데이트를 위한 포그라운드 서비스에서는 onBind(Intent) 메서드가 필요하지 않습니다.
    // 따라서 이 메서드에서 null을 반환하도록 구현하면 됩니다.
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // NOTIFICATION_ID는 알림을 생성할 때 사용하는 고유한 정수 ID입니다.
    // 이 ID를 사용하여 나중에 알림을 업데이트하거나 제거할 수 있습니다.
    companion object {
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Create the location request
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Create the location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d("jhb", "Longitude: ${location.longitude}, Latitude: ${location.latitude}")

                    // 년-월-일
                    val date = LocalDate.now()
                    // 요일
                    val dayOfTheWeek =
                        date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)

                    // 위치를 서버에 전송
                    val loc = Location(1, location.longitude.toString(), location.latitude.toString(), dayOfTheWeek)

                    // GlobalScope
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val response = locationRepo.postLocation(loc)
                            if (response.isSuccess) {
                                Log.d("jhb", "Successfully posted location: ${response.getOrNull()}")
                            } else {
                                Log.e("jhb", "Failed to post location", response.exceptionOrNull())
                            }
                        } catch (e: Exception) {
                            Log.e("jhb", "Exception when posting location", e)
                        }
                    }

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val response = locationRepo.patchLocation(loc)
                            if (response.isSuccess) {
                                Log.d("jhb", "Successfully patched location: ${response.getOrNull()}")
                            } else {
                                Log.e("jhb", "Failed to patch location", response.exceptionOrNull())
                            }
                        } catch (e: Exception) {
                            Log.e("jhb", "Exception when patching location", e)
                        }
                    }


                }
            }
        }

        if (!hasLocationPermission()) {
            return   // Cannot start without permission
        }

        startForeground(NOTIFICATION_ID, createNotification())

        startLocationUpdates()
    }


    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationUpdates() {
        if (hasLocationPermission()) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper()
                )
            } catch (unlikely: SecurityException) {
                Log.e(
                    "jhb", "Lost location permissions. Couldn't remove updates. $unlikely"
                )
            }
        }
    }

    // 포그라운드 서비스 알림 (백그라운드에서 실행중)
    private fun createNotification(): Notification {
        val channelID = "channelID"
        val notificationChannel =
            NotificationChannel(channelID, "My Service", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(notificationChannel)

        // setOngoing(true)는 해당 알림이 진행 중인 상태(사용자가 스와이프해서 제거할 수 없는)
        // 근데 android14부터는 제거할수있다고한다. 그래서 다른 방법을 찾아야 한다.
        // https://developer.android.com/about/versions/14/behavior-changes-all?hl=ko
        val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }
}