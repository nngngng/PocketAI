package com.pocketai.app

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var ramText: TextView
    private lateinit var deviceText: TextView

    private val background = Color.rgb(16, 19, 27)
    private val panel = Color.rgb(28, 36, 52)
    private val white = Color.rgb(242, 245, 252)
    private val muted = Color.rgb(167, 176, 194)
    private val blue = Color.rgb(139, 184, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildDashboard()
        refreshDeviceInfo()
    }

    private fun buildDashboard() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(background)
            setPadding(22, 28, 22, 24)
        }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(title("PocketAI", 30, white))
        content.addView(subtitle("YOUR LOCAL AI CONTROL CENTER"))
        content.addView(space(24))

        content.addView(section("SERVER STATUS"))
        content.addView(card {
            addView(title("Offline", 27, white))
            addView(subtitle("Your local AI server is not running yet."))
            addView(space(12))
            addView(subtitle("API server will be implemented in Stage 5."))
        })

        content.addView(space(20))
        content.addView(section("DEVICE RESOURCES"))

        content.addView(card {
            addView(subtitle("AVAILABLE RAM"))
            ramText = title("--", 26, blue)
            addView(ramText)
            addView(space(8))
            deviceText = subtitle("Reading device information...")
            addView(deviceText)
            addView(space(12))

            val refresh = Button(this@MainActivity).apply {
                text = "Refresh device information"
                setOnClickListener {
                    refreshDeviceInfo()
                    Toast.makeText(
                        this@MainActivity,
                        "Device information updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            addView(refresh)
        })

        content.addView(space(20))
        content.addView(section("MODEL MANAGER"))
        content.addView(card {
            addView(title("No model loaded", 20, white))
            addView(subtitle("GGUF model support is planned for Stage 2."))
        })

        content.addView(space(20))
        content.addView(section("QUICK ACTIONS"))
        content.addView(card {
            addView(title("Local AI Chat", 18, white))
            addView(subtitle("Coming in Stage 4"))
            addView(space(12))
            addView(title("API Manager", 18, white))
            addView(subtitle("Coming in Stage 5"))
        })

        content.addView(space(28))
        content.addView(subtitle("POCKETAI  •  VERSION 0.1.0"))

        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)
    }

    private fun refreshDeviceInfo() {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memory = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(memory)

        ramText.text = "${memory.availMem / (1024 * 1024)} MB"

        val total = memory.totalMem / (1024 * 1024)
        deviceText.text = "Available system memory • Total: ${total} MB"
    }

    private fun title(
        text: String,
        size: Float,
        color: Int
    ) = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(color)
        gravity = Gravity.START
    }

    private fun subtitle(text: String) = TextView(this).apply {
        this.text = text
        textSize = 12f
        setTextColor(muted)
    }

    private fun section(text: String) = TextView(this).apply {
        this.text = text
        textSize = 13f
        setTextColor(blue)
        setPadding(0, 4, 0, 10)
    }

    private fun space(height: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            1,
            (height * resources.displayMetrics.density).toInt()
        )
    }

    private fun card(
        content: LinearLayout.() -> Unit
    ) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(panel)
        setPadding(16, 18, 16, 18)
        content()
    }.also {
        it.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 8
        }
    }
}
