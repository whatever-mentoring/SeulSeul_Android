package com.timi.seulseul.presentation.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
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
                showUpgradeToExactLocationDialog()
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
                        showUpgradeToExactLocationDialog()

                    !hasFineLocationPermission && !hasCoarseLocationPermission ->
                        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))

                    else -> showFirstPermissionDialog()


                }
            }

        }
    }

    //정확한 위치로 업그레이드 요청하는 경우
    private fun showUpgradeToExactLocationDialog() {
        AlertDialog.Builder(this)
            .setTitle("대략적인 위치 권한으로는 정확한 알림을 받을 수 없습니다.")
            .setMessage("정확한 알림을 받기 위해서는 정확한 위치 권한이 필요합니다.")
            .setPositiveButton("업그레이드") { _, _ ->
                requestExactLocationPermissions()
            }
    }

    //정확한 위치 권한 요청
    private fun requestExactLocationPermissions() {
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    // 이번만 허용 혹은 거부를 눌렀을 경우
    private fun showFirstPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("정확한 알림을 받아보기 위해서는 위치 권한을 항상 허용해주세요")
            .setPositiveButton("확인") { _, _ ->
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }.show()
    }

    //MainActivity 이동
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}
