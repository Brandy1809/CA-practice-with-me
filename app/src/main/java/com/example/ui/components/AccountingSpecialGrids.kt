package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountRow
import com.example.model.AccountingTableType
import com.example.model.BrsRow
import com.example.model.CashBookRow
import com.example.model.JournalRow
import com.example.model.PartnersCapitalRow
import com.example.model.PettyCashRow
import com.example.model.TrialBalanceRow
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

private data class TwoSidedAccountBalancingInfo(
    val leftLabel: String,
    val rightLabel: String,
    val balLabel: String,
    val balColor: Color
)

// Generic Two-Sided T-Account Grid (Trading, P&L, Manufacturing, Revaluation, Realisation, Consignment)
@Composable
fun TwoSidedAccountGrid(
    tableType: AccountingTableType,
    drRows: List<AccountRow>,
    crRows: List<AccountRow>,
    onUpdateDr: (Int, String, String) -> Unit,
    onUpdateCr: (Int, String, String) -> Unit,
    onAddDr: () -> Unit,
    onAddCr: () -> Unit,
    onDeleteDr: (Int) -> Unit,
    onDeleteCr: (Int) -> Unit,
    customLeftHeader: String? = null,
    customRightHeader: String? = null
) {
    val scrollState = rememberScrollState()
    val totalDr = drRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalCr = crRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val diff = totalCr - totalDr

    val balancingInfo = when (tableType) {
        AccountingTableType.CUSTOM_FORMAT -> {
            val isProfit = diff >= 0
            TwoSidedAccountBalancingInfo(
                leftLabel = customLeftHeader?.takeIf { it.isNotBlank() } ?: "Dr. (Particulars)",
                rightLabel = customRightHeader?.takeIf { it.isNotBlank() } ?: "Cr. (Particulars)",
                balLabel = if (isProfit) "Balancing Figure: ₹%,.2f".format(diff) else "Balancing Figure: ₹%,.2f".format(-diff),
                balColor = if (isProfit) VintageGreen else VintageRed
            )
        }
        AccountingTableType.BALANCE_SHEET -> {
            val difference = totalCr - totalDr
            val isTallied = difference == 0.0
            TwoSidedAccountBalancingInfo(
                leftLabel = "Liabilities & Capital",
                rightLabel = "Assets & Properties",
                balLabel = if (isTallied) "✓ Balance Sheet Tallied" else "Diff: ₹%,.2f".format(kotlin.math.abs(difference)),
                balColor = if (isTallied) VintageGreen else VintageRed
            )
        }
        AccountingTableType.PROFIT_LOSS_ACCOUNT -> {
            val isProfit = diff >= 0
            TwoSidedAccountBalancingInfo(
                leftLabel = "Dr. (Indirect Expenses / Losses)",
                rightLabel = "Cr. (Indirect Incomes / Gains)",
                balLabel = if (isProfit) "Net Profit: ₹%,.2f".format(diff) else "Net Loss: ₹%,.2f".format(-diff),
                balColor = if (isProfit) VintageGreen else VintageRed
            )
        }
        AccountingTableType.MANUFACTURING_ACCOUNT -> {
            val costOfProduction = totalDr - totalCr
            val isStandard = costOfProduction >= 0
            TwoSidedAccountBalancingInfo(
                leftLabel = "Dr. (Raw Materials & Direct Costs)",
                rightLabel = "Cr. (Scrap & Cost of Production)",
                balLabel = if (isStandard) "Cost of Production: ₹%,.2f".format(costOfProduction) else "Cost of Production (Excess Scrap): ₹%,.2f".format(-costOfProduction),
                balColor = if (isStandard) VintageGreen else VintageRed
            )
        }
        AccountingTableType.REVALUATION_ACCOUNT -> {
            val isProfit = diff >= 0
            TwoSidedAccountBalancingInfo(
                leftLabel = "Dr. (Decrease in Assets / Liab Inc)",
                rightLabel = "Cr. (Increase in Assets / Liab Dec)",
                balLabel = if (isProfit) "Profit on Revaluation: ₹%,.2f".format(diff) else "Loss on Revaluation: ₹%,.2f".format(-diff),
                balColor = if (isProfit) VintageGreen else VintageRed
            )
        }
        AccountingTableType.REALISATION_ACCOUNT -> {
            val isProfit = diff >= 0
            TwoSidedAccountBalancingInfo(
                leftLabel = "Dr. (Sundry Assets & Discharges)",
                rightLabel = "Cr. (Sundry Liab & Realisations)",
                balLabel = if (isProfit) "Profit on Realisation: ₹%,.2f".format(diff) else "Loss on Realisation: ₹%,.2f".format(-diff),
                balColor = if (isProfit) VintageGreen else VintageRed
            )
        }
        AccountingTableType.CONSIGNMENT_ACCOUNT -> {
            val isProfit = diff >= 0
            TwoSidedAccountBalancingInfo(
                leftLabel = "Dr. (Goods Sent, Freight, Comm)",
                rightLabel = "Cr. (Gross Sales & Stock c/d)",
                balLabel = if (isProfit) "Profit on Consignment: ₹%,.2f".format(diff) else "Loss on Consignment: ₹%,.2f".format(-diff),
                balColor = if (isProfit) VintageGreen else VintageRed
            )
        }
        else -> {
            val isProfit = diff >= 0
            TwoSidedAccountBalancingInfo(
                leftLabel = "Dr. (Particulars / Cost of Goods)",
                rightLabel = "Cr. (Particulars / Sales & Stock)",
                balLabel = if (isProfit) "Gross Profit: ₹%,.2f".format(diff) else "Gross Loss: ₹%,.2f".format(-diff),
                balColor = if (isProfit) VintageGreen else VintageRed
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(4.dp)
            .testTag("two_sided_account_grid")
    ) {
        Row(
            modifier = Modifier
                .width(680.dp)
                .background(VintageNavy)
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = balancingInfo.leftLabel,
                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            )
            Text(
                text = balancingInfo.rightLabel,
                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            )
        }

        Row(modifier = Modifier.width(680.dp)) {
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
                            value = row.particulars,
                            onValueChange = { onUpdateDr(index, it, row.amount) },
                            textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 12.sp, color = VintageInk),
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = row.amount,
                            onValueChange = { onUpdateDr(index, row.particulars, it) },
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
                            value = row.particulars,
                            onValueChange = { onUpdateCr(index, it, row.amount) },
                            textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 12.sp, color = VintageInk),
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = row.amount,
                            onValueChange = { onUpdateCr(index, row.particulars, it) },
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
                .width(680.dp)
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
                    text = balancingInfo.balLabel,
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = balancingInfo.balColor)
                )
                Text(
                    text = "Total Cr: ₹%,.2f".format(totalCr),
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                )
            }
        }
    }
}

