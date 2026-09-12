import re

with open("app/src/main/java/com/example/ui/PracticeScreen.kt", "r") as f:
    content = f.read()

target = """                                            Spacer(modifier = Modifier.height(14.dp))

                                            // Prominent "Write Answer" button as requested by the user
                                            Button(
                                                onClick = viewModel::openWritingView,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(46.dp)
                                                    .testTag("write_answer_button"),
                                                shape = RoundedCornerShape(4.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = VintageNavy,
                                                    contentColor = Color.White
                                                ),
                                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (uiState.userAnswerText.isNotBlank() || uiState.activeTableType != AccountingTableType.NONE || uiState.insertedBlocks.isNotEmpty()) {
                                                        "Continue Writing Answer"
                                                    } else {
                                                        "Write Answer"
                                                    },
                                                    style = TextStyle(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Option to preview ICAI Model Answer right from question screen
                                            OutlinedButton(
                                                onClick = viewModel::toggleModelAnswer,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("toggle_model_answer_btn"),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, VintageNavy),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Visibility,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (uiState.showModelAnswer) "Hide ICAI Model Answer" else "Preview ICAI Model Answer",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }"""

replacement = """                                            Spacer(modifier = Modifier.height(14.dp))

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
                                                            imageVector = if (uiState.showModelAnswer) androidx.compose.material.icons.filled.VisibilityOff else Icons.Default.Visibility,
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
                                            }"""

if target in content:
    content = content.replace(target, replacement)
    # also add VisibilityOff import if missing
    if "import androidx.compose.material.icons.filled.VisibilityOff" not in content:
        content = content.replace("import androidx.compose.material.icons.filled.Visibility", "import androidx.compose.material.icons.filled.Visibility\nimport androidx.compose.material.icons.filled.VisibilityOff")
    
    with open("app/src/main/java/com/example/ui/PracticeScreen.kt", "w") as f:
        f.write(content)
    print("Successfully replaced.")
else:
    print("Target not found.")

