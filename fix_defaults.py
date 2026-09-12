import re

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "r") as f:
    content = f.read()

# Fix Balance Sheet
content = content.replace('AccountRow(particulars = "Capital (Opening)", amount = "250000")', 'AccountRow(particulars = "Capital (Opening)", amount = "")')
content = content.replace('AccountRow(particulars = "Add: Net Profit for Year", amount = "65000")', 'AccountRow(particulars = "Add: Net Profit for Year", amount = "")')
content = content.replace('AccountRow(particulars = "Less: Drawings", amount = "15000")', 'AccountRow(particulars = "Less: Drawings", amount = "")')
content = content.replace('AccountRow(particulars = "Bank Loan (Secured)", amount = "100000")', 'AccountRow(particulars = "Bank Loan (Secured)", amount = "")')
content = content.replace('AccountRow(particulars = "Sundry Creditors", amount = "45000")', 'AccountRow(particulars = "Sundry Creditors", amount = "")')

content = content.replace('AccountRow(particulars = "Plant & Machinery", amount = "180000")', 'AccountRow(particulars = "Plant & Machinery", amount = "")')
content = content.replace('AccountRow(particulars = "Furniture & Fixtures", amount = "40000")', 'AccountRow(particulars = "Furniture & Fixtures", amount = "")')
content = content.replace('AccountRow(particulars = "Closing Stock", amount = "40000")', 'AccountRow(particulars = "Closing Stock", amount = "")')
content = content.replace('AccountRow(particulars = "Sundry Debtors", amount = "120000")', 'AccountRow(particulars = "Sundry Debtors", amount = "")')
content = content.replace('AccountRow(particulars = "Cash & Bank Balances", amount = "65000")', 'AccountRow(particulars = "Cash & Bank Balances", amount = "")')

# Fix Ledger
content = content.replace('amount = "50000"', 'amount = ""')
content = content.replace('amount = "25000"', 'amount = ""')
content = content.replace('amount = "18000"', 'amount = ""')
content = content.replace('amount = "7000"', 'amount = ""')

# Fix Journal
content = content.replace('debit = "100000"', 'debit = ""')
content = content.replace('credit = "100000"', 'credit = ""')
content = content.replace('debit = "30000"', 'debit = ""')
content = content.replace('credit = "30000"', 'credit = ""')
content = content.replace('debit = "45000"', 'debit = ""')
content = content.replace('credit = "45000"', 'credit = ""')

# Fix Trial Balance
content = content.replace('credit = "250000"', 'credit = ""')
content = content.replace('debit = "150000"', 'debit = ""')
content = content.replace('debit = "180000"', 'debit = ""')
content = content.replace('credit = "240000"', 'credit = ""')
content = content.replace('debit = "95000"', 'debit = ""')
content = content.replace('credit = "60000"', 'credit = ""')
content = content.replace('debit = "80000"', 'debit = ""')

# Fix BRS
content = content.replace('amount = "24000"', 'amount = ""')
content = content.replace('amount = "3500"', 'amount = ""')
content = content.replace('amount = "650"', 'amount = ""')

# Fix Cash Book
content = content.replace('cash = "12000"', 'cash = ""')
content = content.replace('bank = "45000"', 'bank = ""')
content = content.replace('cash = "8500"', 'cash = ""')
content = content.replace('discount = "500"', 'discount = ""')
content = content.replace('bank = "24500"', 'bank = ""')
content = content.replace('cash = "4000"', 'cash = ""')
content = content.replace('bank = "7000"', 'bank = ""')
content = content.replace('discount = "300"', 'discount = ""')
content = content.replace('bank = "15700"', 'bank = ""')

# Fix Partners Capital
content = content.replace('partnerA = "12000"', 'partnerA = ""')
content = content.replace('partnerB = "8000"', 'partnerB = ""')
content = content.replace('partnerA = "185000"', 'partnerA = ""')
content = content.replace('partnerB = "124000"', 'partnerB = ""')
content = content.replace('partnerA = "150000"', 'partnerA = ""')
content = content.replace('partnerB = "100000"', 'partnerB = ""')
content = content.replace('partnerA = "9000"', 'partnerA = ""')
content = content.replace('partnerB = "6000"', 'partnerB = ""')
content = content.replace('partnerA = "38000"', 'partnerA = ""')
content = content.replace('partnerB = "26000"', 'partnerB = ""')

# Fix Petty Cash
content = content.replace('receipts = "5000"', 'receipts = ""')
content = content.replace('totalPayment = "350"', 'totalPayment = ""')
content = content.replace('conveyance = "350"', 'conveyance = ""')
content = content.replace('totalPayment = "180"', 'totalPayment = ""')
content = content.replace('misc = "180"', 'misc = ""')
content = content.replace('totalPayment = "620"', 'totalPayment = ""')
content = content.replace('stationery = "620"', 'stationery = ""')
content = content.replace('totalPayment = "400"', 'totalPayment = ""')
content = content.replace('cartage = "400"', 'cartage = ""')

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "w") as f:
    f.write(content)
