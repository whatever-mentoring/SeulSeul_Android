package com.timi.seulseul.presentation.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityPermissionBinding
import com.timi.seulseul.presentation.main.MainActivity
import com.timi.seulseul.presentation.common.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>(R.layout.activity_permission) {

    private val viewModel by viewModels<PermissionViewModel>()

    private var notificationDeniedCount = 0
    private var locationDeniedCount = 0

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // 알림 권한을 허용한 경우
            checkPermissionForLocation()
        } else {
            // 알림 권한을 거부한 경우
            notificationDeniedCount++
            if (notificationDeniedCount == 1) {
                showFirstNotificationPermissionDialog()
            } else if (notificationDeniedCount >= 2) {
                showSecondNotificationPermissionDialog()
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            // 위치 권한을 허용한 경우
            goToMainActivity()
        } else {
            // 위치 권한을 거부한 경우
            locationDeniedCount++
            if (locationDeniedCount == 1) {
                showFirstLocationPermissionDialog()
            } else if (locationDeniedCount >= 2) {
                showSecondLocationPermissionDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.permissionBtnOk.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 버튼 클릭 시에만 알림 권한 요청
                checkPermissionForNotification()
            } else {
                // TIRAMISU 이전 버전에서는 위치 권한 요청
                checkPermissionForLocation()
            }
        }

        // TODO: 이후 onBoarding 로직으로 이동될 예정
        // FCM 토큰 받기 & 보내기
        getFcmToken()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionForNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.areNotificationsEnabled()) {
            // 알림 권한이 허용되어 있으면 위치 권한을 확인
            checkPermissionForLocation()
        } else {
            // 알림 권한을 요청
            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun checkPermissionForLocation() {
        if (hasAllPermissions()) {
            // 위치 권한이 허용되어 있으면 메인 화면으로 이동
            goToMainActivity()
        } else if (locationDeniedCount >= 2) {
            showSecondLocationPermissionDialog()
        } else {
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (!hasFineLocationPermission || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && hasFineLocationPermission && shouldShowRationale)) {
                locationPermissionRequest.launch(getRequiredPermissions())
            } else {
                goToMainActivity()
            }

        }
    }

    private fun hasFineLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasAllPermissions(): Boolean =
        getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(
                this, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun getRequiredPermissions(): Array<String> =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            else -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showFirstNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setMessage("앱 실행을 위해서는 알림 권한을 설정해야 합니다")
            .setPositiveButton("확인") { _, _ ->
                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            }.show()
    }

    private fun showSecondNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setMessage("설정에서 알림 권한을 허용해주세요")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }.show()
    }

    private fun showFirstLocationPermissionDialog() {
        AlertDialog.Builder(this)
            .setMessage("정확한 알림을 받아보기 위해서는 위치 권한을 항상 허용해주세요")
            .setPositiveButton("확인") { _, _ ->
                locationPermissionRequest.launch(getRequiredPermissions())
            }
            .show()
    }

    private fun showSecondLocationPermissionDialog() {
        AlertDialog.Builder(this)
            .setMessage("설정에서 위치 권한을 항상 허용해주세요")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }.show()
    }

    private fun goToMainActivity() {
        if (!hasFineLocationPermission()) {
            showSecondLocationPermissionDialog()
        } else if (!isAlwaysAllow()) {
            showSecondLocationPermissionDialog()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun isAlwaysAllow(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.d("Fetching FCM registeration token failed", task.exception)
                return@OnCompleteListener
            }

            // 토큰값 가져오기
            val token = task.result
            prefs.edit().putString("fcm_token", token).apply()
            Timber.d("fcm_token: $token")

            // 토큰값 보내기
            viewModel.postFcmToken(token)

        }).addOnFailureListener {
            Timber.e(it)
        }
    }
}
