package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountRow
import com.example.model.AccountingTableType
import com.example.model.BrsRow
import com.example.model.CashBookRow
import com.example.model.JournalRow
import com.example.model.LedgerRow
import com.example.model.PartnersCapitalRow
import com.example.model.PettyCashRow
import com.example.model.TrialBalanceRow
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet
import com.example.ui.theme.VintageRed
import com.example.ui.theme.VintageRedBg
import java.text.DecimalFormat

private val currencyFormatter = DecimalFormat("#,##,##0.00")

@Composable
fun AccountingTableContainer(
    tableType: AccountingTableType,
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
    // Specialized format parameters
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
    onChangeFormat: () -> Unit = {},
    onCloseTable: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tableType == AccountingTableType.NONE) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("accounting_table_container"),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, VintageNavy),
        color = VintagePaperSheet,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tableType.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VintageNavy
                    )
                    Text(
                        text = "Command: ${tableType.command} • ${tableType.category}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = VintageInkSoft
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onChangeFormat,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("change_format_button"),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Formats",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        )
                    }

                    IconButton(
                        onClick = onCloseTable,
                        modifier = Modifier.testTag("close_table_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Table",
                            tint = VintageInkSoft
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (tableType) {
                AccountingTableType.TRADING_ACCOUNT -> {
                    TradingAccountGrid(
                        drRows = tradingDrRows,
                        crRows = tradingCrRows,
                        onUpdateDr = onUpdateTradingDrRow,
                        onUpdateCr = onUpdateTradingCrRow,
                        onAddDr = onAddTradingDrRow,
                        onAddCr = onAddTradingCrRow,
                        onDeleteDr = onDeleteTradingDrRow,
                        onDeleteCr = onDeleteTradingCrRow
                    )
                }
                AccountingTableType.PROFIT_LOSS_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = plDrRows,
                        crRows = plCrRows,
                        onUpdateDr = onUpdatePlDrRow,
                        onUpdateCr = onUpdatePlCrRow,
                        onAddDr = onAddPlDrRow,
                        onAddCr = onAddPlCrRow,
                        onDeleteDr = onDeletePlDrRow,
                        onDeleteCr = onDeletePlCrRow
                    )
                }
                AccountingTableType.MANUFACTURING_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = manufacturingDrRows,
                        crRows = manufacturingCrRows,
                        onUpdateDr = onUpdateManufacturingDrRow,
                        onUpdateCr = onUpdateManufacturingCrRow,
                        onAddDr = onAddManufacturingDrRow,
                        onAddCr = onAddManufacturingCrRow,
                        onDeleteDr = onDeleteManufacturingDrRow,
                        onDeleteCr = onDeleteManufacturingCrRow
                    )
                }
                AccountingTableType.REVALUATION_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = revaluationDrRows,
                        crRows = revaluationCrRows,
                        onUpdateDr = onUpdateRevaluationDrRow,
                        onUpdateCr = onUpdateRevaluationCrRow,
                        onAddDr = onAddRevaluationDrRow,
                        onAddCr = onAddRevaluationCrRow,
                        onDeleteDr = onDeleteRevaluationDrRow,
                        onDeleteCr = onDeleteRevaluationCrRow
                    )
                }
                AccountingTableType.REALISATION_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = realisationDrRows,
                        crRows = realisationCrRows,
                        onUpdateDr = onUpdateRealisationDrRow,
                        onUpdateCr = onUpdateRealisationCrRow,
                        onAddDr = onAddRealisationDrRow,
                        onAddCr = onAddRealisationCrRow,
                        onDeleteDr = onDeleteRealisationDrRow,
                        onDeleteCr = onDeleteRealisationCrRow
                    )
                }
                AccountingTableType.CONSIGNMENT_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = consignmentDrRows,
                        crRows = consignmentCrRows,
                        onUpdateDr = onUpdateConsignmentDrRow,
                        onUpdateCr = onUpdateConsignmentCrRow,
                        onAddDr = onAddConsignmentDrRow,
                        onAddCr = onAddConsignmentCrRow,
                        onDeleteDr = onDeleteConsignmentDrRow,
                        onDeleteCr = onDeleteConsignmentCrRow
                    )
                }
                AccountingTableType.CUSTOM_FORMAT -> {
                    Text("Custom Formats are only supported as inserted blocks.")
                }
                AccountingTableType.BALANCE_SHEET -> {
                    BalanceSheetGrid(
                        liabilities = bsLiabilityRows,
                        assets = bsAssetRows,
                        onUpdateLiability = onUpdateBsLiabilityRow,
                        onUpdateAsset = onUpdateBsAssetRow,
                        onAddLiability = onAddBsLiabilityRow,
                        onAddAsset = onAddBsAssetRow,
                        onDeleteLiability = onDeleteBsLiabilityRow,
                        onDeleteAsset = onDeleteBsAssetRow
                    )
                }
                AccountingTableType.LEDGER -> {
                    LedgerAccountGrid(
                        accountName = ledgerAccountName,
                        onUpdateAccountName = onUpdateLedgerAccountName,
                        drRows = ledgerDrRows,
                        crRows = ledgerCrRows,
                        onUpdateDr = onUpdateLedgerDrRow,
                        onUpdateCr = onUpdateLedgerCrRow,
                        onAddDr = onAddLedgerDrRow,
                        onAddCr = onAddLedgerCrRow,
                        onDeleteDr = onDeleteLedgerDrRow,
                        onDeleteCr = onDeleteLedgerCrRow
                    )
                }
                AccountingTableType.JOURNAL -> {
                    JournalEntryGrid(
                        journalRows = journalRows,
                        onUpdateRow = onUpdateJournalRow,
                        onAddRow = onAddJournalRow,
                        onDeleteRow = onDeleteJournalRow
                    )
                }
                AccountingTableType.TRIAL_BALANCE -> {
                    TrialBalanceGrid(
                        trialBalanceRows = trialBalanceRows,
                        onUpdateRow = onUpdateTrialBalanceRow,
                        onAddRow = onAddTrialBalanceRow,
                        onDeleteRow = onDeleteTrialBalanceRow
                    )
                }
                AccountingTableType.BRS -> {
                    BrsGrid(
                        startingBalance = brsStartingBalance,
                        onUpdateStartingBalance = onUpdateBrsStartingBalance,
                        brsRows = brsRows,
                        onUpdateRow = onUpdateBrsRow,
                        onAddRow = onAddBrsRow,
                        onDeleteRow = onDeleteBrsRow
                    )
                }
                AccountingTableType.CASH_BOOK -> {
                    CashBookGrid(
                        cashBookDrRows = cashBookDrRows,
                        cashBookCrRows = cashBookCrRows,
                        onUpdateDr = onUpdateCashBookDr,
                        onUpdateCr = onUpdateCashBookCr,
                        onAddDr = onAddCashBookDr,
                        onAddCr = onAddCashBookCr,
                        onDeleteDr = onDeleteCashBookDr,
                        onDeleteCr = onDeleteCashBookCr
                    )
                }
                AccountingTableType.PARTNERS_CAPITAL -> {
                    PartnersCapitalGrid(
                        drRows = partnersCapitalDrRows,
                        crRows = partnersCapitalCrRows,
                        onUpdateDr = onUpdatePartnersCapitalDr,
                        onUpdateCr = onUpdatePartnersCapitalCr,
                        onAddDr = onAddPartnersCapitalDr,
                        onAddCr = onAddPartnersCapitalCr,
                        onDeleteDr = onDeletePartnersCapitalDr,
                        onDeleteCr = onDeletePartnersCapitalCr
                    )
                }
                AccountingTableType.PETTY_CASH_BOOK -> {
                    PettyCashGrid(
                        pettyCashRows = pettyCashRows,
                        onUpdateRow = onUpdatePettyCashRow,
                        onAddRow = onAddPettyCashRow,
                        onDeleteRow = onDeletePettyCashRow
                    )
                }
                AccountingTableType.NONE,
                AccountingTableType.WORKING_NOTE -> {}
            }
        }
    }
}

