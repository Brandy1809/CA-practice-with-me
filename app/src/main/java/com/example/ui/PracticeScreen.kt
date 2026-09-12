package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AccountingTableType
import com.example.model.QuestionMode
import com.example.model.Subject
import com.example.ui.components.ExamActionControls
import com.example.ui.components.ExamPaperHeader
import com.example.ui.components.FilterBar
import com.example.ui.components.HintRewardedModal
import com.example.ui.components.ObjectiveMcqView
import com.example.ui.components.VintageMarkdownText
import com.example.ui.components.WritingAnswerSheetScreen
import com.example.ui.theme.VintageGold
import com.example.ui.theme.VintageGoldBg
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet

@Composable
fun PracticeScreen(
    viewModel: PracticeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Dedicated Full-Screen Exam Writing Script View
    if (uiState.isWritingViewOpen && uiState.currentQuestion != null) {
        WritingAnswerSheetScreen(
            question = uiState.currentQuestion!!,
            userAnswer = uiState.userAnswerText,
            onAnswerChange = viewModel::onAnswerTextChanged,
            showModelAnswer = uiState.showModelAnswer,
            onToggleModelAnswer = viewModel::toggleModelAnswer,
            isQuestionSheetOpen = uiState.isQuestionSheetOpenInWritingView,
            onToggleQuestionSheet = viewModel::toggleQuestionSheetInWritingView,
            onCloseWritingView = viewModel::closeWritingView,
            tradingDrRows = uiState.tradingDrRows,
            tradingCrRows = uiState.tradingCrRows,
            onUpdateTradingDrRow = viewModel::updateTradingDrRow,
            onUpdateTradingCrRow = viewModel::updateTradingCrRow,
            onAddTradingDrRow = viewModel::addTradingDrRow,
            onAddTradingCrRow = viewModel::addTradingCrRow,
            onDeleteTradingDrRow = viewModel::deleteTradingDrRow,
            onDeleteTradingCrRow = viewModel::deleteTradingCrRow,
            plDrRows = uiState.plDrRows,
            plCrRows = uiState.plCrRows,
            onUpdatePlDrRow = viewModel::updatePlDrRow,
            onUpdatePlCrRow = viewModel::updatePlCrRow,
            onAddPlDrRow = viewModel::addPlDrRow,
            onAddPlCrRow = viewModel::addPlCrRow,
            onDeletePlDrRow = viewModel::deletePlDrRow,
            onDeletePlCrRow = viewModel::deletePlCrRow,
            manufacturingDrRows = uiState.manufacturingDrRows,
            manufacturingCrRows = uiState.manufacturingCrRows,
            onUpdateManufacturingDrRow = viewModel::updateManufacturingDrRow,
            onUpdateManufacturingCrRow = viewModel::updateManufacturingCrRow,
            onAddManufacturingDrRow = viewModel::addManufacturingDrRow,
            onAddManufacturingCrRow = viewModel::addManufacturingCrRow,
            onDeleteManufacturingDrRow = viewModel::deleteManufacturingDrRow,
            onDeleteManufacturingCrRow = viewModel::deleteManufacturingCrRow,
            revaluationDrRows = uiState.revaluationDrRows,
            revaluationCrRows = uiState.revaluationCrRows,
            onUpdateRevaluationDrRow = viewModel::updateRevaluationDrRow,
            onUpdateRevaluationCrRow = viewModel::updateRevaluationCrRow,
            onAddRevaluationDrRow = viewModel::addRevaluationDrRow,
            onAddRevaluationCrRow = viewModel::addRevaluationCrRow,
            onDeleteRevaluationDrRow = viewModel::deleteRevaluationDrRow,
            onDeleteRevaluationCrRow = viewModel::deleteRevaluationCrRow,
            realisationDrRows = uiState.realisationDrRows,
            realisationCrRows = uiState.realisationCrRows,
            onUpdateRealisationDrRow = viewModel::updateRealisationDrRow,
            onUpdateRealisationCrRow = viewModel::updateRealisationCrRow,
            onAddRealisationDrRow = viewModel::addRealisationDrRow,
            onAddRealisationCrRow = viewModel::addRealisationCrRow,
            onDeleteRealisationDrRow = viewModel::deleteRealisationDrRow,
            onDeleteRealisationCrRow = viewModel::deleteRealisationCrRow,
            consignmentDrRows = uiState.consignmentDrRows,
            consignmentCrRows = uiState.consignmentCrRows,
            onUpdateConsignmentDrRow = viewModel::updateConsignmentDrRow,
            onUpdateConsignmentCrRow = viewModel::updateConsignmentCrRow,
            onAddConsignmentDrRow = viewModel::addConsignmentDrRow,
            onAddConsignmentCrRow = viewModel::addConsignmentCrRow,
            onDeleteConsignmentDrRow = viewModel::deleteConsignmentDrRow,
            onDeleteConsignmentCrRow = viewModel::deleteConsignmentCrRow,
            bsLiabilityRows = uiState.bsLiabilityRows,
            bsAssetRows = uiState.bsAssetRows,
            onUpdateBsLiabilityRow = viewModel::updateBsLiabilityRow,
            onUpdateBsAssetRow = viewModel::updateBsAssetRow,
            onAddBsLiabilityRow = viewModel::addBsLiabilityRow,
            onAddBsAssetRow = viewModel::addBsAssetRow,
            onDeleteBsLiabilityRow = viewModel::deleteBsLiabilityRow,
            onDeleteBsAssetRow = viewModel::deleteBsAssetRow,
            ledgerAccountName = uiState.ledgerAccountName,
            onUpdateLedgerAccountName = viewModel::updateLedgerAccountName,
            ledgerDrRows = uiState.ledgerDrRows,
            ledgerCrRows = uiState.ledgerCrRows,
            onUpdateLedgerDrRow = viewModel::updateLedgerDrRow,
            onUpdateLedgerCrRow = viewModel::updateLedgerCrRow,
            onAddLedgerDrRow = viewModel::addLedgerDrRow,
            onAddLedgerCrRow = viewModel::addLedgerCrRow,
            onDeleteLedgerDrRow = viewModel::deleteLedgerDrRow,
            onDeleteLedgerCrRow = viewModel::deleteLedgerCrRow,
            formatSearchQuery = uiState.formatSearchQuery,
            onFormatSearchQueryChange = viewModel::onFormatSearchQueryChanged,
            isFormatSearchExpanded = uiState.isFormatSearchExpanded,
            onToggleFormatSearchExpanded = viewModel::toggleFormatSearch,
            journalRows = uiState.journalRows,
            onUpdateJournalRow = viewModel::updateJournalRow,
            onAddJournalRow = viewModel::addJournalRow,
            onDeleteJournalRow = viewModel::deleteJournalRow,
            trialBalanceRows = uiState.trialBalanceRows,
            onUpdateTrialBalanceRow = viewModel::updateTrialBalanceRow,
            onAddTrialBalanceRow = viewModel::addTrialBalanceRow,
            onDeleteTrialBalanceRow = viewModel::deleteTrialBalanceRow,
            brsStartingBalance = uiState.brsStartingBalance,
            onUpdateBrsStartingBalance = viewModel::updateBrsStartingBalance,
            brsRows = uiState.brsRows,
            onUpdateBrsRow = viewModel::updateBrsRow,
            onAddBrsRow = viewModel::addBrsRow,
            onDeleteBrsRow = viewModel::deleteBrsRow,
            cashBookDrRows = uiState.cashBookDrRows,
            cashBookCrRows = uiState.cashBookCrRows,
            onUpdateCashBookDr = viewModel::updateCashBookDrRow,
            onUpdateCashBookCr = viewModel::updateCashBookCrRow,
            onAddCashBookDr = viewModel::addCashBookDrRow,
            onAddCashBookCr = viewModel::addCashBookCrRow,
            onDeleteCashBookDr = viewModel::deleteCashBookDrRow,
            onDeleteCashBookCr = viewModel::deleteCashBookCrRow,
            partnersCapitalDrRows = uiState.partnersCapitalDrRows,
            partnersCapitalCrRows = uiState.partnersCapitalCrRows,
            onUpdatePartnersCapitalDr = viewModel::updatePartnersCapitalDrRow,
            onUpdatePartnersCapitalCr = viewModel::updatePartnersCapitalCrRow,
            onAddPartnersCapitalDr = viewModel::addPartnersCapitalDrRow,
            onAddPartnersCapitalCr = viewModel::addPartnersCapitalCrRow,
            onDeletePartnersCapitalDr = viewModel::deletePartnersCapitalDrRow,
            onDeletePartnersCapitalCr = viewModel::deletePartnersCapitalCrRow,
            pettyCashRows = uiState.pettyCashRows,
            onUpdatePettyCashRow = viewModel::updatePettyCashRow,
            onAddPettyCashRow = viewModel::addPettyCashRow,
            onDeletePettyCashRow = viewModel::deletePettyCashRow,
            onDownloadJson = viewModel::generateExportJson,
            insertedBlocks = uiState.insertedBlocks,
            onAddBlock = { type, title, leftHeader, rightHeader ->
                viewModel.insertBlock(type, title, leftHeader, rightHeader)
            },
            onRemoveBlock = viewModel::removeBlock,
            onClearBlockValues = viewModel::clearBlockValues,
            onMoveBlockUp = viewModel::moveBlockUp,
            onMoveBlockDown = viewModel::moveBlockDown,
            onToggleBlockCollapse = viewModel::toggleBlockCollapse,
            workingNotes = uiState.workingNotes,
            onUpdateWorkingNote = viewModel::updateWorkingNote,
            twoSidedBlocks = uiState.twoSidedBlocks,
            onUpdateTwoSidedDrRow = viewModel::updateTwoSidedDrRow,
            onUpdateTwoSidedCrRow = viewModel::updateTwoSidedCrRow,
            onAddTwoSidedDrRow = viewModel::addTwoSidedDrRow,
            onAddTwoSidedCrRow = viewModel::addTwoSidedCrRow,
            onDeleteTwoSidedDrRow = viewModel::deleteTwoSidedDrRow,
            onDeleteTwoSidedCrRow = viewModel::deleteTwoSidedCrRow,
            ledgerBlocks = uiState.ledgerBlocks,
            onUpdateLedgerAccountNameForBlock = viewModel::updateLedgerAccountNameForBlock,
            onUpdateLedgerDrRowForBlock = viewModel::updateLedgerDrRowForBlock,
            onUpdateLedgerCrRowForBlock = viewModel::updateLedgerCrRowForBlock,
            onAddLedgerDrRowForBlock = viewModel::addLedgerDrRowForBlock,
            onAddLedgerCrRowForBlock = viewModel::addLedgerCrRowForBlock,
            onDeleteLedgerDrRowForBlock = viewModel::deleteLedgerDrRowForBlock,
            onDeleteLedgerCrRowForBlock = viewModel::deleteLedgerCrRowForBlock,
            journalBlocks = uiState.journalBlocks,
            onUpdateJournalRowForBlock = viewModel::updateJournalRowForBlock,
            onAddJournalRowForBlock = viewModel::addJournalRowForBlock,
            onDeleteJournalRowForBlock = viewModel::deleteJournalRowForBlock,
            trialBalanceBlocks = uiState.trialBalanceBlocks,
            onUpdateTrialBalanceRowForBlock = viewModel::updateTrialBalanceRowForBlock,
            onAddTrialBalanceRowForBlock = viewModel::addTrialBalanceRowForBlock,
            onDeleteTrialBalanceRowForBlock = viewModel::deleteTrialBalanceRowForBlock,
            brsBlocks = uiState.brsBlocks,
            onUpdateBrsStartingBalanceForBlock = viewModel::updateBrsStartingBalanceForBlock,
            onUpdateBrsRowForBlock = viewModel::updateBrsRowForBlock,
            onAddBrsRowForBlock = viewModel::addBrsRowForBlock,
            onDeleteBrsRowForBlock = viewModel::deleteBrsRowForBlock,
            cashBookBlocks = uiState.cashBookBlocks,
            onUpdateCashBookDrForBlock = viewModel::updateCashBookDrRowForBlock,
            onUpdateCashBookCrForBlock = viewModel::updateCashBookCrRowForBlock,
            onAddCashBookDrForBlock = viewModel::addCashBookDrRowForBlock,
            onAddCashBookCrForBlock = viewModel::addCashBookCrRowForBlock,
            onDeleteCashBookDrForBlock = viewModel::deleteCashBookDrRowForBlock,
            onDeleteCashBookCrForBlock = viewModel::deleteCashBookCrRowForBlock,
            partnersCapitalBlocks = uiState.partnersCapitalBlocks,
            onUpdatePartnersCapitalDrForBlock = viewModel::updatePartnersCapitalDrRowForBlock,
            onUpdatePartnersCapitalCrForBlock = viewModel::updatePartnersCapitalCrRowForBlock,
            onAddPartnersCapitalDrForBlock = viewModel::addPartnersCapitalDrRowForBlock,
            onAddPartnersCapitalCrForBlock = viewModel::addPartnersCapitalCrRowForBlock,
            onDeletePartnersCapitalDrForBlock = viewModel::deletePartnersCapitalDrRowForBlock,
            onDeletePartnersCapitalCrForBlock = viewModel::deletePartnersCapitalCrRowForBlock,
            pettyCashBlocks = uiState.pettyCashBlocks,
            onUpdatePettyCashRowForBlock = viewModel::updatePettyCashRowForBlock,
            onAddPettyCashRowForBlock = viewModel::addPettyCashRowForBlock,
            onDeletePettyCashRowForBlock = viewModel::deletePettyCashRowForBlock,
            isGrading = uiState.isGrading,
            gradingResult = uiState.gradingResult,
            onSubmitForGrading = viewModel::submitAnswerForGrading,
            modifier = modifier
        )
        return
    }

    val scrollState = rememberScrollState()

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
            // Main Answer Script Paper Sheet (resembling a physical notebook page)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("practice_copy_sheet"),
                shape = RoundedCornerShape(4.dp),
                color = VintagePaperSheet,
                border = BorderStroke(1.2.dp, VintageLine),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                        .verticalScroll(scrollState)
                ) {
                    // Vintage Header & Stamp
                    ExamPaperHeader()

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filters
                    FilterBar(
                        selectedSubject = uiState.selectedSubject,
                        onSubjectChange = viewModel::onSubjectChanged,
                        availableChapters = uiState.availableChapters,
                        selectedChapter = uiState.selectedChapter,
                        onChapterChange = viewModel::onChapterChanged,
                        selectedDepth = uiState.selectedDepth,
                        onDepthChange = viewModel::onDepthChanged,
                        questionCount = uiState.questionCount
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Animated Question Area
                    AnimatedContent(
                        targetState = uiState.currentQuestion,
                        transitionSpec = {
                            (slideInHorizontally { width -> width / 3 } + fadeIn())
                                .togetherWith(slideOutHorizontally { width -> -width / 3 } + fadeOut())
                        },
                        label = "question_transition"
                    ) { currentQ ->
                        if (currentQ == null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No questions match this filter",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = VintageNavy,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Try choosing a different subject or chapter filter above.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VintageInkSoft
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                // Question Kicker Tag
                                val kickerText = buildString {
                                    append(currentQ.subject.displayName)
                                    append(" · ")
                                    append(currentQ.chapter)
                                    append(" · ")
                                    append(if (currentQ.mode == QuestionMode.MCQ) "Objective" else "Subjective")
                                }

                                Text(
                                    text = kickerText,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = VintageNavy,
                                        letterSpacing = 0.3.sp
                                    ),
                                    modifier = Modifier.testTag("question_kicker")
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Response UI based on Mode
                                if (currentQ.mode == QuestionMode.SUBJECTIVE) {
                                    // Question Title Text for Subjective Mode
                                    VintageMarkdownText(
                                        text = if (currentQ.questionText.isNotBlank()) {
                                            currentQ.questionText
                                        } else {
                                            currentQ.topic
                                        },
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 19.sp,
                                            lineHeight = 27.sp,
                                            color = VintageInk
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("question_title")
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Subjective Question: Clean Question Card with "Write Answer" Button at the bottom
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                            .testTag("subjective_answer_card"),
                                        shape = RoundedCornerShape(4.dp),
                                        color = VintagePaperBg.copy(alpha = 0.6f),
                                        border = BorderStroke(1.dp, VintageLine)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "EXAMINATION ANSWER SCRIPT",
                                                    style = TextStyle(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = VintageNavy,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                )

                                                val wordCount = uiState.userAnswerText.trim()
                                                    .split("\\s+".toRegex())
                                                    .count { it.isNotBlank() }
                                                val hasStarted = wordCount > 0 || uiState.activeTableType != AccountingTableType.NONE

                                                if (hasStarted) {
                                                    Surface(
                                                        shape = RoundedCornerShape(3.dp),
                                                        color = VintageGreenBg,
                                                        border = BorderStroke(1.dp, VintageGreen.copy(alpha = 0.5f))
                                                    ) {
                                                        Text(
                                                            text = "In Progress: $wordCount words",
                                                            style = TextStyle(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = VintageGreen
                                                            ),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = if (uiState.userAnswerText.isNotBlank()) {
                                                    val preview = uiState.userAnswerText.trim().replace("\n", " ").take(130)
                                                    "\"$preview...\""
                                                } else if (currentQ.subject == Subject.ACC) {
                                                    "Full ruled answer script with 14+ searchable CA accounting formats (Journal, Cash Book, BRS, Ledger, Trial Balance, etc.)."
                                                } else {
                                                    "Full ruled answer script with ICAI statutory legal structuring tools (Provision, Analysis & Conclusion)."
                                                },
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = 12.5.sp,
                                                    color = VintageInkSoft,
                                                    lineHeight = 17.sp
                                                ),
                                                maxLines = 2
                                            )

                                            Spacer(modifier = Modifier.height(14.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (currentQ.modelAnswer.isNotBlank()) {
                                                    androidx.compose.material3.IconButton(
                                                        onClick = viewModel::toggleModelAnswer,
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .testTag("toggle_model_answer_btn")
                                                    ) {
                                                        Icon(
                                                            imageVector = if (uiState.showModelAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                            contentDescription = "Preview ICAI Model Answer",
                                                            tint = if (uiState.showModelAnswer) VintageNavy else VintageInkSoft,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }

                                                androidx.compose.material3.IconButton(
                                                    onClick = viewModel::openWritingView,
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .testTag("write_answer_button")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Write Answer",
                                                        tint = VintageNavy,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            if (uiState.showModelAnswer) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Surface(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = VintageGoldBg,
                                                    border = BorderStroke(1.dp, VintageGold)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            text = "ICAI SUGGESTED KEY POINTS",
                                                            style = TextStyle(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontSize = 10.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = VintageGold,
                                                                letterSpacing = 0.5.sp
                                                            )
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        VintageMarkdownText(
                                                            text = currentQ.modelAnswer,
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
                                        }
                                    }
                                } else {
                                    ObjectiveMcqView(
                                        question = currentQ,
                                        selectedOptionIndex = uiState.selectedOptionIndex,
                                        onSelectOption = viewModel::onSelectMcqOption,
                                        isHintUnlocked = uiState.isHintUnlocked,
                                        onOpenHintModal = viewModel::openHintModal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Exam Action Controls: Spin New Question & Room Progress Stats
                    ExamActionControls(
                        onNewQuestion = { viewModel.loadNextQuestion() },
                        questionsAnsweredCount = uiState.questionsAnsweredCount,
                        questionsAttemptedCount = uiState.questionsAttemptedCount,
                        mcqCorrectCount = uiState.mcqCorrectCount,
                        mcqAttemptedCount = uiState.mcqAttemptedCount,
                        isLoadingQuestion = uiState.isFetchingQuestion
                    )
                }
            }
        }

        // Rewarded Ad Hint Modal
        if (uiState.isHintModalVisible && uiState.currentQuestion != null) {
            HintRewardedModal(
                hintFormula = uiState.currentQuestion?.hintFormula ?: "",
                initialUnlocked = uiState.isHintUnlocked,
                onDismiss = viewModel::closeHintModal,
                onRewardEarned = viewModel::onHintRewardEarned
            )
        }
    }
}
