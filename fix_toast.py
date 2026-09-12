import re

with open("app/src/main/java/com/example/ui/components/InsertedBlockCard.kt", "r") as f:
    ibc = f.read()

ibc = ibc.replace("import kotlinx.coroutines.launch", "import kotlinx.coroutines.launch\nimport android.widget.Toast\nimport androidx.compose.ui.platform.LocalContext")

clear_btn = """
                        val context = LocalContext.current
                        // Clear button
                        IconButton(
                            onClick = { 
                                onClearBlockValues()
                                Toast.makeText(context, "Values cleared", Toast.LENGTH_SHORT).show()
                            },"""

ibc = ibc.replace("""                        // Clear button
                        IconButton(
                            onClick = { 
                                onClearBlockValues()
                            },""", clear_btn)

with open("app/src/main/java/com/example/ui/components/InsertedBlockCard.kt", "w") as f:
    f.write(ibc)
