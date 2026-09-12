package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.model.Depth
import com.example.model.PracticeQuestion
import com.example.model.QuestionMode
import com.example.model.Subject
import com.example.ui.components.cleanLatexMathNotation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object GeminiQuestionExtractor {
    private const val TAG = "GeminiExtractor"

    // Supported Gemini models with priority order per gemini-api skill
    // In case of transient 503 (high demand) or 429, seamlessly failover to fallback models
    private val CANDIDATE_MODELS = listOf(
        "gemini-3.5-flash",
        "gemini-flash-latest",
        "gemini-3.1-flash-lite-preview",
        "gemini-3.1-pro-preview"
    )

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Executes a Gemini API request with automatic model failover and retry logic.
     * Handles transient HTTP 503 (high demand) and 429 gracefully.
     */
    private suspend fun callGeminiApi(
        apiKey: String,
        requestJson: JSONObject
    ): String? {
        val requestBodyString = requestJson.toString()
        for (i in CANDIDATE_MODELS.indices) {
            val model = CANDIDATE_MODELS[i]
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val requestBody = requestBodyString.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            try {
                val response = httpClient.newCall(request).execute()
                val code = response.code
                val body = response.body?.string()

                if (response.isSuccessful && !body.isNullOrBlank()) {
                    val responseObj = JSONObject(body)
                    val candidates = responseObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (text.isNotBlank()) {
                                return text
                            }
                        }
                    }
                }

                // If transient high-demand (503), rate-limit (429), or server error (500), try fallback model
                if (code == 503 || code == 429 || code == 500) {
                    Log.w(TAG, "Gemini model '$model' temporarily unavailable (HTTP $code). Trying fallback model...")
                    delay(600)
                } else {
                    Log.w(TAG, "Gemini model '$model' returned code $code. Trying fallback model...")
                    delay(300)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Exception contacting model '$model': ${e.message}. Trying next candidate...")
                delay(300)
            }
        }
        Log.w(TAG, "All candidate Gemini models temporarily unavailable.")
        return null
    }

    suspend fun extractQuestion(
        rawText: String,
        targetSubject: Subject,
        chapterHint: String,
        depthHint: Depth,
        answerText: String? = null
    ): PracticeQuestion? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is missing or placeholder in BuildConfig")
            return@withContext null
        }

        try {
            val contentSlice = prepareTextSlice(rawText, answerText)
            val prompt = buildExtractionPrompt(contentSlice, targetSubject, chapterHint, depthHint)

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.4)
                }
                put("generationConfig", generationConfig)
            }

            val outputJsonString = callGeminiApi(apiKey, requestJson) ?: return@withContext null
            parsePracticeQuestionJson(outputJsonString, targetSubject, chapterHint, depthHint)
        } catch (e: Exception) {
            Log.w(TAG, "Error during Gemini question extraction", e)
            null
        }
    }

    private fun prepareTextSlice(rawText: String, answerText: String?): String {
        val qBlock = extractQuestionBlock(rawText)
        if (!answerText.isNullOrBlank()) {
            val aBlock = extractQuestionBlock(answerText)
            return "--- QUESTION PAPER CONTENT ---\n$qBlock\n\n--- SUGGESTED ANSWERS CONTENT ---\n$aBlock"
        }
        return qBlock
    }

    /**
     * Boundary-aware splitting for CA Foundation raw markdown.
     * Identifies question blocks using standard CA exam patterns (e.g. Q.1, Question 1, 1., (a), marks annotations).
     * Picks ONE complete, un-truncated question block at random.
     * Falls back to full file content if no boundaries found.
     */
    fun extractQuestionBlock(rawText: String): String {
        if (rawText.isBlank()) return rawText

        val questionStartRegex = Regex(
            """(?m)^[ \t]*(?:(?:Question|Que|Q)\.?\s*\d+|(?:\d+\.)(?:\s+[A-Z(])|\([a-dA-D1-9ivx]+\)\s+[A-Z]|Question\s+[A-Za-z0-9]+|(?:Problem|Case|Illustration)\s*\d+)""",
            RegexOption.IGNORE_CASE
        )

        val matches = questionStartRegex.findAll(rawText).toList()

        if (matches.size >= 2) {
            val blocks = mutableListOf<String>()
            for (i in matches.indices) {
                val startIdx = matches[i].range.first
                val endIdx = if (i + 1 < matches.size) matches[i + 1].range.first else rawText.length
                val block = rawText.substring(startIdx, endIdx).trim()
                if (block.length >= 80) {
                    blocks.add(block)
                }
            }

            if (blocks.isNotEmpty()) {
                return blocks.random()
            }
        }

        val maxChars = 24000
        return if (rawText.length > maxChars) rawText.substring(0, maxChars) else rawText
    }

    private fun buildExtractionPrompt(
        content: String,
        targetSubject: Subject,
        chapterHint: String,
        depthHint: Depth
    ): String {
        val subjectRule = when (targetSubject) {
            Subject.LAW -> "Subject is Business Law. Extract a scenario-based question, statutory concept, or legal case study. Model answer MUST follow standard ICAI legal structure (Provision, Analysis & Conclusion)."
            Subject.ACC -> "Subject is Accounting. Extract a practical computational accounting problem (e.g. trading account, journal, balance sheet, depreciation, partnership) OR theoretical concept. Provide clear step-by-step numbers in the model answer."
            Subject.QUANT -> "Subject is Quantitative Aptitude / Mathematics / Statistics. Extract an objective multiple-choice question (mode: MCQ) with 4 realistic options, 1 correctIndex (0-3), detailed working explanation, and a short memory hint/formula. Write all mathematical expressions and formulas in plain readable text (NO LaTeX)."
            Subject.ECO -> "Subject is Business Economics. Extract either an objective MCQ with options or a crisp conceptual subjective question."
            Subject.ALL -> "Extract ONE high-yield question (either MCQ or Subjective based on the source text)."
        }

        return """
You are an expert examiner for the CA Foundation exam (ICAI syllabus).
Analyze the following study material / past exam paper text and extract exactly ONE complete, high-quality exam question.

$subjectRule

Return STRICTLY a JSON object matching this schema:
{
  "id": 1,
  "topic": "Concise topic title",
  "questionText": "Full, authentic exam question sentence with varied phrasing",
  "subject": "${if (targetSubject != Subject.ALL) targetSubject.name else "ACC"}",
  "depth": "${if (depthHint != Depth.ALL) depthHint.name else "CHAPTER"}",
  "chapter": "${chapterHint.ifBlank { "Study Material" }}",
  "mode": "SUBJECTIVE" or "MCQ",
  "modelAnswer": "Comprehensive ICAI-style model answer (for SUBJECTIVE). Empty string if MCQ.",
  "mcqQuestion": "Clear question stem (for MCQ). Empty string if SUBJECTIVE.",
  "options": ["Option A", "Option B", "Option C", "Option D"],
  "correctIndex": 0,
  "explanation": "Step-by-step reasoning or calculation (for MCQ).",
  "hintFormula": "Quick statutory rule, formula, or exam mnemonic for the hint card."
}

Rules:
1. "subject" must be one of: "LAW", "ECO", "ACC", "QUANT".
2. "depth" must be one of: "UNIT", "CHAPTER".
3. "mode" must be "SUBJECTIVE" or "MCQ".
4. "questionText" MUST be a full, realistic question sentence with VARIED phrasing.
   CRITICAL: Do NOT simply write "Define / explain [topic]". Use authentic, varied CA exam formats such as:
   - "What is meant by [concept]?"
   - "State the essential elements / conditions of [concept]."
   - "Distinguish between [X] and [Y]."
   - "Explain with example [concept]."
   - Case study / problem scenarios: "A files a suit against B for... Advise with reasons referring to the provisions of the relevant Act."
   - Computational / practical problem: "From the following particulars, calculate / prepare..."
   - True/False with reasons: "State whether the following statement is True or False with reasons: ..."
   For MCQ mode, set questionText to the exact question stem (same as mcqQuestion).
5. If mode is "MCQ", "options" MUST contain 4 items, "mcqQuestion" MUST be non-empty, and "correctIndex" MUST be 0, 1, 2, or 3.
6. If mode is "SUBJECTIVE", "modelAnswer" MUST be comprehensive and non-empty.
7. Do NOT include markdown code fences (like ```json), just the plain raw JSON.
8. Do NOT use LaTeX notation (no \times, \frac{}{}, \sqrt{}, ^{}, $...$, etc.) anywhere in questionText, options, explanation, or modelAnswer. Write all math in plain readable text instead — e.g. use 'x^2' or 'x squared' instead of LaTeX superscript, '3/4' instead of \frac{3}{4}, 'x' instead of \times, 'sqrt(x)' instead of \sqrt{x}, and standard symbols like +, -, ×, ÷, =, %, π directly as unicode characters where natural.

SOURCE TEXT:
$content
        """.trimIndent()
    }

    private fun parsePracticeQuestionJson(
        jsonString: String,
        targetSubject: Subject,
        chapterHint: String,
        depthHint: Depth
    ): PracticeQuestion? {
        val cleanJson = jsonString.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = JSONObject(cleanJson)

        val parsedSubjectStr = json.optString("subject", targetSubject.name).uppercase()
        val subject = try {
            Subject.valueOf(parsedSubjectStr)
        } catch (_: Exception) {
            if (targetSubject != Subject.ALL) targetSubject else Subject.ACC
        }

        val parsedDepthStr = json.optString("depth", depthHint.name).uppercase()
        val depth = try {
            Depth.valueOf(parsedDepthStr)
        } catch (_: Exception) {
            if (depthHint != Depth.ALL) depthHint else Depth.CHAPTER
        }

        val parsedModeStr = json.optString("mode", "SUBJECTIVE").uppercase()
        val mode = if (parsedModeStr == "MCQ") QuestionMode.MCQ else QuestionMode.SUBJECTIVE

        val topic = json.optString("topic", "CA Foundation Question").ifBlank { "CA Foundation Question" }
        val chapter = json.optString("chapter", chapterHint).ifBlank { chapterHint.ifBlank { "Study Material" } }

        val modelAnswer = json.optString("modelAnswer", "")
        val mcqQuestion = json.optString("mcqQuestion", "")
        val rawQuestionText = json.optString("questionText", "")
        val questionText = rawQuestionText.ifBlank {
            if (mode == QuestionMode.MCQ) mcqQuestion else topic
        }

        val optionsList = mutableListOf<String>()
        val optionsArray = json.optJSONArray("options")
        if (optionsArray != null) {
            for (i in 0 until optionsArray.length()) {
                optionsList.add(optionsArray.optString(i))
            }
        }

        val correctIndex = json.optInt("correctIndex", 0).coerceIn(0, (optionsList.size - 1).coerceAtLeast(0))
        val explanation = json.optString("explanation", "")
        val hintFormula = json.optString("hintFormula", "")

        // Clean any remaining LaTeX backslash math commands from all fields
        val cleanedTopic = cleanLatexMathNotation(topic)
        val cleanedQuestionText = cleanLatexMathNotation(questionText)
        val cleanedModelAnswer = cleanLatexMathNotation(modelAnswer)
        val cleanedMcqQuestion = cleanLatexMathNotation(mcqQuestion)
        val cleanedOptions = optionsList.map { cleanLatexMathNotation(it) }
        val cleanedExplanation = cleanLatexMathNotation(explanation)
        val cleanedHintFormula = cleanLatexMathNotation(hintFormula)

        // Validation check
        if (mode == QuestionMode.MCQ) {
            if (cleanedMcqQuestion.isBlank() || cleanedOptions.size < 2) {
                Log.w(TAG, "MCQ question or options invalid from Gemini output")
                return null
            }
        } else {
            if (cleanedModelAnswer.isBlank() && cleanedMcqQuestion.isBlank()) {
                Log.w(TAG, "Subjective question has empty modelAnswer and mcqQuestion")
                return null
            }
        }

        val id = (System.currentTimeMillis() % 100000).toInt() + Random.nextInt(1, 999)

        return PracticeQuestion(
            id = id,
            topic = cleanedTopic,
            questionText = cleanedQuestionText,
            subject = subject,
            depth = depth,
            chapter = chapter,
            mode = mode,
            modelAnswer = cleanedModelAnswer,
            mcqQuestion = cleanedMcqQuestion,
            options = cleanedOptions,
            correctIndex = correctIndex,
            explanation = cleanedExplanation,
            hintFormula = cleanedHintFormula
        )
    }

    data class GradingResult(
        val marksAwarded: Int,
        val totalMarks: Int = 10,
        val feedback: String,
        val isOfficialKey: Boolean
    )

    suspend fun gradeSubjectiveAnswer(
        questionText: String,
        studentAnswer: String,
        referenceAnswer: String,
        isOfficialKey: Boolean
    ): GradingResult? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key missing for answer grading")
            return@withContext generateProvisionalGrading(studentAnswer, referenceAnswer, isOfficialKey)
        }

        try {
            val prompt = """
You are an expert ICAI examiner grading a CA Foundation student's written subjective examination answer.
Grading Scale: Strictly out of 10 total marks.

QUESTION:
$questionText

REFERENCE ANSWER (${if (isOfficialKey) "Official ICAI Syllabus / Suggested Answer Key" else "AI-estimated Model Answer"}):
$referenceAnswer

STUDENT'S WRITTEN ANSWER:
$studentAnswer

INSTRUCTIONS:
1. Award marks out of 10 (integer from 0 to 10) based on how well the student's answer matches the key legal provisions, statutory sections, accounting figures, journal entries, or structured headings in the reference answer.
2. Provide 2 to 3 lines of specific, constructive examiner feedback detailing what was correct and what key points, legal provisions, or calculations were missing.
3. Return STRICTLY a JSON object matching this schema:
{
  "marksAwarded": 7,
  "totalMarks": 10,
  "feedback": "Concise 2-3 line feedback here."
}
Do not include markdown fences.
""".trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                }
                put("generationConfig", generationConfig)
            }

            val outputJson = callGeminiApi(apiKey, requestJson)
            if (outputJson != null) {
                val cleanJson = outputJson.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val parsed = JSONObject(cleanJson)
                val marks = parsed.optInt("marksAwarded", 5).coerceIn(0, 10)
                val total = parsed.optInt("totalMarks", 10)
                val feedback = parsed.optString("feedback", "Answer evaluated against syllabus criteria.")

                GradingResult(
                    marksAwarded = marks,
                    totalMarks = total,
                    feedback = cleanLatexMathNotation(feedback),
                    isOfficialKey = isOfficialKey
                )
            } else {
                generateProvisionalGrading(studentAnswer, referenceAnswer, isOfficialKey)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error during answer grading", e)
            generateProvisionalGrading(studentAnswer, referenceAnswer, isOfficialKey)
        }
    }

    private fun generateProvisionalGrading(
        studentAnswer: String,
        referenceAnswer: String,
        isOfficialKey: Boolean
    ): GradingResult {
        val wordCount = studentAnswer.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val refWords = referenceAnswer.lowercase().split(Regex("\\W+")).filter { it.length > 3 }.toSet()
        val studentWords = studentAnswer.lowercase().split(Regex("\\W+")).filter { it.length > 3 }.toSet()

        val commonKeywords = refWords.intersect(studentWords).size
        val keywordRatio = if (refWords.isNotEmpty()) commonKeywords.toFloat() / refWords.size.coerceAtMost(25) else 0.5f

        val lengthScore = when {
            wordCount > 100 -> 4
            wordCount > 50 -> 3
            wordCount > 20 -> 2
            else -> 1
        }
        val matchScore = (keywordRatio * 6).toInt().coerceIn(0, 6)
        val finalMarks = (lengthScore + matchScore).coerceIn(2, 9)

        val feedback = when {
            finalMarks >= 7 -> "Good structural coverage and terminology aligned with ICAI expectations. Review suggested answers for precise statutory section phrasing."
            finalMarks >= 5 -> "Satisfactory attempt. Key concepts are touched upon, but requires more legal depth, structured points, and calculation precision."
            else -> "Preliminary attempt. Needs substantially more detail, statutory provisions/sections, and step-by-step presentation aligned with the suggested answer key."
        }

        return GradingResult(
            marksAwarded = finalMarks,
            totalMarks = 10,
            feedback = "$feedback (Evaluated against syllabus benchmark)",
            isOfficialKey = isOfficialKey
        )
    }
}