@Composable
fun TradingAccountGrid(
    drRows: List<AccountRow>,
    crRows: List<AccountRow>,
    onUpdateDr: (Int, String, String) -> Unit,
    onUpdateCr: (Int, String, String) -> Unit,
    onAddDr: () -> Unit,
    onAddCr: () -> Unit,
    onDeleteDr: (Int) -> Unit,
    onDeleteCr: (Int) -> Unit
) {
    TwoSidedAccountGrid(
        tableType = AccountingTableType.TRADING_ACCOUNT,
        drRows = drRows,
        crRows = crRows,
        onUpdateDr = onUpdateDr,
        onUpdateCr = onUpdateCr,
        onAddDr = onAddDr,
        onAddCr = onAddCr,
        onDeleteDr = onDeleteDr,
        onDeleteCr = onDeleteCr
    )
}

@Composable
fun BalanceSheetGrid(
    liabilities: List<AccountRow>,
    assets: List<AccountRow>,
    onUpdateLiability: (Int, String, String) -> Unit,
    onUpdateAsset: (Int, String, String) -> Unit,
    onAddLiability: () -> Unit,
    onAddAsset: () -> Unit,
    onDeleteLiability: (Int) -> Unit,
    onDeleteAsset: (Int) -> Unit
) {
    TwoSidedAccountGrid(
        tableType = AccountingTableType.BALANCE_SHEET,
        drRows = liabilities,
        crRows = assets,
        onUpdateDr = onUpdateLiability,
        onUpdateCr = onUpdateAsset,
        onAddDr = onAddLiability,
        onAddCr = onAddAsset,
        onDeleteDr = onDeleteLiability,
        onDeleteCr = onDeleteAsset
    )
}

