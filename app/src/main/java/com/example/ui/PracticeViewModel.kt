package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuestionRepository
import com.example.data.local.AppDatabase
import com.example.data.local.AttemptedQuestionEntity
import com.example.data.local.ProgressStatsEntity
import com.example.data.local.SyncedQuestionEntity
import com.example.model.AccountRow
import com.example.model.AccountingTableType
import com.example.model.BlockType
import com.example.model.BrsRow
import com.example.model.BrsTableData
import com.example.model.CashBookRow
import com.example.model.CashBookTableData
import com.example.model.Depth
import com.example.model.InsertedBlock
import com.example.model.JournalRow
import com.example.model.LedgerRow
import com.example.model.LedgerTableData
import com.example.model.PartnersCapitalRow
import com.example.model.PartnersCapitalTableData
import com.example.model.PettyCashRow
import com.example.model.PracticeQuestion
import com.example.model.QuestionMode
import com.example.model.Subject
import com.example.model.TrialBalanceRow
import com.example.model.TwoSidedTableData
import com.example.model.WorkingNote
import com.example.network.GeminiQuestionExtractor
import com.example.network.HuggingFaceService
import com.example.util.AudioHapticManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class PracticeUiState(
    val selectedSubject: Subject = Subject.ALL,
    val selectedChapter: String = "all",
    val selectedDepth: Depth = Depth.ALL,
    val availableChapters: List<String> = emptyList(),
    val questionCount: Int = 0,
    val currentQuestion: PracticeQuestion? = null,
    val isFetchingQuestion: Boolean = false,
    val userAnswerText: String = "",
    val showModelAnswer: Boolean = false,
    val activeTableType: AccountingTableType = AccountingTableType.NONE,
    val insertedBlocks: List<InsertedBlock> = emptyList(),
    val workingNotes: Map<String, WorkingNote> = emptyMap(),
    val twoSidedBlocks: Map<String, TwoSidedTableData> = emptyMap(),
    val ledgerBlocks: Map<String, LedgerTableData> = emptyMap(),
    val journalBlocks: Map<String, List<JournalRow>> = emptyMap(),
    val trialBalanceBlocks: Map<String, List<TrialBalanceRow>> = emptyMap(),
    val brsBlocks: Map<String, BrsTableData> = emptyMap(),
    val cashBookBlocks: Map<String, CashBookTableData> = emptyMap(),
    val partnersCapitalBlocks: Map<String, PartnersCapitalTableData> = emptyMap(),
    val pettyCashBlocks: Map<String, List<PettyCashRow>> = emptyMap(),
    val tradingDrRows: List<AccountRow> = emptyList(),
    val tradingCrRows: List<AccountRow> = emptyList(),
    val plDrRows: List<AccountRow> = emptyList(),
    val plCrRows: List<AccountRow> = emptyList(),
    val manufacturingDrRows: List<AccountRow> = emptyList(),
    val manufacturingCrRows: List<AccountRow> = emptyList(),
    val revaluationDrRows: List<AccountRow> = emptyList(),
    val revaluationCrRows: List<AccountRow> = emptyList(),
    val realisationDrRows: List<AccountRow> = emptyList(),
    val realisationCrRows: List<AccountRow> = emptyList(),
    val consignmentDrRows: List<AccountRow> = emptyList(),
    val consignmentCrRows: List<AccountRow> = emptyList(),
    val bsLiabilityRows: List<AccountRow> = emptyList(),
    val bsAssetRows: List<AccountRow> = emptyList(),
    val ledgerAccountName: String = "Cash Account",
    val ledgerDrRows: List<LedgerRow> = emptyList(),
    val ledgerCrRows: List<LedgerRow> = emptyList(),
    val journalRows: List<JournalRow> = emptyList(),
    val trialBalanceRows: List<TrialBalanceRow> = emptyList(),
    val brsStartingBalance: String = "50000",
    val brsRows: List<BrsRow> = emptyList(),
    val cashBookDrRows: List<CashBookRow> = emptyList(),
    val cashBookCrRows: List<CashBookRow> = emptyList(),
    val partnersCapitalDrRows: List<PartnersCapitalRow> = emptyList(),
    val partnersCapitalCrRows: List<PartnersCapitalRow> = emptyList(),
    val pettyCashRows: List<PettyCashRow> = emptyList(),
    val formatSearchQuery: String = "",
    val isFormatSearchExpanded: Boolean = false,
    val selectedOptionIndex: Int? = null,
    val isHintModalVisible: Boolean = false,
    val isHintUnlocked: Boolean = false,
    val questionsAttemptedCount: Int = 0,
    val questionsAnsweredCount: Int = 0,
    val questionsViewedCount: Int = 0,
    val hasSubmittedCurrentSubjective: Boolean = false,
    val mcqCorrectCount: Int = 0,
    val mcqAttemptedCount: Int = 0,
    val isWritingViewOpen: Boolean = false,
    val isQuestionSheetOpenInWritingView: Boolean = false,
    val isGrading: Boolean = false,
    val gradingResult: GeminiQuestionExtractor.GradingResult? = null
)

class PracticeViewModel(application: Application) : AndroidViewModel(application) {

    private val audioHapticManager = AudioHapticManager(application.applicationContext)
    private val appDao = AppDatabase.getInstance(application).appDao()
    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var loadQuestionJob: Job? = null
    private var lastQuestionId: Int = -1

    init {
        // Observe Room persistence as the source of truth for stats
        viewModelScope.launch {
            appDao.observeAllProgressStats().collect { statsList ->
                val totalAnswered = statsList.sumOf { it.questionsAnsweredCount }
                val totalAttempted = statsList.sumOf { it.questionsAttemptedCount }
                val finalAnswered = if (totalAnswered > 0) totalAnswered else totalAttempted
                val totalViewed = statsList.sumOf { it.questionsViewedCount }
                val totalCorrect = statsList.sumOf { it.mcqCorrectCount }
                val totalMcq = statsList.sumOf { it.mcqAttemptedCount }
                _uiState.update {
                    it.copy(
                        questionsAnsweredCount = finalAnswered,
                        questionsAttemptedCount = finalAnswered,
                        questionsViewedCount = maxOf(it.questionsViewedCount, totalViewed),
                        mcqCorrectCount = totalCorrect,
                        mcqAttemptedCount = totalMcq
                    )
                }
            }
        }

        updateAvailableChaptersAndPool()
        loadNextQuestion(initial = true)

        // Preload dataset file metadata tree in background
        viewModelScope.launch(Dispatchers.IO) {
            try {
                HuggingFaceService.getCachedFiles()
                val count = QuestionRepository.getAvailableFileCount(
                    _uiState.value.selectedSubject,
                    _uiState.value.selectedChapter,
                    _uiState.value.selectedDepth
                )
                _uiState.update { it.copy(questionCount = count) }
            } catch (_: Exception) {
                // Ignore background pre-fetch failures
            }
        }
    }

    companion object {
        fun getDefaultTradingDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Opening Stock", amount = ""),
            AccountRow(particulars = "To Purchases (less returns)", amount = ""),
            AccountRow(particulars = "To Direct Wages & Freight", amount = "")
        )

