package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAssistant {
    private const val TAG = "GeminiAssistant"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Sends a chat prompt to Gemini 3.5 Flash and returns the text response.
     */
    suspend fun getHealthAssistantResponse(userPrompt: String, chatHistory: List<Pair<String, Boolean>> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Key is missing or invalid. Please configure GEMINI_API_KEY in the AI Studio Secrets panel."
        }

        try {
            // Build the contents JSON array
            val contentsArray = JSONArray()

            // Include chat history if present
            // Pair is: first = message text, second = isUserMessage
            chatHistory.forEach { (msgText, isUser) ->
                val contentObj = JSONObject()
                contentObj.put("role", if (isUser) "user" else "model")
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", msgText)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }

            // Add the latest user prompt
            val latestContentObj = JSONObject()
            latestContentObj.put("role", "user")
            val latestPartsArray = JSONArray()
            val latestPartObj = JSONObject()
            latestPartObj.put("text", userPrompt)
            latestPartsArray.put(latestPartObj)
            latestContentObj.put("parts", latestPartsArray)
            contentsArray.put(latestContentObj)

            // System Instruction
            val systemInstructionObj = JSONObject()
            val systemPartsArray = JSONArray()
            val systemPartObj = JSONObject()
            systemPartObj.put("text", "You are MedTrack AI, an empathetic, highly knowledgeable medical chatbot and health assistant. " +
                    "Your goals are to answer healthcare questions, demystify medical terms, explain medicine dosages, and provide visual health wellness guidelines. " +
                    "CRITICAL DISCLAIMER: You must always explicitly state that your response is for informational and educational purposes only and DOES NOT substitute professional medical advice, diagnosis, or treatment. Suggest consulting a Doctor if they report severe symptoms.")
            systemPartsArray.put(systemPartObj)
            systemInstructionObj.put("parts", systemPartsArray)

            // Generation Config
            val generationConfigObj = JSONObject()
            generationConfigObj.put("temperature", 0.3)

            // Master Request Object
            val masterRequestObj = JSONObject()
            masterRequestObj.put("contents", contentsArray)
            masterRequestObj.put("systemInstruction", systemInstructionObj)
            masterRequestObj.put("generationConfig", generationConfigObj)

            val requestBodyString = masterRequestObj.toString()
            Log.d(TAG, "Request payload constructed successfully")

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestBodyString.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful API Response: Code=${response.code}, Body=$errBody")
                    return@withContext "Error: API returned status ${response.code}. Please check if the API key is active."
                }

                val responseBodyString = response.body?.string() ?: ""
                Log.d(TAG, "Response obtained successfully")

                val responseJson = JSONObject(responseBodyString)
                val candidatesArray = responseJson.optJSONArray("candidates")
                if (candidatesArray != null && candidatesArray.length() > 0) {
                    val firstCandidate = candidatesArray.getJSONObject(0)
                    val contentObjResult = firstCandidate.optJSONObject("content")
                    val partsArrayResult = contentObjResult?.optJSONArray("parts")
                    if (partsArrayResult != null && partsArrayResult.length() > 0) {
                        return@withContext partsArrayResult.getJSONObject(0).optString("text", "No readable text from assistant")
                    }
                }
                return@withContext "No response candidates generated by Gemini."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during call to Gemini API: ${e.message}", e)
            return@withContext "Network or processing error occurred: ${e.localizedMessage ?: "Unknown error"}. Check internet connection or API settings."
        }
    }

    /**
     * Simulates scanning a prescription sheet (using standard Gemini vision or OCR parsing simulation).
     * If there's an image, we can prompt Gemini to read and extract medication schedule and details as structured JSON.
     */
    suspend fun extractPrescriptionDetails(extractedTextFromOCR: String): PrescriptionScanResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext PrescriptionScanResult(
                success = false,
                message = "API Key missing. Cannot call analyzer."
            )
        }

        val prompt = "Extract prescription details from the following raw text scan. Respond ONLY under these keys: " +
                "{\n" +
                "  \"medicineName\": \"string\",\n" +
                "  \"dosage\": \"string (e.g. 500mg, 1 pill, 5ml)\",\n" +
                "  \"frequency\": \"string (e.g. Daily, Twice a day, Once a week, As needed)\",\n" +
                "  \"timesList\": [\"08:00\", \"20:00\"],\n" +
                "  \"instructions\": \"string (e.g. After food, take with water)\",\n" +
                "  \"categoryColorValue\": 0xFF4CAF50,\n" +
                "  \"type\": \"string (Pill, Syrup, Injection, Inhaler, Other)\"\n" +
                "}\n" +
                "Text to extract from: \"$extractedTextFromOCR\"\n" +
                "Respond strictly with valid JSON. Do not include markdown blocks or quotes. If details cannot be found, make plausible healthcare suggestions based on context."

        try {
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)

            val contentsArray = JSONArray().put(contentObj)

            // Configuration for JSON response
            val responseFormatTextObj = JSONObject()
            responseFormatTextObj.put("mimeType", "application/json")
            val responseFormatObj = JSONObject()
            responseFormatObj.put("text", responseFormatTextObj)

            val generationConfigObj = JSONObject()
            generationConfigObj.put("responseFormat", responseFormatObj)
            generationConfigObj.put("temperature", 0.1)

            val masterRequestObj = JSONObject()
            masterRequestObj.put("contents", contentsArray)
            masterRequestObj.put("generationConfig", generationConfigObj)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(masterRequestObj.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext PrescriptionScanResult(success = false, message = "HTTP ${response.code}")
                }
                val bodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val text = candidates.getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    val resultJson = JSONObject(text.trim())
                    val timesArray = resultJson.optJSONArray("timesList")
                    val times = mutableListOf<String>()
                    if (timesArray != null) {
                        for (i in 0 until timesArray.length()) {
                            times.add(timesArray.getString(i))
                        }
                    }

                    return@withContext PrescriptionScanResult(
                        success = true,
                        medicineName = resultJson.optString("medicineName", "Unknown Medication"),
                        dosage = resultJson.optString("dosage", "1 pill"),
                        frequency = resultJson.optString("frequency", "Daily"),
                        times = times,
                        instructions = resultJson.optString("instructions", "Take with water"),
                        categoryColorValue = resultJson.optInt("categoryColorValue", -11419137), // Cyan/Blue default
                        type = resultJson.optString("type", "Pill"),
                        message = "Extraction complete!"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "OCR Analysis failed", e)
        }
        return@withContext PrescriptionScanResult(success = false, message = "Failed to parse text scan. Please enter manually.")
    }
}

data class PrescriptionScanResult(
    val success: Boolean,
    val medicineName: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val times: List<String> = emptyList(),
    val instructions: String = "",
    val categoryColorValue: Int = -11419137,
    val type: String = "Pill",
    val message: String = ""
)
