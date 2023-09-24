package com.timi.seulseul.presentation.location.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationSearchBinding
import com.timi.seulseul.presentation.common.base.BaseActivity

class LocationSearchActivity :
    BaseActivity<ActivityLocationSearchBinding>(R.layout.activity_location_search) {

    companion object {
        const val EXTRA_ROAD_ADDR = "roadAddr"
        const val EXTRA_JIBUN_ADDR = "jibunAddr"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.locationIvBack.setOnClickListener {
            finish()
        }

        val webView: WebView = binding.locationSearchWebView

        webView.clearCache(true)
        webView.settings.javaScriptEnabled = true

        webView.addJavascriptInterface(BridgeInterface(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:sample2_execDaumPostcode();")
            }
        }

        webView.loadUrl("https://seulseul-35d52.web.app")
    }

    inner class BridgeInterface() {
        @JavascriptInterface
        @SuppressWarnings("unused")
        fun processDATA(fullRoadAddr: String, jibunAddr: String) {
            val intent = Intent(this@LocationSearchActivity, LocationDetailActivity::class.java)
            intent.putExtra(EXTRA_ROAD_ADDR, fullRoadAddr)
            intent.putExtra(EXTRA_JIBUN_ADDR, jibunAddr)
            Log.d("jibun", jibunAddr)
            Log.d("jibun", fullRoadAddr)
            setResult(RESULT_OK, intent)
            startActivity(intent)
        }

    }
}