package com.example.phishing

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private lateinit var toggleButton: Button
    private lateinit var instructionsText: TextView
    private lateinit var prefs: SharedPreferences

    companion object {
        const val PREFS_NAME = "PhishingPrefs"
        const val KEY_DETECTION_ENABLED = "detection_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Updated IDs according to activity_main.xml
        toggleButton = findViewById(R.id.btnToggleDetection)
        instructionsText = findViewById(R.id.tvInstructions)
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Request POST_NOTIFICATIONS permission (for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        toggleButton.setOnClickListener {
            val isEnabled = prefs.getBoolean(KEY_DETECTION_ENABLED, true)
            prefs.edit().putBoolean(KEY_DETECTION_ENABLED, !isEnabled).apply()
            updateToggleButtonText()
            Toast.makeText(this, "Phishing Detection is now ${if (!isEnabled) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        updateToggleButtonText()

        // Check if notification listener service is enabled
        if (!isNotificationServiceEnabled()) {
            openNotificationAccessSettings()
        }
    }

    private fun updateToggleButtonText() {
        val isEnabled = prefs.getBoolean(KEY_DETECTION_ENABLED, true)
        toggleButton.text = if (isEnabled) "Disable Detection" else "Enable Detection"
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val enabledListeners =
            Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return enabledListeners != null && enabledListeners.contains(packageName)
    }

    private fun openNotificationAccessSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
}
