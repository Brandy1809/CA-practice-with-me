import re

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "r") as f:
    content = f.read()

content = content.replace('amount = "0"', 'amount = ""')

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "w") as f:
    f.write(content)
