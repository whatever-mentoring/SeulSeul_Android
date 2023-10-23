package com.timi.seulseul.presentation.setting

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        binding.settingIvBack.setOnClickListener {
            finish()
        }

        binding.settingContent1.setOnClickListener{
            val url = "https://sites.google.com/view/seulseul-terms-use/%ED%99%88"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        binding.settingContent2.setOnClickListener{
            val url = "https://sites.google.com/view/seulseul-privacy-policy/%ED%99%88"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}