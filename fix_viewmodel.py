import re

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "r") as f:
    content = f.read()

# Fix Journal
content = content.replace('put("drAmount", it.drAmount); put("crAmount", it.crAmount)', 'put("debit", it.debit); put("credit", it.credit); put("narration", it.narration)')
# Fix Trial Balance
content = content.replace('put("serialNo", it.serialNo); put("headOfAccount", it.headOfAccount); put("lf", it.lf); put("drBalance", it.drBalance); put("crBalance", it.crBalance)', 'put("sNo", it.sNo); put("headOfAccount", it.headOfAccount); put("lf", it.lf); put("debit", it.debit); put("credit", it.credit)')
# Fix Cash Book
content = content.replace('put("vn", it.vn); put("lf", it.lf); put("discount", it.discount); put("cash", it.cash); put("bank", it.bank)', 'put("vn", it.vn); put("discount", it.discount); put("cash", it.cash); put("bank", it.bank)')
# Fix Partners Capital
content = content.replace('put("partnerA", it.partnerA); put("partnerB", it.partnerB); put("partnerC", it.partnerC)', 'put("partnerA", it.partnerA); put("partnerB", it.partnerB)')
# Fix Petty Cash
content = content.replace('put("vn", it.vn); put("particulars", it.particulars); put("totalPayments", it.totalPayments); put("postage", it.postage); put("conveyance", it.conveyance); put("stationery", it.stationery); put("misc", it.misc)', 'put("particulars", it.particulars); put("totalPayment", it.totalPayment); put("cartage", it.cartage); put("conveyance", it.conveyance); put("stationery", it.stationery); put("misc", it.misc)')

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "w") as f:
    f.write(content)