        fun getDefaultTradingCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Sales (less returns)", amount = ""),
            AccountRow(particulars = "By Closing Stock", amount = "")
        )

        fun getDefaultBsLiabilities(): List<AccountRow> = listOf(
            AccountRow(particulars = "Capital (Opening)", amount = ""),
            AccountRow(particulars = "Add: Net Profit for Year", amount = ""),
            AccountRow(particulars = "Less: Drawings", amount = ""),
            AccountRow(particulars = "Bank Loan (Secured)", amount = ""),
            AccountRow(particulars = "Sundry Creditors", amount = "")
        )

        fun getDefaultBsAssets(): List<AccountRow> = listOf(
            AccountRow(particulars = "Plant & Machinery", amount = ""),
            AccountRow(particulars = "Furniture & Fixtures", amount = ""),
            AccountRow(particulars = "Closing Stock", amount = ""),
            AccountRow(particulars = "Sundry Debtors", amount = ""),
            AccountRow(particulars = "Cash & Bank Balances", amount = "")
        )

        fun getDefaultLedgerDr(): List<LedgerRow> = listOf(
            LedgerRow(date = "01/04", particulars = "To Capital A/c", jf = "1", amount = ""),
            LedgerRow(date = "10/04", particulars = "To Sales A/c", jf = "4", amount = "")
        )

        fun getDefaultLedgerCr(): List<LedgerRow> = listOf(
            LedgerRow(date = "05/04", particulars = "By Purchases A/c", jf = "2", amount = ""),
            LedgerRow(date = "15/04", particulars = "By Rent A/c", jf = "3", amount = ""),
            LedgerRow(date = "30/04", particulars = "By Balance c/d", jf = "", amount = "")
        )

        fun getDefaultPlDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Salaries & Wages", amount = ""),
            AccountRow(particulars = "To Rent, Rates & Taxes", amount = ""),
            AccountRow(particulars = "To Depreciation on Plant", amount = ""),
            AccountRow(particulars = "To Discount Allowed", amount = "")
        )

        fun getDefaultPlCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Gross Profit b/d", amount = ""),
            AccountRow(particulars = "By Discount Received", amount = ""),
            AccountRow(particulars = "By Commission Received", amount = "")
        )

        fun getDefaultManufacturingDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Opening Raw Materials", amount = ""),
            AccountRow(particulars = "To Purchases of Raw Materials", amount = ""),
            AccountRow(particulars = "To Direct Factory Wages", amount = ""),
            AccountRow(particulars = "To Factory Power & Fuel", amount = "")
        )

        fun getDefaultManufacturingCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Sale of Factory Scrap", amount = ""),
            AccountRow(particulars = "By Closing Raw Materials", amount = "")
        )

        fun getDefaultRevaluationDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Provision for Doubtful Debts", amount = ""),
            AccountRow(particulars = "To Plant & Machinery (Depreciation)", amount = "")
        )

        fun getDefaultRevaluationCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Land & Building (Appreciation)", amount = ""),
            AccountRow(particulars = "By Sundry Creditors (Written back)", amount = "")
        )

        fun getDefaultRealisationDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Sundry Assets transferred", amount = ""),
            AccountRow(particulars = "To Bank A/c (Creditors settled)", amount = ""),
            AccountRow(particulars = "To Realisation Expenses", amount = "")
        )

        fun getDefaultRealisationCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Sundry Creditors transferred", amount = ""),
            AccountRow(particulars = "By Bank A/c (Assets realised)", amount = "")
        )

        fun getDefaultConsignmentDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Goods Sent on Consignment", amount = ""),
            AccountRow(particulars = "To Bank A/c (Freight & Insurance)", amount = ""),
            AccountRow(particulars = "To Consignee A/c (Godown rent & selling exp)", amount = ""),
            AccountRow(particulars = "To Consignee A/c (Commission)", amount = "")
        )

        fun getDefaultConsignmentCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Consignee A/c (Gross Sales)", amount = ""),
            AccountRow(particulars = "By Consignment Stock c/d", amount = "")
        )

        fun getDefaultJournal(): List<JournalRow> = listOf(
            JournalRow(date = "01/04", particulars = "Bank A/c   ...Dr.", lf = "12", debit = "", credit = "", narration = "(Being capital introduced in bank)"),
            JournalRow(date = "01/04", particulars = "    To Capital A/c", lf = "1", debit = "", credit = "", narration = ""),
            JournalRow(date = "05/04", particulars = "Purchases A/c   ...Dr.", lf = "24", debit = "", credit = "", narration = "(Being goods purchased for cash)"),
            JournalRow(date = "05/04", particulars = "    To Cash A/c", lf = "2", debit = "", credit = "", narration = ""),
            JournalRow(date = "12/04", particulars = "Sundry Debtors A/c   ...Dr.", lf = "35", debit = "", credit = "", narration = "(Being credit sales made)"),
            JournalRow(date = "12/04", particulars = "    To Sales A/c", lf = "15", debit = "", credit = "", narration = "")
        )

        fun getDefaultTrialBalance(): List<TrialBalanceRow> = listOf(
            TrialBalanceRow(sNo = "1", headOfAccount = "Capital Account", lf = "1", debit = "", credit = ""),
            TrialBalanceRow(sNo = "2", headOfAccount = "Plant & Machinery", lf = "4", debit = "", credit = ""),
            TrialBalanceRow(sNo = "3", headOfAccount = "Purchases Account", lf = "8", debit = "", credit = ""),
            TrialBalanceRow(sNo = "4", headOfAccount = "Sales Account", lf = "12", debit = "", credit = ""),
            TrialBalanceRow(sNo = "5", headOfAccount = "Sundry Debtors", lf = "16", debit = "", credit = ""),
            TrialBalanceRow(sNo = "6", headOfAccount = "Sundry Creditors", lf = "20", debit = "", credit = ""),
            TrialBalanceRow(sNo = "7", headOfAccount = "Cash & Bank Balances", lf = "24", debit = "", credit = ""),
            TrialBalanceRow(sNo = "8", headOfAccount = "Salaries & Rent Expenses", lf = "28", debit = "", credit = "")
        )
    }

    private fun getDefaultBrs(): List<BrsRow> = listOf(
        BrsRow(particulars = "Cheques issued to suppliers but not yet presented for payment", isAdd = true, amount = ""),
        BrsRow(particulars = "Interest directly credited by Bank in Passbook", isAdd = true, amount = ""),
        BrsRow(particulars = "Cheques paid into Bank but not yet cleared/collected", isAdd = false, amount = ""),
        BrsRow(particulars = "Bank charges directly debited by Bank", isAdd = false, amount = "")
    )

    private fun getDefaultCashBookDr(): List<CashBookRow> = listOf(
        CashBookRow(date = "01/04", particulars = "To Balance b/d", vn = "", discount = "", cash = "", bank = ""),
        CashBookRow(date = "08/04", particulars = "To Sales A/c", vn = "14", discount = "", cash = "", bank = ""),
        CashBookRow(date = "15/04", particulars = "To Sharma & Co.", vn = "19", discount = "", cash = "", bank = "")
    )

    private fun getDefaultCashBookCr(): List<CashBookRow> = listOf(
        CashBookRow(date = "05/04", particulars = "By Purchases A/c", vn = "21", discount = "", cash = "", bank = ""),
        CashBookRow(date = "12/04", particulars = "By Rent A/c", vn = "25", discount = "", cash = "", bank = ""),
        CashBookRow(date = "28/04", particulars = "By Verma Ltd.", vn = "30", discount = "", cash = "", bank = "")
    )

    private fun getDefaultPartnersCapitalDr(): List<PartnersCapitalRow> = listOf(
        PartnersCapitalRow(date = "15/09", particulars = "To Drawings A/c", partnerA = "", partnerB = ""),
        PartnersCapitalRow(date = "31/03", particulars = "To Balance c/d", partnerA = "", partnerB = "")
    )

    private fun getDefaultPartnersCapitalCr(): List<PartnersCapitalRow> = listOf(
        PartnersCapitalRow(date = "01/04", particulars = "By Balance b/d", partnerA = "", partnerB = ""),
        PartnersCapitalRow(date = "31/03", particulars = "By Interest on Capital (6%)", partnerA = "", partnerB = ""),
        PartnersCapitalRow(date = "31/03", particulars = "By Share of Profit (P&L Approp.)", partnerA = "", partnerB = "")
    )

    private fun getDefaultPettyCash(): List<PettyCashRow> = listOf(
        PettyCashRow(receipts = "", date = "01/04", particulars = "To Cash A/c (Imprest received)", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = ""),
        PettyCashRow(receipts = "", date = "04/04", particulars = "By Taxi fare", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = ""),
        PettyCashRow(receipts = "", date = "08/04", particulars = "By Courier charges", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = ""),
        PettyCashRow(receipts = "", date = "12/04", particulars = "By Printing paper & pens", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = ""),
        PettyCashRow(receipts = "", date = "20/04", particulars = "By Cartage on office goods", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = "")
    )

    private fun updateAvailableChaptersAndPool() {
        val chapters = QuestionRepository.getChaptersForSubject(_uiState.value.selectedSubject)
        val count = QuestionRepository.getAvailableFileCount(
            _uiState.value.selectedSubject,
            _uiState.value.selectedChapter,
            _uiState.value.selectedDepth
        )
        _uiState.update {
            it.copy(
                availableChapters = chapters,
                questionCount = count
            )
        }
    }

    fun onSubjectChanged(subject: Subject) {
        if (_uiState.value.selectedSubject == subject) return
        _uiState.update {
            it.copy(
                selectedSubject = subject,
                selectedChapter = "all"
            )
        }
        updateAvailableChaptersAndPool()
        loadNextQuestion()
    }

    fun onChapterChanged(chapter: String) {
        if (_uiState.value.selectedChapter == chapter) return
        _uiState.update { it.copy(selectedChapter = chapter) }
        updateAvailableChaptersAndPool()
        loadNextQuestion()
    }

    fun onDepthChanged(depth: Depth) {
        if (_uiState.value.selectedDepth == depth) return
        _uiState.update { it.copy(selectedDepth = depth) }
        updateAvailableChaptersAndPool()
        loadNextQuestion()
    }

    fun loadNextQuestion(initial: Boolean = false) {
        loadQuestionJob?.cancel()
        _uiState.update { it.copy(isFetchingQuestion = true) }

        loadQuestionJob = viewModelScope.launch {
            val nextQuestion = withContext(Dispatchers.IO) {
                QuestionRepository.fetchLiveQuestion(
                    _uiState.value.selectedSubject,
                    _uiState.value.selectedChapter,
                    _uiState.value.selectedDepth
                )
            }

            lastQuestionId = nextQuestion.id

            if (!initial) {
                audioHapticManager.playFlip()
            }

            // Automatic Law Structuring pre-fill if subject is Business Law and mode is SUBJECTIVE
            val initialAnswerText = if (nextQuestion.subject == Subject.LAW && nextQuestion.mode == QuestionMode.SUBJECTIVE) {
                "I) Provision:\nUnder the relevant provisions of the statute...\n\nII) Analysis and Conclusion:\nApplying the statutory principles to the facts given..."
            } else {
                ""
            }

            _uiState.update { state ->
                state.copy(
                    currentQuestion = nextQuestion,
                    questionsViewedCount = state.questionsViewedCount + 1,
                    hasSubmittedCurrentSubjective = false,
                    isFetchingQuestion = false,
                    userAnswerText = initialAnswerText,
                    showModelAnswer = false,
                    activeTableType = AccountingTableType.NONE,
                    insertedBlocks = emptyList(),
                    workingNotes = emptyMap(),
                    twoSidedBlocks = emptyMap(),
                    ledgerBlocks = emptyMap(),
                    journalBlocks = emptyMap(),
                    trialBalanceBlocks = emptyMap(),
                    brsBlocks = emptyMap(),
                    cashBookBlocks = emptyMap(),
                    partnersCapitalBlocks = emptyMap(),
                    pettyCashBlocks = emptyMap(),
                    tradingDrRows = emptyList(),
                    tradingCrRows = emptyList(),
                    plDrRows = emptyList(),
                    plCrRows = emptyList(),
                    manufacturingDrRows = emptyList(),
                    manufacturingCrRows = emptyList(),
                    revaluationDrRows = emptyList(),
                    revaluationCrRows = emptyList(),
                    realisationDrRows = emptyList(),
                    realisationCrRows = emptyList(),
                    consignmentDrRows = emptyList(),
                    consignmentCrRows = emptyList(),
                    bsLiabilityRows = getDefaultBsLiabilities(),
                    bsAssetRows = getDefaultBsAssets(),
                    ledgerAccountName = "Cash Account",
                    ledgerDrRows = getDefaultLedgerDr(),
                    ledgerCrRows = getDefaultLedgerCr(),
                    journalRows = getDefaultJournal(),
                    trialBalanceRows = getDefaultTrialBalance(),
                    brsStartingBalance = "50000",
                    brsRows = getDefaultBrs(),
                    cashBookDrRows = getDefaultCashBookDr(),
                    cashBookCrRows = getDefaultCashBookCr(),
                    partnersCapitalDrRows = getDefaultPartnersCapitalDr(),
                    partnersCapitalCrRows = getDefaultPartnersCapitalCr(),
                    pettyCashRows = getDefaultPettyCash(),
                    formatSearchQuery = "",
                    isFormatSearchExpanded = false,
                    selectedOptionIndex = null,
                    isHintUnlocked = false,
                    isHintModalVisible = false,
                    isWritingViewOpen = false,
                    isQuestionSheetOpenInWritingView = false,
                    isGrading = false,
                    gradingResult = null
                )
            }

            // Record viewed question stat in Room and sync to HF Question Bank
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val subjectKey = nextQuestion.subject.name
                    val existing = appDao.getProgressStats(subjectKey) ?: ProgressStatsEntity(subject = subjectKey)
                    val updated = existing.copy(
                        questionsViewedCount = existing.questionsViewedCount + 1
                    )
                    appDao.insertOrUpdateProgressStats(updated)

                    val alreadySynced = appDao.isQuestionSynced(nextQuestion.topic, nextQuestion.chapter) > 0
                    if (!alreadySynced) {
                        val committed = HuggingFaceService.commitQuestionToBank(nextQuestion)
                        if (committed) {
                            appDao.recordSyncedQuestion(
                                SyncedQuestionEntity(
                                    topic = nextQuestion.topic,
                                    chapter = nextQuestion.chapter,
                                    syncedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w("PracticeViewModel", "Background stats or HF Question Bank sync error", e)
                }
            }
        }
    }

    fun openWritingView() {
        _uiState.update { it.copy(isWritingViewOpen = true, isQuestionSheetOpenInWritingView = false) }
    }

    fun closeWritingView() {
        _uiState.update { it.copy(isWritingViewOpen = false, isQuestionSheetOpenInWritingView = false) }
    }

    fun toggleQuestionSheetInWritingView(open: Boolean) {
        _uiState.update { it.copy(isQuestionSheetOpenInWritingView = open) }
    }

    fun onAnswerTextChanged(text: String) {
        _uiState.update { it.copy(userAnswerText = text) }
    }

    fun toggleModelAnswer() {
        _uiState.update { it.copy(showModelAnswer = !it.showModelAnswer) }
    }

    fun selectTableType(tableType: AccountingTableType) {
        if (tableType == AccountingTableType.NONE) {
            _uiState.update { it.copy(activeTableType = AccountingTableType.NONE) }
            return
        }
        addBlock(tableType)
    }

    fun addBlock(type: BlockType, providedTitle: String? = null, customLeftHeader: String? = null, customRightHeader: String? = null): String {
        val blockId = "block_${System.currentTimeMillis()}_${(100..999).random()}"
        val existingCount = _uiState.value.insertedBlocks.count { it.type == type }
        val ordinal = when (existingCount + 1) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            else -> "${existingCount + 1}th"
        }
        val title = providedTitle ?: when (type) {
            BlockType.WORKING_NOTE -> "Working Note ${existingCount + 1}"
            BlockType.LEDGER -> if (existingCount == 0) "Ledger Account (T-Format)" else "Ledger - $ordinal Account"
            else -> if (existingCount == 0) type.title else "${type.title} ($ordinal)"
        }
        val block = InsertedBlock(
            id = blockId,
            type = type,
            isCollapsed = false,
            customTitle = title,
            customLeftHeader = customLeftHeader ?: "",
            customRightHeader = customRightHeader ?: ""
        )

        _uiState.update { state ->
            val updatedBlocks = state.insertedBlocks + block
            when (type) {
                BlockType.TRADING_ACCOUNT -> {
                    val dr = getDefaultTradingDr()
                    val cr = getDefaultTradingCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(dr, cr)),
                        tradingDrRows = if (state.tradingDrRows.isEmpty()) dr else state.tradingDrRows,
                        tradingCrRows = if (state.tradingCrRows.isEmpty()) cr else state.tradingCrRows
                    )
                }
                BlockType.PROFIT_LOSS_ACCOUNT -> {
                    val dr = getDefaultPlDr()
                    val cr = getDefaultPlCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(dr, cr)),
                        plDrRows = if (state.plDrRows.isEmpty()) dr else state.plDrRows,
                        plCrRows = if (state.plCrRows.isEmpty()) cr else state.plCrRows
                    )
                }
                BlockType.MANUFACTURING_ACCOUNT -> {
                    val dr = getDefaultManufacturingDr()
                    val cr = getDefaultManufacturingCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(dr, cr)),
                        manufacturingDrRows = if (state.manufacturingDrRows.isEmpty()) dr else state.manufacturingDrRows,
                        manufacturingCrRows = if (state.manufacturingCrRows.isEmpty()) cr else state.manufacturingCrRows
                    )
                }
                BlockType.REVALUATION_ACCOUNT -> {
                    val dr = getDefaultRevaluationDr()
                    val cr = getDefaultRevaluationCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(dr, cr)),
                        revaluationDrRows = if (state.revaluationDrRows.isEmpty()) dr else state.revaluationDrRows,
                        revaluationCrRows = if (state.revaluationCrRows.isEmpty()) cr else state.revaluationCrRows
                    )
                }
                BlockType.REALISATION_ACCOUNT -> {
                    val dr = getDefaultRealisationDr()
                    val cr = getDefaultRealisationCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(dr, cr)),
                        realisationDrRows = if (state.realisationDrRows.isEmpty()) dr else state.realisationDrRows,
                        realisationCrRows = if (state.realisationCrRows.isEmpty()) cr else state.realisationCrRows
                    )
                }
                BlockType.CUSTOM_FORMAT -> {
                    val dr = List(3) { AccountRow(particulars = "", amount = "") }
                    val cr = List(3) { AccountRow(particulars = "", amount = "") }
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(dr, cr))
                    )
                }
                BlockType.CONSIGNMENT_ACCOUNT -> {
                    val dr = getDefaultConsignmentDr()
                    val cr = getDefaultConsignmentCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(dr, cr)),
                        consignmentDrRows = if (state.consignmentDrRows.isEmpty()) dr else state.consignmentDrRows,
                        consignmentCrRows = if (state.consignmentCrRows.isEmpty()) cr else state.consignmentCrRows
                    )
                }
                BlockType.BALANCE_SHEET -> {
                    val liab = getDefaultBsLiabilities()
                    val ass = getDefaultBsAssets()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        twoSidedBlocks = state.twoSidedBlocks + (blockId to TwoSidedTableData(liab, ass)),
                        bsLiabilityRows = if (state.bsLiabilityRows.isEmpty()) liab else state.bsLiabilityRows,
                        bsAssetRows = if (state.bsAssetRows.isEmpty()) ass else state.bsAssetRows
                    )
                }
                BlockType.LEDGER -> {
                    val acctName = when (existingCount) {
                        0 -> "Cash Account"
                        1 -> "Capital Account"
                        2 -> "Bank Account"
                        else -> "Account ${existingCount + 1}"
                    }
                    val dr = getDefaultLedgerDr()
                    val cr = getDefaultLedgerCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        ledgerBlocks = state.ledgerBlocks + (blockId to LedgerTableData(acctName, dr, cr)),
                        ledgerAccountName = if (existingCount == 0) acctName else state.ledgerAccountName,
                        ledgerDrRows = if (state.ledgerDrRows.isEmpty()) dr else state.ledgerDrRows,
                        ledgerCrRows = if (state.ledgerCrRows.isEmpty()) cr else state.ledgerCrRows
                    )
                }
                BlockType.JOURNAL -> {
                    val rows = getDefaultJournal()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        journalBlocks = state.journalBlocks + (blockId to rows),
                        journalRows = if (state.journalRows.isEmpty()) rows else state.journalRows
                    )
                }
                BlockType.TRIAL_BALANCE -> {
                    val rows = getDefaultTrialBalance()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        trialBalanceBlocks = state.trialBalanceBlocks + (blockId to rows),
                        trialBalanceRows = if (state.trialBalanceRows.isEmpty()) rows else state.trialBalanceRows
                    )
                }
                BlockType.BRS -> {
                    val rows = getDefaultBrs()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        brsBlocks = state.brsBlocks + (blockId to BrsTableData("50000", rows)),
                        brsRows = if (state.brsRows.isEmpty()) rows else state.brsRows
                    )
                }
                BlockType.CASH_BOOK -> {
                    val dr = getDefaultCashBookDr()
                    val cr = getDefaultCashBookCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        cashBookBlocks = state.cashBookBlocks + (blockId to CashBookTableData(dr, cr)),
                        cashBookDrRows = if (state.cashBookDrRows.isEmpty()) dr else state.cashBookDrRows,
                        cashBookCrRows = if (state.cashBookCrRows.isEmpty()) cr else state.cashBookCrRows
                    )
                }
                BlockType.PARTNERS_CAPITAL -> {
                    val dr = getDefaultPartnersCapitalDr()
                    val cr = getDefaultPartnersCapitalCr()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        partnersCapitalBlocks = state.partnersCapitalBlocks + (blockId to PartnersCapitalTableData(dr, cr)),
                        partnersCapitalDrRows = if (state.partnersCapitalDrRows.isEmpty()) dr else state.partnersCapitalDrRows,
                        partnersCapitalCrRows = if (state.partnersCapitalCrRows.isEmpty()) cr else state.partnersCapitalCrRows
                    )
                }
                BlockType.PETTY_CASH_BOOK -> {
                    val rows = getDefaultPettyCash()
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        pettyCashBlocks = state.pettyCashBlocks + (blockId to rows),
                        pettyCashRows = if (state.pettyCashRows.isEmpty()) rows else state.pettyCashRows
                    )
                }
                BlockType.WORKING_NOTE -> {
                    state.copy(
                        insertedBlocks = updatedBlocks,
                        activeTableType = type,
                        workingNotes = state.workingNotes + (blockId to WorkingNote(id = blockId, title = title, content = ""))
                    )
                }
                BlockType.NONE -> state
            }
        }
        return blockId
    }

    fun moveBlockUp(blockId: String) {
        _uiState.update { state ->
            val index = state.insertedBlocks.indexOfFirst { it.id == blockId }
            if (index > 0) {
                val list = state.insertedBlocks.toMutableList()
                val temp = list[index]
                list[index] = list[index - 1]
                list[index - 1] = temp
                state.copy(insertedBlocks = list)
            } else state
        }
    }

    fun moveBlockDown(blockId: String) {
        _uiState.update { state ->
            val index = state.insertedBlocks.indexOfFirst { it.id == blockId }
            if (index != -1 && index < state.insertedBlocks.size - 1) {
                val list = state.insertedBlocks.toMutableList()
                val temp = list[index]
                list[index] = list[index + 1]
                list[index + 1] = temp
                state.copy(insertedBlocks = list)
            } else state
        }
    }

    
    fun clearBlockValues(blockId: String) {
        _uiState.update { state ->
            val block = state.insertedBlocks.find { it.id == blockId } ?: return@update state
            
            val updatedTwoSided = state.twoSidedBlocks.toMutableMap()
            updatedTwoSided[blockId]?.let { ts ->
                updatedTwoSided[blockId] = ts.copy(
                    drRows = ts.drRows.map { it.copy(amount = "") },
                    crRows = ts.crRows.map { it.copy(amount = "") }
                )
            }
            
            val updatedLedger = state.ledgerBlocks.toMutableMap()
            updatedLedger[blockId]?.let { lg ->
                updatedLedger[blockId] = lg.copy(
                    drRows = lg.drRows.map { it.copy(amount = "") },
                    crRows = lg.crRows.map { it.copy(amount = "") }
                )
            }

            val updatedJournal = state.journalBlocks.toMutableMap()
            updatedJournal[blockId]?.let { rows ->
                updatedJournal[blockId] = rows.map { it.copy(debit = "", credit = "") }
            }

            val updatedTrialBalance = state.trialBalanceBlocks.toMutableMap()
            updatedTrialBalance[blockId]?.let { rows ->
                updatedTrialBalance[blockId] = rows.map { it.copy(debit = "", credit = "") }
            }

            val updatedBrs = state.brsBlocks.toMutableMap()
            updatedBrs[blockId]?.let { brs ->
                updatedBrs[blockId] = brs.copy(
                    startingBalance = "",
                    rows = brs.rows.map { it.copy(amount = "") }
                )
            }

            val updatedCashBook = state.cashBookBlocks.toMutableMap()
            updatedCashBook[blockId]?.let { cb ->
                updatedCashBook[blockId] = cb.copy(
                    drRows = cb.drRows.map { it.copy(discount = "", cash = "", bank = "") },
                    crRows = cb.crRows.map { it.copy(discount = "", cash = "", bank = "") }
                )
            }

            val updatedPartnersCapital = state.partnersCapitalBlocks.toMutableMap()
            updatedPartnersCapital[blockId]?.let { pc ->
                updatedPartnersCapital[blockId] = pc.copy(
                    drRows = pc.drRows.map { it.copy(partnerA = "", partnerB = "") },
                    crRows = pc.crRows.map { it.copy(partnerA = "", partnerB = "") }
                )
            }

            val updatedPettyCash = state.pettyCashBlocks.toMutableMap()
            updatedPettyCash[blockId]?.let { rows ->
                updatedPettyCash[blockId] = rows.map { it.copy(receipts = "", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = "") }
            }
            
            val updatedWorkingNotes = state.workingNotes.toMutableMap()
            updatedWorkingNotes[blockId]?.let { wn ->
                updatedWorkingNotes[blockId] = wn.copy(content = "")
            }

            state.copy(
                twoSidedBlocks = updatedTwoSided,
                ledgerBlocks = updatedLedger,
                journalBlocks = updatedJournal,
                trialBalanceBlocks = updatedTrialBalance,
                brsBlocks = updatedBrs,
                cashBookBlocks = updatedCashBook,
                partnersCapitalBlocks = updatedPartnersCapital,
                pettyCashBlocks = updatedPettyCash,
                workingNotes = updatedWorkingNotes
            )
        }
    }

    fun removeBlock(blockId: String) {
        _uiState.update { state ->
            val updatedBlocks = state.insertedBlocks.filterNot { it.id == blockId }
            state.copy(
                insertedBlocks = updatedBlocks,
                activeTableType = updatedBlocks.lastOrNull()?.type ?: AccountingTableType.NONE,
                workingNotes = state.workingNotes - blockId,
                twoSidedBlocks = state.twoSidedBlocks - blockId,
                ledgerBlocks = state.ledgerBlocks - blockId,
                journalBlocks = state.journalBlocks - blockId,
                trialBalanceBlocks = state.trialBalanceBlocks - blockId,
                brsBlocks = state.brsBlocks - blockId,
                cashBookBlocks = state.cashBookBlocks - blockId,
                partnersCapitalBlocks = state.partnersCapitalBlocks - blockId,
                pettyCashBlocks = state.pettyCashBlocks - blockId
            )
        }
    }

    fun toggleBlockCollapse(blockId: String) {
        _uiState.update { state ->
            val updated = state.insertedBlocks.map {
                if (it.id == blockId) it.copy(isCollapsed = !it.isCollapsed) else it
            }
            state.copy(insertedBlocks = updated)
        }
    }

    fun generateExportJson(): String {
        val state = _uiState.value
        val json = org.json.JSONObject()
        
        state.currentQuestion?.let { q ->
            val qObj = org.json.JSONObject().apply {
                put("id", q.id)
                put("topic", q.topic)
                put("questionText", q.questionText)
                put("subject", q.subject.name)
                put("chapter", q.chapter)
                put("depth", q.depth.name)
                put("mode", q.mode.name)
                put("modelAnswer", q.modelAnswer)
                put("explanation", q.explanation)
            }
            json.put("question", qObj)
        }
        json.put("userAnswer", state.userAnswerText)
        
        val blocksArr = org.json.JSONArray()
        state.insertedBlocks.forEachIndexed { index, block ->
            val bObj = org.json.JSONObject().apply {
                put("id", block.id)
                put("type", block.type.name)
                put("order", index)
                put("title", block.displayTitle)
                put("isCollapsed", block.isCollapsed)
                
                // Working Note
                state.workingNotes[block.id]?.let { wn ->
                    val wnObj = org.json.JSONObject()
                    wnObj.put("title", wn.title)
                    wnObj.put("content", wn.content)
                    put("workingNote", wnObj)
                }
                
                // Two-Sided Data
                state.twoSidedBlocks[block.id]?.let { ts ->
                    val tsObj = org.json.JSONObject()
                    val drArr = org.json.JSONArray()
                    ts.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("particulars", it.particulars); put("amount", it.amount) }) }
                    val crArr = org.json.JSONArray()
                    ts.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("particulars", it.particulars); put("amount", it.amount) }) }
                    tsObj.put("drRows", drArr)
                    tsObj.put("crRows", crArr)
                    put("twoSidedData", tsObj)
                }
                
                // Ledger Data
                state.ledgerBlocks[block.id]?.let { lg ->
                    val lgObj = org.json.JSONObject()
                    lgObj.put("accountName", lg.accountName)
                    val drArr = org.json.JSONArray()
                    lg.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("jf", it.jf); put("amount", it.amount) }) }
                    val crArr = org.json.JSONArray()
                    lg.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("jf", it.jf); put("amount", it.amount) }) }
                    lgObj.put("drRows", drArr)
                    lgObj.put("crRows", crArr)
                    put("ledgerData", lgObj)
                }

                // Journal Data
                state.journalBlocks[block.id]?.let { rows ->
                    val rArr = org.json.JSONArray()
                    rows.forEach { rArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("lf", it.lf); put("debit", it.debit); put("credit", it.credit); put("narration", it.narration) }) }
                    put("journalData", rArr)
                }

                // Trial Balance Data
                state.trialBalanceBlocks[block.id]?.let { rows ->
                    val rArr = org.json.JSONArray()
                    rows.forEach { rArr.put(org.json.JSONObject().apply { put("sNo", it.sNo); put("headOfAccount", it.headOfAccount); put("lf", it.lf); put("debit", it.debit); put("credit", it.credit) }) }
                    put("trialBalanceData", rArr)
                }

                // BRS Data
                state.brsBlocks[block.id]?.let { brs ->
                    val brsObj = org.json.JSONObject()
                    brsObj.put("startingBalance", brs.startingBalance)
                    val rArr = org.json.JSONArray()
                    brs.rows.forEach { rArr.put(org.json.JSONObject().apply { put("particulars", it.particulars); put("isAdd", it.isAdd); put("amount", it.amount) }) }
                    brsObj.put("rows", rArr)
                    put("brsData", brsObj)
                }

                // Cash Book Data
                state.cashBookBlocks[block.id]?.let { cb ->
                    val cbObj = org.json.JSONObject()
                    val drArr = org.json.JSONArray()
                    cb.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("vn", it.vn); put("discount", it.discount); put("cash", it.cash); put("bank", it.bank) }) }
                    val crArr = org.json.JSONArray()
                    cb.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("vn", it.vn); put("discount", it.discount); put("cash", it.cash); put("bank", it.bank) }) }
                    cbObj.put("drRows", drArr)
                    cbObj.put("crRows", crArr)
                    put("cashBookData", cbObj)
                }

                // Partners Capital Data
                state.partnersCapitalBlocks[block.id]?.let { pc ->
                    val pcObj = org.json.JSONObject()
                    val drArr = org.json.JSONArray()
                    pc.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("partnerA", it.partnerA); put("partnerB", it.partnerB) }) }
                    val crArr = org.json.JSONArray()
                    pc.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("partnerA", it.partnerA); put("partnerB", it.partnerB) }) }
                    pcObj.put("drRows", drArr)
                    pcObj.put("crRows", crArr)
                    put("partnersCapitalData", pcObj)
                }

                // Petty Cash Data
                state.pettyCashBlocks[block.id]?.let { rows ->
                    val rArr = org.json.JSONArray()
                    rows.forEach { rArr.put(org.json.JSONObject().apply { put("receipts", it.receipts); put("date", it.date); put("particulars", it.particulars); put("totalPayment", it.totalPayment); put("cartage", it.cartage); put("conveyance", it.conveyance); put("stationery", it.stationery); put("misc", it.misc) }) }
                    put("pettyCashData", rArr)
                }
            }
            blocksArr.put(bObj)
        }
        json.put("insertedBlocks", blocksArr)
        
        return json.toString(4)
    }
    fun updateWorkingNote(blockId: String, title: String, content: String) {
        _uiState.update { state ->
            val existing = state.workingNotes[blockId] ?: WorkingNote(blockId, title, content)
            state.copy(
                workingNotes = state.workingNotes + (blockId to existing.copy(title = title, content = content))
            )
        }
    }

    // Two-Sided Block Mutations (Trading, P&L, Mfg, Reval, Realisation, Consignment, Balance Sheet)
    fun updateTwoSidedDrRow(blockId: String, index: Int, particulars: String, amount: String) {
        _uiState.update { state ->
            val current = state.twoSidedBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            state.copy(twoSidedBlocks = state.twoSidedBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun updateTwoSidedCrRow(blockId: String, index: Int, particulars: String, amount: String) {
        _uiState.update { state ->
            val current = state.twoSidedBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            state.copy(twoSidedBlocks = state.twoSidedBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    fun addTwoSidedDrRow(blockId: String, defaultParticulars: String = "To Particulars") {
        _uiState.update { state ->
            val current = state.twoSidedBlocks[blockId] ?: return@update state
            state.copy(twoSidedBlocks = state.twoSidedBlocks + (blockId to current.copy(drRows = current.drRows + AccountRow(particulars = defaultParticulars, amount = ""))))
        }
    }

    fun addTwoSidedCrRow(blockId: String, defaultParticulars: String = "By Particulars") {
        _uiState.update { state ->
            val current = state.twoSidedBlocks[blockId] ?: return@update state
            state.copy(twoSidedBlocks = state.twoSidedBlocks + (blockId to current.copy(crRows = current.crRows + AccountRow(particulars = defaultParticulars, amount = ""))))
        }
    }

    fun deleteTwoSidedDrRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.twoSidedBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(twoSidedBlocks = state.twoSidedBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun deleteTwoSidedCrRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.twoSidedBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(twoSidedBlocks = state.twoSidedBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    // Ledger Block Mutations
    fun updateLedgerAccountName(blockId: String, name: String) {
        _uiState.update { state ->
            val current = state.ledgerBlocks[blockId] ?: return@update state
            state.copy(ledgerBlocks = state.ledgerBlocks + (blockId to current.copy(accountName = name)))
        }
    }

    fun updateLedgerDrRow(blockId: String, index: Int, date: String, particulars: String, jf: String, amount: String) {
        _uiState.update { state ->
            val current = state.ledgerBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list[index] = LedgerRow(date = date, particulars = particulars, jf = jf, amount = amount)
            state.copy(ledgerBlocks = state.ledgerBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun updateLedgerCrRow(blockId: String, index: Int, date: String, particulars: String, jf: String, amount: String) {
        _uiState.update { state ->
            val current = state.ledgerBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list[index] = LedgerRow(date = date, particulars = particulars, jf = jf, amount = amount)
            state.copy(ledgerBlocks = state.ledgerBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    fun addLedgerDrRow(blockId: String) {
        _uiState.update { state ->
            val current = state.ledgerBlocks[blockId] ?: return@update state
            state.copy(ledgerBlocks = state.ledgerBlocks + (blockId to current.copy(drRows = current.drRows + LedgerRow(date = "01/05", particulars = "To Entry", jf = "", amount = ""))))
        }
    }

    fun addLedgerCrRow(blockId: String) {
        _uiState.update { state ->
            val current = state.ledgerBlocks[blockId] ?: return@update state
            state.copy(ledgerBlocks = state.ledgerBlocks + (blockId to current.copy(crRows = current.crRows + LedgerRow(date = "01/05", particulars = "By Entry", jf = "", amount = ""))))
        }
    }

    fun deleteLedgerDrRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.ledgerBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(ledgerBlocks = state.ledgerBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun deleteLedgerCrRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.ledgerBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(ledgerBlocks = state.ledgerBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    // Journal Block Mutations
    fun updateJournalRow(blockId: String, index: Int, date: String, particulars: String, lf: String, debit: String, credit: String, narration: String) {
        _uiState.update { state ->
            val current = state.journalBlocks[blockId] ?: return@update state
            val list = current.toMutableList()
            if (index in list.indices) list[index] = JournalRow(date = date, particulars = particulars, lf = lf, debit = debit, credit = credit, narration = narration)
            state.copy(journalBlocks = state.journalBlocks + (blockId to list))
        }
    }

    fun addJournalRow(blockId: String) {
        _uiState.update { state ->
            val current = state.journalBlocks[blockId] ?: return@update state
            state.copy(journalBlocks = state.journalBlocks + (blockId to current + JournalRow(particulars = "To Account A/c", debit = "0", credit = "")))
        }
    }

    fun deleteJournalRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.journalBlocks[blockId] ?: return@update state
            val list = current.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(journalBlocks = state.journalBlocks + (blockId to list))
        }
    }

    // Trial Balance Block Mutations
    fun updateTrialBalanceRow(blockId: String, index: Int, sNo: String, headOfAccount: String, lf: String, debit: String, credit: String) {
        _uiState.update { state ->
            val current = state.trialBalanceBlocks[blockId] ?: return@update state
            val list = current.toMutableList()
            if (index in list.indices) list[index] = TrialBalanceRow(sNo = sNo, headOfAccount = headOfAccount, lf = lf, debit = debit, credit = credit)
            state.copy(trialBalanceBlocks = state.trialBalanceBlocks + (blockId to list))
        }
    }

    fun addTrialBalanceRow(blockId: String) {
        _uiState.update { state ->
            val current = state.trialBalanceBlocks[blockId] ?: return@update state
            val nextNo = (current.size + 1).toString()
            state.copy(trialBalanceBlocks = state.trialBalanceBlocks + (blockId to current + TrialBalanceRow(sNo = nextNo, headOfAccount = "Account Head", debit = "0", credit = "")))
        }
    }

    fun deleteTrialBalanceRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.trialBalanceBlocks[blockId] ?: return@update state
            val list = current.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(trialBalanceBlocks = state.trialBalanceBlocks + (blockId to list))
        }
    }

    // BRS Block Mutations
    fun updateBrsStartingBalance(blockId: String, balance: String) {
        _uiState.update { state ->
            val current = state.brsBlocks[blockId] ?: return@update state
            state.copy(brsBlocks = state.brsBlocks + (blockId to current.copy(startingBalance = balance)))
        }
    }

    fun updateBrsRow(blockId: String, index: Int, particulars: String, isAdd: Boolean, amount: String) {
        _uiState.update { state ->
            val current = state.brsBlocks[blockId] ?: return@update state
            val list = current.rows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, isAdd = isAdd, amount = amount)
            state.copy(brsBlocks = state.brsBlocks + (blockId to current.copy(rows = list)))
        }
    }

    fun addBrsRow(blockId: String, isAdd: Boolean) {
        _uiState.update { state ->
            val current = state.brsBlocks[blockId] ?: return@update state
            val defaultParticulars = if (isAdd) "Add: Cheques deposited but not credited" else "Less: Cheques issued but not presented"
            state.copy(brsBlocks = state.brsBlocks + (blockId to current.copy(rows = current.rows + BrsRow(particulars = defaultParticulars, isAdd = isAdd, amount = ""))))
        }
    }

    fun deleteBrsRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.brsBlocks[blockId] ?: return@update state
            val list = current.rows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(brsBlocks = state.brsBlocks + (blockId to current.copy(rows = list)))
        }
    }

    // Cash Book Block Mutations
    fun updateCashBookDr(blockId: String, index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) {
        _uiState.update { state ->
            val current = state.cashBookBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list[index] = CashBookRow(date = date, particulars = particulars, vn = vn, discount = discount, cash = cash, bank = bank)
            state.copy(cashBookBlocks = state.cashBookBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun updateCashBookCr(blockId: String, index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) {
        _uiState.update { state ->
            val current = state.cashBookBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list[index] = CashBookRow(date = date, particulars = particulars, vn = vn, discount = discount, cash = cash, bank = bank)
            state.copy(cashBookBlocks = state.cashBookBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    fun addCashBookDr(blockId: String) {
        _uiState.update { state ->
            val current = state.cashBookBlocks[blockId] ?: return@update state
            state.copy(cashBookBlocks = state.cashBookBlocks + (blockId to current.copy(drRows = current.drRows + CashBookRow(date = "01/05", particulars = "To Receipts", vn = "", discount = "", cash = "0", bank = "0"))))
        }
    }

    fun addCashBookCr(blockId: String) {
        _uiState.update { state ->
            val current = state.cashBookBlocks[blockId] ?: return@update state
            state.copy(cashBookBlocks = state.cashBookBlocks + (blockId to current.copy(crRows = current.crRows + CashBookRow(date = "01/05", particulars = "By Payments", vn = "", discount = "", cash = "0", bank = "0"))))
        }
    }

    fun deleteCashBookDr(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.cashBookBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(cashBookBlocks = state.cashBookBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun deleteCashBookCr(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.cashBookBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(cashBookBlocks = state.cashBookBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    // Partners Capital Block Mutations
    fun updatePartnersCapitalDr(blockId: String, index: Int, date: String, particulars: String, partnerA: String, partnerB: String) {
        _uiState.update { state ->
            val current = state.partnersCapitalBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list[index] = PartnersCapitalRow(date = date, particulars = particulars, partnerA = partnerA, partnerB = partnerB)
            state.copy(partnersCapitalBlocks = state.partnersCapitalBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun updatePartnersCapitalCr(blockId: String, index: Int, date: String, particulars: String, partnerA: String, partnerB: String) {
        _uiState.update { state ->
            val current = state.partnersCapitalBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list[index] = PartnersCapitalRow(date = date, particulars = particulars, partnerA = partnerA, partnerB = partnerB)
            state.copy(partnersCapitalBlocks = state.partnersCapitalBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    fun addPartnersCapitalDr(blockId: String) {
        _uiState.update { state ->
            val current = state.partnersCapitalBlocks[blockId] ?: return@update state
            state.copy(partnersCapitalBlocks = state.partnersCapitalBlocks + (blockId to current.copy(drRows = current.drRows + PartnersCapitalRow(date = "01/05", particulars = "To Drawings", partnerA = "0", partnerB = "0"))))
        }
    }

    fun addPartnersCapitalCr(blockId: String) {
        _uiState.update { state ->
            val current = state.partnersCapitalBlocks[blockId] ?: return@update state
            state.copy(partnersCapitalBlocks = state.partnersCapitalBlocks + (blockId to current.copy(crRows = current.crRows + PartnersCapitalRow(date = "01/05", particulars = "By Balance b/d", partnerA = "0", partnerB = "0"))))
        }
    }

    fun deletePartnersCapitalDr(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.partnersCapitalBlocks[blockId] ?: return@update state
            val list = current.drRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(partnersCapitalBlocks = state.partnersCapitalBlocks + (blockId to current.copy(drRows = list)))
        }
    }

    fun deletePartnersCapitalCr(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.partnersCapitalBlocks[blockId] ?: return@update state
            val list = current.crRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(partnersCapitalBlocks = state.partnersCapitalBlocks + (blockId to current.copy(crRows = list)))
        }
    }

    // Petty Cash Block Mutations
    fun updatePettyCashRow(blockId: String, index: Int, receipts: String, date: String, particulars: String, totalPayment: String, conveyance: String, cartage: String, stationery: String, misc: String) {
        _uiState.update { state ->
            val current = state.pettyCashBlocks[blockId] ?: return@update state
            val list = current.toMutableList()
            if (index in list.indices) list[index] = PettyCashRow(receipts = receipts, date = date, particulars = particulars, totalPayment = totalPayment, conveyance = conveyance, cartage = cartage, stationery = stationery, misc = misc)
            state.copy(pettyCashBlocks = state.pettyCashBlocks + (blockId to list))
        }
    }

    fun addPettyCashRow(blockId: String) {
        _uiState.update { state ->
            val current = state.pettyCashBlocks[blockId] ?: return@update state
            state.copy(pettyCashBlocks = state.pettyCashBlocks + (blockId to current + PettyCashRow(date = "01/05", particulars = "Sundry Expense", totalPayment = "0", misc = "0")))
        }
    }

    fun deletePettyCashRow(blockId: String, index: Int) {
        _uiState.update { state ->
            val current = state.pettyCashBlocks[blockId] ?: return@update state
            val list = current.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(pettyCashBlocks = state.pettyCashBlocks + (blockId to list))
        }
    }

    // Explicit Block Binding Aliases for WritingAnswerSheetScreen
    fun insertBlock(type: BlockType, customTitle: String? = null, customLeftHeader: String? = null, customRightHeader: String? = null): String = addBlock(type, customTitle, customLeftHeader, customRightHeader)
    fun updateLedgerAccountNameForBlock(blockId: String, name: String) = updateLedgerAccountName(blockId, name)
    fun updateLedgerDrRowForBlock(blockId: String, index: Int, date: String, particulars: String, jf: String, amount: String) = updateLedgerDrRow(blockId, index, date, particulars, jf, amount)
    fun updateLedgerCrRowForBlock(blockId: String, index: Int, date: String, particulars: String, jf: String, amount: String) = updateLedgerCrRow(blockId, index, date, particulars, jf, amount)
    fun addLedgerDrRowForBlock(blockId: String) = addLedgerDrRow(blockId)
    fun addLedgerCrRowForBlock(blockId: String) = addLedgerCrRow(blockId)
    fun deleteLedgerDrRowForBlock(blockId: String, index: Int) = deleteLedgerDrRow(blockId, index)
    fun deleteLedgerCrRowForBlock(blockId: String, index: Int) = deleteLedgerCrRow(blockId, index)
    fun updateJournalRowForBlock(blockId: String, index: Int, date: String, particulars: String, lf: String, debit: String, credit: String, narration: String) = updateJournalRow(blockId, index, date, particulars, lf, debit, credit, narration)
    fun addJournalRowForBlock(blockId: String) = addJournalRow(blockId)
    fun deleteJournalRowForBlock(blockId: String, index: Int) = deleteJournalRow(blockId, index)
    fun updateTrialBalanceRowForBlock(blockId: String, index: Int, sNo: String, headOfAccount: String, lf: String, debit: String, credit: String) = updateTrialBalanceRow(blockId, index, sNo, headOfAccount, lf, debit, credit)
    fun addTrialBalanceRowForBlock(blockId: String) = addTrialBalanceRow(blockId)
    fun deleteTrialBalanceRowForBlock(blockId: String, index: Int) = deleteTrialBalanceRow(blockId, index)
    fun updateBrsStartingBalanceForBlock(blockId: String, balance: String) = updateBrsStartingBalance(blockId, balance)
    fun updateBrsRowForBlock(blockId: String, index: Int, particulars: String, isAdd: Boolean, amount: String) = updateBrsRow(blockId, index, particulars, isAdd, amount)
    fun addBrsRowForBlock(blockId: String, isAdd: Boolean) = addBrsRow(blockId, isAdd)
    fun deleteBrsRowForBlock(blockId: String, index: Int) = deleteBrsRow(blockId, index)
    fun updateCashBookDrRowForBlock(blockId: String, index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) = updateCashBookDr(blockId, index, date, particulars, vn, discount, cash, bank)
    fun updateCashBookCrRowForBlock(blockId: String, index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) = updateCashBookCr(blockId, index, date, particulars, vn, discount, cash, bank)
    fun addCashBookDrRowForBlock(blockId: String) = addCashBookDr(blockId)
    fun addCashBookCrRowForBlock(blockId: String) = addCashBookCr(blockId)
    fun deleteCashBookDrRowForBlock(blockId: String, index: Int) = deleteCashBookDr(blockId, index)
    fun deleteCashBookCrRowForBlock(blockId: String, index: Int) = deleteCashBookCr(blockId, index)
    fun updatePartnersCapitalDrRowForBlock(blockId: String, index: Int, date: String, particulars: String, partnerA: String, partnerB: String) = updatePartnersCapitalDr(blockId, index, date, particulars, partnerA, partnerB)
    fun updatePartnersCapitalCrRowForBlock(blockId: String, index: Int, date: String, particulars: String, partnerA: String, partnerB: String) = updatePartnersCapitalCr(blockId, index, date, particulars, partnerA, partnerB)
    fun addPartnersCapitalDrRowForBlock(blockId: String) = addPartnersCapitalDr(blockId)
    fun addPartnersCapitalCrRowForBlock(blockId: String) = addPartnersCapitalCr(blockId)
    fun deletePartnersCapitalDrRowForBlock(blockId: String, index: Int) = deletePartnersCapitalDr(blockId, index)
    fun deletePartnersCapitalCrRowForBlock(blockId: String, index: Int) = deletePartnersCapitalCr(blockId, index)
    fun updatePettyCashRowForBlock(blockId: String, index: Int, receipts: String, date: String, particulars: String, totalPayment: String, conveyance: String, cartage: String, stationery: String, misc: String) = updatePettyCashRow(blockId, index, receipts, date, particulars, totalPayment, conveyance, cartage, stationery, misc)
    fun addPettyCashRowForBlock(blockId: String) = addPettyCashRow(blockId)
    fun deletePettyCashRowForBlock(blockId: String, index: Int) = deletePettyCashRow(blockId, index)

    // MCQ Selection with Room persistence
    fun onSelectMcqOption(optionIndex: Int) {
        val currentQ = _uiState.value.currentQuestion ?: return
        if (_uiState.value.selectedOptionIndex != null) return

        val isCorrect = optionIndex == currentQ.correctIndex
        if (isCorrect) {
            audioHapticManager.playCorrect()
        } else {
            audioHapticManager.playWrong()
        }

        val studentAns = currentQ.options.getOrNull(optionIndex) ?: ""

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                mcqAttemptedCount = it.mcqAttemptedCount + 1,
                mcqCorrectCount = if (isCorrect) it.mcqCorrectCount + 1 else it.mcqCorrectCount,
                questionsAnsweredCount = it.questionsAnsweredCount + 1,
                questionsAttemptedCount = it.questionsAttemptedCount + 1
            )
        }

        // Persist attempt and update stats in Room (Room is source of truth underneath)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entity = AttemptedQuestionEntity(
                    questionId = currentQ.id,
                    topic = currentQ.topic,
                    questionText = currentQ.questionText,
                    userAnswerText = studentAns,
                    subject = currentQ.subject.name,
                    chapter = currentQ.chapter,
                    mode = currentQ.mode.name,
                    isCorrect = isCorrect,
                    aiMarksAwarded = null,
                    aiFeedback = null,
                    timestamp = System.currentTimeMillis()
                )
                appDao.insertAttempt(entity)

                val subjectKey = currentQ.subject.name
                val existing = appDao.getProgressStats(subjectKey)
                    ?: ProgressStatsEntity(subject = subjectKey)
                val updated = existing.copy(
                    questionsAnsweredCount = existing.questionsAnsweredCount + 1,
                    questionsAttemptedCount = existing.questionsAttemptedCount + 1,
                    mcqAttemptedCount = existing.mcqAttemptedCount + 1,
                    mcqCorrectCount = if (isCorrect) existing.mcqCorrectCount + 1 else existing.mcqCorrectCount
                )
                appDao.insertOrUpdateProgressStats(updated)
            } catch (e: Exception) {
                Log.e("PracticeViewModel", "Error persisting MCQ attempt to Room", e)
            }
        }
    }

    // Subjective Answer Grading via Gemini against Syllabus / Model answer
    fun submitAnswerForGrading() {
        val currentQ = _uiState.value.currentQuestion ?: return
        val studentAnswer = _uiState.value.userAnswerText.trim()
        if (studentAnswer.isBlank()) return

        val isFirstSubmission = !_uiState.value.hasSubmittedCurrentSubjective

        _uiState.update {
            it.copy(
                isGrading = true,
                hasSubmittedCurrentSubjective = true,
                questionsAnsweredCount = if (isFirstSubmission) it.questionsAnsweredCount + 1 else it.questionsAnsweredCount,
                questionsAttemptedCount = if (isFirstSubmission) it.questionsAttemptedCount + 1 else it.questionsAttemptedCount
            )
        }

        viewModelScope.launch {
            val hasOfficialKey = !currentQ.rawSyllabusAnswer.isNullOrBlank()
            val reference = if (hasOfficialKey) currentQ.rawSyllabusAnswer!! else currentQ.modelAnswer

            val result = GeminiQuestionExtractor.gradeSubjectiveAnswer(
                questionText = currentQ.questionText,
                studentAnswer = studentAnswer,
                referenceAnswer = reference,
                isOfficialKey = hasOfficialKey
            )

            _uiState.update {
                it.copy(
                    isGrading = false,
                    gradingResult = result
                )
            }

            if (result != null) {
                audioHapticManager.playCorrect()
                withContext(Dispatchers.IO) {
                    try {
                        val entity = AttemptedQuestionEntity(
                            questionId = currentQ.id,
                            topic = currentQ.topic,
                            questionText = currentQ.questionText,
                            userAnswerText = studentAnswer,
                            subject = currentQ.subject.name,
                            chapter = currentQ.chapter,
                            mode = currentQ.mode.name,
                            isCorrect = null,
                            aiMarksAwarded = result.marksAwarded,
                            aiFeedback = result.feedback,
                            timestamp = System.currentTimeMillis()
                        )
                        appDao.insertAttempt(entity)

                        if (isFirstSubmission) {
                            val subjectKey = currentQ.subject.name
                            val existing = appDao.getProgressStats(subjectKey)
                                ?: ProgressStatsEntity(subject = subjectKey)
                            val updated = existing.copy(
                                questionsAnsweredCount = existing.questionsAnsweredCount + 1,
                                questionsAttemptedCount = existing.questionsAttemptedCount + 1
                            )
                            appDao.insertOrUpdateProgressStats(updated)
                        }
                    } catch (e: Exception) {
                        Log.e("PracticeViewModel", "Error persisting subjective grading to Room", e)
                    }
                }
            }
        }
    }

    // Hint Rewarded Modal
    fun openHintModal() {
        _uiState.update { it.copy(isHintModalVisible = true) }
    }

    fun closeHintModal() {
        _uiState.update { it.copy(isHintModalVisible = false) }
    }

    fun onHintRewardEarned() {
        audioHapticManager.playHintUnlocked()
        _uiState.update { it.copy(isHintUnlocked = true) }
    }

    // Accounting Table Mutations
    fun updateTradingDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.tradingDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(tradingDrRows = list)
        }
    }

    fun updateTradingCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.tradingCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(tradingCrRows = list)
        }
    }

    fun addTradingDrRow() {
        _uiState.update {
            it.copy(tradingDrRows = it.tradingDrRows + AccountRow(particulars = "To Other Expense", amount = ""))
        }
    }

    fun addTradingCrRow() {
        _uiState.update {
            it.copy(tradingCrRows = it.tradingCrRows + AccountRow(particulars = "By Other Income", amount = ""))
        }
    }

    fun deleteTradingDrRow(index: Int) {
        _uiState.update {
            val list = it.tradingDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(tradingDrRows = list)
        }
    }

    fun deleteTradingCrRow(index: Int) {
        _uiState.update {
            val list = it.tradingCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(tradingCrRows = list)
        }
    }

    // Profit & Loss Account Mutations
    fun updatePlDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.plDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(plDrRows = list)
        }
    }

    fun updatePlCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.plCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(plCrRows = list)
        }
    }

    fun addPlDrRow() {
        _uiState.update {
            it.copy(plDrRows = it.plDrRows + AccountRow(particulars = "To Other Expense", amount = ""))
        }
    }

    fun addPlCrRow() {
        _uiState.update {
            it.copy(plCrRows = it.plCrRows + AccountRow(particulars = "By Other Income", amount = ""))
        }
    }

    fun deletePlDrRow(index: Int) {
        _uiState.update {
            val list = it.plDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(plDrRows = list)
        }
    }

    fun deletePlCrRow(index: Int) {
        _uiState.update {
            val list = it.plCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(plCrRows = list)
        }
    }

    // Manufacturing Account Mutations
    fun updateManufacturingDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.manufacturingDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(manufacturingDrRows = list)
        }
    }

    fun updateManufacturingCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.manufacturingCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(manufacturingCrRows = list)
        }
    }

    fun addManufacturingDrRow() {
        _uiState.update {
            it.copy(manufacturingDrRows = it.manufacturingDrRows + AccountRow(particulars = "To Factory Overhead", amount = ""))
        }
    }

    fun addManufacturingCrRow() {
        _uiState.update {
            it.copy(manufacturingCrRows = it.manufacturingCrRows + AccountRow(particulars = "By Other Item", amount = ""))
        }
    }

    fun deleteManufacturingDrRow(index: Int) {
        _uiState.update {
            val list = it.manufacturingDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(manufacturingDrRows = list)
        }
    }

    fun deleteManufacturingCrRow(index: Int) {
        _uiState.update {
            val list = it.manufacturingCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(manufacturingCrRows = list)
        }
    }

    // Revaluation Account Mutations
    fun updateRevaluationDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.revaluationDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(revaluationDrRows = list)
        }
    }

    fun updateRevaluationCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.revaluationCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(revaluationCrRows = list)
        }
    }

    fun addRevaluationDrRow() {
        _uiState.update {
            it.copy(revaluationDrRows = it.revaluationDrRows + AccountRow(particulars = "To Loss on Asset", amount = ""))
        }
    }

    fun addRevaluationCrRow() {
        _uiState.update {
            it.copy(revaluationCrRows = it.revaluationCrRows + AccountRow(particulars = "By Gain on Asset", amount = ""))
        }
    }

    fun deleteRevaluationDrRow(index: Int) {
        _uiState.update {
            val list = it.revaluationDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(revaluationDrRows = list)
        }
    }

    fun deleteRevaluationCrRow(index: Int) {
        _uiState.update {
            val list = it.revaluationCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(revaluationCrRows = list)
        }
    }

    // Realisation Account Mutations
    fun updateRealisationDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.realisationDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(realisationDrRows = list)
        }
    }

    fun updateRealisationCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.realisationCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(realisationCrRows = list)
        }
    }

    fun addRealisationDrRow() {
        _uiState.update {
            it.copy(realisationDrRows = it.realisationDrRows + AccountRow(particulars = "To Payment / Discharge", amount = ""))
        }
    }

    fun addRealisationCrRow() {
        _uiState.update {
            it.copy(realisationCrRows = it.realisationCrRows + AccountRow(particulars = "By Realisation", amount = ""))
        }
    }

    fun deleteRealisationDrRow(index: Int) {
        _uiState.update {
            val list = it.realisationDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(realisationDrRows = list)
        }
    }

    fun deleteRealisationCrRow(index: Int) {
        _uiState.update {
            val list = it.realisationCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(realisationCrRows = list)
        }
    }

    // Consignment Account Mutations
    fun updateConsignmentDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.consignmentDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(consignmentDrRows = list)
        }
    }

    fun updateConsignmentCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.consignmentCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(consignmentCrRows = list)
        }
    }

    fun addConsignmentDrRow() {
        _uiState.update {
            it.copy(consignmentDrRows = it.consignmentDrRows + AccountRow(particulars = "To Consignment Exp", amount = ""))
        }
    }

    fun addConsignmentCrRow() {
        _uiState.update {
            it.copy(consignmentCrRows = it.consignmentCrRows + AccountRow(particulars = "By Consignment Sales", amount = ""))
        }
    }

    fun deleteConsignmentDrRow(index: Int) {
        _uiState.update {
            val list = it.consignmentDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(consignmentDrRows = list)
        }
    }

    fun deleteConsignmentCrRow(index: Int) {
        _uiState.update {
            val list = it.consignmentCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(consignmentCrRows = list)
        }
    }

    fun updateBsLiabilityRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.bsLiabilityRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(bsLiabilityRows = list)
        }
    }

    fun updateBsAssetRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.bsAssetRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(bsAssetRows = list)
        }
    }

    fun addBsLiabilityRow() {
        _uiState.update {
            it.copy(bsLiabilityRows = it.bsLiabilityRows + AccountRow(particulars = "Other Liability", amount = ""))
        }
    }

    fun addBsAssetRow() {
        _uiState.update {
            it.copy(bsAssetRows = it.bsAssetRows + AccountRow(particulars = "Other Asset", amount = ""))
        }
    }

    fun deleteBsLiabilityRow(index: Int) {
        _uiState.update {
            val list = it.bsLiabilityRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(bsLiabilityRows = list)
        }
    }

    fun deleteBsAssetRow(index: Int) {
        _uiState.update {
            val list = it.bsAssetRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(bsAssetRows = list)
        }
    }

    fun updateLedgerAccountName(name: String) {
        _uiState.update { it.copy(ledgerAccountName = name) }
    }

    fun updateLedgerDrRow(index: Int, date: String, particulars: String, jf: String, amount: String) {
        _uiState.update {
            val list = it.ledgerDrRows.toMutableList()
            if (index in list.indices) list[index] = LedgerRow(date = date, particulars = particulars, jf = jf, amount = amount)
            it.copy(ledgerDrRows = list)
        }
    }

    fun updateLedgerCrRow(index: Int, date: String, particulars: String, jf: String, amount: String) {
        _uiState.update {
            val list = it.ledgerCrRows.toMutableList()
            if (index in list.indices) list[index] = LedgerRow(date = date, particulars = particulars, jf = jf, amount = amount)
            it.copy(ledgerCrRows = list)
        }
    }

    fun addLedgerDrRow() {
        _uiState.update {
            it.copy(ledgerDrRows = it.ledgerDrRows + LedgerRow(date = "01/05", particulars = "To Entry", jf = "", amount = ""))
        }
    }

    fun addLedgerCrRow() {
        _uiState.update {
            it.copy(ledgerCrRows = it.ledgerCrRows + LedgerRow(date = "01/05", particulars = "By Entry", jf = "", amount = ""))
        }
    }

    fun deleteLedgerDrRow(index: Int) {
        _uiState.update {
            val list = it.ledgerDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(ledgerDrRows = list)
        }
    }

    fun deleteLedgerCrRow(index: Int) {
        _uiState.update {
            val list = it.ledgerCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(ledgerCrRows = list)
        }
    }

    // Journal Entry Mutations
    fun updateJournalRow(index: Int, date: String, particulars: String, lf: String, debit: String, credit: String, narration: String) {
        _uiState.update {
            val list = it.journalRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(
                    date = date,
                    particulars = particulars,
                    lf = lf,
                    debit = debit,
                    credit = credit,
                    narration = narration
                )
            }
            it.copy(journalRows = list)
        }
    }

    fun addJournalRow() {
        _uiState.update {
            it.copy(journalRows = it.journalRows + JournalRow(date = "15/04", particulars = "Expense A/c ...Dr.", lf = "", debit = "5000", credit = "", narration = "(Being expenses paid)"))
        }
    }

    fun deleteJournalRow(index: Int) {
        _uiState.update {
            val list = it.journalRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(journalRows = list)
        }
    }

    // Trial Balance Mutations
    fun updateTrialBalanceRow(index: Int, sNo: String, head: String, lf: String, debit: String, credit: String) {
        _uiState.update {
            val list = it.trialBalanceRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(sNo = sNo, headOfAccount = head, lf = lf, debit = debit, credit = credit)
            }
            it.copy(trialBalanceRows = list)
        }
    }

    fun addTrialBalanceRow() {
        _uiState.update {
            val nextSno = (it.trialBalanceRows.size + 1).toString()
            it.copy(trialBalanceRows = it.trialBalanceRows + TrialBalanceRow(sNo = nextSno, headOfAccount = "Rent & Rates", lf = "", debit = "12000", credit = ""))
        }
    }

    fun deleteTrialBalanceRow(index: Int) {
        _uiState.update {
            val list = it.trialBalanceRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(trialBalanceRows = list)
        }
    }

    // BRS Mutations
    fun updateBrsStartingBalance(amount: String) {
        _uiState.update { it.copy(brsStartingBalance = amount) }
    }

    fun updateBrsRow(index: Int, particulars: String, isAdd: Boolean, amount: String) {
        _uiState.update {
            val list = it.brsRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(particulars = particulars, isAdd = isAdd, amount = amount)
            }
            it.copy(brsRows = list)
        }
    }

    fun addBrsRow(isAdd: Boolean = true) {
        _uiState.update {
            it.copy(brsRows = it.brsRows + BrsRow(
                particulars = if (isAdd) "Direct deposit into bank by debtor" else "Bank charges & commission debited",
                isAdd = isAdd,
                amount = "2500"
            ))
        }
    }

    fun deleteBrsRow(index: Int) {
        _uiState.update {
            val list = it.brsRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(brsRows = list)
        }
    }

    // Cash Book Mutations
    fun updateCashBookDrRow(index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) {
        _uiState.update {
            val list = it.cashBookDrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, vn = vn, discount = discount, cash = cash, bank = bank)
            }
            it.copy(cashBookDrRows = list)
        }
    }

    fun updateCashBookCrRow(index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) {
        _uiState.update {
            val list = it.cashBookCrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, vn = vn, discount = discount, cash = cash, bank = bank)
            }
            it.copy(cashBookCrRows = list)
        }
    }

    fun addCashBookDrRow() {
        _uiState.update {
            it.copy(cashBookDrRows = it.cashBookDrRows + CashBookRow(date = "20/04", particulars = "To Commission", vn = "", discount = "", cash = "3000", bank = ""))
        }
    }

    fun addCashBookCrRow() {
        _uiState.update {
            it.copy(cashBookCrRows = it.cashBookCrRows + CashBookRow(date = "22/04", particulars = "By Stationery Exp", vn = "", discount = "", cash = "1200", bank = ""))
        }
    }

    fun deleteCashBookDrRow(index: Int) {
        _uiState.update {
            val list = it.cashBookDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(cashBookDrRows = list)
        }
    }

    fun deleteCashBookCrRow(index: Int) {
        _uiState.update {
            val list = it.cashBookCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(cashBookCrRows = list)
        }
    }

    // Partners Capital Mutations
    fun updatePartnersCapitalDrRow(index: Int, date: String, particulars: String, pA: String, pB: String) {
        _uiState.update {
            val list = it.partnersCapitalDrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, partnerA = pA, partnerB = pB)
            }
            it.copy(partnersCapitalDrRows = list)
        }
    }

    fun updatePartnersCapitalCrRow(index: Int, date: String, particulars: String, pA: String, pB: String) {
        _uiState.update {
            val list = it.partnersCapitalCrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, partnerA = pA, partnerB = pB)
            }
            it.copy(partnersCapitalCrRows = list)
        }
    }

    fun addPartnersCapitalDrRow() {
        _uiState.update {
            it.copy(partnersCapitalDrRows = it.partnersCapitalDrRows + PartnersCapitalRow(date = "31/03", particulars = "To Interest on Drawings", partnerA = "1500", partnerB = "1000"))
        }
    }

    fun addPartnersCapitalCrRow() {
        _uiState.update {
            it.copy(partnersCapitalCrRows = it.partnersCapitalCrRows + PartnersCapitalRow(date = "31/03", particulars = "By Salary to Partner", partnerA = "24000", partnerB = ""))
        }
    }

    fun deletePartnersCapitalDrRow(index: Int) {
        _uiState.update {
            val list = it.partnersCapitalDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(partnersCapitalDrRows = list)
        }
    }

    fun deletePartnersCapitalCrRow(index: Int) {
        _uiState.update {
            val list = it.partnersCapitalCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(partnersCapitalCrRows = list)
        }
    }

    // Petty Cash Mutations
    fun updatePettyCashRow(index: Int, receipts: String, date: String, particulars: String, total: String, conveyance: String, cartage: String, stationery: String, misc: String) {
        _uiState.update {
            val list = it.pettyCashRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(receipts = receipts, date = date, particulars = particulars, totalPayment = total, conveyance = conveyance, cartage = cartage, stationery = stationery, misc = misc)
            }
            it.copy(pettyCashRows = list)
        }
    }

    fun addPettyCashRow() {
        _uiState.update {
            it.copy(pettyCashRows = it.pettyCashRows + PettyCashRow(receipts = "", date = "25/04", particulars = "By Office supplies", totalPayment = "", conveyance = "", cartage = "", stationery = "350", misc = ""))
        }
    }

    fun deletePettyCashRow(index: Int) {
        _uiState.update {
            val list = it.pettyCashRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(pettyCashRows = list)
        }
    }

    // Format Search Handlers
    fun onFormatSearchQueryChanged(query: String) {
        _uiState.update { it.copy(formatSearchQuery = query) }
    }

    fun toggleFormatSearch(expanded: Boolean) {
        _uiState.update { it.copy(isFormatSearchExpanded = expanded) }
    }
}
