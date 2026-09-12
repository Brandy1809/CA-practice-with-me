import re

with open("app/src/main/java/com/example/ui/components/AccountingFormatSearchBar.kt", "r") as f:
    content = f.read()

content = content.replace('import androidx.compose.material.icons.filled.Search\n', 'import androidx.compose.material.icons.filled.Search\nimport androidx.compose.material.icons.filled.TableChart\n')
content = content.replace('format.displayName', 'format.title')

with open("app/src/main/java/com/example/ui/components/AccountingFormatSearchBar.kt", "w") as f:
    f.write(content)