// Journal Entry Register Grid
@Composable
fun JournalEntryGrid(
    journalRows: List<JournalRow>,
    onUpdateRow: (Int, String, String, String, String, String, String) -> Unit,
    onAddRow: () -> Unit,
    onDeleteRow: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalDebit = journalRows.sumOf { it.debit.toDoubleOrNull() ?: 0.0 }
    val totalCredit = journalRows.sumOf { it.credit.toDoubleOrNull() ?: 0.0 }
    val isTallied = kotlin.math.abs(totalDebit - totalCredit) < 0.01 && totalDebit > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(4.dp)
            .testTag("journal_entry_grid")
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .width(680.dp)
                .background(VintageNavy)
                .padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Date", modifier = Modifier.width(65.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Particulars & Narration", modifier = Modifier.weight(1f), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("L.F.", modifier = Modifier.width(40.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Debit (₹)", modifier = Modifier.width(85.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Credit (₹)", modifier = Modifier.width(85.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Act", modifier = Modifier.width(30.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
        }

        // Table Rows
        Column(
            modifier = Modifier
                .width(680.dp)
                .border(1.dp, VintageLine)
                .background(VintagePaperSheet)
        ) {
            journalRows.forEachIndexed { index, row ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, VintageLine.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = row.date,
                            onValueChange = { onUpdateRow(index, it, row.particulars, row.lf, row.debit, row.credit, row.narration) },
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageInk),
                            modifier = Modifier
                                .width(65.dp)
                                .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .padding(3.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = row.particulars,
                            onValueChange = { onUpdateRow(index, row.date, it, row.lf, row.debit, row.credit, row.narration) },
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VintageInk),
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .padding(3.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = row.lf,
                            onValueChange = { onUpdateRow(index, row.date, row.particulars, it, row.debit, row.credit, row.narration) },
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageNavy),
                            modifier = Modifier
                                .width(40.dp)
                                .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .padding(3.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = row.debit,
                            onValueChange = { onUpdateRow(index, row.date, row.particulars, row.lf, it, row.credit, row.narration) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy),
                            modifier = Modifier
                                .width(85.dp)
                                .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .padding(3.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = row.credit,
                            onValueChange = { onUpdateRow(index, row.date, row.particulars, row.lf, row.debit, it, row.narration) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy),
                            modifier = Modifier
                                .width(85.dp)
                                .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .padding(3.dp)
                        )
                        IconButton(onClick = { onDeleteRow(index) }, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete entry", tint = VintageRed, modifier = Modifier.size(15.dp))
                        }
                    }

                    // Narration input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, start = 69.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Narration: ",
                            style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 10.5.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = VintageInkSoft)
                        )
                        BasicTextField(
                            value = row.narration,
                            onValueChange = { onUpdateRow(index, row.date, row.particulars, row.lf, row.debit, row.credit, it) },
                            textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = VintageInkSoft),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, VintageLine.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                                .padding(2.dp)
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = onAddRow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Journal Entry Row", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.5.sp))
            }
        }

        // Summary Bar & Tally Status
        Surface(
            modifier = Modifier
                .width(680.dp)
                .border(1.dp, VintageLine),
            color = if (isTallied) VintageGreen.copy(alpha = 0.12f) else VintagePaperBg
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Debits: ₹%,.2f".format(totalDebit),
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                )
                Text(
                    text = if (isTallied) "✓ Debits Equal Credits (Tallied)" else "⚠ Difference: ₹%,.2f".format(kotlin.math.abs(totalDebit - totalCredit)),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTallied) VintageGreen else VintageRed
                    )
                )
                Text(
                    text = "Total Credits: ₹%,.2f".format(totalCredit),
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                )
            }
        }
    }
}

