package com.example.data

import android.util.Log
import com.example.model.Depth
import com.example.model.PracticeQuestion
import com.example.model.QuestionMode
import com.example.model.Subject
import com.example.network.DatasetFile
import com.example.network.GeminiQuestionExtractor
import com.example.network.HuggingFaceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object QuestionRepository {
    private const val TAG = "QuestionRepository"

    // Default chapters for immediate UI availability before or alongside dataset caching
    private val defaultChaptersMap: Map<Subject, List<String>> = mapOf(
        Subject.LAW to listOf(
            "Chapter 1 - Indian Regulatory Framework",
            "Chapter 2 - Indian Contract Act, 1872",
            "Chapter 3 - Sale of Goods Act, 1930",
            "Chapter 4 - Indian Partnership Act, 1932",
            "Chapter 5 - LLP Act, 2008",
            "Chapter 6 - Companies Act, 2013",
            "Chapter 7 - Negotiable Instruments Act, 1881",
            "Practice Papers (MTP/RTP/PYQ)"
        ),
        Subject.ACC to listOf(
            "Chapter 1 - Theoretical Framework",
            "Chapter 2 - Accounting Process",
            "Chapter 3 - Bank Reconciliation Statement",
            "Chapter 4 - Inventories",
            "Chapter 5 - Depreciation and Amortisation",
            "Chapter 6 - Bills of Exchange and Promissory Notes",
            "Chapter 7 - Preparation of Final Accounts of Sole Proprietors",
            "Chapter 8 - Financial Statements of NPO",
            "Chapter 9 - Accounts from Incomplete Records",
            "Chapter 10 - Partnership and LLP Accounts",
            "Chapter 11 - Company Accounts",
            "Practice Papers (MTP/RTP/PYQ)"
        ),
        Subject.QUANT to listOf(
            "Chapter 1 - Ratio and Proportion, Indices, Logarithms",
            "Chapter 2 - Equations",
            "Chapter 3 - Linear Inequalities",
            "Chapter 4 - Mathematics of Finance",
            "Chapter 5 - Permutations and Combinations",
            "Chapter 6 - Sequence and Series",
            "Chapter 7 - Sets, Relations and Functions",
            "Chapter 8 - Calculus",
            "Chapter 9 - Number Series, Coding",
            "Chapter 10 - Direction Sense Test",
            "Chapter 11 - Seating Arrangements",
            "Chapter 12 - Blood Relations",
            "Chapter 13 - Statistical Description of & Sampling",
            "Chapter 14 - Measures Central Tendency & Dispersion",
            "Chapter 15 - Probability",
            "Chapter 16 - Theoretical Distributions",
            "Chapter 17 - Correlation and Regression",
            "Chapter 18 - Index Numbers",
            "Practice Papers (MTP/RTP/PYQ)"
        ),
        Subject.ECO to listOf(
            "Chapter 1 - Scope & Nature of Business Economics",
            "Chapter 2 - Demand and Supply",
            "Chapter 3 - Production and Cost",
            "Chapter 4 - Price Determination in Different Markets",
            "Chapter 5 - National Income Determination",
            "Chapter 6 - Business Cycles",
            "Chapter 7 - Public Finance",
            "Chapter 8 - Monetary Policy and Money Market",
            "Chapter 9 - International Trade",
            "Chapter 10 - Indian Economy",
            "Practice Papers (MTP/RTP/PYQ)"
        )
    )

    // Safety fallback questions if device is offline or during Robolectric testing
    private val offlineFallbacks: List<PracticeQuestion> = listOf(
        PracticeQuestion(
            id = 1,
            topic = "Doctrine of Privity of Contract",
            questionText = "Explain what is meant by the Doctrine of Privity of Contract and state the recognized exceptions where a stranger to a contract can sue under the Indian Contract Act, 1872.",
            subject = Subject.LAW,
            depth = Depth.UNIT,
            chapter = "Chapter 2 - Indian Contract Act, 1872",
            mode = QuestionMode.SUBJECTIVE,
            modelAnswer = "The Doctrine of Privity of Contract implies that only parties to a contract can sue and be sued upon it; a stranger to a contract cannot enforce it even if it was made for his benefit (Dunlop Pneumatic Tyre Co. Ltd. v. Selfridge & Co.).\n\nRecognised Exceptions:\n1. Beneficiary under a trust or charge on specific immovable property.\n2. Family arrangements and marriage settlements in writing.\n3. Acknowledgment or estoppel (promisor admits liability directly to third party).\n4. Assignee of a contract (benefits assigned under enforceable deed).\n5. Covenants running with the land."
        ),
        PracticeQuestion(
            id = 2,
            topic = "Fundamental Accounting Assumptions",
            questionText = "State the essential elements of Fundamental Accounting Assumptions as per AS-1 and explain the disclosure requirements if an enterprise fails to observe them.",
            subject = Subject.ACC,
            depth = Depth.UNIT,
            chapter = "Chapter 1 - Theoretical Framework",
            mode = QuestionMode.SUBJECTIVE,
            modelAnswer = "Under AS-1 / Conceptual Framework, there are three fundamental accounting assumptions:\n1. Going Concern: The enterprise is normally viewed as continuing in operation for the foreseeable future with neither intention nor necessity of liquidation.\n2. Consistency: Accounting policies are consistent from one period to another.\n3. Accrual: Revenues and costs are recognised as they are earned or incurred (not as money is received or paid).\n\nIf fundamental assumptions are followed, specific disclosure in financial statements is NOT required. Only if they are NOT followed must the fact be disclosed."
        ),
        PracticeQuestion(
            id = 3,
            topic = "Compound Interest & Depreciation",
            questionText = "If the difference between CI and SI on a certain sum for 2 years at 10% per annum is ₹ 150, what is the principal sum?",
            subject = Subject.QUANT,
            depth = Depth.CHAPTER,
            chapter = "Chapter 4 - Mathematics of Finance",
            mode = QuestionMode.MCQ,
            mcqQuestion = "If the difference between CI and SI on a certain sum for 2 years at 10% per annum is ₹ 150, what is the principal sum?",
            options = listOf("₹ 12,000", "₹ 15,000", "₹ 18,000", "₹ 20,000"),
            correctIndex = 1,
            explanation = "Difference for 2 years = P * (r / 100)². Here: 150 = P * (10 / 100)² = P * (1 / 100) = P / 100. Therefore: P = 150 * 100 = ₹ 15,000.",
            hintFormula = "For 2 years: CI - SI = P * (r / 100)²."
        ),
        PracticeQuestion(
            id = 4,
            topic = "Price Elasticity of Demand",
            questionText = "When total outlay/expenditure on a good increases as its price decreases, the elasticity of demand is:",
            subject = Subject.ECO,
            depth = Depth.UNIT,
            chapter = "Chapter 2 - Demand and Supply",
            mode = QuestionMode.MCQ,
            mcqQuestion = "When total outlay/expenditure on a good increases as its price decreases, the elasticity of demand is:",
            options = listOf("Greater than 1 (Elastic)", "Equal to 1 (Unitary)", "Less than 1 (Inelastic)", "Zero (Perfectly Inelastic)"),
            correctIndex = 0,
            explanation = "Under Marshall's Total Outlay method: When price and total expenditure move in opposite directions (i.e., expenditure rises when price falls), demand is price-elastic (Ed > 1).",
            hintFormula = "Opposite direction (P↓, Exp↑) => Ed > 1; Same direction => Ed < 1; Constant => Ed = 1."
        )
    )

    /**
     * Primary live fetch-and-parse flow.
     * Fetches exactly ONE real question live from the dataset using Hugging Face + Gemini.
     */
    suspend fun fetchLiveQuestion(
        subject: Subject,
        chapter: String,
        depth: Depth
    ): PracticeQuestion = withContext(Dispatchers.IO) {
        val allFiles = HuggingFaceService.getCachedFiles()
        val candidateFiles = filterFiles(allFiles, subject, chapter, depth).shuffled()

        val filesToTry = if (candidateFiles.isNotEmpty()) {
            candidateFiles
        } else {
            // Broaden filter to subject or all files if specific filter has no exact files
            val broader = allFiles.filter { subject == Subject.ALL || it.subject == subject }.shuffled()
            if (broader.isNotEmpty()) broader else allFiles.shuffled()
        }

        // Try candidate files with built-in model failover
        val maxAttempts = filesToTry.size.coerceAtMost(2).coerceAtLeast(1)
        for (i in 0 until maxAttempts) {
            val targetFile = filesToTry[i]
            val rawText = HuggingFaceService.fetchRawContent(targetFile.path)
            if (rawText.isNullOrBlank()) {
                continue
            }

            val answerText = targetFile.answerPath?.let { HuggingFaceService.fetchRawContent(it) }

            val question = GeminiQuestionExtractor.extractQuestion(
                rawText = rawText,
                targetSubject = if (subject != Subject.ALL) subject else targetFile.subject,
                chapterHint = targetFile.chapter,
                depthHint = if (depth != Depth.ALL) depth else targetFile.depth,
                answerText = answerText
            )

            if (question != null) {
                val finalQuestion = if (!answerText.isNullOrBlank()) {
                    question.copy(rawSyllabusAnswer = answerText)
                } else {
                    question
                }
                return@withContext finalQuestion
            }
        }

        getCuratedFallback(subject, chapter, depth)
    }

    /**
     * Preserves existing signature so callers (UI, ViewModel, tests) don't break.
     */
    fun getFilteredQuestions(
        subject: Subject,
        chapter: String,
        depth: Depth
    ): List<PracticeQuestion> {
        return runBlocking(Dispatchers.IO) {
            listOf(fetchLiveQuestion(subject, chapter, depth))
        }
    }

    fun getChaptersForSubject(subject: Subject): List<String> {
        val defaultList = if (subject == Subject.ALL) {
            defaultChaptersMap.values.flatten().distinct().sorted()
        } else {
            defaultChaptersMap[subject] ?: emptyList()
        }
        return defaultList
    }

    private val chapterUnitsMap: Map<String, List<String>> = mapOf(
        // Business Law
        "Chapter 2 - Indian Contract Act, 1872" to listOf(
            "Unit 1: Nature of Contracts",
            "Unit 2: Consideration",
            "Unit 3: Other Essential Elements of a Contract",
            "Unit 4: Performance of Contract",
            "Unit 5: Breach of Contract and its Remedies",
            "Unit 6: Contingent and Quasi Contracts"
        ),
        "Chapter 3 - Sale of Goods Act, 1930" to listOf(
            "Unit 1: Formation of the Contract of Sale",
            "Unit 2: Conditions & Warranties",
            "Unit 3: Transfer of Ownership & Delivery",
            "Unit 4: Unpaid Seller"
        ),
        "Chapter 4 - Indian Partnership Act, 1932" to listOf(
            "Unit 1: General Nature of a Partnership",
            "Unit 2: Relations of Partners",
            "Unit 3: Registration and Dissolution of a Firm"
        ),
        "Chapter 7 - Negotiable Instruments Act, 1881" to listOf(
            "Unit 1: Meaning and Characteristics of Negotiable Instruments",
            "Unit 2: Parties to Notes, Bills and Cheques",
            "Unit 3: Negotiation, Presentment & Dishonour"
        ),
        // Accounting
        "Chapter 1 - Theoretical Framework" to listOf(
            "Unit 1: Meaning and Scope of Accounting",
            "Unit 2: Accounting Concepts, Principles & Conventions",
            "Unit 3: Terms, Capital and Revenue Expenditures",
            "Unit 4: Contingent Assets & Liabilities",
            "Unit 5: Accounting Policies",
            "Unit 6: Accounting as a Measurement Discipline",
            "Unit 7: Accounting Standards and Ind AS"
        ),
        "Chapter 2 - Accounting Process" to listOf(
            "Unit 1: Basic Accounting Procedures & Journal",
            "Unit 2: Ledgers",
            "Unit 3: Trial Balance",
            "Unit 4: Subsidiary Books & Cash Book",
            "Unit 5: Rectification of Errors"
        ),
        "Chapter 10 - Partnership and LLP Accounts" to listOf(
            "Unit 1: Introduction to Partnership Accounts",
            "Unit 2: Treatment of Goodwill in Partnership Accounts",
            "Unit 3: Admission of a New Partner",
            "Unit 4: Retirement of a Partner",
            "Unit 5: Death of a Partner"
        ),
        "Chapter 11 - Company Accounts" to listOf(
            "Unit 1: Introduction to Company Accounts",
            "Unit 2: Issue, Forfeiture & Re-issue of Shares",
            "Unit 3: Redemption of Preference Shares",
            "Unit 4: Redemption of Debentures"
        ),
        // Economics
        "Chapter 2 - Demand and Supply" to listOf(
            "Unit 1: Law of Demand & Elasticity of Demand",
            "Unit 2: Theory of Consumer Behaviour",
            "Unit 3: Supply and Elasticity of Supply"
        ),
        "Chapter 3 - Production and Cost" to listOf(
            "Unit 1: Theory of Production",
            "Unit 2: Theory of Cost"
        ),
        "Chapter 4 - Price Determination in Different Markets" to listOf(
            "Unit 1: Meaning and Types of Markets",
            "Unit 2: Determination of Prices",
            "Unit 3: Price-Output Determination under Different Market Forms"
        )
    )

    fun getUnitsForChapter(subject: Subject, chapter: String): List<String> {
        return chapterUnitsMap[chapter] ?: emptyList()
    }

    fun getAvailableFileCount(subject: Subject, chapter: String, depth: Depth): Int {
        val files = runBlocking(Dispatchers.IO) { HuggingFaceService.getCachedFiles() }
        val count = filterFiles(files, subject, chapter, depth).size
        return if (count > 0) count else 10
    }

    private fun filterFiles(
        files: List<DatasetFile>,
        subject: Subject,
        chapter: String,
        depth: Depth
    ): List<DatasetFile> {
        return files.filter { file ->
            val matchesSubject = subject == Subject.ALL || file.subject == subject
            val matchesChapter = chapter == "all" ||
                    file.chapter.equals(chapter, ignoreCase = true) ||
                    file.path.contains(chapter, ignoreCase = true)
            val matchesDepth = depth == Depth.ALL || file.depth == depth
            matchesSubject && matchesChapter && matchesDepth
        }
    }

    private fun getCuratedFallback(
        subject: Subject,
        chapter: String,
        depth: Depth
    ): PracticeQuestion {
        val matching = offlineFallbacks.filter { q ->
            (subject == Subject.ALL || q.subject == subject) &&
                    (chapter == "all" || q.chapter.contains(chapter, ignoreCase = true)) &&
                    (depth == Depth.ALL || q.depth == depth)
        }
        return if (matching.isNotEmpty()) {
            matching.random()
        } else {
            val subjectMatching = offlineFallbacks.filter { subject == Subject.ALL || it.subject == subject }
            if (subjectMatching.isNotEmpty()) subjectMatching.random() else offlineFallbacks.first()
        }
    }
}
