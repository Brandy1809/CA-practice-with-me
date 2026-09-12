package com.example.model

enum class Subject(val id: String, val displayName: String, val shortTag: String) {
    ALL("all", "All subjects", "ALL"),
    LAW("law", "Business Law", "LAW"),
    ECO("eco", "Economics", "ECO"),
    ACC("acc", "Accounting", "ACC"),
    QUANT("quant", "Quant Aptitude", "QUANT")
}

enum class Depth(val id: String, val displayName: String) {
    ALL("all", "Mixed"),
    CHAPTER("chapter", "Chapter-level only"),
    UNIT("unit", "Concept-level only")
}

enum class QuestionMode {
    SUBJECTIVE,
    MCQ
}

data class PracticeQuestion(
    val id: Int,
    val topic: String,
    val questionText: String = "",
    val subject: Subject,
    val depth: Depth,
    val chapter: String,
    val mode: QuestionMode,
    val modelAnswer: String = "",
    val mcqQuestion: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String = "",
    val hintFormula: String = "",
    val rawSyllabusAnswer: String? = null
)

enum class AccountingTableType(
    val command: String,
    val title: String,
    val description: String = "",
    val category: String = "Final Accounts"
) {
    NONE("", "None", "", ""),
    TRADING_ACCOUNT("/Trading-Account", "Trading Account for the Year Ended", "Gross profit & cost of goods sold calculation", "Final Accounts"),
    PROFIT_LOSS_ACCOUNT("/Profit-Loss-Account", "Profit & Loss Account for the Year Ended", "Operating/indirect expenses, incomes & Net Profit", "Final Accounts"),
    BALANCE_SHEET("/Balance-Sheet", "Balance Sheet as at Date", "Position Statement: Liabilities, Capital & Assets", "Final Accounts"),
    MANUFACTURING_ACCOUNT("/Manufacturing-Account", "Manufacturing Account for the Year Ended", "Raw materials consumed, factory overheads & cost of production", "Final Accounts"),
    LEDGER("/Ledger", "Ledger Account (T-Format)", "T-shape individual posting with Date & J.F.", "General Accounting"),
    JOURNAL("/Journal", "Journal Entry Register", "Date, Particulars (Dr/Cr), L.F., Debit & Credit amounts", "General Accounting"),
    TRIAL_BALANCE("/Trial-Balance", "Trial Balance as at Date", "Summary of ledger debit/credit balances to test arithmetic accuracy", "General Accounting"),
    CASH_BOOK("/Cash-Book", "Three-Column Cash & Bank Book", "Cash, Bank & Discount columns for receipts & payments", "Special Journals"),
    BRS("/BRS", "Bank Reconciliation Statement", "Reconciliation of Cash Book balance with Bank Passbook balance", "Reconciliation"),
    PARTNERS_CAPITAL("/Partners-Capital", "Partners' Capital Accounts", "Multi-column capital/current accounts of partners", "Partnership"),
    REVALUATION_ACCOUNT("/Revaluation-Account", "Revaluation Account (P&L Adjustment)", "Revaluation of assets & reassessment of liabilities", "Partnership"),
    REALISATION_ACCOUNT("/Realisation-Account", "Realisation Account", "Dissolution of partnership: closing assets & liabilities", "Partnership"),
    CONSIGNMENT_ACCOUNT("/Consignment-Account", "Consignment Account", "Goods sent, freight, consignee expenses, sales & commission", "Special Accounts"),
    PETTY_CASH_BOOK("/Petty-Cash-Book", "Analytical Petty Cash Book", "Imprest system with analytical expense breakdown", "Special Journals"),
    CUSTOM_FORMAT("/Custom", "Custom Format", "Create your own two-sided format", "Custom"),
    WORKING_NOTE("/Working-Notes", "Working Notes", "Detailed calculations, assumptions, notes & statutory disclosures", "Exam Notes");

    val isWorkingNote: Boolean get() = this == WORKING_NOTE

    companion object {
        val ALL_FORMATS: List<AccountingTableType> = values().filter { it != NONE }

        fun findByCommandOrQuery(query: String): AccountingTableType? {
            val clean = query.trim().removePrefix("/").lowercase()
            if (clean.isBlank()) return null
            val normalized = clean.replace(Regex("[^a-z0-9]"), "")
            return ALL_FORMATS.firstOrNull { type ->
                val typeCommandNormalized = type.command.removePrefix("/").lowercase().replace(Regex("[^a-z0-9]"), "")
                val typeTitleNormalized = type.title.lowercase().replace(Regex("[^a-z0-9]"), "")
                val typeNameNormalized = type.name.lowercase().replace(Regex("[^a-z0-9]"), "")

                type.command.removePrefix("/").lowercase() == clean ||
                type.title.lowercase().contains(clean) ||
                type.name.lowercase() == clean ||
                type.description.lowercase().contains(clean) ||
                typeCommandNormalized.contains(normalized) ||
                typeTitleNormalized.contains(normalized) ||
                typeNameNormalized.contains(normalized) ||
                when (normalized) {
                    "pl", "profitloss", "profitandloss" -> type == PROFIT_LOSS_ACCOUNT
                    "trading" -> type == TRADING_ACCOUNT
                    "bs", "balancesheet" -> type == BALANCE_SHEET
                    "manufacturing", "mfg" -> type == MANUFACTURING_ACCOUNT
                    "ledger" -> type == LEDGER
                    "journal", "journalentry", "journalentries" -> type == JOURNAL
                    "trialbalance", "tb" -> type == TRIAL_BALANCE
                    "cashbook", "cash", "bankbook" -> type == CASH_BOOK
                    "brs", "bankreconciliation" -> type == BRS
                    "partners", "partner", "capital", "partnerscapital" -> type == PARTNERS_CAPITAL
                    "revaluation", "reval" -> type == REVALUATION_ACCOUNT
                    "realisation", "dissolution" -> type == REALISATION_ACCOUNT
                    "consignment" -> type == CONSIGNMENT_ACCOUNT
                    "pettycash", "petty" -> type == PETTY_CASH_BOOK
                    "workingnotes", "workingnote", "working", "notes", "note", "wn", "calc", "calculation" -> type == WORKING_NOTE
                    else -> false
                }
            }
        }
    }
}