@Composable
fun LedgerAccountGrid(
    accountName: String,
    onUpdateAccountName: (String) -> Unit,
    drRows: List<LedgerRow>,
    crRows: List<LedgerRow>,
    onUpdateDr: (Int, String, String, String, String) -> Unit,
    onUpdateCr: (Int, String, String, String, String) -> Unit,
    onAddDr: () -> Unit,
    onAddCr: () -> Unit,
    onDeleteDr: (Int) -> Unit,
    onDeleteCr: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalDr = drRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalCr = crRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val balance = totalDr - totalCr

    Column(modifier = Modifier.fillMaxWidth()) {
        // Account Name Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Account Title:",
                style = MaterialTheme.typography.labelMedium,
                color = VintageNavy,
                modifier = Modifier.padding(end = 8.dp)
            )
            BasicTextField(
                value = accountName,
                onValueChange = onUpdateAccountName,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = VintageInk
                ),
                modifier = Modifier
                    .weight(1f)
                    .background(VintagePaperBg, RoundedCornerShape(3.dp))
                    .border(1.dp, VintageLine, RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(4.dp)
                .testTag("ledger_account_grid")
        ) {
            // Dark Blue Header Row
            Row(
                modifier = Modifier
                    .width(880.dp)
                    .background(VintageNavy)
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dr. (Date, Particulars, J.F., Amount)",
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = "Cr. (Date, Particulars, J.F., Amount)",
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
            }

            // Body Columns
            Row(modifier = Modifier.width(880.dp)) {
                // Debit Side Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, VintageLine)
                        .background(VintagePaperSheet)
                        .padding(6.dp)
                ) {
                    drRows.forEachIndexed { index, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = row.date,
                                onValueChange = { onUpdateDr(index, it, row.particulars, row.jf, row.amount) },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageInk),
                                modifier = Modifier
                                    .width(60.dp)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            BasicTextField(
                                value = row.particulars,
                                onValueChange = { onUpdateDr(index, row.date, it, row.jf, row.amount) },
                                textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 12.sp, color = VintageInk),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            BasicTextField(
                                value = row.jf,
                                onValueChange = { onUpdateDr(index, row.date, row.particulars, it, row.amount) },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageNavy),
                                modifier = Modifier
                                    .width(36.dp)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            BasicTextField(
                                value = row.amount,
                                onValueChange = { onUpdateDr(index, row.date, row.particulars, row.jf, it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VintageNavy),
                                modifier = Modifier
                                    .width(70.dp)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            IconButton(onClick = { onDeleteDr(index) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete row", tint = VintageRed, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = onAddDr,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Dr Item", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp))
                    }
                }

                // Credit Side Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, VintageLine)
                        .background(VintagePaperSheet)
                        .padding(6.dp)
                ) {
                    crRows.forEachIndexed { index, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = row.date,
                                onValueChange = { onUpdateCr(index, it, row.particulars, row.jf, row.amount) },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageInk),
                                modifier = Modifier
                                    .width(60.dp)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            BasicTextField(
                                value = row.particulars,
                                onValueChange = { onUpdateCr(index, row.date, it, row.jf, row.amount) },
                                textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 12.sp, color = VintageInk),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            BasicTextField(
                                value = row.jf,
                                onValueChange = { onUpdateCr(index, row.date, row.particulars, it, row.amount) },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageNavy),
                                modifier = Modifier
                                    .width(36.dp)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            BasicTextField(
                                value = row.amount,
                                onValueChange = { onUpdateCr(index, row.date, row.particulars, row.jf, it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VintageNavy),
                                modifier = Modifier
                                    .width(70.dp)
                                    .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            )
                            IconButton(onClick = { onDeleteCr(index) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete row", tint = VintageRed, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = onAddCr,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Cr Item", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp))
                    }
                }
            }

            // Totals & Balancing Figure Bar
            Surface(
                modifier = Modifier
                    .width(880.dp)
                    .border(1.dp, VintageLine),
                color = VintageNavy.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Dr: ₹%,.2f".format(totalDr),
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                    )
                    Text(
                        text = if (balance >= 0) "Dr Balance c/d: ₹%,.2f".format(balance) else "Cr Balance c/d: ₹%,.2f".format(-balance),
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                    )
                    Text(
                        text = "Total Cr: ₹%,.2f".format(totalCr),
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                    )
                }
            }
        }
    }
}

// ---------------- Helper Components for Cells ----------------

@Composable
fun TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false,
    alignRight: Boolean = false
) {
    Box(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = if (alignRight) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) VintageNavy else VintageInk,
            textAlign = if (alignRight) TextAlign.End else TextAlign.Start
        )
    }
}

@Composable
fun EditableParticularsCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = VintageInk
            ),
            modifier = Modifier.weight(1f)
        )
        if (onDelete != null) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Row",
                tint = VintageInkSoft.copy(alpha = 0.5f),
                modifier = Modifier
                    .height(14.dp)
                    .width(14.dp)
                    .clickable { onDelete() }
            )
        }
    }
}

@Composable
fun EditableAmountCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float
) {
    Box(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        BasicTextField(
            value = value,
            onValueChange = { newVal ->
                // Allow digits and decimal point
                if (newVal.isEmpty() || newVal.matches(Regex("""^\d*\.?\d*$"""))) {
                    onValueChange(newVal)
                }
            },
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = VintageInk,
                textAlign = TextAlign.End
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditableLedgerCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float
) {
    Box(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = VintageInk
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
