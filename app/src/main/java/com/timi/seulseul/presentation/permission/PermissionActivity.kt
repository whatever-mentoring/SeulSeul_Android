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

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                goToMainActivity()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                showFirstPermissionDialog()
            }

            else -> {
                showFirstPermissionDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.permissionBtnOk.setOnClickListener {
            checkPermissionForLocation()
        }
    }

    // 위치 권한 허용
    private fun checkPermissionForLocation() {
        when {
            //Android 9 이하(API 28 이하)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasFineLocationPermission) {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else if (hasFineLocationPermission) {
                    goToMainActivity()
                } else {
                    showFirstPermissionDialog()
                }
            }

            //Android 10(API 29)
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val hasBackgroundLocationPermission = if (hasFineLocationPermission) {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                } else false

                when {
                    hasFineLocationPermission && hasBackgroundLocationPermission -> goToMainActivity()

                    !hasFineLocationPermission || !hasBackgroundLocationPermission ->
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                        )

                    else -> showFirstPermissionDialog()
                }
            }


            else -> {

                //Android 11 이상(API 30이상)

                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                when {
                    hasFineLocationPermission -> goToMainActivity()

                    !hasFineLocationPermission && hasCoarseLocationPermission ->
                        showFirstPermissionDialog()

                    !hasFineLocationPermission && !hasCoarseLocationPermission ->
                        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))

                    else -> showFirstPermissionDialog()


                }
            }

        }
    }

    // 이번만 허용 혹은 거부를 눌렀을 경우
    private fun showFirstPermissionDialog() {
        AlertDialog.Builder(this)
            .setMessage("정확한 알림을 받아보기  위해서는\n위치 권한을 항상 허용해주세요")
            .setPositiveButton("확인") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }.show()
    }

    //MainActivity 이동
    private fun goToMainActivity() {

        //Android 10(API 29이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasBackgroundLocationAccess = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasBackgroundLocationAccess) {
                showFirstPermissionDialog()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }



}
