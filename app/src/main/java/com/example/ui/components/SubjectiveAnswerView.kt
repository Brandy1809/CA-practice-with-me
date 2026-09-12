package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountRow
import com.example.model.AccountingTableType
import com.example.model.BlockType
import com.example.model.BrsRow
import com.example.model.BrsTableData
import com.example.model.CashBookRow
import com.example.model.CashBookTableData
import com.example.model.InsertedBlock
import com.example.model.JournalRow
import com.example.model.LedgerRow
import com.example.model.LedgerTableData
import com.example.model.PartnersCapitalRow
import com.example.model.PartnersCapitalTableData
import com.example.model.PettyCashRow
import com.example.model.PracticeQuestion
import com.example.model.Subject
import com.example.model.TrialBalanceRow
import com.example.model.TwoSidedTableData
import com.example.model.WorkingNote
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectiveAnswerView(
    question: PracticeQuestion,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    showModelAnswer: Boolean,
    onToggleModelAnswer: () -> Unit,
    tradingDrRows: List<AccountRow> = emptyList(),
    tradingCrRows: List<AccountRow> = emptyList(),
    onUpdateTradingDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateTradingCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddTradingDrRow: () -> Unit = {},
    onAddTradingCrRow: () -> Unit = {},
    onDeleteTradingDrRow: (Int) -> Unit = {},
    onDeleteTradingCrRow: (Int) -> Unit = {},
    // Profit & Loss
    plDrRows: List<AccountRow> = emptyList(),
    plCrRows: List<AccountRow> = emptyList(),
    onUpdatePlDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdatePlCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddPlDrRow: () -> Unit = {},
    onAddPlCrRow: () -> Unit = {},
    onDeletePlDrRow: (Int) -> Unit = {},
    onDeletePlCrRow: (Int) -> Unit = {},
    // Manufacturing
    manufacturingDrRows: List<AccountRow> = emptyList(),
    manufacturingCrRows: List<AccountRow> = emptyList(),
    onUpdateManufacturingDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateManufacturingCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddManufacturingDrRow: () -> Unit = {},
    onAddManufacturingCrRow: () -> Unit = {},
    onDeleteManufacturingDrRow: (Int) -> Unit = {},
    onDeleteManufacturingCrRow: (Int) -> Unit = {},
    // Revaluation
    revaluationDrRows: List<AccountRow> = emptyList(),
    revaluationCrRows: List<AccountRow> = emptyList(),
    onUpdateRevaluationDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateRevaluationCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddRevaluationDrRow: () -> Unit = {},
    onAddRevaluationCrRow: () -> Unit = {},
    onDeleteRevaluationDrRow: (Int) -> Unit = {},
    onDeleteRevaluationCrRow: (Int) -> Unit = {},
    // Realisation
    realisationDrRows: List<AccountRow> = emptyList(),
    realisationCrRows: List<AccountRow> = emptyList(),
    onUpdateRealisationDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateRealisationCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddRealisationDrRow: () -> Unit = {},
    onAddRealisationCrRow: () -> Unit = {},
    onDeleteRealisationDrRow: (Int) -> Unit = {},
    onDeleteRealisationCrRow: (Int) -> Unit = {},
    // Consignment
    consignmentDrRows: List<AccountRow> = emptyList(),
    consignmentCrRows: List<AccountRow> = emptyList(),
    onUpdateConsignmentDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateConsignmentCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddConsignmentDrRow: () -> Unit = {},
    onAddConsignmentCrRow: () -> Unit = {},
    onDeleteConsignmentDrRow: (Int) -> Unit = {},
    onDeleteConsignmentCrRow: (Int) -> Unit = {},
    bsLiabilityRows: List<AccountRow> = emptyList(),
    bsAssetRows: List<AccountRow> = emptyList(),
    onUpdateBsLiabilityRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateBsAssetRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddBsLiabilityRow: () -> Unit = {},
    onAddBsAssetRow: () -> Unit = {},
    onDeleteBsLiabilityRow: (Int) -> Unit = {},
    onDeleteBsAssetRow: (Int) -> Unit = {},
    ledgerAccountName: String = "Cash Account",
    onUpdateLedgerAccountName: (String) -> Unit = {},
    ledgerDrRows: List<LedgerRow> = emptyList(),
    ledgerCrRows: List<LedgerRow> = emptyList(),
    onUpdateLedgerDrRow: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpdateLedgerCrRow: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onAddLedgerDrRow: () -> Unit = {},
    onAddLedgerCrRow: () -> Unit = {},
    onDeleteLedgerDrRow: (Int) -> Unit = {},
    onDeleteLedgerCrRow: (Int) -> Unit = {},
    // Format Search State
    formatSearchQuery: String = "",
    onFormatSearchQueryChange: (String) -> Unit = {},
    isFormatSearchExpanded: Boolean = false,
    onToggleFormatSearchExpanded: (Boolean) -> Unit = {},
    // All Accounting Format Models
    journalRows: List<JournalRow> = emptyList(),
    onUpdateJournalRow: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddJournalRow: () -> Unit = {},
    onDeleteJournalRow: (Int) -> Unit = {},
    trialBalanceRows: List<TrialBalanceRow> = emptyList(),
    onUpdateTrialBalanceRow: (Int, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onAddTrialBalanceRow: () -> Unit = {},
    onDeleteTrialBalanceRow: (Int) -> Unit = {},
    brsStartingBalance: String = "50000",
    onUpdateBrsStartingBalance: (String) -> Unit = {},
    brsRows: List<BrsRow> = emptyList(),
    onUpdateBrsRow: (Int, String, Boolean, String) -> Unit = { _, _, _, _ -> },
    onAddBrsRow: (Boolean) -> Unit = {},
    onDeleteBrsRow: (Int) -> Unit = {},
    cashBookDrRows: List<CashBookRow> = emptyList(),
    cashBookCrRows: List<CashBookRow> = emptyList(),
    onUpdateCashBookDr: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onUpdateCashBookCr: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddCashBookDr: () -> Unit = {},
    onAddCashBookCr: () -> Unit = {},
    onDeleteCashBookDr: (Int) -> Unit = {},
    onDeleteCashBookCr: (Int) -> Unit = {},
    partnersCapitalDrRows: List<PartnersCapitalRow> = emptyList(),
    partnersCapitalCrRows: List<PartnersCapitalRow> = emptyList(),
    onUpdatePartnersCapitalDr: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpdatePartnersCapitalCr: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onAddPartnersCapitalDr: () -> Unit = {},
    onAddPartnersCapitalCr: () -> Unit = {},
    onDeletePartnersCapitalDr: (Int) -> Unit = {},
    onDeletePartnersCapitalCr: (Int) -> Unit = {},
    pettyCashRows: List<PettyCashRow> = emptyList(),
    onUpdatePettyCashRow: (Int, String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onAddPettyCashRow: () -> Unit = {},
    onDeletePettyCashRow: (Int) -> Unit = {},
    // Multi-instance inserted blocks
    insertedBlocks: List<InsertedBlock> = emptyList(),
    onAddBlock: (AccountingTableType, String?, String?, String?) -> Unit = { _, _, _, _ -> },
    onRemoveBlock: (String) -> Unit = {},
    onClearBlockValues: (String) -> Unit = {},
    onMoveBlockUp: (String) -> Unit = {},
    onMoveBlockDown: (String) -> Unit = {},
    onToggleBlockCollapse: (String) -> Unit = {},
    workingNotes: Map<String, WorkingNote> = emptyMap(),
    onUpdateWorkingNote: (String, String, String) -> Unit = { _, _, _ -> },
    twoSidedBlocks: Map<String, TwoSidedTableData> = emptyMap(),
    onUpdateTwoSidedDrRow: (String, Int, String, String) -> Unit = { _, _, _, _ -> },
    onUpdateTwoSidedCrRow: (String, Int, String, String) -> Unit = { _, _, _, _ -> },
    onAddTwoSidedDrRow: (String) -> Unit = {},
    onAddTwoSidedCrRow: (String) -> Unit = {},
    onDeleteTwoSidedDrRow: (String, Int) -> Unit = { _, _ -> },
    onDeleteTwoSidedCrRow: (String, Int) -> Unit = { _, _ -> },
    ledgerBlocks: Map<String, LedgerTableData> = emptyMap(),
    onUpdateLedgerAccountNameForBlock: (String, String) -> Unit = { _, _ -> },
    onUpdateLedgerDrRowForBlock: (String, Int, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onUpdateLedgerCrRowForBlock: (String, Int, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onAddLedgerDrRowForBlock: (String) -> Unit = {},
    onAddLedgerCrRowForBlock: (String) -> Unit = {},
    onDeleteLedgerDrRowForBlock: (String, Int) -> Unit = { _, _ -> },
    onDeleteLedgerCrRowForBlock: (String, Int) -> Unit = { _, _ -> },
    journalBlocks: Map<String, List<JournalRow>> = emptyMap(),
    onUpdateJournalRowForBlock: (String, Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onAddJournalRowForBlock: (String) -> Unit = {},
    onDeleteJournalRowForBlock: (String, Int) -> Unit = { _, _ -> },
    trialBalanceBlocks: Map<String, List<TrialBalanceRow>> = emptyMap(),
    onUpdateTrialBalanceRowForBlock: (String, Int, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddTrialBalanceRowForBlock: (String) -> Unit = {},
    onDeleteTrialBalanceRowForBlock: (String, Int) -> Unit = { _, _ -> },
    brsBlocks: Map<String, BrsTableData> = emptyMap(),
    onUpdateBrsStartingBalanceForBlock: (String, String) -> Unit = { _, _ -> },
    onUpdateBrsRowForBlock: (String, Int, String, Boolean, String) -> Unit = { _, _, _, _, _ -> },
    onAddBrsRowForBlock: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteBrsRowForBlock: (String, Int) -> Unit = { _, _ -> },
    cashBookBlocks: Map<String, CashBookTableData> = emptyMap(),
    onUpdateCashBookDrForBlock: (String, Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onUpdateCashBookCrForBlock: (String, Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onAddCashBookDrForBlock: (String) -> Unit = {},
    onAddCashBookCrForBlock: (String) -> Unit = {},
    onDeleteCashBookDrForBlock: (String, Int) -> Unit = { _, _ -> },
    onDeleteCashBookCrForBlock: (String, Int) -> Unit = { _, _ -> },
    partnersCapitalBlocks: Map<String, PartnersCapitalTableData> = emptyMap(),
    onUpdatePartnersCapitalDrForBlock: (String, Int, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onUpdatePartnersCapitalCrForBlock: (String, Int, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onAddPartnersCapitalDrForBlock: (String) -> Unit = {},
    onAddPartnersCapitalCrForBlock: (String) -> Unit = {},
    onDeletePartnersCapitalDrForBlock: (String, Int) -> Unit = { _, _ -> },
    onDeletePartnersCapitalCrForBlock: (String, Int) -> Unit = { _, _ -> },
    pettyCashBlocks: Map<String, List<PettyCashRow>> = emptyMap(),
    onUpdatePettyCashRowForBlock: (String, Int, String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _, _, _ -> },
    onAddPettyCashRowForBlock: (String) -> Unit = {},
    onDeletePettyCashRowForBlock: (String, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Module specific helper toolbars
        if (question.subject == Subject.LAW) {
            LawStructuringToolbar(
                currentText = userAnswer,
                onApplyStructure = {
                    val structuredTemplate = buildString {
                        append("I) Provision:\n")
                        append("Under the relevant provisions of the statute...\n\n")
                        append("II) Analysis and Conclusion:\n")
                        append("Applying the statutory provisions to the facts given...\n")
                    }
                    onAnswerChange(structuredTemplate)
                }
            )
        } else if (question.subject == Subject.ACC) {
            AccountingFormatSearchBar(
                searchQuery = formatSearchQuery,
                onSearchQueryChange = onFormatSearchQueryChange,
                isExpanded = isFormatSearchExpanded,
                onToggleExpanded = onToggleFormatSearchExpanded,
                onSelectFormat = { format, customTitle, customLeft, customRight ->
                    onAddBlock(format, customTitle, customLeft, customRight)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        var heightOverridePx by remember { mutableStateOf<Float?>(null) }
        var currentHeightPx by remember { mutableStateOf(0f) }
        val density = LocalDensity.current
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        val maxHeightDp = screenHeight * 0.9f

        // Ruled Notebook Answer Script Area
        val baseModifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VintageLine, RoundedCornerShape(4.dp))
            .onSizeChanged { currentHeightPx = it.height.toFloat() }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    val minHeight = with(density) { 80.dp.toPx() }
                    val maxHeight = with(density) { maxHeightDp.toPx() }
                    val currentH = heightOverridePx ?: currentHeightPx
                    heightOverridePx = (currentH * zoom).coerceIn(minHeight, maxHeight)
                }
            }
            
        val finalModifier = if (heightOverridePx != null) {
            baseModifier.height(with(density) { heightOverridePx!!.toDp() })
        } else {
            baseModifier.weight(1f)
        }

        Surface(
            modifier = finalModifier,
            shape = RoundedCornerShape(4.dp),
            color = VintagePaperBg
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Notebook ruling lines (subtle horizontal ruled lines every 28dp)
                        val lineSpacing = 28.dp.toPx()
                        var y = lineSpacing
                        while (y < size.height) {
                            drawLine(
                                color = VintageLine.copy(alpha = 0.45f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                            y += lineSpacing
                        }
                    }
                    .padding(12.dp)
            ) {
                if (userAnswer.isEmpty()) {
                    Text(
                        text = if (question.subject == Subject.ACC) {
                            "Type your answer here or enter /Trading-Account, /Working-Notes, /Ledger to insert formats..."
                        } else if (question.subject == Subject.LAW) {
                            "Draft your answer following ICAI standard: I) Provision, II) Analysis & Conclusion..."
                        } else {
                            "Type your answer here in your own words..."
                        },
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = VintageInkSoft.copy(alpha = 0.7f),
                            lineHeight = 28.sp
                        )
                    )
                }

                BasicTextField(
                    value = userAnswer,
                    onValueChange = { newText ->
                        onAnswerChange(newText)
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        color = VintageInk,
                        lineHeight = 28.sp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .testTag("subjective_answer_input")
                )
            }
        }
        
        // Drag Handle Grip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        val minHeight = with(density) { 80.dp.toPx() }
                        val maxHeight = with(density) { maxHeightDp.toPx() }
                        val currentH = heightOverridePx ?: currentHeightPx
                        heightOverridePx = (currentH + dragAmount).coerceIn(minHeight, maxHeight)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⋰",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = VintageInkSoft.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Render Inserted Blocks (Multiple Independent Blocks: Tables & Working Notes)
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {

        if (insertedBlocks.isNotEmpty()) {
            insertedBlocks.forEachIndexed { index, block ->
                InsertedBlockCard(
                    block = block,
                    index = index,
                    onToggleCollapse = { onToggleBlockCollapse(block.id) },
                    onRemoveBlock = { onRemoveBlock(block.id) },
                    onClearBlockValues = { onClearBlockValues(block.id) },
                    onMoveUp = { onMoveBlockUp(block.id) },
                    onMoveDown = { onMoveBlockDown(block.id) },
                    workingNote = workingNotes[block.id],
                    onUpdateWorkingNote = { title, content -> onUpdateWorkingNote(block.id, title, content) },
                    twoSidedData = twoSidedBlocks[block.id],
                    onUpdateTwoSidedDrRow = { rIdx, part, amt -> onUpdateTwoSidedDrRow(block.id, rIdx, part, amt) },
                    onUpdateTwoSidedCrRow = { rIdx, part, amt -> onUpdateTwoSidedCrRow(block.id, rIdx, part, amt) },
                    onAddTwoSidedDrRow = { onAddTwoSidedDrRow(block.id) },
                    onAddTwoSidedCrRow = { onAddTwoSidedCrRow(block.id) },
                    onDeleteTwoSidedDrRow = { rIdx -> onDeleteTwoSidedDrRow(block.id, rIdx) },
                    onDeleteTwoSidedCrRow = { rIdx -> onDeleteTwoSidedCrRow(block.id, rIdx) },
                    ledgerData = ledgerBlocks[block.id],
                    onUpdateLedgerAccountName = { name -> onUpdateLedgerAccountNameForBlock(block.id, name) },
                    onUpdateLedgerDrRow = { rIdx, d, p, j, a -> onUpdateLedgerDrRowForBlock(block.id, rIdx, d, p, j, a) },
                    onUpdateLedgerCrRow = { rIdx, d, p, j, a -> onUpdateLedgerCrRowForBlock(block.id, rIdx, d, p, j, a) },
                    onAddLedgerDrRow = { onAddLedgerDrRowForBlock(block.id) },
                    onAddLedgerCrRow = { onAddLedgerCrRowForBlock(block.id) },
                    onDeleteLedgerDrRow = { rIdx -> onDeleteLedgerDrRowForBlock(block.id, rIdx) },
                    onDeleteLedgerCrRow = { rIdx -> onDeleteLedgerCrRowForBlock(block.id, rIdx) },
                    journalRows = journalBlocks[block.id] ?: emptyList(),
                    onUpdateJournalRow = { rIdx, d, p, l, dr, cr, n -> onUpdateJournalRowForBlock(block.id, rIdx, d, p, l, dr, cr, n) },
                    onAddJournalRow = { onAddJournalRowForBlock(block.id) },
                    onDeleteJournalRow = { rIdx -> onDeleteJournalRowForBlock(block.id, rIdx) },
                    trialBalanceRows = trialBalanceBlocks[block.id] ?: emptyList(),
                    onUpdateTrialBalanceRow = { rIdx, s, h, l, dr, cr -> onUpdateTrialBalanceRowForBlock(block.id, rIdx, s, h, l, dr, cr) },
                    onAddTrialBalanceRow = { onAddTrialBalanceRowForBlock(block.id) },
                    onDeleteTrialBalanceRow = { rIdx -> onDeleteTrialBalanceRowForBlock(block.id, rIdx) },
                    brsData = brsBlocks[block.id],
                    onUpdateBrsStartingBalance = { bal -> onUpdateBrsStartingBalanceForBlock(block.id, bal) },
                    onUpdateBrsRow = { rIdx, p, isPlus, a -> onUpdateBrsRowForBlock(block.id, rIdx, p, isPlus, a) },
                    onAddBrsRow = { isPlus -> onAddBrsRowForBlock(block.id, isPlus) },
                    onDeleteBrsRow = { rIdx -> onDeleteBrsRowForBlock(block.id, rIdx) },
                    cashBookData = cashBookBlocks[block.id],
                    onUpdateCashBookDr = { rIdx, d, p, v, l, c, b -> onUpdateCashBookDrForBlock(block.id, rIdx, d, p, v, l, c, b) },
                    onUpdateCashBookCr = { rIdx, d, p, v, l, c, b -> onUpdateCashBookCrForBlock(block.id, rIdx, d, p, v, l, c, b) },
                    onAddCashBookDr = { onAddCashBookDrForBlock(block.id) },
                    onAddCashBookCr = { onAddCashBookCrForBlock(block.id) },
                    onDeleteCashBookDr = { rIdx -> onDeleteCashBookDrForBlock(block.id, rIdx) },
                    onDeleteCashBookCr = { rIdx -> onDeleteCashBookCrForBlock(block.id, rIdx) },
                    partnersCapitalData = partnersCapitalBlocks[block.id],
                    onUpdatePartnersCapitalDr = { rIdx, d, p, a, b -> onUpdatePartnersCapitalDrForBlock(block.id, rIdx, d, p, a, b) },
                    onUpdatePartnersCapitalCr = { rIdx, d, p, a, b -> onUpdatePartnersCapitalCrForBlock(block.id, rIdx, d, p, a, b) },
                    onAddPartnersCapitalDr = { onAddPartnersCapitalDrForBlock(block.id) },
                    onAddPartnersCapitalCr = { onAddPartnersCapitalCrForBlock(block.id) },
                    onDeletePartnersCapitalDr = { rIdx -> onDeletePartnersCapitalDrForBlock(block.id, rIdx) },
                    onDeletePartnersCapitalCr = { rIdx -> onDeletePartnersCapitalCrForBlock(block.id, rIdx) },
                    pettyCashRows = pettyCashBlocks[block.id] ?: emptyList(),
                    onUpdatePettyCashRow = { rIdx, a, d, p, v, t, po, te, m -> onUpdatePettyCashRowForBlock(block.id, rIdx, a, d, p, v, t, po, te, m) },
                    onAddPettyCashRow = { onAddPettyCashRowForBlock(block.id) },
                    onDeletePettyCashRow = { rIdx -> onDeletePettyCashRowForBlock(block.id, rIdx) }
                )
            }
        }



        // Model Answer Box
        AnimatedVisibility(
            visible = showModelAnswer,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("icai_model_answer_box"),
                shape = RoundedCornerShape(4.dp),
                color = VintageGreenBg,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, VintageGreen)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ICAI-STYLE MODEL ANSWER",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = VintageGreen,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = VintageGreen
                        ) {
                            Text(
                                text = "OFFICIAL SYLLABUS",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    VintageMarkdownText(
                        text = question.modelAnswer,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp,
                            color = VintageInk
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        // End of the inner scrollable Column
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LawStructuringToolbar(
    currentText: String,
    onApplyStructure: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VintagePaperBg.copy(alpha = 0.6f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = null,
                    tint = VintageNavy,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ICAI Law Structure:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = VintageNavy
                )
            }

            AssistChip(
                onClick = onApplyStructure,
                label = { Text("Pre-fill Headings (I & II)", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = VintageNavy
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = VintageNavy
                ),
                border = AssistChipDefaults.assistChipBorder(true, borderColor = VintageLine),
                modifier = Modifier.testTag("prefill_law_structure_btn")
            )
        }
    }
}
