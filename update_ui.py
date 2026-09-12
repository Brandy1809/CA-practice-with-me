import re

# 1. Update PracticeScreen.kt
with open("app/src/main/java/com/example/ui/PracticeScreen.kt", "r") as f:
    ps = f.read()
ps = ps.replace("onRemoveBlock = viewModel::removeBlock,", "onRemoveBlock = viewModel::removeBlock,\n            onClearBlockValues = viewModel::clearBlockValues,")
with open("app/src/main/java/com/example/ui/PracticeScreen.kt", "w") as f:
    f.write(ps)

# 2. Update WritingAnswerSheetScreen.kt
with open("app/src/main/java/com/example/ui/components/WritingAnswerSheetScreen.kt", "r") as f:
    was = f.read()
was = was.replace("onRemoveBlock: (String) -> Unit = {},", "onRemoveBlock: (String) -> Unit = {},\n    onClearBlockValues: (String) -> Unit = {},")
was = was.replace("onRemoveBlock = onRemoveBlock,", "onRemoveBlock = onRemoveBlock,\n                            onClearBlockValues = onClearBlockValues,")
with open("app/src/main/java/com/example/ui/components/WritingAnswerSheetScreen.kt", "w") as f:
    f.write(was)

# 3. Update SubjectiveAnswerView.kt
with open("app/src/main/java/com/example/ui/components/SubjectiveAnswerView.kt", "r") as f:
    sav = f.read()
sav = sav.replace("onRemoveBlock: (String) -> Unit = {},", "onRemoveBlock: (String) -> Unit = {},\n    onClearBlockValues: (String) -> Unit = {},")
sav = sav.replace("onRemoveBlock = { onRemoveBlock(block.id) },", "onRemoveBlock = { onRemoveBlock(block.id) },\n                    onClearBlockValues = { onClearBlockValues(block.id) },")
with open("app/src/main/java/com/example/ui/components/SubjectiveAnswerView.kt", "w") as f:
    f.write(sav)

# 4. Update InsertedBlockCard.kt
with open("app/src/main/java/com/example/ui/components/InsertedBlockCard.kt", "r") as f:
    ibc = f.read()

ibc = ibc.replace("import androidx.compose.material.icons.filled.Delete", "import androidx.compose.material.icons.filled.Delete\nimport androidx.compose.material.icons.filled.CleaningServices\nimport androidx.compose.material3.SnackbarHostState\nimport androidx.compose.runtime.rememberCoroutineScope\nimport kotlinx.coroutines.launch")

ibc = ibc.replace("onRemoveBlock: () -> Unit,", "onRemoveBlock: () -> Unit,\n    onClearBlockValues: () -> Unit = {},")

ibc = ibc.replace("var showDeleteConfirmDialog by remember { mutableStateOf(false) }", """val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }""")

clear_btn = """
                        // Clear button
                        IconButton(
                            onClick = { 
                                onClearBlockValues()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("clear_block_${block.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Clear all values",
                                tint = VintageNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        // Collapse / Expand toggle button"""

ibc = ibc.replace("// Collapse / Expand toggle button", clear_btn)
with open("app/src/main/java/com/example/ui/components/InsertedBlockCard.kt", "w") as f:
    f.write(ibc)