// Trial Balance Grid
@Composable
fun TrialBalanceGrid(
    trialBalanceRows: List<TrialBalanceRow>,
    onUpdateRow: (Int, String, String, String, String, String) -> Unit,
    onAddRow: () -> Unit,
    onDeleteRow: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalDebit = trialBalanceRows.sumOf { it.debit.toDoubleOrNull() ?: 0.0 }
    val totalCredit = trialBalanceRows.sumOf { it.credit.toDoubleOrNull() ?: 0.0 }
    val isTallied = kotlin.math.abs(totalDebit - totalCredit) < 0.01 && totalDebit > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(4.dp)
            .testTag("trial_balance_grid")
    ) {
        Row(
            modifier = Modifier
                .width(680.dp)
                .background(VintageNavy)
                .padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("S.No", modifier = Modifier.width(40.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Heads of Account", modifier = Modifier.weight(1f), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("L.F.", modifier = Modifier.width(40.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Debit Balance (₹)", modifier = Modifier.width(100.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Credit Balance (₹)", modifier = Modifier.width(100.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Act", modifier = Modifier.width(30.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
        }

        Column(
            modifier = Modifier
                .width(680.dp)
                .border(1.dp, VintageLine)
                .background(VintagePaperSheet)
        ) {
            trialBalanceRows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, VintageLine.copy(alpha = 0.4f))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = row.sNo,
                        onValueChange = { onUpdateRow(index, it, row.headOfAccount, row.lf, row.debit, row.credit) },
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageInk),
                        modifier = Modifier
                            .width(40.dp)
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .padding(3.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(
                        value = row.headOfAccount,
                        onValueChange = { onUpdateRow(index, row.sNo, it, row.lf, row.debit, row.credit) },
                        textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 12.5.sp, color = VintageInk),
                        modifier = Modifier
                            .weight(1f)
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .padding(3.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(
                        value = row.lf,
                        onValueChange = { onUpdateRow(index, row.sNo, row.headOfAccount, it, row.debit, row.credit) },
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = VintageNavy),
                        modifier = Modifier
                            .width(40.dp)
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .padding(3.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(
                        value = row.debit,
                        onValueChange = { onUpdateRow(index, row.sNo, row.headOfAccount, row.lf, it, row.credit) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy),
                        modifier = Modifier
                            .width(100.dp)
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .padding(3.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(
                        value = row.credit,
                        onValueChange = { onUpdateRow(index, row.sNo, row.headOfAccount, row.lf, row.debit, it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy),
                        modifier = Modifier
                            .width(100.dp)
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .padding(3.dp)
                    )
                    IconButton(onClick = { onDeleteRow(index) }, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete row", tint = VintageRed, modifier = Modifier.size(15.dp))
                    }
                }
            }

            OutlinedButton(
                onClick = onAddRow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Trial Balance Head", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.5.sp))
            }
        }

        Surface(
            modifier = Modifier
                .width(680.dp)
                .border(1.dp, VintageLine),
            color = if (isTallied) VintageGreen.copy(alpha = 0.12f) else VintagePaperBg
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Debits: ₹%,.2f".format(totalDebit), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                Text(
                    text = if (isTallied) "✓ Trial Balance Tallied" else "⚠ Difference: ₹%,.2f".format(kotlin.math.abs(totalDebit - totalCredit)),
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isTallied) VintageGreen else VintageRed)
                )
                Text("Total Credits: ₹%,.2f".format(totalCredit), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
            }
        }
    }
}

// Bank Reconciliation Statement (BRS) Grid
@Composable
fun BrsGrid(
    startingBalance: String,
    onUpdateStartingBalance: (String) -> Unit,
    brsRows: List<BrsRow>,
    onUpdateRow: (Int, String, Boolean, String) -> Unit,
    onAddRow: (Boolean) -> Unit,
    onDeleteRow: (Int) -> Unit
) {
    val startAmt = startingBalance.toDoubleOrNull() ?: 0.0
    val totalAdd = brsRows.filter { it.isAdd }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalLess = brsRows.filter { !it.isAdd }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val passBookBalance = startAmt + totalAdd - totalLess

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .testTag("brs_grid")
    ) {
        // Starting Balance Input
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VintageNavy, RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp),
            color = VintagePaperSheet
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Balance as per Cash Book (Favourable/Dr):",
                    style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VintageInk)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("₹ ", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                    BasicTextField(
                        value = startingBalance,
                        onValueChange = onUpdateStartingBalance,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VintageNavy),
                        modifier = Modifier
                            .width(110.dp)
                            .border(1.dp, VintageLine, RoundedCornerShape(3.dp))
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reconciliation Items
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VintageLine, RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp),
            color = VintagePaperBg
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "ADD / LESS RECONCILING ITEMS",
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                )
                Spacer(modifier = Modifier.height(6.dp))

                brsRows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = if (row.isAdd) VintageGreen.copy(alpha = 0.15f) else VintageRed.copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = if (row.isAdd) "ADD (+)" else "LESS (-)",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (row.isAdd) VintageGreen else VintageRed
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        BasicTextField(
                            value = row.particulars,
                            onValueChange = { onUpdateRow(index, it, row.isAdd, row.amount) },
                            textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 12.sp, color = VintageInk),
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, VintageLine, RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        BasicTextField(
                            value = row.amount,
                            onValueChange = { onUpdateRow(index, row.particulars, row.isAdd, it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (row.isAdd) VintageGreen else VintageRed),
                            modifier = Modifier
                                .width(90.dp)
                                .border(0.5.dp, VintageLine, RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        )

                        IconButton(onClick = { onDeleteRow(index) }, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete item", tint = VintageRed, modifier = Modifier.size(15.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAddRow(true) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Addition Item", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp))
                    }
                    OutlinedButton(
                        onClick = { onAddRow(false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageRed)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("- Add Deduction Item", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Computed Balance as per Pass Book
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, VintageGreen, RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp),
            color = VintageGreen.copy(alpha = 0.08f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Balance as per Bank Pass Book",
                        style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                    )
                    Text(
                        text = "Starting ₹%,.0f + Additions ₹%,.0f - Deductions ₹%,.0f".format(startAmt, totalAdd, totalLess),
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = VintageInkSoft)
                    )
                }
                Text(
                    text = "₹ %,.2f".format(passBookBalance),
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VintageGreen)
                )
            }
        }
    }
}

