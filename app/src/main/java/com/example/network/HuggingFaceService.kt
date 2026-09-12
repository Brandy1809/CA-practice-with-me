package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.model.Depth
import com.example.model.PracticeQuestion
import com.example.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class DatasetFile(
    val path: String,
    val subject: Subject,
    val chapter: String,
    val depth: Depth,
    val isPractice: Boolean,
    val isQuestionFile: Boolean,
    val answerPath: String? = null
)

object HuggingFaceService {
    private const val TAG = "HuggingFaceService"
    private const val TREE_URL = "https://huggingface.co/api/datasets/devArv/2027_SM_Data_set_CA_Found/tree/main?recursive=true"
    private const val RESOLVE_BASE_URL = "https://huggingface.co/datasets/devArv/2027_SM_Data_set_CA_Found/resolve/main/"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // In-memory cache of parsed file metadata
    @Volatile
    private var cachedFiles: List<DatasetFile>? = null

    suspend fun getCachedFiles(): List<DatasetFile> = withContext(Dispatchers.IO) {
        cachedFiles?.let { return@withContext it }
        val files = fetchFileList()
        cachedFiles = files
        files
    }

    suspend fun fetchFileList(): List<DatasetFile> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(TREE_URL)
                .header("User-Agent", "Mozilla/5.0 (Android; CA-Foundation-Practice)")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e(TAG, "Failed to fetch tree: code ${response.code}")
                return@withContext emptyList()
            }

            val bodyString = response.body?.string() ?: return@withContext emptyList()
            val jsonArray = JSONArray(bodyString)
            val allMdPaths = mutableListOf<String>()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val type = item.optString("type")
                val path = item.optString("path")
                if (type == "file" && path.endsWith(".md", ignoreCase = true)) {
                    allMdPaths.add(path)
                }
            }

            parseDatasetFiles(allMdPaths)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching dataset file list", e)
            emptyList()
        }
    }

    private fun parseDatasetFiles(paths: List<String>): List<DatasetFile> {
        val result = mutableListOf<DatasetFile>()
        val practiceFiles = paths.filter { it.startsWith("Practice") }

        for (path in paths) {
            // Determine Subject
            val subject = when {
                path.contains("Accounting", ignoreCase = true) || path.contains("ACC", ignoreCase = true) -> Subject.ACC
                path.contains("Business Law", ignoreCase = true) || path.contains("LAW", ignoreCase = true) -> Subject.LAW
                path.contains("Quantitative Aptitude", ignoreCase = true) || path.contains("QA", ignoreCase = true) || path.contains("MATH", ignoreCase = true) -> Subject.QUANT
                path.contains("Business Economics", ignoreCase = true) || path.contains("ECO", ignoreCase = true) -> Subject.ECO
                else -> continue // Skip non-subject files like README.md
            }

            val isPractice = path.startsWith("Practice")
            val isQuestionFile = if (isPractice) {
                path.contains("(Q)") || path.contains(" (Q) ")
            } else {
                false
            }

            // For Practice Question files, attempt to pair with Answer file
            var answerPath: String? = null
            if (isPractice && isQuestionFile) {
                val candidateA = path.replace(" (Q) ", " (A) ").replace("(Q)", "(A)")
                if (practiceFiles.contains(candidateA)) {
                    answerPath = candidateA
                } else {
                    val parentFolder = path.substringBeforeLast("/")
                    val candidateInFolder = practiceFiles.firstOrNull {
                        it.startsWith(parentFolder) && (it.contains("(A)") || it.contains(" (A) "))
                    }
                    answerPath = candidateInFolder
                }
            }

            // Determine Chapter
            val chapter = parseChapterName(path, subject, isPractice)

            // Determine Depth
            val isUnit = path.contains("/Unit ", ignoreCase = true) ||
                    path.contains("_U1", ignoreCase = true) ||
                    path.contains("_U2", ignoreCase = true) ||
                    path.contains("_U3", ignoreCase = true) ||
                    path.contains("_U4", ignoreCase = true) ||
                    path.contains("_U5", ignoreCase = true)

            val depth = if (isUnit) Depth.UNIT else Depth.CHAPTER

            result.add(
                DatasetFile(
                    path = path,
                    subject = subject,
                    chapter = chapter,
                    depth = depth,
                    isPractice = isPractice,
                    isQuestionFile = isQuestionFile,
                    answerPath = answerPath
                )
            )
        }
        return result
    }

    private fun parseChapterName(path: String, subject: Subject, isPractice: Boolean): String {
        if (isPractice) {
            return "Practice Papers (MTP/RTP/PYQ)"
        }

        val parts = path.split("/")
        for (part in parts) {
            if (part.startsWith("Chapter", ignoreCase = true) || part.startsWith("Ch", ignoreCase = true)) {
                return normalizeChapterName(part, subject)
            }
        }

        // Check filename if folder didn't have Chapter
        val filename = path.substringAfterLast("/")
        if (filename.startsWith("ENG_CA_F_SM_2027_CH", ignoreCase = true)) {
            val chPart = filename.substringAfter("ENG_CA_F_SM_2027_").substringBefore("_")
            return "Chapter ${chPart.removePrefix("CH")}"
        }

        return "Study Material"
    }

    private fun normalizeChapterName(raw: String, subject: Subject): String {
        // Normalize names for consistent display and filter matching
        return raw.trim()
    }

    suspend fun fetchRawContent(path: String): String? = withContext(Dispatchers.IO) {
        try {
            // Encode path segments properly for URL
            val encodedSegments = path.split("/").joinToString("/") { segment ->
                URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
            }
            val url = "$RESOLVE_BASE_URL$encodedSegments"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android; CA-Foundation-Practice)")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                Log.e(TAG, "Failed to resolve $path: code ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching raw content for $path", e)
            null
        }
    }

    private const val COMMIT_DATASET_URL = "https://huggingface.co/api/datasets/devArv/202X_CA_Found_Question_Bank/commit/main"
    private const val SYNC_TAG = "HFQuestionBankSync"

    suspend fun commitQuestionToBank(question: PracticeQuestion): Boolean = withContext(Dispatchers.IO) {
        val hfToken = BuildConfig.HF_WRITE_TOKEN
        if (hfToken.isBlank() || hfToken == "MY_HF_WRITE_TOKEN") {
            Log.w(SYNC_TAG, "HF_WRITE_TOKEN is blank or placeholder ('$hfToken'). Skipping Hugging Face Question Bank sync.")
            return@withContext false
        }

        try {
            val subjectDir = question.subject.name
            val sanitizedChapter = question.chapter
                .replace("/", "-")
                .replace("\\", "-")
                .replace(":", "-")
                .trim()
                .ifBlank { "General" }
            val unitDir = if (question.depth == Depth.UNIT) "Unit-level" else "Chapter-level"
            val filePath = "$subjectDir/$sanitizedChapter/$unitDir/${question.id}.json"

            Log.d(SYNC_TAG, "Initiating commit to HF Question Bank: targetPath='$filePath', tokenPresent=${hfToken.isNotBlank()} (length=${hfToken.length})")

            val jsonObject = JSONObject().apply {
                put("id", question.id)
                put("topic", question.topic)
                put("questionText", question.questionText)
                put("subject", question.subject.name)
                put("depth", question.depth.name)
                put("chapter", question.chapter)
                put("mode", question.mode.name)
                put("modelAnswer", question.modelAnswer)
                put("mcqQuestion", question.mcqQuestion)
                val optionsArray = JSONArray()
                question.options.forEach { optionsArray.put(it) }
                put("options", optionsArray)
                put("correctIndex", question.correctIndex)
                put("explanation", question.explanation)
                put("hintFormula", question.hintFormula)
                if (question.rawSyllabusAnswer != null) {
                    put("rawSyllabusAnswer", question.rawSyllabusAnswer)
                }
            }

            val fileContentString = jsonObject.toString(2)
            val base64Content = try {
                android.util.Base64.encodeToString(
                    fileContentString.toByteArray(Charsets.UTF_8),
                    android.util.Base64.NO_WRAP
                )
            } catch (_: Throwable) {
                java.util.Base64.getEncoder().encodeToString(
                    fileContentString.toByteArray(Charsets.UTF_8)
                )
            }

            val summaryText = "Add question: ${question.topic.take(60)}"

            val headerLine = JSONObject().apply {
                put("key", "header")
                put("value", JSONObject().apply {
                    put("summary", summaryText)
                    put("description", "")
                })
            }

            val fileLine = JSONObject().apply {
                put("key", "file")
                put("value", JSONObject().apply {
                    put("content", base64Content)
                    put("encoding", "base64")
                    put("path", filePath)
                })
            }

            val ndjsonPayload = "$headerLine\n$fileLine\n"
            val requestBody = ndjsonPayload.toRequestBody("application/x-ndjson".toMediaType())

            val request = Request.Builder()
                .url(COMMIT_DATASET_URL)
                .header("Authorization", "Bearer $hfToken")
                .header("Content-Type", "application/x-ndjson")
                .header("Accept", "*/*")
                .post(requestBody)
                .build()

            var response = httpClient.newCall(request).execute()
            var responseCode = response.code
            var responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Log.d(SYNC_TAG, "Successfully committed question ${question.id} to HF Question Bank at $filePath (HTTP $responseCode)")
                return@withContext true
            }

            // Fallback to standard application/json commit format if NDJSON returned 400 or other client error
            Log.w(SYNC_TAG, "NDJSON commit returned HTTP $responseCode ($responseBody). Attempting application/json commit fallback...")

            val jsonCommitObject = JSONObject().apply {
                put("summary", summaryText)
                put("description", "")
                val filesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("path", filePath)
                        put("content", base64Content)
                        put("encoding", "base64")
                    })
                }
                put("files", filesArray)
            }

            val jsonRequestBody = jsonCommitObject.toString().toRequestBody("application/json".toMediaType())
            val jsonRequest = Request.Builder()
                .url(COMMIT_DATASET_URL)
                .header("Authorization", "Bearer $hfToken")
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .post(jsonRequestBody)
                .build()

            response = httpClient.newCall(jsonRequest).execute()
            responseCode = response.code
            responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Log.d(SYNC_TAG, "Successfully committed question ${question.id} via application/json to HF Question Bank at $filePath (HTTP $responseCode)")
                true
            } else {
                Log.e(SYNC_TAG, "Failed to commit question to HF Question Bank: HTTP $responseCode, response body: $responseBody")
                false
            }
        } catch (e: Exception) {
            Log.e(SYNC_TAG, "Exception while committing question to HF Question Bank: ${e.message}", e)
            false
        }
    }
}
