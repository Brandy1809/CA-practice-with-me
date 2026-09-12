package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PracticeQuestion
import com.example.ui.theme.VintageGold
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
import kotlinx.coroutines.delay

@Composable
fun ObjectiveMcqView(
    question: PracticeQuestion,
    selectedOptionIndex: Int?,
    onSelectOption: (Int) -> Unit,
    isHintUnlocked: Boolean,
    onOpenHintModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAnswered = selectedOptionIndex != null
    var isFlipped by remember(question.id) { mutableStateOf(false) }
    var frontHeightPx by remember(question.id) { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // The instant an option is selected: options flip green/red immediately,
    // and after a slight delay the card smoothly performs a flip transition to reveal the back face.
    LaunchedEffect(selectedOptionIndex) {
        if (selectedOptionIndex != null) {
            delay(250)
            isFlipped = true
        } else {
            isFlipped = false
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "mcq_card_flip"
    )

    val isShowingBack = rotation > 90f

    Column(modifier = modifier.fillMaxWidth()) {
        // Flip Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14f * density.density
                }
                .testTag("mcq_flip_card")
        ) {
            if (!isShowingBack) {
                // FRONT OF THE CARD: Question text + 4 options + highlights & badge once answered
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size ->
                            if (size.height > 0) {
                                frontHeightPx = size.height
                            }
                        },
                    shape = RoundedCornerShape(4.dp),
                    color = VintagePaperSheet,
                    border = BorderStroke(1.dp, VintageLine),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Question text on front of card
                        val questionTitle = if (question.questionText.isNotBlank()) {
                            question.questionText
                        } else {
                            question.mcqQuestion
                        }

                        VintageMarkdownText(
                            text = questionTitle,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                lineHeight = 25.sp,
                                color = VintageInk
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("question_title")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 4 Options
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            question.options.forEachIndexed { index, optionText ->
                                val isSelected = selectedOptionIndex == index
                                val isCorrectAnswer = index == question.correctIndex

                                val cardBg = when {
                                    !isAnswered -> VintagePaperSheet
                                    isSelected && isCorrectAnswer -> VintageGreenBg
                                    isSelected && !isCorrectAnswer -> VintageRedBg
                                    !isSelected && isCorrectAnswer -> VintageGreenBg
                                    else -> VintagePaperSheet.copy(alpha = 0.5f)
                                }

                                val borderColor = when {
                                    !isAnswered -> VintageLine
                                    isSelected && isCorrectAnswer -> VintageGreen
                                    isSelected && !isCorrectAnswer -> VintageRed
                                    !isSelected && isCorrectAnswer -> VintageGreen
                                    else -> VintageLine.copy(alpha = 0.4f)
                                }

                                val textColor = when {
                                    !isAnswered -> VintageInk
                                    isSelected && isCorrectAnswer -> VintageGreen
                                    isSelected && !isCorrectAnswer -> VintageRed
                                    !isSelected && isCorrectAnswer -> VintageGreen
                                    else -> VintageInkSoft
                                }

                                val optionLabel = when (index) {
                                    0 -> "A"
                                    1 -> "B"
                                    2 -> "C"
                                    else -> "D"
                                }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !isAnswered) {
                                            onSelectOption(index)
                                        }
                                        .testTag("mcq_option_$index"),
                                    shape = RoundedCornerShape(4.dp),
                                    color = cardBg,
                                    border = BorderStroke(1.dp, borderColor),
                                    shadowElevation = if (!isAnswered) 1.dp else 0.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Option Letter Badge
                                        Surface(
                                            shape = CircleShape,
                                            color = when {
                                                !isAnswered -> VintagePaperBg
                                                isCorrectAnswer -> VintageGreen
                                                isSelected -> VintageRed
                                                else -> VintagePaperBg
                                            },
                                            border = BorderStroke(1.dp, borderColor),
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (isAnswered && isCorrectAnswer) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Correct",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                } else if (isAnswered && isSelected && !isCorrectAnswer) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Incorrect",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                } else {
                                                    Text(
                                                        text = optionLabel,
                                                        style = TextStyle(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = if (isAnswered && (isCorrectAnswer || isSelected)) Color.White else VintageNavy
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        VintageMarkdownText(
                                            text = optionText,
                                            style = TextStyle(
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected || (isAnswered && isCorrectAnswer)) FontWeight.SemiBold else FontWeight.Normal,
                                                color = textColor,
                                                lineHeight = 19.sp
                                            ),
                                            boldColor = textColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Answer result badge + flip to explanation link on front face
                        if (isAnswered) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val wasCorrect = selectedOptionIndex == question.correctIndex
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = if (wasCorrect) VintageGreenBg else VintageRedBg,
                                    border = BorderStroke(1.dp, if (wasCorrect) VintageGreen else VintageRed)
                                ) {
                                    Text(
                                        text = if (wasCorrect) "✓ Correct (+1.0)" else "✗ Wrong (-0.25)",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (wasCorrect) VintageGreen else VintageRed,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                OutlinedButton(
                                    onClick = { isFlipped = true },
                                    modifier = Modifier
                                        .height(28.dp)
                                        .testTag("flip_to_explanation_btn"),
                                    shape = RoundedCornerShape(3.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    border = BorderStroke(1.dp, VintageNavy),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                                ) {
                                    Text(
                                        text = "View Explanation ↻",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // BACK OF THE CARD: ICAI Concept Explanation full-height within same container boundary
                val backModifier = if (frontHeightPx > 0) {
                    Modifier
                        .fillMaxWidth()
                        .height(with(density) { frontHeightPx.toDp() })
                } else {
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 280.dp)
                }

                Surface(
                    modifier = backModifier
                        .graphicsLayer {
                            rotationY = 180f // un-mirror text
                        }
                        .testTag("mcq_explanation_box"),
                    shape = RoundedCornerShape(4.dp),
                    color = VintagePaperBg,
                    border = BorderStroke(1.dp, VintageLine),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        // Back Face Header: Info Icon, Title, and "Flip Back to Question" button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = VintageNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ICAI CONCEPT EXPLANATION",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VintageNavy,
                                        letterSpacing = 0.5.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = { isFlipped = false },
                                shape = RoundedCornerShape(3.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                border = BorderStroke(1.dp, VintageNavy),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("flip_to_question_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Flip Back",
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Flip to Question",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        // Answer status recap bar
                        val wasCorrect = selectedOptionIndex == question.correctIndex
                        val correctLetter = when (question.correctIndex) {
                            0 -> "A"
                            1 -> "B"
                            2 -> "C"
                            else -> "D"
                        }
                        val correctOptionText = question.options.getOrNull(question.correctIndex) ?: ""

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = if (wasCorrect) VintageGreenBg else VintageRedBg.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, if (wasCorrect) VintageGreen else VintageRed.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (wasCorrect) "✓ Correct (+1.0)" else "✗ Wrong (-0.25)",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (wasCorrect) VintageGreen else VintageRed
                                )
                                Text(
                                    text = "Key: ($correctLetter) $correctOptionText",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VintageInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Explanation text scrollable within the back face if long
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            VintageMarkdownText(
                                text = question.explanation.ifBlank { "ICAI concept explanation is available for this syllabus topic." },
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 13.5.sp,
                                    lineHeight = 19.sp,
                                    color = VintageInk
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Outside/below the flip-card: Unlock Concept Hint button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(
                onClick = onOpenHintModal,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("unlock_hint_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = if (isHintUnlocked) "View Concept Hint" else "Unlock Concept Hint",
                    tint = if (isHintUnlocked) VintageGreen else VintageGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