// Three-Column Cash Book Grid
@Composable
fun CashBookGrid(
    cashBookDrRows: List<CashBookRow>,
    cashBookCrRows: List<CashBookRow>,
    onUpdateDr: (Int, String, String, String, String, String, String) -> Unit,
    onUpdateCr: (Int, String, String, String, String, String, String) -> Unit,
    onAddDr: () -> Unit,
    onAddCr: () -> Unit,
    onDeleteDr: (Int) -> Unit,
    onDeleteCr: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalCashDr = cashBookDrRows.sumOf { it.cash.toDoubleOrNull() ?: 0.0 }
    val totalBankDr = cashBookDrRows.sumOf { it.bank.toDoubleOrNull() ?: 0.0 }
    val totalCashCr = cashBookCrRows.sumOf { it.cash.toDoubleOrNull() ?: 0.0 }
    val totalBankCr = cashBookCrRows.sumOf { it.bank.toDoubleOrNull() ?: 0.0 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(4.dp)
            .testTag("cash_book_grid")
    ) {
        Row(
            modifier = Modifier
                .width(820.dp)
                .background(VintageNavy)
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Receipts (Dr Side) - Date, Particulars, Disc, Cash, Bank", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Payments (Cr Side) - Date, Particulars, Disc, Cash, Bank", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
        }

        Row(modifier = Modifier.width(820.dp)) {
            // Dr Side Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, VintageLine)
                    .background(VintagePaperSheet)
                    .padding(4.dp)
            ) {
                cashBookDrRows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(value = row.date, onValueChange = { onUpdateDr(index, it, row.particulars, row.vn, row.discount, row.cash, row.bank) }, modifier = Modifier.width(45.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.particulars, onValueChange = { onUpdateDr(index, row.date, it, row.vn, row.discount, row.cash, row.bank) }, modifier = Modifier.weight(1f).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 11.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.discount, onValueChange = { onUpdateDr(index, row.date, row.particulars, row.vn, it, row.cash, row.bank) }, modifier = Modifier.width(40.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.cash, onValueChange = { onUpdateDr(index, row.date, row.particulars, row.vn, row.discount, it, row.bank) }, modifier = Modifier.width(55.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.bank, onValueChange = { onUpdateDr(index, row.date, row.particulars, row.vn, row.discount, row.cash, it) }, modifier = Modifier.width(55.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = VintageGreen))
                        IconButton(onClick = { onDeleteDr(index) }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = VintageRed, modifier = Modifier.size(13.dp))
                        }
                    }
                }
                OutlinedButton(onClick = onAddDr, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(2.dp)) {
                    Text("+ Add Receipt", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp))
                }
            }

            // Cr Side Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, VintageLine)
                    .background(VintagePaperSheet)
                    .padding(4.dp)
            ) {
                cashBookCrRows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(value = row.date, onValueChange = { onUpdateCr(index, it, row.particulars, row.vn, row.discount, row.cash, row.bank) }, modifier = Modifier.width(45.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.particulars, onValueChange = { onUpdateCr(index, row.date, it, row.vn, row.discount, row.cash, row.bank) }, modifier = Modifier.weight(1f).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 11.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.discount, onValueChange = { onUpdateCr(index, row.date, row.particulars, row.vn, it, row.cash, row.bank) }, modifier = Modifier.width(40.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.cash, onValueChange = { onUpdateCr(index, row.date, row.particulars, row.vn, row.discount, it, row.bank) }, modifier = Modifier.width(55.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.bank, onValueChange = { onUpdateCr(index, row.date, row.particulars, row.vn, row.discount, row.cash, it) }, modifier = Modifier.width(55.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = VintageGreen))
                        IconButton(onClick = { onDeleteCr(index) }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = VintageRed, modifier = Modifier.size(13.dp))
                        }
                    }
                }
                OutlinedButton(onClick = onAddCr, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(2.dp)) {
                    Text("+ Add Payment", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp))
                }
            }
        }

        // Totals Footer
        Surface(modifier = Modifier.width(820.dp).border(1.dp, VintageLine), color = VintageNavy.copy(alpha = 0.08f)) {
            Row(modifier = Modifier.fillMaxWidth().padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dr Cash: ₹%,.0f | Dr Bank: ₹%,.0f".format(totalCashDr, totalBankDr), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                Text("Cr Cash: ₹%,.0f | Cr Bank: ₹%,.0f".format(totalCashCr, totalBankCr), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
            }
        }
    }
}

