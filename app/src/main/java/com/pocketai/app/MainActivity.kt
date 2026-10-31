package com.pocketai.app

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.StatFs
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var ramText: TextView
    private lateinit var storageText: TextView
    private lateinit var batteryText: TextView
    private lateinit var cpuText: TextView

    private val background = Color.rgb(10, 14, 22)
    private val panel = Color.rgb(22, 30, 42)
    private val white = Color.rgb(240, 244, 255)
    private val muted = Color.rgb(166, 180, 200)
    private val blue = Color.rgb(121, 177, 255)
    private val green = Color.rgb(103, 218, 163)

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

        content.addView(title("PocketAI", 30f, white))
        content.addView(subtitle("YOUR PHONE. YOUR MODELS. YOUR API."))
        content.addView(space(22))

        content.addView(section("SERVER STATUS"))
        content.addView(card {
            addView(title("Offline", 27f, white))
            addView(subtitle("Your local AI server is not running yet."))
            addView(space(10))
            addView(subtitle("This app is ready for Stage 3+ local inference and API server work."))
        })

        content.addView(space(20))
        content.addView(section("DEVICE RESOURCES"))
        content.addView(card {
            addView(subtitle("AVAILABLE RAM"))
            ramText = title("--", 24f, blue)
            addView(ramText)
            addView(space(12))

            addView(subtitle("STORAGE"))
            storageText = title("--", 18f, white)
            addView(storageText)
            addView(space(12))

            addView(subtitle("BATTERY"))
            batteryText = title("--", 18f, green)
            addView(batteryText)
            addView(space(12))

            addView(subtitle("CPU / PLATFORM"))
            cpuText = title("--", 16f, white)
            addView(cpuText)
            addView(space(12))

            val refresh = Button(this@MainActivity).apply {
                text = "Refresh device information"
                setOnClickListener {
                    refreshDeviceInfo()
                    Toast.makeText(this@MainActivity, "Device information updated", Toast.LENGTH_SHORT).show()
                }
            }
            addView(refresh)
        })

        content.addView(space(20))
        content.addView(section("MODEL MANAGER"))
        content.addView(card {
            addView(title("No model loaded", 20f, white))
            addView(subtitle("GGUF support is planned for the next development stage."))
            addView(space(12))
            addView(subtitle("Model folder: /storage/emulated/0/PocketAI/models"))
        })

        content.addView(space(20))
        content.addView(section("QUICK ACTIONS"))
        content.addView(card {
            addView(title("Local AI Chat", 18f, white))
            addView(subtitle("Coming after the model engine is ready."))
            addView(space(10))
            addView(title("API Manager", 18f, white))
            addView(subtitle("HTTP server setup is scheduled for later stages."))
        })

        content.addView(space(26))
        content.addView(subtitle("PocketAI  •  VERSION 0.1.0"))

        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)
    }

    private fun refreshDeviceInfo() {
        val manager = getSystemService(ActivityManager::class.java) as ActivityManager
        val memory = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(memory)

        val totalRamMb = memory.totalMem / (1024L * 1024L)
        val availableRamMb = memory.availMem / (1024L * 1024L)
        ramText.text = "${availableRamMb} MB free / ${totalRamMb} MB total"

        val storageTextValue = buildStorageSummary()
        storageText.text = storageTextValue

        batteryText.text = readBatteryStatus()
        cpuText.text = buildCpuSummary()
    }

    private fun buildStorageSummary(): String {
        val appDir = filesDir
        val statFs = StatFs(appDir.absolutePath)

        val totalBytes = statFs.blockCountLong * statFs.blockSizeLong
        val availableBytes = statFs.availableBytes
        val usedBytes = totalBytes - availableBytes

        val totalGb = totalBytes / (1024L * 1024L * 1024L)
        val usedGb = usedBytes / (1024L * 1024L * 1024L)
        return "${usedGb} GB used / ${totalGb} GB total"
    }

    private fun readBatteryStatus(): String {
        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        return if (level >= 0 && scale > 0) {
            val percent = (level * 100f / scale).toInt()
            "$percent% battery"
        } else {
            "Battery unavailable"
        }
    }

    private fun buildCpuSummary(): String {
        val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"
        return "Android ${Build.VERSION.RELEASE} • ${abi}"
    }

    private fun title(text: String, size: Float, color: Int) = TextView(this).apply {
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

    private fun card(content: LinearLayout.() -> Unit) = LinearLayout(this).apply {
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
