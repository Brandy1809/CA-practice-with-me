package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet

/**
 * Strips or converts LaTeX math notation into clean, readable plain unicode text.
 * E.g.:
 * - \frac{3}{4} -> 3/4
 * - \times -> ×
 * - \sqrt{x} -> sqrt(x)
 * - x^{2} -> x^2
 * - $...$ or $$...$$ -> ...
 * - \\[a-zA-Z]+\{...\} -> fallback plain text
 */
fun cleanLatexMathNotation(input: String): String {
    if (input.isBlank()) return input
    var text = input

    // 1. Common LaTeX math symbols
    text = text
        .replace(Regex("""\\times\b"""), "×")
        .replace(Regex("""\\div\b"""), "÷")
        .replace(Regex("""\\pm\b"""), "±")
        .replace(Regex("""\\mp\b"""), "∓")
        .replace(Regex("""\\leq?\b"""), "≤")
        .replace(Regex("""\\geq?\b"""), "≥")
        .replace(Regex("""\\neq\b"""), "≠")
        .replace(Regex("""\\approx\b"""), "≈")
        .replace(Regex("""\\equiv\b"""), "≡")
        .replace(Regex("""\\cdot\b"""), "·")
        .replace(Regex("""\\bullet\b"""), "•")
        .replace(Regex("""\\infty\b"""), "∞")
        .replace(Regex("""\\pi\b"""), "π")
        .replace(Regex("""\\alpha\b"""), "α")
        .replace(Regex("""\\beta\b"""), "β")
        .replace(Regex("""\\gamma\b"""), "γ")
        .replace(Regex("""\\delta\b"""), "δ")
        .replace(Regex("""\\Delta\b"""), "Δ")
        .replace(Regex("""\\theta\b"""), "θ")
        .replace(Regex("""\\sigma\b"""), "σ")
        .replace(Regex("""\\mu\b"""), "μ")
        .replace(Regex("""\\lambda\b"""), "λ")
        .replace(Regex("""\\sum\b"""), "Σ")
        .replace(Regex("""\\prod\b"""), "Π")
        .replace(Regex("""\\sqrt\b(?!\s*[\{\[])"""), "sqrt")

    // 2. Fractions: \frac{num}{den} -> num/den or (num)/(den)
    var prev = ""
    while (prev != text) {
        prev = text
        text = text.replace(Regex("""\\frac\{([^{}]+)\}\{([^{}]+)\}""")) { mr ->
            val num = mr.groupValues[1].trim()
            val den = mr.groupValues[2].trim()
            val formattedNum = if (num.contains(" ") || num.contains("+") || num.contains("-")) "($num)" else num
            val formattedDen = if (den.contains(" ") || den.contains("+") || den.contains("-")) "($den)" else den
            "$formattedNum/$formattedDen"
        }
    }

    // 3. Roots: \sqrt[n]{x} -> root_n(x), \sqrt{x} -> sqrt(x)
    text = text.replace(Regex("""\\sqrt\[([^{}]+)\]\{([^{}]+)\}""")) { mr ->
        val n = mr.groupValues[1].trim()
        val x = mr.groupValues[2].trim()
        "root_$n($x)"
    }
    text = text.replace(Regex("""\\sqrt\{([^{}]+)\}""")) { mr ->
        val x = mr.groupValues[1].trim()
        "sqrt($x)"
    }

    // 4. Superscripts & subscripts: ^{x} -> ^x or ^(x), _{x} -> _x or _(x)
    text = text.replace(Regex("""\^\{([^{}]+)\}""")) { mr ->
        val c = mr.groupValues[1].trim()
        if (c.length == 1 || c.all { it.isDigit() }) "^$c" else "^($c)"
    }
    text = text.replace(Regex("""_\{([^{}]+)\}""")) { mr ->
        val c = mr.groupValues[1].trim()
        if (c.length == 1 || c.all { it.isDigit() }) "_$c" else "_($c)"
    }

    // 5. Delimiters \left and \right
    text = text.replace(Regex("""\\(?:left|right)(?=[()\[\]\{\}.|])"""), "")
    text = text.replace(Regex("""\\(?:left|right)\b"""), "")
    text = text.replace(Regex("""\\\{"""), "{").replace(Regex("""\\\}"""), "}")

    // 6. Common text wrappers
    text = text.replace(Regex("""\\(?:text|textbf|textit|mathrm|mathbf|mathit|mathsf|mathtt|operatorname)\{([^{}]+)\}""")) {
        it.groupValues[1]
    }

    // 7. Generic LaTeX command with braces fallback: \\[a-zA-Z]+\{([^{}]*)\} -> $1
    prev = ""
    while (prev != text) {
        prev = text
        text = text.replace(Regex("""\\[a-zA-Z]+\{([^{}]*)\}""")) { mr ->
            mr.groupValues[1]
        }
    }

    // 8. Math delimiters $...$, $$...$$, \(...\), \[...\]
    text = text.replace(Regex("""\$\$([^\$]+)\$\$""")) { it.groupValues[1].trim() }
    text = text.replace(Regex("""\$([^\$]+)\$""")) { it.groupValues[1].trim() }
    text = text.replace(Regex("""\\\(([^\\]+)\\\)""")) { it.groupValues[1].trim() }
    text = text.replace(Regex("""\\\[([^\]]+)\\\]""")) { it.groupValues[1].trim() }

    // 9. Spacing commands
    text = text.replace(Regex("""\\(?:quad|qquad|;|!|,)\b"""), " ")

    // 10. Any remaining standalone command: \xyz -> xyz
    text = text.replace(Regex("""\\([a-zA-Z]+)""")) { it.groupValues[1] }

    // 11. Stray backslashes
    text = text.replace(Regex("""\\"""), "")

    return text
}

