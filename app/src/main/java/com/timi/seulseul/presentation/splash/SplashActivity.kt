package com.timi.seulseul.presentation.splash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.R
import com.timi.seulseul.Utils
import com.timi.seulseul.presentation.MainActivity
import com.timi.seulseul.presentation.onboarding.OnBoardingActivity
import com.timi.seulseul.presentation.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

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
            dialog = AlertDialog.Builder(this)
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

                // KEY_ONBOARDING true일 때
                if (checkOnBoarding()) {
                    // 권한이 전부 다 허용됐을 때
                    if (Utils.checkPermission(this)) {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // 권한 허용 안된게 존재할 때
                        startActivity(Intent(this, PermissionActivity::class.java))
                    }
                } else {
                    // KEY_ONBOARDING false면 온보딩으로
                    startActivity(Intent(this, OnBoardingActivity::class.java))
                }
            }

            finish()

            return

    }

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

    private fun checkOnBoarding(): Boolean {
        if (prefs.getBoolean("KEY_ONBOARDING", false)) {
            return true
        }
        return false
    }


}