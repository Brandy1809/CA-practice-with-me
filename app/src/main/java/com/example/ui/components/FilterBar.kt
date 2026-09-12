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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuestionRepository
import com.example.model.Depth
import com.example.model.Subject
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
fun ExamPaperHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.dp, color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CA Foundation — Practice Copy",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = VintageInk
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Subjective for Law and Accounting. Objective for Economics and Quant Aptitude.",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            color = VintageInkSoft,
                            lineHeight = 15.sp
                        )
                    )
                }

                // Rotated Vintage Stamp
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, start = 8.dp)
                        .rotate(8f)
                        .border(
                            border = BorderStroke(1.5.dp, VintageNavy.copy(alpha = 0.75f)),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .background(VintagePaperSheet.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PRACTICE COPY",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.8.sp,
                            color = VintageNavy.copy(alpha = 0.85f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Thick Ink Line Divider under header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(VintageInk)
            )
        }
    }
}

@Composable
fun FilterBar(
    selectedSubject: Subject,
    onSubjectChange: (Subject) -> Unit,
    availableChapters: List<String>,
    selectedChapter: String,
    onChapterChange: (String) -> Unit,
    selectedDepth: Depth,
    onDepthChange: (Depth) -> Unit,
    questionCount: Int,
    onOpenSubjectDrillDown: (Subject) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var depthMenuExpanded by remember { mutableStateOf(false) }
    var chapterMenuExpanded by remember { mutableStateOf(false) }
    var unitMenuExpanded by remember { mutableStateOf(false) }

    val chapters = remember(selectedSubject, availableChapters) {
        if (selectedSubject != Subject.ALL) {
            val repoChapters = QuestionRepository.getChaptersForSubject(selectedSubject)
            if (repoChapters.isNotEmpty()) repoChapters else availableChapters
        } else {
            emptyList()
        }
    }

    val unitsForSelectedChapter = remember(selectedSubject, selectedChapter) {
        if (selectedSubject != Subject.ALL && selectedChapter != "all") {
            QuestionRepository.getUnitsForChapter(selectedSubject, selectedChapter)
        } else {
            emptyList()
        }
    }
    val hasUnits = unitsForSelectedChapter.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Kicker
        Text(
            text = "SUBJECT SELECTOR",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = VintageNavy,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // "All Subjects" Full-Width Tile
        val isAllSelected = selectedSubject == Subject.ALL
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSubjectChange(Subject.ALL)
                    onChapterChange("all")
                    onDepthChange(Depth.ALL)
                }
                .testTag("subject_tile_all"),
            shape = RoundedCornerShape(3.dp),
            color = if (isAllSelected) VintageGoldBg else VintagePaperSheet,
            border = BorderStroke(
                if (isAllSelected) 1.5.dp else 1.dp,
                if (isAllSelected) VintageGold else VintageLine
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isAllSelected) VintageGold else VintageNavy,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "All Subjects (Mixed Exam Mode)",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                            color = VintageInk
                        )
                    )
                }

                if (isAllSelected) {
                    Surface(
                        shape = CircleShape,
                        color = VintageGreen,
                        modifier = Modifier.size(15.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4 Core Subject Tiles: 2x2 Grid
        val subjects = listOf(
            Triple(Subject.ACC, "Accounting", "Paper 1 • Subj"),
            Triple(Subject.LAW, "Business Law", "Paper 2 • Subj"),
            Triple(Subject.QUANT, "Quant Aptitude", "Paper 3 • MCQ"),
            Triple(Subject.ECO, "Economics", "Paper 4 • MCQ")
        )

        val handleSubjectTileClick = { targetSubject: Subject ->
            if (selectedSubject != targetSubject) {
                onSubjectChange(targetSubject)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SubjectTile(
                subject = subjects[0].first,
                title = subjects[0].second,
                badge = subjects[0].third,
                isSelected = selectedSubject == subjects[0].first,
                onClick = { handleSubjectTileClick(subjects[0].first) },
                modifier = Modifier.weight(1f)
            )
            SubjectTile(
                subject = subjects[1].first,
                title = subjects[1].second,
                badge = subjects[1].third,
                isSelected = selectedSubject == subjects[1].first,
                onClick = { handleSubjectTileClick(subjects[1].first) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SubjectTile(
                subject = subjects[2].first,
                title = subjects[2].second,
                badge = subjects[2].third,
                isSelected = selectedSubject == subjects[2].first,
                onClick = { handleSubjectTileClick(subjects[2].first) },
                modifier = Modifier.weight(1f)
            )
            SubjectTile(
                subject = subjects[3].first,
                title = subjects[3].second,
                badge = subjects[3].third,
                isSelected = selectedSubject == subjects[3].first,
                onClick = { handleSubjectTileClick(subjects[3].first) },
                modifier = Modifier.weight(1f)
            )
        }

        if (selectedSubject != Subject.ALL) {
            // SUBJECT-SPECIFIC SCREEN: Compact Two-Dropdown Selector (Chapter + Unit)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Dropdown 1: Chapter
                Column(modifier = Modifier.weight(1.15f)) {
                    Text(
                        text = "CHAPTER",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VintageNavy,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clickable { chapterMenuExpanded = true }
                                .testTag("chapter_dropdown_trigger"),
                            shape = RoundedCornerShape(3.dp),
                            color = VintagePaperSheet,
                            border = BorderStroke(1.dp, VintageNavy.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val chapterText = if (selectedChapter == "all") "All Chapters (Mixed)" else selectedChapter
                                Text(
                                    text = chapterText,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = VintageInk
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Chapter",
                                    tint = VintageNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = chapterMenuExpanded,
                            onDismissRequest = { chapterMenuExpanded = false },
                            modifier = Modifier
                                .widthIn(min = 280.dp, max = 340.dp)
                                .background(VintagePaperSheet)
                                .testTag("chapter_dropdown_menu")
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "All Chapters (Mixed)",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selectedChapter == "all") VintageNavy else VintageInk
                                            )
                                        )
                                        Text(
                                            text = "Random practice across ${selectedSubject.displayName}",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 10.5.sp,
                                                color = VintageInkSoft
                                            )
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (selectedChapter == "all") {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = VintageGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                onClick = {
                                    onChapterChange("all")
                                    onDepthChange(Depth.ALL)
                                    chapterMenuExpanded = false
                                },
                                modifier = Modifier.testTag("chapter_dropdown_item_all")
                            )

                            HorizontalDivider(color = VintageLine.copy(alpha = 0.5f))

                            chapters.forEach { ch ->
                                val isChSelected = selectedChapter == ch
                                val chUnits = QuestionRepository.getUnitsForChapter(selectedSubject, ch)
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = ch,
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isChSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isChSelected) VintageNavy else VintageInk
                                                )
                                            )
                                            Text(
                                                text = if (chUnits.isNotEmpty()) "${chUnits.size} Units available" else "Chapter-level practice",
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp,
                                                    color = VintageInkSoft
                                                )
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        if (isChSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = VintageGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        if (chUnits.isEmpty()) {
                                            onDepthChange(Depth.CHAPTER)
                                        }
                                        onChapterChange(ch)
                                        chapterMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Dropdown 2: Unit (Enabled when units are available)
                val isUnitEnabled = hasUnits
                Column(modifier = Modifier.weight(0.85f)) {
                    Text(
                        text = "UNIT",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnitEnabled) VintageNavy else VintageInkSoft.copy(alpha = 0.5f),
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clickable(enabled = isUnitEnabled) { unitMenuExpanded = true }
                                .testTag("unit_dropdown_trigger"),
                            shape = RoundedCornerShape(3.dp),
                            color = if (isUnitEnabled) VintagePaperSheet else VintagePaperBg.copy(alpha = 0.6f),
                            border = BorderStroke(
                                1.dp,
                                if (isUnitEnabled) VintageNavy.copy(alpha = 0.6f) else VintageLine.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val unitDisplayText = when {
                                    !hasUnits -> {
                                        if (selectedChapter == "all") "Units (N/A)" else "No Units"
                                    }
                                    selectedDepth == Depth.UNIT -> "Unit Practice"
                                    else -> "All Units"
                                }

                                Text(
                                    text = unitDisplayText,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        fontWeight = if (isUnitEnabled) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isUnitEnabled) VintageInk else VintageInkSoft.copy(alpha = 0.6f)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = if (isUnitEnabled) "Select Unit" else "Units unavailable",
                                    tint = if (isUnitEnabled) VintageNavy else VintageInkSoft.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (hasUnits) {
                            DropdownMenu(
                                expanded = unitMenuExpanded,
                                onDismissRequest = { unitMenuExpanded = false },
                                modifier = Modifier
                                    .widthIn(min = 280.dp, max = 340.dp)
                                    .background(VintagePaperSheet)
                                    .testTag("unit_dropdown_menu")
                            ) {
                                val isAllUnitsSelected = selectedDepth == Depth.CHAPTER || selectedDepth == Depth.ALL
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "All Units in this Chapter",
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAllUnitsSelected) VintageNavy else VintageInk
                                                )
                                            )
                                            Text(
                                                text = "Mixed practice across all units in $selectedChapter",
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = 10.5.sp,
                                                    color = VintageInkSoft
                                                )
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        if (isAllUnitsSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = VintageGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        onDepthChange(Depth.CHAPTER)
                                        unitMenuExpanded = false
                                    },
                                    modifier = Modifier.testTag("unit_dropdown_item_all")
                                )

                                HorizontalDivider(color = VintageLine.copy(alpha = 0.5f))

                                unitsForSelectedChapter.forEach { unitItem ->
                                    val isThisUnitSelected = selectedDepth == Depth.UNIT
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = unitItem,
                                                    style = TextStyle(
                                                        fontFamily = FontFamily.Serif,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isThisUnitSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isThisUnitSelected) VintageNavy else VintageInk
                                                    )
                                                )
                                                Text(
                                                    text = "Unit depth practice",
                                                    style = TextStyle(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 10.sp,
                                                        color = VintageInkSoft
                                                    )
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            if (isThisUnitSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = VintageGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            onDepthChange(Depth.UNIT)
                                            unitMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Active Filter Summary & Count Bar for Subject Screens
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_filter_summary"),
                shape = RoundedCornerShape(3.dp),
                color = VintagePaperBg,
                border = BorderStroke(1.dp, VintageLine)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val chLabel = if (selectedChapter == "all") "All Chapters" else selectedChapter
                    val unitLabel = if (hasUnits && selectedDepth == Depth.UNIT) " • Unit Depth" else if (selectedChapter != "all") " • Chapter Depth" else ""
                    Text(
                        text = "Scope: ${selectedSubject.displayName} › $chLabel$unitLabel",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VintageInk
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$questionCount ${if (questionCount == 1) "question" else "questions"}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = VintageInkSoft
                        )
                    )
                }
            }
        } else {
            // HOME SCREEN FLOW ("All Subjects (Mixed Exam Mode)"): Completely Unchanged
            Spacer(modifier = Modifier.height(8.dp))

            // Active Selection Summary Trigger (Scope Row)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_filter_summary"),
                shape = RoundedCornerShape(3.dp),
                color = VintagePaperBg,
                border = BorderStroke(1.dp, VintageLine)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Scope: All 4 Subjects (Mixed Questions)",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VintageInk
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Depth Row & Filter Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Depth:",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            color = VintageInkSoft
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    Box {
                        Surface(
                            modifier = Modifier
                                .clickable { depthMenuExpanded = true }
                                .testTag("depth_filter_dropdown"),
                            shape = RoundedCornerShape(3.dp),
                            color = VintagePaperSheet,
                            border = BorderStroke(1.dp, VintageLine)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedDepth.displayName,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.5.sp,
                                        color = VintageInk
                                    )
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = VintageInkSoft,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = depthMenuExpanded,
                            onDismissRequest = { depthMenuExpanded = false },
                            modifier = Modifier.background(VintagePaperSheet)
                        ) {
                            Depth.values().forEach { d ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            d.displayName,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = if (d == selectedDepth) VintageNavy else VintageInk,
                                            fontWeight = if (d == selectedDepth) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onDepthChange(d)
                                        depthMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Count Indicator
                Text(
                    text = "$questionCount ${if (questionCount == 1) "question" else "questions"} in filter",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = VintageInkSoft
                    )
                )
            }
        }
    }
}

@Composable
private fun SubjectTile(
    subject: Subject,
    title: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("subject_tile_${subject.name.lowercase()}"),
        shape = RoundedCornerShape(3.dp),
        color = if (isSelected) VintageGoldBg else VintagePaperSheet,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) VintageNavy else VintageLine
        ),
        shadowElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = VintageInk
                    ),
                    maxLines = 1
                )
                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = VintageGreen,
                        modifier = Modifier.size(13.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = badge,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    color = if (isSelected) VintageNavy else VintageInkSoft
                ),
                maxLines = 1
            )
        }
    }
}