/**
 * Builds an AnnotatedString parsing inline Markdown (**bold**, *italic*, `code`)
 * after running LaTeX cleanup.
 */
fun buildVintageMarkdownAnnotatedString(
    rawText: String,
    baseColor: Color,
    boldColor: Color = VintageNavy,
    baseFontFamily: FontFamily = FontFamily.Serif
): AnnotatedString {
    val cleaned = cleanLatexMathNotation(rawText)
    return buildAnnotatedString {
        val pattern = Regex("""(\*\*(?:[^*]|\*(?!\*))+\*\*|\*(?:[^*])+\*|`[^`]+`)""")
        var currentIndex = 0
        pattern.findAll(cleaned).forEach { match ->
            val matchRange = match.range
            if (matchRange.first > currentIndex) {
                append(cleaned.substring(currentIndex, matchRange.first))
            }
            val token = match.value
            when {
                token.startsWith("**") && token.endsWith("**") && token.length >= 4 -> {
                    val inner = token.substring(2, token.length - 2)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) {
                        append(inner)
                    }
                }
                token.startsWith("*") && token.endsWith("*") && token.length >= 2 -> {
                    val inner = token.substring(1, token.length - 1)
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(inner)
                    }
                }
                token.startsWith("`") && token.endsWith("`") && token.length >= 2 -> {
                    val inner = token.substring(1, token.length - 1)
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = VintageNavy.copy(alpha = 0.08f)
                        )
                    ) {
                        append(inner)
                    }
                }
                else -> {
                    append(token)
                }
            }
            currentIndex = matchRange.last + 1
        }
        if (currentIndex < cleaned.length) {
            append(cleaned.substring(currentIndex))
        }
    }
}

/**
 * Represents structured blocks extracted from Markdown question/answer text.
 */
sealed class VintageMarkdownBlock {
    data class Paragraph(val text: String) : VintageMarkdownBlock()
    data class BulletItem(val text: String) : VintageMarkdownBlock()
    data class NumberedItem(val prefix: String, val text: String) : VintageMarkdownBlock()
    data class Table(
        val headers: List<String>,
        val alignments: List<TextAlign>,
        val rows: List<List<String>>
    ) : VintageMarkdownBlock()
    object BlankLine : VintageMarkdownBlock()
}

private fun parseTableRowCells(line: String): List<String> {
    var s = line.trim()
    if (s.startsWith("|")) s = s.substring(1)
    if (s.endsWith("|")) s = s.substring(0, s.length - 1)
    return s.split("|").map { it.trim() }
}

