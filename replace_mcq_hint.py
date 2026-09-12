import re

with open("app/src/main/java/com/example/ui/components/ObjectiveMcqView.kt", "r") as f:
    content = f.read()

target = """        // Outside/below the flip-card: Unlock Concept Hint button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onOpenHintModal,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .testTag("unlock_hint_btn"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isHintUnlocked) VintageGreen else VintageNavy
                ),
                border = BorderStroke(1.dp, if (isHintUnlocked) VintageGreen else VintageLine)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = if (isHintUnlocked) VintageGreen else VintageGold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHintUnlocked) "View Concept Hint" else "Unlock Concept Hint",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
            }
        }"""

replacement = """        // Outside/below the flip-card: Unlock Concept Hint button
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
        }"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/ObjectiveMcqView.kt", "w") as f:
        f.write(content)
    print("Replaced successfully.")
else:
    print("Target not found.")
