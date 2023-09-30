package com.timi.seulseul.presentation.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityPermissionBinding
import com.timi.seulseul.databinding.DialogPermissionRequestBinding
import com.timi.seulseul.presentation.MainActivity
import com.timi.seulseul.presentation.common.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>(R.layout.activity_permission) {

    companion object {
        private const val KEY_DENIED_COUNT = "deniedCount"
        private const val KEY_NOTI_DENIED_COUNT = "notiDeniedCount"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFirstRun = prefs.getBoolean("isFirstRun", true)
        val locationDeniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)


        binding.permissionBtnOk.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 버튼 클릭 시에만 알림 권한 요청
                checkPermissionForNotification()
                if (isFirstRun) {
                    prefs.edit().putBoolean("isFirstRun", false).apply()
                }
                Log.d("deniedCount", locationDeniedCount.toString())
            } else {
                // TIRAMISU 이전 버전에서는 위치 권한 요청
                checkPermissionForLocation()
                if (isFirstRun) {
                    prefs.edit().putBoolean("isFirstRun", false).apply()
                }
                Log.d("deniedCount", locationDeniedCount.toString())
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            goToMainActivity()
        } else {
            val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)
            locationIncreaseDeniedCount() // denied count 값을 증가시킨 후
            if (deniedCount == 0) {
                showFirstPermissionDialog() // 처음으로 거부했다면 첫 번째 대화상자 표시
            } else if (deniedCount >= 1) {
                showSecondPermissionDialog() // 두 번 이상 거부했다면 두 번째 대화상자 표시
            }
        }
    }

    private val locationPermissionRequestAPI33 = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            goToMainActivity()
        } else {
            val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)
            locationIncreaseDeniedCount() // denied count 값을 증가시킨 후
            if (deniedCount == 1) {
                showFirstPermissionDialog() // 처음으로 거부했다면 첫 번째 대화상자 표시
            } else if (deniedCount >= 2) {
                showSecondPermissionDialog() // 두 번 이상 거부했다면 두 번째 대화상자 표시
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // 알림 권한을 허용한 경우
            resetDeniedCount()
            checkPermissionForLocationAPI33()
        } else {
            // 알림 권한을 거부한 경우
            notiIncreaseDeniedCount()
            val notiDeniedCount = prefs.getInt(KEY_NOTI_DENIED_COUNT, 0)
            if (notiDeniedCount == 1) {
                showFirstNotificationPermissionDialog()
            } else if (notiDeniedCount >= 2) {
                showSecondNotificationPermissionDialog()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)
        val notiDeniedCount = prefs.getInt(KEY_NOTI_DENIED_COUNT, 0)
        Log.d("deniedCount", notiDeniedCount.toString())
        Log.d("deniedCount", deniedCount.toString())

        val isFirstRun = prefs.getBoolean("isFirstRun", true)
        if (!isFirstRun && !hasFineLocationPermission()) {
            locationIncreaseDeniedCount()
            Log.d("deniedCount", deniedCount.toString())
        }

    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        } else true
    }


    private fun checkPermissionForLocation() {
        if (hasAllPermissions()) {
            goToMainActivity()
        } else if (prefs.getInt(KEY_DENIED_COUNT, 0) >= 2) {
            showSecondPermissionDialog()
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
                Log.d("deniedCount", "locationRequest 열림")
            } else {
                goToMainActivity()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionForLocationAPI33() {
        if (hasAllPermissions()) {
            goToMainActivity()
        } else if (prefs.getInt(KEY_DENIED_COUNT, 0) >= 2) {
            showSecondPermissionDialog()
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
                locationPermissionRequestAPI33.launch(getRequiredPermissions())
                Log.d("deniedCount", "locationRequest 열림")
            } else {
                goToMainActivity()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionForNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.areNotificationsEnabled()) {
            // 알림 권한이 허용되어 있으면 위치 권한을 확인
            resetDeniedCount()
            checkPermissionForLocation()
        } else {
            // 알림 권한을 요청
            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
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

            else -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showFirstNotificationPermissionDialog() {
        val dialogBinding = DialogPermissionRequestBinding.inflate(layoutInflater)

        dialogBinding.dialogTvMessage.text = "앱 실행을 위해서는\n알림 권한을 설정해야 합니다"
        dialogBinding.dialogTvOk.text = "확인"

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .show() // 바로 show 호출

        val windowParams = alertDialog.window?.attributes
        val widthPercentage = 0.83f

        val displayMetrics = Resources.getSystem().displayMetrics

        windowParams?.width = (displayMetrics.widthPixels * widthPercentage).toInt()

        alertDialog.window?.attributes = windowParams
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.dialogTvOk.setOnClickListener {
            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            alertDialog.dismiss()
        }
    }


    private fun showSecondNotificationPermissionDialog() {
        val dialogBinding = DialogPermissionRequestBinding.inflate(layoutInflater)

        dialogBinding.dialogTvMessage.text = "설정에서 알림 권한을\n항상 허용해주세요"
        dialogBinding.dialogTvOk.text = "지금 설정"

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .show() // 바로 show 호출

        val windowParams = alertDialog.window?.attributes
        val widthPercentage = 0.83f

        val displayMetrics = Resources.getSystem().displayMetrics

        windowParams?.width = (displayMetrics.widthPixels * widthPercentage).toInt()

        alertDialog.window?.attributes = windowParams
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.dialogTvOk.setOnClickListener {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            }
            alertDialog.dismiss()
            startActivity(intent)
        }
    }

    private fun showFirstPermissionDialog() {
        val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)

        val dialogBinding = DialogPermissionRequestBinding.inflate(layoutInflater)

        dialogBinding.dialogTvMessage.text = "정확한 알림을 받아보기 위해서는\n위치 권한을 항상 허용해주세요"
        dialogBinding.dialogTvOk.text = "확인"

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .show() // 여기서 바로 show 호출

        val windowParams = alertDialog.window?.attributes
        val widthPercentage = 0.83f

        val displayMetrics = Resources.getSystem().displayMetrics

        windowParams?.width = (displayMetrics.widthPixels * widthPercentage).toInt()

        alertDialog.window?.attributes = windowParams
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (deniedCount == 1) {
            dialogBinding.dialogTvOk.setOnClickListener {
                locationPermissionRequest.launch(getRequiredPermissions())
                alertDialog.dismiss()
            }
        }
    }


    private fun showSecondPermissionDialog() {
        val dialogBinding = DialogPermissionRequestBinding.inflate(layoutInflater)

        dialogBinding.dialogTvMessage.text = "설정에서 위치 권한을\n항상 허용해주세요"
        dialogBinding.dialogTvOk.text = "지금 설정"

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .show() // 바로 show 호출

        val windowParams = alertDialog.window?.attributes
        val widthPercentage = 0.83f

        val displayMetrics = Resources.getSystem().displayMetrics

        windowParams?.width = (displayMetrics.widthPixels * widthPercentage).toInt()

        alertDialog.window?.attributes = windowParams
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.dialogTvOk.setOnClickListener {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            }
            alertDialog.dismiss()
            startActivity(intent)
        }
    }


    private fun goToMainActivity() {
        if (!hasFineLocationPermission()) {
            showFirstPermissionDialog()
        } else if (!isAlwaysAllow()) {
            showSecondPermissionDialog()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun locationIncreaseDeniedCount() {
        val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)
        prefs.edit().apply {
            putInt(KEY_DENIED_COUNT, deniedCount + 1)
            apply()
        }
    }

    private fun notiIncreaseDeniedCount() {
        val notiDeniedCount = prefs.getInt(KEY_NOTI_DENIED_COUNT, 0)
        prefs.edit().apply() {
            putInt(KEY_NOTI_DENIED_COUNT, notiDeniedCount + 1)
            apply()
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

    private fun resetDeniedCount() {
        prefs.edit().apply {
            putInt(KEY_DENIED_COUNT, 0)
            apply()
        }
    }
}
