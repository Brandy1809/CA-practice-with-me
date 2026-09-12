import re

with open("app/src/main/java/com/example/ui/components/SubjectiveAnswerView.kt", "r") as f:
    content = f.read()

target = """        Spacer(modifier = Modifier.height(12.dp))

        // Compare with ICAI Answer Button
        OutlinedButton(
            onClick = onToggleModelAnswer,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("toggle_icai_answer_btn"),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (showModelAnswer) VintageNavy else VintageInk
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (showModelAnswer) VintageNavy else VintageLine
            )
        ) {
            Icon(
                imageVector = if (showModelAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (showModelAnswer) "Hide ICAI Model Answer" else "Compare with ICAI Model Answer",
                style = MaterialTheme.typography.labelMedium
            )
        }"""

if target in content:
    content = content.replace(target, "")
else:
    print("Could not find bottom button in SubjectiveAnswerView.kt")

with open("app/src/main/java/com/example/ui/components/SubjectiveAnswerView.kt", "w") as f:
    f.write(content)

