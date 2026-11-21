package com.sextexer.sex

import android.content.Context
import android.net.wifi.WifiManager
import org.json.JSONArray
import org.json.JSONObject

object WifiPwn {
    fun scanWifiNetworks(context: Context): JSONArray {
        val array = JSONArray()
        try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // Note: Scanning requires location permission and usually triggers a UI prompt on newer Android
            // So we rely on already scanned results or connected info
            val info = wifiManager.connectionInfo
            if (info != null) {
                array.put(JSONObject().apply {
                    put("ssid", info.ssid.replace("\"", ""))
                    put("bssid", info.bssid)
                    put("frequency", info.frequency)
                    put("signalStrength", info.rssi)
                    put("security", if (info.wepKeys.size > 0) "WEP" else if (info.wpaKeys.size > 0) "WPA/WPA2" else "Open")
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return array
    }
}
