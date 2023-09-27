package com.timi.seulseul.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.timi.seulseul.R
import com.timi.seulseul.Utils
import com.timi.seulseul.presentation.MainActivity
import com.timi.seulseul.presentation.onboarding.OnBoardingActivity
import com.timi.seulseul.presentation.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel>()

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
        if (!Utils.isNetworkAvailable(this)) {
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

            viewModel.spLiveData.observe(this) { isOnBoardingCompleted ->
                // KEY_ONBOARDING true일 때
                if (isOnBoardingCompleted) {
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
        }
    }



}