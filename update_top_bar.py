import re

with open("app/src/main/java/com/example/ui/components/WritingAnswerSheetScreen.kt", "r") as f:
    content = f.read()

# Replace the existing download and view question buttons
target = """                        // Download JSON Button
                        IconButton(
                            onClick = {
                                val jsonStr = onDownloadJson()
                                jsonContent.value = jsonStr
                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val subject = question.subject.name.lowercase()
                                val chapter = question.chapter.lowercase().replace(" ", "_")
                                val filename = "ca_answer_${subject}_${chapter}_$timeStamp.json"
                                createDocumentLauncher.launch(filename)
                            },
                            modifier = Modifier.size(32.dp).testTag("download_json_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download as JSON",
                                tint = VintageNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Small Button to Read Question ("ek small button ho usko click karte hi question dikhe")
                        Button(
                            onClick = { onToggleQuestionSheet(true) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VintageNavy,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("small_view_question_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "View Question",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }"""

replacement = """                        // Compare with ICAI Model Answer
                        IconButton(
                            onClick = onToggleModelAnswer,
                            modifier = Modifier.size(32.dp).testTag("toggle_icai_answer_btn")
                        ) {
                            Icon(
                                imageVector = if (showModelAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Compare with ICAI Model Answer",
                                tint = if (showModelAnswer) VintageNavy else VintageInkSoft,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Download JSON Button
                        IconButton(
                            onClick = {
                                val jsonStr = onDownloadJson()
                                jsonContent.value = jsonStr
                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val subject = question.subject.name.lowercase()
                                val chapter = question.chapter.lowercase().replace(" ", "_")
                                val filename = "ca_answer_${subject}_${chapter}_$timeStamp.json"
                                createDocumentLauncher.launch(filename)
                            },
                            modifier = Modifier.size(32.dp).testTag("download_json_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download as JSON",
                                tint = VintageNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Small Button to Read Question
                        IconButton(
                            onClick = { onToggleQuestionSheet(true) },
                            modifier = Modifier.size(32.dp).testTag("small_view_question_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "View Question",
                                tint = VintageNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("Could not find target block in WritingAnswerSheetScreen.kt")
    
with open("app/src/main/java/com/example/ui/components/WritingAnswerSheetScreen.kt", "w") as f:
    f.write(content)

