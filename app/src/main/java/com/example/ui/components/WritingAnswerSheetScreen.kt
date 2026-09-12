package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.model.QuestionMode
import com.example.model.Subject
import com.example.model.TrialBalanceRow
import com.example.model.TwoSidedTableData
import com.example.model.WorkingNote
import com.example.network.GeminiQuestionExtractor
import com.example.ui.theme.VintageGold
import com.example.ui.theme.VintageGoldBg
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintageNavyDeep
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet
import com.example.ui.theme.VintageRed
import com.example.ui.theme.VintageRedBg

/**
 * Dedicated Full-Screen Examination Answer Writing Sheet.
 * Opened when user taps "Write Answer" from the question card.
 * Provides a dedicated ruled notebook view with a small button to view/read the question at any time.
 */
@Composable
fun WritingAnswerSheetScreen(
    question: PracticeQuestion,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    showModelAnswer: Boolean,
    onToggleModelAnswer: () -> Unit,
    isQuestionSheetOpen: Boolean,
    onToggleQuestionSheet: (Boolean) -> Unit,
    onCloseWritingView: () -> Unit,
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
    formatSearchQuery: String = "",
    onFormatSearchQueryChange: (String) -> Unit = {},
    isFormatSearchExpanded: Boolean = false,
    onToggleFormatSearchExpanded: (Boolean) -> Unit = {},
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
    
    onDownloadJson: () -> String = { "{}" },

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
    isGrading: Boolean = false,
    gradingResult: GeminiQuestionExtractor.GradingResult? = null,
    onSubmitForGrading: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        onCloseWritingView()
    }

    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val jsonContent = remember { mutableStateOf("") }
    
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri: Uri? ->
            if (uri != null) {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(jsonContent.value.toByteArray())
                    }
                    Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error saving file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    // Smooth scroll to newly added block
    LaunchedEffect(insertedBlocks.size) {
        if (insertedBlocks.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VintagePaperBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    // Vintage paper horizontal ruled lines effect in background
                    val step = 34.dp.toPx()
                    var y = step
                    while (y < size.height) {
                        drawLine(
                            color = VintageLine.copy(alpha = 0.35f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Top Header Bar: Back Button, Title, and Small "View Question" button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("writing_view_top_bar"),
                    shape = RoundedCornerShape(4.dp),
                    color = VintagePaperSheet,
                    border = BorderStroke(1.dp, VintageLine),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = onCloseWritingView,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("back_to_question_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Question",
                                    tint = VintageNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Column {
                                Text(
                                    text = "EXAM ANSWER SCRIPT",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VintageNavy,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "${question.subject.name} • ${question.chapter}",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 11.sp,
                                        color = VintageInkSoft
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        // Compare with ICAI Model Answer
                        IconButton(
                            onClick = onToggleModelAnswer,
                            modifier = Modifier.size(32.dp).testTag("toggle_icai_answer_btn")
                        ) {
                            Icon(
                                imageVector = if (showModelAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Compare with ICAI Model Answer",
                                tint = if (showModelAnswer) VintageNavy else VintageInkSoft,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Download JSON Button
                        IconButton(
                            onClick = {
                                val jsonStr = onDownloadJson()
                                jsonContent.value = jsonStr
                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val subject = question.subject.name.lowercase()
                                val chapter = question.chapter.lowercase().replace(" ", "_")
                                val filename = "ca_answer_${subject}_${chapter}_$timeStamp.json"
                                createDocumentLauncher.launch(filename)
                            },
                            modifier = Modifier.size(32.dp).testTag("download_json_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download as JSON",
                                tint = VintageNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Small Button to Read Question
                        IconButton(
                            onClick = { onToggleQuestionSheet(true) },
                            modifier = Modifier.size(32.dp).testTag("small_view_question_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "View Question",
                                tint = VintageNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Writing Notebook Paper Sheet
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("writing_notebook_sheet"),
                    shape = RoundedCornerShape(4.dp),
                    color = VintagePaperSheet,
                    border = BorderStroke(1.2.dp, VintageLine),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        SubjectiveAnswerView(
                            question = question,
                            userAnswer = userAnswer,
                            onAnswerChange = onAnswerChange,
                            showModelAnswer = showModelAnswer,
                            onToggleModelAnswer = onToggleModelAnswer,
                            tradingDrRows = tradingDrRows,
                            tradingCrRows = tradingCrRows,
                            onUpdateTradingDrRow = onUpdateTradingDrRow,
                            onUpdateTradingCrRow = onUpdateTradingCrRow,
                            onAddTradingDrRow = onAddTradingDrRow,
                            onAddTradingCrRow = onAddTradingCrRow,
                            onDeleteTradingDrRow = onDeleteTradingDrRow,
                            onDeleteTradingCrRow = onDeleteTradingCrRow,
                            plDrRows = plDrRows,
                            plCrRows = plCrRows,
                            onUpdatePlDrRow = onUpdatePlDrRow,
                            onUpdatePlCrRow = onUpdatePlCrRow,
                            onAddPlDrRow = onAddPlDrRow,
                            onAddPlCrRow = onAddPlCrRow,
                            onDeletePlDrRow = onDeletePlDrRow,
                            onDeletePlCrRow = onDeletePlCrRow,
                            manufacturingDrRows = manufacturingDrRows,
                            manufacturingCrRows = manufacturingCrRows,
                            onUpdateManufacturingDrRow = onUpdateManufacturingDrRow,
                            onUpdateManufacturingCrRow = onUpdateManufacturingCrRow,
                            onAddManufacturingDrRow = onAddManufacturingDrRow,
                            onAddManufacturingCrRow = onAddManufacturingCrRow,
                            onDeleteManufacturingDrRow = onDeleteManufacturingDrRow,
                            onDeleteManufacturingCrRow = onDeleteManufacturingCrRow,
                            revaluationDrRows = revaluationDrRows,
                            revaluationCrRows = revaluationCrRows,
                            onUpdateRevaluationDrRow = onUpdateRevaluationDrRow,
                            onUpdateRevaluationCrRow = onUpdateRevaluationCrRow,
                            onAddRevaluationDrRow = onAddRevaluationDrRow,
                            onAddRevaluationCrRow = onAddRevaluationCrRow,
                            onDeleteRevaluationDrRow = onDeleteRevaluationDrRow,
                            onDeleteRevaluationCrRow = onDeleteRevaluationCrRow,
                            realisationDrRows = realisationDrRows,
                            realisationCrRows = realisationCrRows,
                            onUpdateRealisationDrRow = onUpdateRealisationDrRow,
                            onUpdateRealisationCrRow = onUpdateRealisationCrRow,
                            onAddRealisationDrRow = onAddRealisationDrRow,
                            onAddRealisationCrRow = onAddRealisationCrRow,
                            onDeleteRealisationDrRow = onDeleteRealisationDrRow,
                            onDeleteRealisationCrRow = onDeleteRealisationCrRow,
                            consignmentDrRows = consignmentDrRows,
                            consignmentCrRows = consignmentCrRows,
                            onUpdateConsignmentDrRow = onUpdateConsignmentDrRow,
                            onUpdateConsignmentCrRow = onUpdateConsignmentCrRow,
                            onAddConsignmentDrRow = onAddConsignmentDrRow,
                            onAddConsignmentCrRow = onAddConsignmentCrRow,
                            onDeleteConsignmentDrRow = onDeleteConsignmentDrRow,
                            onDeleteConsignmentCrRow = onDeleteConsignmentCrRow,
                            bsLiabilityRows = bsLiabilityRows,
                            bsAssetRows = bsAssetRows,
                            onUpdateBsLiabilityRow = onUpdateBsLiabilityRow,
                            onUpdateBsAssetRow = onUpdateBsAssetRow,
                            onAddBsLiabilityRow = onAddBsLiabilityRow,
                            onAddBsAssetRow = onAddBsAssetRow,
                            onDeleteBsLiabilityRow = onDeleteBsLiabilityRow,
                            onDeleteBsAssetRow = onDeleteBsAssetRow,
                            ledgerAccountName = ledgerAccountName,
                            onUpdateLedgerAccountName = onUpdateLedgerAccountName,
                            ledgerDrRows = ledgerDrRows,
                            ledgerCrRows = ledgerCrRows,
                            onUpdateLedgerDrRow = onUpdateLedgerDrRow,
                            onUpdateLedgerCrRow = onUpdateLedgerCrRow,
                            onAddLedgerDrRow = onAddLedgerDrRow,
                            onAddLedgerCrRow = onAddLedgerCrRow,
                            onDeleteLedgerDrRow = onDeleteLedgerDrRow,
                            onDeleteLedgerCrRow = onDeleteLedgerCrRow,
                            formatSearchQuery = formatSearchQuery,
                            onFormatSearchQueryChange = onFormatSearchQueryChange,
                            isFormatSearchExpanded = isFormatSearchExpanded,
                            onToggleFormatSearchExpanded = onToggleFormatSearchExpanded,
                            journalRows = journalRows,
                            onUpdateJournalRow = onUpdateJournalRow,
                            onAddJournalRow = onAddJournalRow,
                            onDeleteJournalRow = onDeleteJournalRow,
                            trialBalanceRows = trialBalanceRows,
                            onUpdateTrialBalanceRow = onUpdateTrialBalanceRow,
                            onAddTrialBalanceRow = onAddTrialBalanceRow,
                            onDeleteTrialBalanceRow = onDeleteTrialBalanceRow,
                            brsStartingBalance = brsStartingBalance,
                            onUpdateBrsStartingBalance = onUpdateBrsStartingBalance,
                            brsRows = brsRows,
                            onUpdateBrsRow = onUpdateBrsRow,
                            onAddBrsRow = onAddBrsRow,
                            onDeleteBrsRow = onDeleteBrsRow,
                            cashBookDrRows = cashBookDrRows,
                            cashBookCrRows = cashBookCrRows,
                            onUpdateCashBookDr = onUpdateCashBookDr,
                            onUpdateCashBookCr = onUpdateCashBookCr,
                            onAddCashBookDr = onAddCashBookDr,
                            onAddCashBookCr = onAddCashBookCr,
                            onDeleteCashBookDr = onDeleteCashBookDr,
                            onDeleteCashBookCr = onDeleteCashBookCr,
                            partnersCapitalDrRows = partnersCapitalDrRows,
                            partnersCapitalCrRows = partnersCapitalCrRows,
                            onUpdatePartnersCapitalDr = onUpdatePartnersCapitalDr,
                            onUpdatePartnersCapitalCr = onUpdatePartnersCapitalCr,
                            onAddPartnersCapitalDr = onAddPartnersCapitalDr,
                            onAddPartnersCapitalCr = onAddPartnersCapitalCr,
                            onDeletePartnersCapitalDr = onDeletePartnersCapitalDr,
                            onDeletePartnersCapitalCr = onDeletePartnersCapitalCr,
                            pettyCashRows = pettyCashRows,
                            onUpdatePettyCashRow = onUpdatePettyCashRow,
                            onAddPettyCashRow = onAddPettyCashRow,
                            onDeletePettyCashRow = onDeletePettyCashRow,
                            insertedBlocks = insertedBlocks,
                            onAddBlock = onAddBlock,
                            onRemoveBlock = onRemoveBlock,
                            onClearBlockValues = onClearBlockValues,
                            onMoveBlockUp = onMoveBlockUp,
                            onMoveBlockDown = onMoveBlockDown,
                            onToggleBlockCollapse = onToggleBlockCollapse,
                            workingNotes = workingNotes,
                            onUpdateWorkingNote = onUpdateWorkingNote,
                            twoSidedBlocks = twoSidedBlocks,
                            onUpdateTwoSidedDrRow = onUpdateTwoSidedDrRow,
                            onUpdateTwoSidedCrRow = onUpdateTwoSidedCrRow,
                            onAddTwoSidedDrRow = onAddTwoSidedDrRow,
                            onAddTwoSidedCrRow = onAddTwoSidedCrRow,
                            onDeleteTwoSidedDrRow = onDeleteTwoSidedDrRow,
                            onDeleteTwoSidedCrRow = onDeleteTwoSidedCrRow,
                            ledgerBlocks = ledgerBlocks,
                            onUpdateLedgerAccountNameForBlock = onUpdateLedgerAccountNameForBlock,
                            onUpdateLedgerDrRowForBlock = onUpdateLedgerDrRowForBlock,
                            onUpdateLedgerCrRowForBlock = onUpdateLedgerCrRowForBlock,
                            onAddLedgerDrRowForBlock = onAddLedgerDrRowForBlock,
                            onAddLedgerCrRowForBlock = onAddLedgerCrRowForBlock,
                            onDeleteLedgerDrRowForBlock = onDeleteLedgerDrRowForBlock,
                            onDeleteLedgerCrRowForBlock = onDeleteLedgerCrRowForBlock,
                            journalBlocks = journalBlocks,
                            onUpdateJournalRowForBlock = onUpdateJournalRowForBlock,
                            onAddJournalRowForBlock = onAddJournalRowForBlock,
                            onDeleteJournalRowForBlock = onDeleteJournalRowForBlock,
                            trialBalanceBlocks = trialBalanceBlocks,
                            onUpdateTrialBalanceRowForBlock = onUpdateTrialBalanceRowForBlock,
                            onAddTrialBalanceRowForBlock = onAddTrialBalanceRowForBlock,
                            onDeleteTrialBalanceRowForBlock = onDeleteTrialBalanceRowForBlock,
                            brsBlocks = brsBlocks,
                            onUpdateBrsStartingBalanceForBlock = onUpdateBrsStartingBalanceForBlock,
                            onUpdateBrsRowForBlock = onUpdateBrsRowForBlock,
                            onAddBrsRowForBlock = onAddBrsRowForBlock,
                            onDeleteBrsRowForBlock = onDeleteBrsRowForBlock,
                            cashBookBlocks = cashBookBlocks,
                            onUpdateCashBookDrForBlock = onUpdateCashBookDrForBlock,
                            onUpdateCashBookCrForBlock = onUpdateCashBookCrForBlock,
                            onAddCashBookDrForBlock = onAddCashBookDrForBlock,
                            onAddCashBookCrForBlock = onAddCashBookCrForBlock,
                            onDeleteCashBookDrForBlock = onDeleteCashBookDrForBlock,
                            onDeleteCashBookCrForBlock = onDeleteCashBookCrForBlock,
                            partnersCapitalBlocks = partnersCapitalBlocks,
                            onUpdatePartnersCapitalDrForBlock = onUpdatePartnersCapitalDrForBlock,
                            onUpdatePartnersCapitalCrForBlock = onUpdatePartnersCapitalCrForBlock,
                            onAddPartnersCapitalDrForBlock = onAddPartnersCapitalDrForBlock,
                            onAddPartnersCapitalCrForBlock = onAddPartnersCapitalCrForBlock,
                            onDeletePartnersCapitalDrForBlock = onDeletePartnersCapitalDrForBlock,
                            onDeletePartnersCapitalCrForBlock = onDeletePartnersCapitalCrForBlock,
                            pettyCashBlocks = pettyCashBlocks,
                            onUpdatePettyCashRowForBlock = onUpdatePettyCashRowForBlock,
                            onAddPettyCashRowForBlock = onAddPettyCashRowForBlock,
                            onDeletePettyCashRowForBlock = onDeletePettyCashRowForBlock
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Submit for Grading Section (AI Examiner evaluation against syllabus answer)
                        Button(
                            onClick = onSubmitForGrading,
                            enabled = !isGrading && userAnswer.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_for_grading_btn"),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VintageNavy,
                                contentColor = Color.White
                            )
                        ) {
                            if (isGrading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Evaluating against ICAI Syllabus...",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (gradingResult != null) "Re-Submit for Grading (AI Examiner)" else "Submit for Grading (AI Examiner)",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        if (gradingResult != null) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("grading_result_card"),
                                shape = RoundedCornerShape(4.dp),
                                color = VintageGoldBg,
                                border = BorderStroke(1.2.dp, VintageGold),
                                shadowElevation = 2.dp
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "MARKS: ${gradingResult.marksAwarded} / ${gradingResult.totalMarks}",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (gradingResult.marksAwarded >= 5) VintageGreen else VintageNavy
                                            )
                                        )

                                        Surface(
                                            color = if (gradingResult.isOfficialKey) VintageGreenBg else VintagePaperBg,
                                            shape = RoundedCornerShape(3.dp),
                                            border = BorderStroke(1.dp, if (gradingResult.isOfficialKey) VintageGreen else VintageLine)
                                        ) {
                                            Text(
                                                text = if (gradingResult.isOfficialKey) "Official Syllabus Key" else "AI-estimated (no official answer key available)",
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (gradingResult.isOfficialKey) VintageGreen else VintageInkSoft
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "EXAMINER FEEDBACK:",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = VintageInk
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    VintageMarkdownText(
                                        text = gradingResult.feedback,
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 13.sp,
                                            color = VintageInk,
                                            lineHeight = 19.sp
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Return to Question button at bottom of sheet
                        OutlinedButton(
                            onClick = onCloseWritingView,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("finish_writing_btn"),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, VintageNavy),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save & Return to Question",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Question Reference Dialog ("usko click karte hi question dikhe padh sakhu question close kar ke likh sakhu")
    if (isQuestionSheetOpen) {
        Dialog(
            onDismissRequest = { onToggleQuestionSheet(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 600.dp)
                    .heightIn(max = 560.dp)
                    .testTag("question_reference_dialog"),
                shape = RoundedCornerShape(8.dp),
                color = VintagePaperSheet,
                border = BorderStroke(1.5.dp, VintageNavy),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Header with Subject, Chapter, and Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = VintageNavy
                            ) {
                                Text(
                                    text = question.subject.name,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = question.chapter,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.5.sp,
                                    color = VintageInkSoft,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                        }

                        IconButton(
                            onClick = { onToggleQuestionSheet(false) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("close_question_reference_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Question",
                                tint = VintageInk,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "EXAMINATION QUESTION",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = VintageGold,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Question Content Box with scroll
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(BorderStroke(1.dp, VintageLine), RoundedCornerShape(4.dp))
                            .background(VintagePaperBg.copy(alpha = 0.5f))
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        VintageMarkdownText(
                            text = if (question.questionText.isNotBlank()) {
                                question.questionText
                            } else {
                                question.topic
                            },
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.5.sp,
                                lineHeight = 25.sp,
                                color = VintageInk,
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prominent Close Button to return back to writing immediately
                    Button(
                        onClick = { onToggleQuestionSheet(false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("resume_writing_btn"),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VintageNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Close & Resume Writing",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