// Partners' Capital Accounts Grid
@Composable
fun PartnersCapitalGrid(
    drRows: List<PartnersCapitalRow>,
    crRows: List<PartnersCapitalRow>,
    onUpdateDr: (Int, String, String, String, String) -> Unit,
    onUpdateCr: (Int, String, String, String, String) -> Unit,
    onAddDr: () -> Unit,
    onAddCr: () -> Unit,
    onDeleteDr: (Int) -> Unit,
    onDeleteCr: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalDrA = drRows.sumOf { it.partnerA.toDoubleOrNull() ?: 0.0 }
    val totalDrB = drRows.sumOf { it.partnerB.toDoubleOrNull() ?: 0.0 }
    val totalCrA = crRows.sumOf { it.partnerA.toDoubleOrNull() ?: 0.0 }
    val totalCrB = crRows.sumOf { it.partnerB.toDoubleOrNull() ?: 0.0 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(4.dp)
            .testTag("partners_capital_grid")
    ) {
        Row(
            modifier = Modifier
                .width(760.dp)
                .background(VintageNavy)
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dr Side: Date | Particulars | Partner A (₹) | Partner B (₹)", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Cr Side: Date | Particulars | Partner A (₹) | Partner B (₹)", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
        }

        Row(modifier = Modifier.width(760.dp)) {
            // Dr Side
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, VintageLine)
                    .background(VintagePaperSheet)
                    .padding(4.dp)
            ) {
                drRows.forEachIndexed { index, row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(value = row.date, onValueChange = { onUpdateDr(index, it, row.particulars, row.partnerA, row.partnerB) }, modifier = Modifier.width(45.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.particulars, onValueChange = { onUpdateDr(index, row.date, it, row.partnerA, row.partnerB) }, modifier = Modifier.weight(1f).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 11.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.partnerA, onValueChange = { onUpdateDr(index, row.date, row.particulars, it, row.partnerB) }, modifier = Modifier.width(60.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.partnerB, onValueChange = { onUpdateDr(index, row.date, row.particulars, row.partnerA, it) }, modifier = Modifier.width(60.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        IconButton(onClick = { onDeleteDr(index) }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = VintageRed, modifier = Modifier.size(13.dp))
                        }
                    }
                }
                OutlinedButton(onClick = onAddDr, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(2.dp)) {
                    Text("+ Add Dr Posting", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp))
                }
            }

            // Cr Side
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, VintageLine)
                    .background(VintagePaperSheet)
                    .padding(4.dp)
            ) {
                crRows.forEachIndexed { index, row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(value = row.date, onValueChange = { onUpdateCr(index, it, row.particulars, row.partnerA, row.partnerB) }, modifier = Modifier.width(45.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.particulars, onValueChange = { onUpdateCr(index, row.date, it, row.partnerA, row.partnerB) }, modifier = Modifier.weight(1f).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 11.sp))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.partnerA, onValueChange = { onUpdateCr(index, row.date, row.particulars, it, row.partnerB) }, modifier = Modifier.width(60.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(value = row.partnerB, onValueChange = { onUpdateCr(index, row.date, row.particulars, row.partnerA, it) }, modifier = Modifier.width(60.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        IconButton(onClick = { onDeleteCr(index) }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = VintageRed, modifier = Modifier.size(13.dp))
                        }
                    }
                }
                OutlinedButton(onClick = onAddCr, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(2.dp)) {
                    Text("+ Add Cr Posting", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp))
                }
            }
        }

        Surface(modifier = Modifier.width(760.dp).border(1.dp, VintageLine), color = VintageNavy.copy(alpha = 0.08f)) {
            Row(modifier = Modifier.fillMaxWidth().padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dr: A ₹%,.0f | B ₹%,.0f".format(totalDrA, totalDrB), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                Text("Cr: A ₹%,.0f | B ₹%,.0f".format(totalCrA, totalCrB), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
            }
        }
    }
}

