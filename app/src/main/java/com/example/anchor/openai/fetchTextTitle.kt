package com.example.anchor.openai

import okhttp3.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

val client = OkHttpClient()
val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


data class OpenAIRequest(val model: String, val prompt: String, val max_tokens: Int)
data class Choice(val text: String)
data class OpenAIResponse(val choices: List<Choice>)

suspend fun generateTitleSuspend(text: String, apiKey: String): String? = withContext(Dispatchers.IO) {
    val jsonAdapter = moshi.adapter(OpenAIRequest::class.java)
    val responseAdapter = moshi.adapter(OpenAIResponse::class.java)

    val prompt = "Generate a concise, meaningful title for this text:\n\n$text"

    val requestBody = jsonAdapter.toJson(
        OpenAIRequest(
            model = "text-davinci-003",
            prompt = prompt,
            max_tokens = 20
        )
    ).toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("https://api.openai.com/v1/completions")
        .header("Authorization", "Bearer $apiKey")
        .post(requestBody)
        .build()

    try {
        val response = client.newCall(request).execute() // sync call in IO dispatcher
        response.body?.string()?.let {
            val openAIResponse = responseAdapter.fromJson(it)
            openAIResponse?.choices?.firstOrNull()?.text?.trim()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

