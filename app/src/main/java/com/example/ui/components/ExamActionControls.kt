package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet
import kotlinx.coroutines.launch

@Composable
fun ExamActionControls(
    onNewQuestion: () -> Unit,
    questionsAttemptedCount: Int = 0,
    mcqCorrectCount: Int,
    mcqAttemptedCount: Int,
    modifier: Modifier = Modifier,
    isLoadingQuestion: Boolean = false,
    questionsAnsweredCount: Int = questionsAttemptedCount
) {
    val rotationAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "spin_loading")
    val continuousRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "continuous_spin"
    )

    val currentRotation = if (isLoadingQuestion) continuousRotation else rotationAnim.value

    Column(modifier = modifier.fillMaxWidth()) {
        // "New question" Button with spin icon
        Button(
            onClick = {
                scope.launch {
                    rotationAnim.snapTo(0f)
                    rotationAnim.animateTo(360f, animationSpec = tween(400))
                }
                onNewQuestion()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("spin_new_question_btn"),
            shape = RoundedCornerShape(3.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VintageInk,
                contentColor = VintagePaperSheet
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier
                    .size(17.dp)
                    .rotate(currentRotation)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isLoadingQuestion) "Fetching..." else "New question",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Live Practice Session Stats Bar (Backed by Room Local Persistence)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = VintagePaperBg.copy(alpha = 0.6f),
            shape = RoundedCornerShape(3.dp),
            border = BorderStroke(1.dp, VintageLine.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Questions Practiced: $questionsAnsweredCount",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        color = VintageInkSoft
                    )
                )

                if (mcqAttemptedCount > 0) {
                    val accuracy = ((mcqCorrectCount.toFloat() / mcqAttemptedCount) * 100).toInt()
                    Text(
                        text = "MCQ Score: $mcqCorrectCount/$mcqAttemptedCount ($accuracy%)",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = if (accuracy >= 60) VintageGreen else VintageNavy
                        )
                    )
                } else {
                    Text(
                        text = "ICAI Syllabus Practice Mode",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            color = VintageInkSoft
                        )
                    )
                }
            }
        }
    }
}