private fun isTableSeparatorRow(line: String): Boolean {
    val trimmed = line.trim()
    if (!trimmed.contains("-")) return false
    val cells = parseTableRowCells(trimmed)
    if (cells.isEmpty()) return false
    return cells.all { cell ->
        cell.isNotBlank() && cell.all { it == '-' || it == ':' || it == ' ' } && cell.count { it == '-' } >= 2
    }
}

private fun parseAlignment(cell: String): TextAlign {
    val trimmed = cell.trim()
    val startsWithColon = trimmed.startsWith(":")
    val endsWithColon = trimmed.endsWith(":")
    return when {
        startsWithColon && endsWithColon -> TextAlign.Center
        endsWithColon -> TextAlign.End
        else -> TextAlign.Start
    }
}

/**
 * Parses raw text into a sequence of VintageMarkdownBlocks.
 */
fun parseMarkdownBlocks(rawText: String): List<VintageMarkdownBlock> {
    if (rawText.isBlank()) return emptyList()

    val lines = rawText.lines()
    val blocks = mutableListOf<VintageMarkdownBlock>()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()

        if (trimmed.isEmpty()) {
            if (blocks.isNotEmpty() && blocks.last() !is VintageMarkdownBlock.BlankLine) {
                blocks.add(VintageMarkdownBlock.BlankLine)
            }
            i++
            continue
        }

        // Check if this could be the start of a Markdown Table
        if (line.contains("|")) {
            val candidateCells = parseTableRowCells(line)
            if (candidateCells.size >= 2) {
                val nextLine = if (i + 1 < lines.size) lines[i + 1].trim() else ""
                val isNextSeparator = isTableSeparatorRow(nextLine)
                val isNextCandidate = nextLine.contains("|") && parseTableRowCells(nextLine).size >= 2

                if (isNextSeparator || isNextCandidate) {
                    val tableLines = mutableListOf<String>()
                    var j = i
                    while (j < lines.size) {
                        val currentTableLine = lines[j].trim()
                        if (currentTableLine.isEmpty()) break
                        if (currentTableLine.contains("|")) {
                            tableLines.add(currentTableLine)
                            j++
                        } else {
                            break
                        }
                    }

                    if (tableLines.size >= 2) {
                        var separatorIndex = -1
                        for (k in tableLines.indices) {
                            if (isTableSeparatorRow(tableLines[k])) {
                                separatorIndex = k
                                break
                            }
                        }

                        val headers: List<String>
                        val alignments: List<TextAlign>
                        val dataRows: List<List<String>>

                        if (separatorIndex == 1) {
                            headers = parseTableRowCells(tableLines[0])
                            val sepCells = parseTableRowCells(tableLines[1])
                            alignments = sepCells.map { parseAlignment(it) }
                            dataRows = tableLines.drop(2).map { parseTableRowCells(it) }
                        } else if (separatorIndex == 0) {
                            headers = if (tableLines.size > 1) parseTableRowCells(tableLines[1]) else emptyList()
                            val sepCells = parseTableRowCells(tableLines[0])
                            alignments = sepCells.map { parseAlignment(it) }
                            dataRows = tableLines.drop(2).map { parseTableRowCells(it) }
                        } else {
                            headers = parseTableRowCells(tableLines[0])
                            alignments = headers.map { TextAlign.Start }
                            dataRows = tableLines.drop(1).map { parseTableRowCells(it) }
                        }

                        if (headers.isNotEmpty() || dataRows.isNotEmpty()) {
                            blocks.add(
                                VintageMarkdownBlock.Table(
                                    headers = headers,
                                    alignments = alignments,
                                    rows = dataRows
                                )
                            )
                            i = j
                            continue
                        }
                    }
                }
            }
        }

        // Check if bullet point (- or * or •)
        val bulletMatch = Regex("""^[-*•]\s+(.*)$""").matchEntire(trimmed)
        if (bulletMatch != null) {
            blocks.add(VintageMarkdownBlock.BulletItem(bulletMatch.groupValues[1]))
            i++
            continue
        }

        // Check if numbered list (e.g. 1. or 1) or (a) or (i) or a.)
        val numberedMatch = Regex("""^(\d+[\.\)]|\([a-zA-Z0-9]+\)|[a-zA-Z][\.\)])\s+(.*)$""").matchEntire(trimmed)
        if (numberedMatch != null) {
            blocks.add(
                VintageMarkdownBlock.NumberedItem(
                    prefix = numberedMatch.groupValues[1],
                    text = numberedMatch.groupValues[2]
                )
            )
            i++
            continue
        }

        // Regular paragraph line
        blocks.add(VintageMarkdownBlock.Paragraph(line))
        i++
    }

    return blocks
}

