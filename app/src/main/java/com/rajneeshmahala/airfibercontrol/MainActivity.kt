package com.rajneeshmahala.airfibercontrol

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var address: EditText
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var status: TextView
    private val prefs by lazy { getSharedPreferences("airfiber", MODE_PRIVATE) }
    private val blue = Color.rgb(31, 83, 181)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 247, 251)
        window.navigationBarColor = Color.rgb(245, 247, 251)
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(245, 247, 251))
            setPadding(dp(16), dp(18), dp(16), dp(8))
        }
        val title = TextView(this).apply {
            text = "AirFiber Control"
            textSize = 24f
            setTextColor(Color.rgb(24, 34, 52))
            typeface = Typeface.DEFAULT_BOLD
        }
        val subtitle = TextView(this).apply {
            text = "Open your OpenWrt router on the local Wi‑Fi network"
            textSize = 14f
            setTextColor(Color.rgb(91, 102, 120))
            setPadding(0, dp(4), 0, dp(14))
        }
        root.addView(title)
        root.addView(subtitle)

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        address = EditText(this).apply {
            setSingleLine(true)
            textSize = 15f
            setText(prefs.getString("url", "http://192.168.2.1") ?: "http://192.168.2.1")
            hint = "http://192.168.2.1"
            setSelectAllOnFocus(false)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_URI
            setPadding(dp(12), 0, dp(8), 0)
            background = roundedBackground(Color.WHITE, Color.rgb(220, 226, 235))
        }
        row.addView(address, LinearLayout.LayoutParams(0, dp(48), 1f))
        val go = Button(this).apply {
            text = "Open"
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = roundedBackground(blue, blue)
            setOnClickListener { openRouter() }
        }
        row.addView(go, LinearLayout.LayoutParams(dp(78), dp(48)).apply { leftMargin = dp(8) })
        root.addView(row)

        status = TextView(this).apply {
            text = "Connect your phone to the AirFiber Wi‑Fi first."
            textSize = 12f
            setTextColor(Color.rgb(87, 98, 116))
            setPadding(dp(2), dp(10), 0, dp(8))
        }
        root.addView(status)
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            isIndeterminate = true
            visibility = android.view.View.GONE
        }
        root.addView(progress, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(3)))

        webView = WebView(this).apply {
            setBackgroundColor(Color.WHITE)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    this@MainActivity.progress.visibility = android.view.View.VISIBLE
                    status.text = "Connecting to router…"
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    this@MainActivity.progress.visibility = android.view.View.GONE
                    status.text = "Router page loaded. Sign in using your router’s admin credentials."
                }
                override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
                    if (request?.isForMainFrame == true) {
                        this@MainActivity.progress.visibility = android.view.View.GONE
                        status.text = "Can’t reach the router. Check Wi‑Fi and the address, then tap Open."
                    }
                }
            }
        }
        root.addView(webView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f).apply { topMargin = dp(4) })
        setContentView(root)

        if (savedInstanceState != null) webView.restoreState(savedInstanceState)
        else if (intent.getBooleanExtra("open_router", false)) openRouter()
    }

    @Deprecated("Use the system back gesture")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    private fun openRouter() {
        var value = address.text.toString().trim()
        if (value.isBlank()) value = "192.168.2.1"
        if (!value.startsWith("http://") && !value.startsWith("https://")) value = "http://$value"
        val parsed = android.net.Uri.parse(value)
        if (parsed.host.isNullOrBlank()) {
            Toast.makeText(this, "Enter a router IP address or URL", Toast.LENGTH_SHORT).show()
            return
        }
        address.setText(value)
        prefs.edit().putString("url", value).apply()
        status.text = "Opening $value"
        webView.loadUrl(value)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if (::webView.isInitialized) webView.destroy()
        super.onDestroy()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun roundedBackground(fill: Int, stroke: Int): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(12).toFloat()
            setStroke(dp(1), stroke)
        }
    }
}
