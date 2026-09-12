package com.example

import com.example.model.AccountingTableType
import com.example.ui.PracticeViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testAllAccountingFormatsExist() {
        val formats = AccountingTableType.ALL_FORMATS
        assertTrue("Should contain at least 10 CA Foundation formats", formats.size >= 10)
        assertTrue(formats.any { it == AccountingTableType.JOURNAL })
        assertTrue(formats.any { it == AccountingTableType.TRIAL_BALANCE })
        assertTrue(formats.any { it == AccountingTableType.BRS })
        assertTrue(formats.any { it == AccountingTableType.CASH_BOOK })
        assertTrue(formats.any { it == AccountingTableType.PARTNERS_CAPITAL })
        assertTrue(formats.any { it == AccountingTableType.PETTY_CASH_BOOK })
        assertTrue(formats.any { it == AccountingTableType.TRADING_ACCOUNT })
        assertTrue(formats.any { it == AccountingTableType.BALANCE_SHEET })
    }

    @Test
    fun testFindByCommandOrQuery() {
        assertEquals(AccountingTableType.JOURNAL, AccountingTableType.findByCommandOrQuery("/Journal"))
        assertEquals(AccountingTableType.BRS, AccountingTableType.findByCommandOrQuery("/BRS"))
        assertEquals(AccountingTableType.CASH_BOOK, AccountingTableType.findByCommandOrQuery("/Cash-Book"))
        assertEquals(AccountingTableType.TRIAL_BALANCE, AccountingTableType.findByCommandOrQuery("/Trial-Balance"))
        assertEquals(AccountingTableType.PARTNERS_CAPITAL, AccountingTableType.findByCommandOrQuery("/Partners-Capital"))
        assertEquals(AccountingTableType.PETTY_CASH_BOOK, AccountingTableType.findByCommandOrQuery("/Petty-Cash-Book"))
        assertEquals(AccountingTableType.TRADING_ACCOUNT, AccountingTableType.findByCommandOrQuery("/Trading-Account"))
        assertEquals(AccountingTableType.BALANCE_SHEET, AccountingTableType.findByCommandOrQuery("/Balance-Sheet"))
        assertEquals(AccountingTableType.LEDGER, AccountingTableType.findByCommandOrQuery("/Ledger"))
        assertEquals(AccountingTableType.PROFIT_LOSS_ACCOUNT, AccountingTableType.findByCommandOrQuery("/Profit-Loss-Account"))
    }

    @Test
    fun testQuerySearching() {
        assertEquals(AccountingTableType.BRS, AccountingTableType.findByCommandOrQuery("bank reconciliation"))
        assertEquals(AccountingTableType.JOURNAL, AccountingTableType.findByCommandOrQuery("journal entry"))
        assertEquals(AccountingTableType.PETTY_CASH_BOOK, AccountingTableType.findByCommandOrQuery("petty cash"))
        assertEquals(AccountingTableType.PARTNERS_CAPITAL, AccountingTableType.findByCommandOrQuery("partners capital"))
    }

    @Test
    fun testWritingViewStateDefaults() {
        val state = com.example.ui.PracticeUiState()
        assertFalse(state.isWritingViewOpen)
        assertFalse(state.isQuestionSheetOpenInWritingView)

        val writingOpenState = state.copy(isWritingViewOpen = true, isQuestionSheetOpenInWritingView = true)
        assertTrue(writingOpenState.isWritingViewOpen)
        assertTrue(writingOpenState.isQuestionSheetOpenInWritingView)
    }

    @Test
    fun testHuggingFacePayloadFormat() {
        val testQuestion = com.example.model.PracticeQuestion(
            id = 9999,
            topic = "Test Topic",
            questionText = "Test question content",
            subject = com.example.model.Subject.LAW,
            depth = com.example.model.Depth.CHAPTER,
            chapter = "Chapter 1 - Introduction",
            mode = com.example.model.QuestionMode.SUBJECTIVE,
            modelAnswer = "Test model answer"
        )

        val jsonObject = org.json.JSONObject().apply {
            put("id", testQuestion.id)
            put("topic", testQuestion.topic)
            put("questionText", testQuestion.questionText)
            put("subject", testQuestion.subject.name)
            put("depth", testQuestion.depth.name)
            put("chapter", testQuestion.chapter)
            put("mode", testQuestion.mode.name)
            put("modelAnswer", testQuestion.modelAnswer)
        }

        val base64Content = java.util.Base64.getEncoder().encodeToString(
            jsonObject.toString(2).toByteArray(Charsets.UTF_8)
        )
        assertFalse(base64Content.contains("\n"))

        val headerLine = org.json.JSONObject().apply {
            put("key", "header")
            put("value", org.json.JSONObject().apply {
                put("summary", "Add question: ${testQuestion.topic.take(60)}")
                put("description", "")
            })
        }

        val fileLine = org.json.JSONObject().apply {
            put("key", "file")
            put("value", org.json.JSONObject().apply {
                put("content", base64Content)
                put("encoding", "base64")
                put("path", "LAW/Chapter 1 - Introduction/Chapter-level/9999.json")
            })
        }

        assertEquals("header", headerLine.getString("key"))
        assertEquals("Add question: Test Topic", headerLine.getJSONObject("value").getString("summary"))
        assertEquals("", headerLine.getJSONObject("value").getString("description"))

        assertEquals("file", fileLine.getString("key"))
        assertEquals("base64", fileLine.getJSONObject("value").getString("encoding"))
        assertEquals("LAW/Chapter 1 - Introduction/Chapter-level/9999.json", fileLine.getJSONObject("value").getString("path"))
        assertEquals(base64Content, fileLine.getJSONObject("value").getString("content"))
    }

    @Test
    fun testTwoSidedAccountFormatsDefaultRowsAreLabelOnlyWithBlankAmounts() {
        val checkRows = { name: String, rows: List<com.example.model.AccountRow> ->
            assertTrue("$name should have default rows", rows.isNotEmpty())
            for (row in rows) {
                assertTrue("$name row '${row.particulars}' should have non-empty label", row.particulars.isNotBlank())
                assertEquals("$name row '${row.particulars}' amount must be blank", "", row.amount)
                assertFalse("$name should not have balancing row '${row.particulars}'",
                    row.particulars.contains("balancing", ignoreCase = true) ||
                    row.particulars.contains("profit c/d", ignoreCase = true) ||
                    row.particulars.contains("loss c/d", ignoreCase = true) ||
                    row.particulars.contains("cost of production", ignoreCase = true) ||
                    row.particulars.contains("transferred to partners", ignoreCase = true) ||
                    row.particulars.contains("transferred to general", ignoreCase = true)
                )
            }
        }

        // Trading
        checkRows("Trading Dr", PracticeViewModel.getDefaultTradingDr())
        checkRows("Trading Cr", PracticeViewModel.getDefaultTradingCr())

        // P&L
        checkRows("P&L Dr", PracticeViewModel.getDefaultPlDr())
        checkRows("P&L Cr", PracticeViewModel.getDefaultPlCr())

        // Manufacturing
        checkRows("Manufacturing Dr", PracticeViewModel.getDefaultManufacturingDr())
        checkRows("Manufacturing Cr", PracticeViewModel.getDefaultManufacturingCr())

        // Revaluation
        checkRows("Revaluation Dr", PracticeViewModel.getDefaultRevaluationDr())
        checkRows("Revaluation Cr", PracticeViewModel.getDefaultRevaluationCr())

        // Realisation
        checkRows("Realisation Dr", PracticeViewModel.getDefaultRealisationDr())
        checkRows("Realisation Cr", PracticeViewModel.getDefaultRealisationCr())

        // Consignment
        checkRows("Consignment Dr", PracticeViewModel.getDefaultConsignmentDr())
        checkRows("Consignment Cr", PracticeViewModel.getDefaultConsignmentCr())
    }

    @Test
    fun testTwoSidedAccountFormatsAreIndependent() {
        var state = com.example.ui.PracticeUiState(
            tradingDrRows = PracticeViewModel.getDefaultTradingDr(),
            tradingCrRows = PracticeViewModel.getDefaultTradingCr(),
            plDrRows = PracticeViewModel.getDefaultPlDr(),
            plCrRows = PracticeViewModel.getDefaultPlCr(),
            manufacturingDrRows = PracticeViewModel.getDefaultManufacturingDr(),
            manufacturingCrRows = PracticeViewModel.getDefaultManufacturingCr(),
            revaluationDrRows = PracticeViewModel.getDefaultRevaluationDr(),
            revaluationCrRows = PracticeViewModel.getDefaultRevaluationCr(),
            realisationDrRows = PracticeViewModel.getDefaultRealisationDr(),
            realisationCrRows = PracticeViewModel.getDefaultRealisationCr(),
            consignmentDrRows = PracticeViewModel.getDefaultConsignmentDr(),
            consignmentCrRows = PracticeViewModel.getDefaultConsignmentCr()
        )
        // Ensure Trading and PL have distinct rows and don't alias
        assertNotEquals(state.tradingDrRows.first().particulars, state.plDrRows.first().particulars)
        assertNotEquals(state.manufacturingDrRows.first().particulars, state.revaluationDrRows.first().particulars)
        assertNotEquals(state.realisationDrRows.first().particulars, state.consignmentDrRows.first().particulars)

        // Modify PL Dr row
        val updatedPlRows = state.plDrRows.mapIndexed { idx, row ->
            if (idx == 0) row.copy(amount = "12500") else row
        }
        state = state.copy(plDrRows = updatedPlRows)

        assertEquals("12500", state.plDrRows[0].amount)
        assertEquals("", state.tradingDrRows[0].amount)
        assertEquals("", state.manufacturingDrRows[0].amount)
        assertEquals("", state.revaluationDrRows[0].amount)
        assertEquals("", state.realisationDrRows[0].amount)
        assertEquals("", state.consignmentDrRows[0].amount)
    }

    @Test
    fun testQuestionsAnsweredAndAttemptedCounts() {
        val state = com.example.ui.PracticeUiState(
            questionsViewedCount = 5,
            questionsAnsweredCount = 2,
            questionsAttemptedCount = 2
        )
        assertEquals(5, state.questionsViewedCount)
        assertEquals(2, state.questionsAnsweredCount)
        assertEquals(2, state.questionsAttemptedCount)
    }

    @Test
    fun testProgressStatsEntitySchema() {
        val entity = com.example.data.local.ProgressStatsEntity(
            subject = "ACC",
            questionsAttemptedCount = 3,
            questionsAnsweredCount = 3,
            questionsViewedCount = 7,
            mcqCorrectCount = 2,
            mcqAttemptedCount = 3
        )
        assertEquals("ACC", entity.subject)
        assertEquals(3, entity.questionsAnsweredCount)
        assertEquals(7, entity.questionsViewedCount)
        assertEquals(2, entity.mcqCorrectCount)
    }

    @Test
    fun testCleanLatexMathNotation() {
        val frac = com.example.ui.components.cleanLatexMathNotation("""\frac{3}{4}""")
        assertEquals("3/4", frac)

        val fracComplex = com.example.ui.components.cleanLatexMathNotation("""\frac{x^2 - 4}{x - 2}""")
        assertEquals("(x^2 - 4)/(x - 2)", fracComplex)

        val timesAndSqrt = com.example.ui.components.cleanLatexMathNotation("""5 \times \sqrt{16}""")
        assertEquals("5 × sqrt(16)", timesAndSqrt)

        val powers = com.example.ui.components.cleanLatexMathNotation("""x^{2} + y_{1}""")
        assertEquals("x^2 + y_1", powers)

        val wrappedInDollar = com.example.ui.components.cleanLatexMathNotation("""${'$'}\frac{1}{2} \times 100${'$'}""")
        assertEquals("1/2 × 100", wrappedInDollar)

        val customLatexCommand = com.example.ui.components.cleanLatexMathNotation("""\mathbf{NPV} = \text{Present Value}""")
        assertEquals("NPV = Present Value", customLatexCommand)
    }

    @Test
    fun testParseMarkdownBlocksWithTable() {
        val markdown = """
            Distinction between Sale and Agreement to Sell:
            | Basis | Sale | Agreement to Sell |
            |:---|:---|:---|
            | Transfer | Passes immediately | Passes at future date |
            | Risk | Buyer | Seller |

            Note: Agreement to sell is executory.
        """.trimIndent()

        val blocks = com.example.ui.components.parseMarkdownBlocks(markdown)
        assertTrue(blocks.size >= 3)
        assertTrue(blocks.any { it is com.example.ui.components.VintageMarkdownBlock.Table })

        val tableBlock = blocks.first { it is com.example.ui.components.VintageMarkdownBlock.Table } as com.example.ui.components.VintageMarkdownBlock.Table
        assertEquals(3, tableBlock.headers.size)
        assertEquals("Basis", tableBlock.headers[0])
        assertEquals("Sale", tableBlock.headers[1])
        assertEquals("Agreement to Sell", tableBlock.headers[2])
        assertEquals(2, tableBlock.rows.size)
        assertEquals("Transfer", tableBlock.rows[0][0])
        assertEquals("Passes immediately", tableBlock.rows[0][1])
    }
}