/**
 * Renders an actual bordered table/grid matching the vintage paper style.
 * Supports horizontal scrolling when table width exceeds available screen width.
 */
@Composable
fun VintageMarkdownTable(
    headers: List<String>,
    alignments: List<TextAlign>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    val colCount = maxOf(headers.size, rows.maxOfOrNull { it.size } ?: 1)
    if (colCount == 0) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        val availableWidth = maxWidth

        // Determine relative weight per column for intelligent proportions
        val relativeWeights = (0 until colCount).map { i ->
            val headerName = headers.getOrNull(i)?.lowercase() ?: ""
            when {
                headerName.contains("particular") || headerName.contains("detail") ||
                headerName.contains("provision") || headerName.contains("description") -> 2.2f
                headerName.contains("dr") || headerName.contains("cr") ||
                headerName.contains("amount") || headerName.contains("₹") || headerName.contains("rs") -> 1.3f
                headerName.contains("lf") || headerName.contains("date") ||
                headerName.contains("sec") || headerName.contains("no") -> 0.8f
                else -> 1.2f
            }
        }
        val totalWeight = relativeWeights.sum().coerceAtLeast(1f)

        // Min width per column to keep text comfortably readable
        val baseMinColWidth = when {
            colCount <= 2 -> 135.dp
            colCount == 3 -> 95.dp
            else -> 82.dp
        }
        val totalMinWidth = baseMinColWidth * colCount
        val isScrollable = totalMinWidth > availableWidth

        val colWidths = (0 until colCount).map { i ->
            if (isScrollable) {
                maxOf(baseMinColWidth, (baseMinColWidth * (relativeWeights[i] / 1.2f)))
            } else {
                availableWidth * (relativeWeights[i] / totalWeight)
            }
        }
        val actualTableWidth = if (isScrollable) colWidths.fold(0.dp) { acc, w -> acc + w } else availableWidth

        val scrollModifier = if (isScrollable) {
            Modifier.horizontalScroll(rememberScrollState())
        } else {
            Modifier
        }

        Surface(
            modifier = Modifier.fillMaxWidth().testTag("vintage_markdown_table"),
            shape = RoundedCornerShape(4.dp),
            color = VintagePaperSheet,
            border = BorderStroke(1.dp, VintageNavy.copy(alpha = 0.35f))
        ) {
            Box(modifier = Modifier.fillMaxWidth().then(scrollModifier)) {
                Column(modifier = Modifier.width(actualTableWidth)) {
                    // Header Row
                    if (headers.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VintageNavy.copy(alpha = 0.08f))
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (colIdx in 0 until colCount) {
                                if (colIdx > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .fillMaxHeight()
                                            .background(VintageNavy.copy(alpha = 0.25f))
                                    )
                                }
                                val headerText = headers.getOrElse(colIdx) { "" }
                                val align = alignments.getOrElse(colIdx) { TextAlign.Start }
                                Box(
                                    modifier = Modifier
                                        .width(colWidths[colIdx])
                                        .padding(horizontal = 7.dp, vertical = 7.dp),
                                    contentAlignment = when (align) {
                                        TextAlign.Center -> Alignment.Center
                                        TextAlign.End -> Alignment.CenterEnd
                                        else -> Alignment.CenterStart
                                    }
                                ) {
                                    Text(
                                        text = cleanLatexMathNotation(headerText),
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = VintageNavy,
                                            textAlign = align
                                        )
                                    )
                                }
                            }
                        }
                        HorizontalDivider(thickness = 1.2.dp, color = VintageNavy.copy(alpha = 0.35f))
                    }

                    // Data Rows
                    rows.forEachIndexed { rowIndex, rowCells ->
                        val rowBg = if (rowIndex % 2 == 1) VintagePaperBg.copy(alpha = 0.5f) else VintagePaperSheet
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (colIdx in 0 until colCount) {
                                if (colIdx > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .fillMaxHeight()
                                            .background(VintageLine.copy(alpha = 0.45f))
                                    )
                                }
                                val cellText = rowCells.getOrElse(colIdx) { "" }
                                val align = alignments.getOrElse(colIdx) { TextAlign.Start }
                                Box(
                                    modifier = Modifier
                                        .width(colWidths[colIdx])
                                        .padding(horizontal = 7.dp, vertical = 6.dp),
                                    contentAlignment = when (align) {
                                        TextAlign.Center -> Alignment.Center
                                        TextAlign.End -> Alignment.CenterEnd
                                        else -> Alignment.CenterStart
                                    }
                                ) {
                                    Text(
                                        text = buildVintageMarkdownAnnotatedString(
                                            rawText = cellText,
                                            baseColor = VintageInk,
                                            boldColor = VintageNavy
                                        ),
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 12.sp,
                                            lineHeight = 16.5.sp,
                                            color = VintageInk,
                                            textAlign = align
                                        )
                                    )
                                }
                            }
                        }
                        if (rowIndex < rows.size - 1) {
                            HorizontalDivider(thickness = 0.8.dp, color = VintageLine.copy(alpha = 0.55f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Lightweight Markdown-aware renderer for CA Foundation question text, model answers,
 * and explanations.
 * Correctly displays:
 * - Markdown pipe tables (| col1 | col2 |, with |---| separator row) as bordered grids.
 * - Bold (**text**) formatting with subtle navy emphasis.
 * - Bullet lists (- item or • item).
 * - Numbered lists (1. item, (a) item).
 * - Cleans and formats math notation into plain readable unicode text.
 */
@Composable
fun VintageMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 15.sp,
        color = VintageInk,
        lineHeight = 22.sp
    ),
    boldColor: Color = VintageNavy
) {
    if (text.isBlank()) return

    val blocks = parseMarkdownBlocks(text)

    // Optimization: If it's just a single paragraph block with no complex structure, render directly
    if (blocks.size == 1 && blocks[0] is VintageMarkdownBlock.Paragraph) {
        val p = blocks[0] as VintageMarkdownBlock.Paragraph
        Text(
            text = buildVintageMarkdownAnnotatedString(
                rawText = p.text,
                baseColor = style.color,
                boldColor = boldColor,
                baseFontFamily = style.fontFamily ?: FontFamily.Serif
            ),
            style = style,
            modifier = modifier
        )
        return
    }

    Column(modifier = modifier) {
        blocks.forEachIndexed { index, block ->
            when (block) {
                is VintageMarkdownBlock.Table -> {
                    VintageMarkdownTable(
                        headers = block.headers,
                        alignments = block.alignments,
                        rows = block.rows
                    )
                }
                is VintageMarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = (style.fontSize.value * 0.95f).sp,
                                color = boldColor
                            ),
                            modifier = Modifier.padding(end = 8.dp, top = 1.dp)
                        )
                        Text(
                            text = buildVintageMarkdownAnnotatedString(
                                rawText = block.text,
                                baseColor = style.color,
                                boldColor = boldColor,
                                baseFontFamily = style.fontFamily ?: FontFamily.Serif
                            ),
                            style = style,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is VintageMarkdownBlock.NumberedItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.5.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = block.prefix,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = (style.fontSize.value * 0.9f).sp,
                                color = boldColor
                            ),
                            modifier = Modifier.padding(end = 8.dp, top = 1.dp)
                        )
                        Text(
                            text = buildVintageMarkdownAnnotatedString(
                                rawText = block.text,
                                baseColor = style.color,
                                boldColor = boldColor,
                                baseFontFamily = style.fontFamily ?: FontFamily.Serif
                            ),
                            style = style,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is VintageMarkdownBlock.Paragraph -> {
                    Text(
                        text = buildVintageMarkdownAnnotatedString(
                            rawText = block.text,
                            baseColor = style.color,
                            boldColor = boldColor,
                            baseFontFamily = style.fontFamily ?: FontFamily.Serif
                        ),
                        style = style,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is VintageMarkdownBlock.BlankLine -> {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}
