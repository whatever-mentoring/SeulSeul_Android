package com.timi.seulseul.presentation.splash

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityOnBoardingBinding
import com.timi.seulseul.presentation.MainActivity
import com.timi.seulseul.presentation.onboarding.OnBoardingActivity
import com.timi.seulseul.presentation.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 상태바 색 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.green_400)

        checkNetworkConnection()

    }

    private fun checkNetworkConnection() {
        // 네트워크가 사용불가능일 경우
        if (!isNetworkAvailable(this)) {
            // AlertDialog 객체 생성
            val dialog = AlertDialog.Builder(this)
                .setView(R.layout.dialog_network_check)
                .setCancelable(false)
                .create()

            // 커스텀 뷰 내의 버튼 참조 획득 및 클릭 리스너 설정
            dialog.setOnShowListener {
                val button = dialog.findViewById<TextView>(R.id.network_btn)

                button?.setOnClickListener {
                    // 다시 시도 버튼 클릭 시 네트워크 연결 체크
                    checkNetworkConnection()
                    // 다이얼로그 닫기, 안 닫아주면 계속 중첩돼서 쌓임
                    dialog.dismiss()
                }
            }
            // Dialog 보여주기
            dialog.show()


        } else {
            // 네트워크가 연결 됐을 때, 어디로 이동할지 결정

            if (checkOnBoarding()) {
                // 권한이 전부 다 허용됐을 때
                if (checkPermission()) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // 권한 허용 안된게 존재할 때
                    startActivity(Intent(this, PermissionActivity::class.java))
                }
            } else {
                // OnBoarding false면 온보딩으로
                startActivity(Intent(this, OnBoardingActivity::class.java))
            }

            finish()
            return
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
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

    private fun checkOnBoarding(): Boolean {
        // sp에서 KEY_ONBOARDING 값체크, 값이 없다면 2번째 인자인 false가 기본값
        if (prefs.getBoolean("KEY_ONBOARDING", false)) {
            // KEY_ONBOARDING이 true라면
            // checkOnBoarding 값을 true로 반환
            return true
        }

        // KEY_ONBOARDING이 false라면
        // checkOnBoarding 값을 false로 반환
        return false
    }

    private fun checkPermission(): Boolean {
        // FINE_LOCATION 체크
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        // COARSE_LOCATION 체크
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        // BACKGROUND_LOCATION 체크
        val hasBackgroundLocationPermission =
            // API 26(Q) 이상일 때
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                // API 26미만이면 백그라운드가 포그라운드 권한에 종속됨
                true
            }


        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // POST_NOTIFICATIONS 체크, API33 알림 퍼미션
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Notification 허용 여부 체크 (모든 알림(채널) 차단만 체크해줌)
            NotificationManagerCompat.from(this).areNotificationsEnabled()
            // 특정 채널 알림 차단여부는 체크따로 해야함
        }

        // 전부다 true일 때 checkPermission을 true로 반환, 하나라도 false가 있으면 false로 반환
        return hasFineLocationPermission && hasCoarseLocationPermission && hasBackgroundLocationPermission && hasNotificationPermission
    }

}