// Analytical Petty Cash Book Grid
@Composable
fun PettyCashGrid(
    pettyCashRows: List<PettyCashRow>,
    onUpdateRow: (Int, String, String, String, String, String, String, String, String) -> Unit,
    onAddRow: () -> Unit,
    onDeleteRow: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalReceipts = pettyCashRows.sumOf { it.receipts.toDoubleOrNull() ?: 0.0 }
    val totalPayments = pettyCashRows.sumOf { it.totalPayment.toDoubleOrNull() ?: 0.0 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(4.dp)
            .testTag("petty_cash_grid")
    ) {
        Row(
            modifier = Modifier
                .width(760.dp)
                .background(VintageNavy)
                .padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Receipt (₹)", modifier = Modifier.width(75.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Date", modifier = Modifier.width(50.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Particulars", modifier = Modifier.weight(1f), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Total Pay", modifier = Modifier.width(70.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Conveyance", modifier = Modifier.width(75.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Cartage", modifier = Modifier.width(65.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Stationery", modifier = Modifier.width(75.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Misc", modifier = Modifier.width(60.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text("Act", modifier = Modifier.width(28.dp), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White))
        }

        Column(
            modifier = Modifier
                .width(760.dp)
                .border(1.dp, VintageLine)
                .background(VintagePaperSheet)
        ) {
            pettyCashRows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, VintageLine.copy(alpha = 0.4f))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(value = row.receipts, onValueChange = { onUpdateRow(index, it, row.date, row.particulars, row.totalPayment, row.conveyance, row.cartage, row.stationery, row.misc) }, modifier = Modifier.width(75.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, color = VintageGreen, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicTextField(value = row.date, onValueChange = { onUpdateRow(index, row.receipts, it, row.particulars, row.totalPayment, row.conveyance, row.cartage, row.stationery, row.misc) }, modifier = Modifier.width(50.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicTextField(value = row.particulars, onValueChange = { onUpdateRow(index, row.receipts, row.date, it, row.totalPayment, row.conveyance, row.cartage, row.stationery, row.misc) }, modifier = Modifier.weight(1f).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 11.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicTextField(value = row.totalPayment, onValueChange = { onUpdateRow(index, row.receipts, row.date, row.particulars, it, row.conveyance, row.cartage, row.stationery, row.misc) }, modifier = Modifier.width(70.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp, color = VintageNavy, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicTextField(value = row.conveyance, onValueChange = { onUpdateRow(index, row.receipts, row.date, row.particulars, row.totalPayment, it, row.cartage, row.stationery, row.misc) }, modifier = Modifier.width(75.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicTextField(value = row.cartage, onValueChange = { onUpdateRow(index, row.receipts, row.date, row.particulars, row.totalPayment, row.conveyance, it, row.stationery, row.misc) }, modifier = Modifier.width(65.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicTextField(value = row.stationery, onValueChange = { onUpdateRow(index, row.receipts, row.date, row.particulars, row.totalPayment, row.conveyance, row.cartage, it, row.misc) }, modifier = Modifier.width(75.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicTextField(value = row.misc, onValueChange = { onUpdateRow(index, row.receipts, row.date, row.particulars, row.totalPayment, row.conveyance, row.cartage, row.stationery, it) }, modifier = Modifier.width(60.dp).border(0.5.dp, VintageLine).padding(2.dp), textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp))
                    IconButton(onClick = { onDeleteRow(index) }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = VintageRed, modifier = Modifier.size(13.dp))
                    }
                }
            }

            OutlinedButton(onClick = onAddRow, modifier = Modifier.fillMaxWidth().padding(4.dp), shape = RoundedCornerShape(2.dp)) {
                Text("+ Add Petty Cash Payment Row", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp))
            }
        }

        Surface(modifier = Modifier.width(760.dp).border(1.dp, VintageLine), color = VintageNavy.copy(alpha = 0.08f)) {
            Row(modifier = Modifier.fillMaxWidth().padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Imprest Receipts: ₹%,.2f".format(totalReceipts), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageGreen))
                Text("Total Petty Payments: ₹%,.2f".format(totalPayments), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                Text("Closing Imprest Balance: ₹%,.2f".format(totalReceipts - totalPayments), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
            }
        }
    }
}
