package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import com.example.model.TrialBalanceRow
import com.example.model.TwoSidedTableData
import com.example.model.WorkingNote
import com.example.ui.theme.VintageGold
import com.example.ui.theme.VintageGoldBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet
import com.example.ui.theme.VintageRed
import com.example.ui.theme.VintageRedBg

@Composable
fun InsertedBlockCard(
    block: InsertedBlock,
    index: Int,
    onToggleCollapse: () -> Unit,
    onRemoveBlock: () -> Unit,
    onClearBlockValues: () -> Unit = {},
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    // Block-specific data
    workingNote: WorkingNote? = null,
    onUpdateWorkingNote: (title: String, content: String) -> Unit = { _, _ -> },
    twoSidedData: TwoSidedTableData? = null,
    onUpdateTwoSidedDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateTwoSidedCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddTwoSidedDrRow: () -> Unit = {},
    onAddTwoSidedCrRow: () -> Unit = {},
    onDeleteTwoSidedDrRow: (Int) -> Unit = {},
    onDeleteTwoSidedCrRow: (Int) -> Unit = {},
    ledgerData: LedgerTableData? = null,
    onUpdateLedgerAccountName: (String) -> Unit = {},
    onUpdateLedgerDrRow: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpdateLedgerCrRow: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onAddLedgerDrRow: () -> Unit = {},
    onAddLedgerCrRow: () -> Unit = {},
    onDeleteLedgerDrRow: (Int) -> Unit = {},
    onDeleteLedgerCrRow: (Int) -> Unit = {},
    journalRows: List<JournalRow> = emptyList(),
    onUpdateJournalRow: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddJournalRow: () -> Unit = {},
    onDeleteJournalRow: (Int) -> Unit = {},
    trialBalanceRows: List<TrialBalanceRow> = emptyList(),
    onUpdateTrialBalanceRow: (Int, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onAddTrialBalanceRow: () -> Unit = {},
    onDeleteTrialBalanceRow: (Int) -> Unit = {},
    brsData: BrsTableData? = null,
    onUpdateBrsStartingBalance: (String) -> Unit = {},
    onUpdateBrsRow: (Int, String, Boolean, String) -> Unit = { _, _, _, _ -> },
    onAddBrsRow: (Boolean) -> Unit = {},
    onDeleteBrsRow: (Int) -> Unit = {},
    cashBookData: CashBookTableData? = null,
    onUpdateCashBookDr: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onUpdateCashBookCr: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddCashBookDr: () -> Unit = {},
    onAddCashBookCr: () -> Unit = {},
    onDeleteCashBookDr: (Int) -> Unit = {},
    onDeleteCashBookCr: (Int) -> Unit = {},
    partnersCapitalData: PartnersCapitalTableData? = null,
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
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Remove Block #${index + 1}?",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = VintageNavy
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove \"${block.displayTitle}\"? Any entered table rows or notes in this block will be discarded.",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.5.sp,
                        color = VintageInk
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onRemoveBlock()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = VintageRed)
                ) {
                    Text("Remove", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = VintageNavy)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = VintagePaperSheet,
            shape = RoundedCornerShape(8.dp)
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("inserted_block_${block.id}"),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.2.dp, if (block.type == BlockType.WORKING_NOTE) VintageGold else VintageNavy),
        color = VintagePaperSheet,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Accordion Header Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleCollapse() }
                    .testTag("block_header_${block.id}"),
                color = if (block.type == BlockType.WORKING_NOTE) VintageGoldBg.copy(alpha = 0.45f) else VintagePaperBg,
                shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Numbering pill + Title + Category
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Number indicator badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (block.type == BlockType.WORKING_NOTE) VintageGold else VintageNavy)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "#${index + 1}",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        // Icon
                        Icon(
                            imageVector = if (block.type == BlockType.WORKING_NOTE) Icons.Default.EditNote else Icons.Default.TableChart,
                            contentDescription = null,
                            tint = if (block.type == BlockType.WORKING_NOTE) VintageGold else VintageNavy,
                            modifier = Modifier.size(18.dp)
                        )

                        // Title & Subtitle
                        Column {
                            Text(
                                text = block.displayTitle,
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VintageNavy
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = "${block.type.command} • ${block.type.category}",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = VintageInkSoft
                                )
                            )
                        }
                    }

                    // Right: Slim control bar (Drag, Collapse, Delete)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Drag Handle
                        var dragAmount by remember { mutableStateOf(0f) }
                        IconButton(
                            onClick = { /* No-op on click, relies on drag */ },
                            modifier = Modifier
                                .size(32.dp)
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures(
                                        onDragEnd = { dragAmount = 0f },
                                        onDragCancel = { dragAmount = 0f }
                                    ) { change, dragAmountDelta ->
                                        change.consume()
                                        dragAmount += dragAmountDelta
                                        if (dragAmount > 60f) {
                                            onMoveDown()
                                            dragAmount = 0f
                                        } else if (dragAmount < -60f) {
                                            onMoveUp()
                                            dragAmount = 0f
                                        }
                                    }
                                }
                                .testTag("drag_handle_${block.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "Reorder block",
                                tint = VintageNavy.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        

                        val context = LocalContext.current
                        // Clear button
                        IconButton(
                            onClick = { 
                                onClearBlockValues()
                                Toast.makeText(context, "Values cleared", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("clear_block_${block.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Clear all values",
                                tint = VintageNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        // Collapse / Expand toggle button
                        IconButton(
                            onClick = onToggleCollapse,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("toggle_collapse_${block.id}")
                        ) {
                            Icon(
                                imageVector = if (block.isCollapsed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (block.isCollapsed) "Expand block" else "Collapse block",
                                tint = VintageNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Delete button
                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_block_${block.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove block",
                                tint = VintageRed.copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Accordion Body Content
            AnimatedVisibility(
                visible = !block.isCollapsed,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    when (block.type) {
                        BlockType.WORKING_NOTE -> {
                            val note = workingNote ?: WorkingNote(block.id, block.displayTitle, "")
                            WorkingNoteEditor(
                                workingNote = note,
                                onUpdate = { updatedTitle, updatedContent ->
                                    onUpdateWorkingNote(updatedTitle, updatedContent)
                                }
                            )
                        }
                        BlockType.TRADING_ACCOUNT -> {
                            val dr = twoSidedData?.drRows ?: emptyList()
                            val cr = twoSidedData?.crRows ?: emptyList()
                            TradingAccountGrid(
                                drRows = dr,
                                crRows = cr,
                                onUpdateDr = onUpdateTwoSidedDrRow,
                                onUpdateCr = onUpdateTwoSidedCrRow,
                                onAddDr = onAddTwoSidedDrRow,
                                onAddCr = onAddTwoSidedCrRow,
                                onDeleteDr = onDeleteTwoSidedDrRow,
                                onDeleteCr = onDeleteTwoSidedCrRow
                            )
                        }
                        BlockType.PROFIT_LOSS_ACCOUNT,
                        BlockType.MANUFACTURING_ACCOUNT,
                        BlockType.REVALUATION_ACCOUNT,
                        BlockType.REALISATION_ACCOUNT,
                        BlockType.CONSIGNMENT_ACCOUNT,
                        BlockType.CUSTOM_FORMAT -> {
                            val dr = twoSidedData?.drRows ?: emptyList()
                            val cr = twoSidedData?.crRows ?: emptyList()
                            TwoSidedAccountGrid(
                                tableType = block.type,
                                drRows = dr,
                                crRows = cr,
                                onUpdateDr = onUpdateTwoSidedDrRow,
                                onUpdateCr = onUpdateTwoSidedCrRow,
                                onAddDr = onAddTwoSidedDrRow,
                                onAddCr = onAddTwoSidedCrRow,
                                onDeleteDr = onDeleteTwoSidedDrRow,
                                onDeleteCr = onDeleteTwoSidedCrRow,
                                customLeftHeader = block.customLeftHeader,
                                customRightHeader = block.customRightHeader
                            )
                        }
                        BlockType.BALANCE_SHEET -> {
                            val liab = twoSidedData?.drRows ?: emptyList()
                            val assets = twoSidedData?.crRows ?: emptyList()
                            BalanceSheetGrid(
                                liabilities = liab,
                                assets = assets,
                                onUpdateLiability = onUpdateTwoSidedDrRow,
                                onUpdateAsset = onUpdateTwoSidedCrRow,
                                onAddLiability = onAddTwoSidedDrRow,
                                onAddAsset = onAddTwoSidedCrRow,
                                onDeleteLiability = onDeleteTwoSidedDrRow,
                                onDeleteAsset = onDeleteTwoSidedCrRow
                            )
                        }
                        BlockType.LEDGER -> {
                            val data = ledgerData ?: LedgerTableData()
                            LedgerAccountGrid(
                                accountName = data.accountName,
                                onUpdateAccountName = onUpdateLedgerAccountName,
                                drRows = data.drRows,
                                crRows = data.crRows,
                                onUpdateDr = onUpdateLedgerDrRow,
                                onUpdateCr = onUpdateLedgerCrRow,
                                onAddDr = onAddLedgerDrRow,
                                onAddCr = onAddLedgerCrRow,
                                onDeleteDr = onDeleteLedgerDrRow,
                                onDeleteCr = onDeleteLedgerCrRow
                            )
                        }
                        BlockType.JOURNAL -> {
                            JournalEntryGrid(
                                journalRows = journalRows,
                                onUpdateRow = onUpdateJournalRow,
                                onAddRow = onAddJournalRow,
                                onDeleteRow = onDeleteJournalRow
                            )
                        }
                        BlockType.TRIAL_BALANCE -> {
                            TrialBalanceGrid(
                                trialBalanceRows = trialBalanceRows,
                                onUpdateRow = onUpdateTrialBalanceRow,
                                onAddRow = onAddTrialBalanceRow,
                                onDeleteRow = onDeleteTrialBalanceRow
                            )
                        }
                        BlockType.BRS -> {
                            val data = brsData ?: BrsTableData()
                            BrsGrid(
                                startingBalance = data.startingBalance,
                                onUpdateStartingBalance = onUpdateBrsStartingBalance,
                                brsRows = data.rows,
                                onUpdateRow = onUpdateBrsRow,
                                onAddRow = onAddBrsRow,
                                onDeleteRow = onDeleteBrsRow
                            )
                        }
                        BlockType.CASH_BOOK -> {
                            val data = cashBookData ?: CashBookTableData()
                            CashBookGrid(
                                cashBookDrRows = data.drRows,
                                cashBookCrRows = data.crRows,
                                onUpdateDr = onUpdateCashBookDr,
                                onUpdateCr = onUpdateCashBookCr,
                                onAddDr = onAddCashBookDr,
                                onAddCr = onAddCashBookCr,
                                onDeleteDr = onDeleteCashBookDr,
                                onDeleteCr = onDeleteCashBookCr
                            )
                        }
                        BlockType.PARTNERS_CAPITAL -> {
                            val data = partnersCapitalData ?: PartnersCapitalTableData()
                            PartnersCapitalGrid(
                                drRows = data.drRows,
                                crRows = data.crRows,
                                onUpdateDr = onUpdatePartnersCapitalDr,
                                onUpdateCr = onUpdatePartnersCapitalCr,
                                onAddDr = onAddPartnersCapitalDr,
                                onAddCr = onAddPartnersCapitalCr,
                                onDeleteDr = onDeletePartnersCapitalDr,
                                onDeleteCr = onDeletePartnersCapitalCr
                            )
                        }
                        BlockType.PETTY_CASH_BOOK -> {
                            PettyCashGrid(
                                pettyCashRows = pettyCashRows,
                                onUpdateRow = onUpdatePettyCashRow,
                                onAddRow = onAddPettyCashRow,
                                onDeleteRow = onDeletePettyCashRow
                            )
                        }
                        BlockType.NONE -> {}
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Working Note free-text editor with ruled paper lines, custom title, and calculation workspace.
 */
@Composable
fun WorkingNoteEditor(
    workingNote: WorkingNote,
    onUpdate: (title: String, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("working_note_editor_${workingNote.id}")
    ) {
        // Editable Note Title
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VintageLine.copy(alpha = 0.6f), RoundedCornerShape(4.dp)),
            color = VintagePaperBg.copy(alpha = 0.7f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Heading: ",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VintageNavy
                    )
                )
                BasicTextField(
                    value = workingNote.title,
                    onValueChange = { onUpdate(it, workingNote.content) },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VintageInk
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("working_note_title_input_${workingNote.id}")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Ruled Notebook multi-line calculation sheet
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VintageLine, RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp),
            color = VintagePaperBg
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val lineSpacing = 26.dp.toPx()
                        var y = lineSpacing
                        while (y < size.height) {
                            drawLine(
                                color = VintageLine.copy(alpha = 0.4f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                            y += lineSpacing
                        }
                    }
                    .padding(10.dp)
            ) {
                if (workingNote.content.isEmpty()) {
                    Text(
                        text = "Enter detailed calculations, formulas, assumptions, ratio workings, or statutory notes here...",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = VintageInkSoft.copy(alpha = 0.65f),
                            lineHeight = 26.sp
                        )
                    )
                }

                BasicTextField(
                    value = workingNote.content,
                    onValueChange = { onUpdate(workingNote.title, it) },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = VintageInk,
                        lineHeight = 26.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp)
                        .testTag("working_note_content_input_${workingNote.id}")
                )
            }
        }
    }
}
