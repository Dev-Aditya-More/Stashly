package com.example.anchor.openai

import android.util.Log
import com.example.stashly.BuildConfig
import okhttp3.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

val client = OkHttpClient.Builder()
    .callTimeout(30, TimeUnit.SECONDS)   // fail fast
    .build()

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// ChatGPT request/response models
data class ChatMessage(val role: String, val content: String)
data class ChatRequest(val model: String, val messages: List<ChatMessage>, val max_tokens: Int)
data class ChatChoice(val message: ChatMessage)
data class ChatResponse(val choices: List<ChatChoice>)

suspend fun generateTitle(text: String, apiKey: String): String? =
    withContext(Dispatchers.IO) {

        try {

            val jsonAdapter = moshi.adapter(ChatRequest::class.java)
            val responseAdapter = moshi.adapter(ChatResponse::class.java)

            val requestBody = jsonAdapter.toJson(
                ChatRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        ChatMessage("system", "You generate short, meaningful titles."),
                        ChatMessage("user", "Generate a concise title for this text:\n\n$text")
                    ),
                    max_tokens = 15
                )
            ).toRequestBody("application/json; charset=utf-8".toMediaType())

            Log.d("AI_DEBUG", "Sending request with key prefix: ${BuildConfig.OPENAI_API_KEY.take(8)}…")
            Log.d("AI_DEBUG", "Request body: $requestBody")

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer $apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val body = response.body?.string() ?: return@withContext null

                Log.d("AI_DEBUG", "Response code: ${response.code}")
                Log.d("AI_DEBUG", "Response body: ${response.body?.string()}")


                val parsed = responseAdapter.fromJson(body)
                parsed?.choices?.firstOrNull()?.message?.content?.trim()
            }
        } catch (e: Exception) {
            Log.e("AI_ERROR", "Failed to generate title", e)
            null
        }
    }