typealias BlockType = AccountingTableType

data class InsertedBlock(
    val id: String,
    val type: BlockType,
    val isCollapsed: Boolean = false,
    val customTitle: String = "",
    val customLeftHeader: String = "",
    val customRightHeader: String = ""
) {
    val displayTitle: String
        get() = if (customTitle.isNotBlank()) customTitle else type.title
}

data class WorkingNote(
    val id: String,
    val title: String,
    val content: String
)

data class TwoSidedTableData(
    val drRows: List<AccountRow> = emptyList(),
    val crRows: List<AccountRow> = emptyList()
)

data class LedgerTableData(
    val accountName: String = "Cash Account",
    val drRows: List<LedgerRow> = emptyList(),
    val crRows: List<LedgerRow> = emptyList()
)

data class BrsTableData(
    val startingBalance: String = "50000",
    val rows: List<BrsRow> = emptyList()
)

data class CashBookTableData(
    val drRows: List<CashBookRow> = emptyList(),
    val crRows: List<CashBookRow> = emptyList()
)

data class PartnersCapitalTableData(
    val drRows: List<PartnersCapitalRow> = emptyList(),
    val crRows: List<PartnersCapitalRow> = emptyList()
)

data class AccountRow(
    val id: Long = System.nanoTime(),
    val particulars: String,
    val amount: String
)

data class LedgerRow(
    val id: Long = System.nanoTime(),
    val date: String = "",
    val particulars: String = "",
    val jf: String = "",
    val amount: String = ""
)

data class JournalRow(
    val id: Long = System.nanoTime(),
    val date: String = "",
    val particulars: String = "",
    val lf: String = "",
    val debit: String = "",
    val credit: String = "",
    val narration: String = ""
)

data class TrialBalanceRow(
    val id: Long = System.nanoTime(),
    val sNo: String = "",
    val headOfAccount: String = "",
    val lf: String = "",
    val debit: String = "",
    val credit: String = ""
)

data class BrsRow(
    val id: Long = System.nanoTime(),
    val particulars: String = "",
    val isAdd: Boolean = true,
    val amount: String = ""
)

data class CashBookRow(
    val id: Long = System.nanoTime(),
    val date: String = "",
    val particulars: String = "",
    val vn: String = "",
    val discount: String = "",
    val cash: String = "",
    val bank: String = ""
)

data class PartnersCapitalRow(
    val id: Long = System.nanoTime(),
    val date: String = "",
    val particulars: String = "",
    val partnerA: String = "",
    val partnerB: String = ""
)

data class PettyCashRow(
    val id: Long = System.nanoTime(),
    val receipts: String = "",
    val date: String = "",
    val particulars: String = "",
    val totalPayment: String = "",
    val conveyance: String = "",
    val cartage: String = "",
    val stationery: String = "",
    val misc: String = ""
)
