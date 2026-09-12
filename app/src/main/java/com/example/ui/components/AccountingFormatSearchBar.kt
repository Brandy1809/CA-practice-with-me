package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AccountingTableType
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

@Composable
fun AccountingFormatSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: (Boolean) -> Unit,
    onSelectFormat: (AccountingTableType, String?, String?, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allFormats = remember { AccountingTableType.ALL_FORMATS }
    
    var showCustomCreator by remember { mutableStateOf(false) }
    var customTitle by remember { mutableStateOf("") }
    var customLeftHeader by remember { mutableStateOf("") }
    var customRightHeader by remember { mutableStateOf("") }

    val filteredFormats = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) {
            allFormats
        } else {
            allFormats.filter { format ->
                format.title.lowercase().contains(q) ||
                format.command.lowercase().contains(q) ||
                format.description.lowercase().contains(q) ||
                format.category.lowercase().contains(q) ||
                when {
                    q in listOf("pl", "p&l", "profit", "loss", "labh") -> format == AccountingTableType.PROFIT_LOSS_ACCOUNT
                    q in listOf("trading", "vyapar") -> format == AccountingTableType.TRADING_ACCOUNT
                    q in listOf("bs", "balance", "sheet", "chittha") -> format == AccountingTableType.BALANCE_SHEET
                    q in listOf("journal", "roznama", "entry") -> format == AccountingTableType.JOURNAL
                    q in listOf("ledger", "khata", "t-account") -> format == AccountingTableType.LEDGER
                    q in listOf("trial", "balance", "talpat", "tb") -> format == AccountingTableType.TRIAL_BALANCE
                    q in listOf("cash", "rokad", "bank") -> format == AccountingTableType.CASH_BOOK
                    q in listOf("brs", "passbook", "reconciliation") -> format == AccountingTableType.BRS
                    q in listOf("partner", "capital", "punji") -> format == AccountingTableType.PARTNERS_CAPITAL
                    q in listOf("reval", "revaluation") -> format == AccountingTableType.REVALUATION_ACCOUNT
                    q in listOf("realisation", "dissolution") -> format == AccountingTableType.REALISATION_ACCOUNT
                    q in listOf("consignment", "chalani") -> format == AccountingTableType.CONSIGNMENT_ACCOUNT
                    q in listOf("petty", "imprest") -> format == AccountingTableType.PETTY_CASH_BOOK
                    q in listOf("mfg", "manufacturing", "nirman") -> format == AccountingTableType.MANUFACTURING_ACCOUNT
                    q in listOf("working", "note", "notes", "wn", "workingnote", "workingnotes", "calc", "calculation") -> format == AccountingTableType.WORKING_NOTE
                    else -> false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("accounting_format_search_container")
    ) {
        // Closed / Compact Search Bar Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VintageNavy.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
            shape = RoundedCornerShape(6.dp),
            color = VintagePaperSheet,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search accounting formats",
                    tint = VintageNavy,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.5.sp,
                            color = VintageInk
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search /Accounts formats (e.g. Journal, BRS, P&L)...",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.5.sp,
                                        color = VintageInkSoft.copy(alpha = 0.7f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Dropdown trigger pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VintageNavy.copy(alpha = 0.1f))
                        .clickable { onToggleExpanded(true) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("toggle_format_dropdown_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "${allFormats.size} Formats",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VintageNavy
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand formats",
                            tint = VintageNavy,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Inline Search Results Dropdown
        if (searchQuery.isNotEmpty() && !isExpanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .border(1.dp, VintageNavy.copy(alpha = 0.2f), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)),
                color = VintagePaperBg,
                shadowElevation = 4.dp
            ) {
                androidx.compose.foundation.lazy.LazyColumn {
                    items(filteredFormats.size) { index ->
                        val format = filteredFormats[index]
                        if (format != AccountingTableType.CUSTOM_FORMAT) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectFormat(format, null, null, null)
                                        onSearchQueryChange("")
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = VintageNavy.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = format.title,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = VintageInk
                                    )
                                )
                            }
                            if (index < filteredFormats.size - 1) {
                                androidx.compose.material3.Divider(color = VintageLine.copy(alpha = 0.3f), thickness = 1.dp)
                            }
                        }
                    }
                    if (filteredFormats.isEmpty() || (filteredFormats.size == 1 && filteredFormats.first() == AccountingTableType.CUSTOM_FORMAT)) {
                        item {
                            Text(
                                "No matching formats found.",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = VintageInkSoft),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Full-Screen Dialog for Browsing Formats
        if (isExpanded) {
            Dialog(
                onDismissRequest = { 
                    onToggleExpanded(false)
                    showCustomCreator = false 
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VintagePaperBg
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Browse Formats",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VintageNavyDeep
                                )
                            )
                            IconButton(onClick = { onToggleExpanded(false) }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = VintageNavy)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Custom Format Creator View
                        if (showCustomCreator) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, VintageNavy, RoundedCornerShape(8.dp)),
                                shape = RoundedCornerShape(8.dp),
                                color = VintagePaperSheet
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Custom Format Creator", style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VintageNavy))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Text("Custom Format Title", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = VintageInk))
                                    BasicTextField(
                                        value = customTitle,
                                        onValueChange = { customTitle = it },
                                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = VintageInk),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp, bottom = 12.dp)
                                            .border(1.dp, VintageLine, RoundedCornerShape(4.dp))
                                            .padding(8.dp)
                                    )
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Left Column Header (Dr)", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = VintageInk))
                                            BasicTextField(
                                                value = customLeftHeader,
                                                onValueChange = { customLeftHeader = it },
                                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = VintageInk),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp)
                                                    .border(1.dp, VintageLine, RoundedCornerShape(4.dp))
                                                    .padding(8.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Right Column Header (Cr)", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = VintageInk))
                                            BasicTextField(
                                                value = customRightHeader,
                                                onValueChange = { customRightHeader = it },
                                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = VintageInk),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp)
                                                    .border(1.dp, VintageLine, RoundedCornerShape(4.dp))
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { 
                                                onSelectFormat(AccountingTableType.CUSTOM_FORMAT, customTitle.ifBlank { "Custom Format" }, customLeftHeader.ifBlank { "Dr. (Particulars)" }, customRightHeader.ifBlank { "Cr. (Particulars)" })
                                                onToggleExpanded(false)
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                                        ) {
                                            Text("Insert Custom Format", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold))
                                        }
                                        OutlinedButton(
                                            onClick = { showCustomCreator = false },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageRed)
                                        ) {
                                            Text("Cancel", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, VintageLine, RoundedCornerShape(8.dp))
                                    .clickable { showCustomCreator = true },
                                shape = RoundedCornerShape(8.dp),
                                color = VintageGoldBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Create Custom", tint = VintageNavy)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "+ Create Custom Format",
                                        style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VintageNavy)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Search Bar
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, VintageNavy.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                            shape = RoundedCornerShape(6.dp),
                            color = VintagePaperSheet
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = VintageNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search /Accounts formats...",
                                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.5.sp, color = VintageInkSoft.copy(alpha = 0.7f))
                                        )
                                    }
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = onSearchQueryChange,
                                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VintageInk),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("format_search_text_input_fullscreen")
                                    )
                                }
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onSearchQueryChange("") },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = VintageInkSoft, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Results List
                        if (filteredFormats.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matching accounting format found for \"$searchQuery\"",
                                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = VintageInkSoft)
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredFormats, key = { it.name }) { format ->
                                    if (format != AccountingTableType.CUSTOM_FORMAT) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = VintageLine.copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    onSelectFormat(format, null, null, null)
                                                    onSearchQueryChange("")
                                                    onToggleExpanded(false)
                                                },
                                            shape = RoundedCornerShape(8.dp),
                                            color = VintagePaperSheet
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.Top,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                                    Text(
                                                        text = format.title,
                                                        style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VintageNavyDeep)
                                                    )
                                                    if (format.description.isNotBlank()) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = format.description,
                                                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.5.sp, color = VintageInkSoft)
                                                        )
                                                    }
                                                }
                                                Column(
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(VintageGold.copy(alpha = 0.18f))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = format.category,
                                                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VintageGold),
                                                            maxLines = 1
                                                        )
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(VintageNavy.copy(alpha = 0.12f))
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = format.command,
                                                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = VintageNavy),
                                                            maxLines = 1
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
