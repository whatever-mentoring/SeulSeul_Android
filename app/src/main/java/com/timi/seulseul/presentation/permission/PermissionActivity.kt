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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityPermissionBinding
import com.timi.seulseul.presentation.MainActivity
import com.timi.seulseul.presentation.common.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>(R.layout.activity_permission) {

    companion object {
        private const val PREFS_NAME = "PermissionPrefs"
        private const val KEY_DENIED_COUNT = "deniedCount"
    }

    //SharedPreferences 초기화
    private val prefs by lazy { getSharedPreferences(PREFS_NAME, MODE_PRIVATE) }


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            goToMainActivity()
        } else {
            val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)
            if (deniedCount == 0) {
                increaseDeniedCount()
                showFirstPermissionDialog()
            } else if (deniedCount == 1) {
                increaseDeniedCount()
                showSecondPermissionDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.permissionBtnOk.setOnClickListener {
            checkPermissionForLocation()
        }
    }


    private fun checkPermissionForLocation() {
        if (hasAllPermissions()) {
            goToMainActivity()
        } else if (prefs.getInt(KEY_DENIED_COUNT, -1) >= 1) {
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

            else -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    private fun showFirstPermissionDialog() {
        val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)

        if (deniedCount == 1) {
            AlertDialog.Builder(this)
                .setMessage("정확한 알림을 받아보기 위해서는 위치 권한을 항상 허용해주세요")
                .setPositiveButton("확인") { _, _ ->
                    locationPermissionRequest.launch(getRequiredPermissions())
                }
                .show()
        }
    }

    private fun showSecondPermissionDialog() {
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
            showFirstPermissionDialog()
        } else if (!isAlwaysAllow()) {
            showSecondPermissionDialog()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun increaseDeniedCount() {
        val deniedCount = prefs.getInt(KEY_DENIED_COUNT, 0)
        prefs.edit().apply {
            putInt(KEY_DENIED_COUNT, deniedCount + 1)
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
}
