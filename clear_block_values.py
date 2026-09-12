import re

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "r") as f:
    content = f.read()

new_func = """
    fun clearBlockValues(blockId: String) {
        _uiState.update { state ->
            val block = state.insertedBlocks.find { it.id == blockId } ?: return@update state
            
            val updatedTwoSided = state.twoSidedBlocks.toMutableMap()
            updatedTwoSided[blockId]?.let { ts ->
                updatedTwoSided[blockId] = ts.copy(
                    drRows = ts.drRows.map { it.copy(amount = "") },
                    crRows = ts.crRows.map { it.copy(amount = "") }
                )
            }
            
            val updatedLedger = state.ledgerBlocks.toMutableMap()
            updatedLedger[blockId]?.let { lg ->
                updatedLedger[blockId] = lg.copy(
                    drRows = lg.drRows.map { it.copy(amount = "") },
                    crRows = lg.crRows.map { it.copy(amount = "") }
                )
            }

            val updatedJournal = state.journalBlocks.toMutableMap()
            updatedJournal[blockId]?.let { rows ->
                updatedJournal[blockId] = rows.map { it.copy(debit = "", credit = "") }
            }

            val updatedTrialBalance = state.trialBalanceBlocks.toMutableMap()
            updatedTrialBalance[blockId]?.let { rows ->
                updatedTrialBalance[blockId] = rows.map { it.copy(debit = "", credit = "") }
            }

            val updatedBrs = state.brsBlocks.toMutableMap()
            updatedBrs[blockId]?.let { brs ->
                updatedBrs[blockId] = brs.copy(
                    startingBalance = "",
                    rows = brs.rows.map { it.copy(amount = "") }
                )
            }

            val updatedCashBook = state.cashBookBlocks.toMutableMap()
            updatedCashBook[blockId]?.let { cb ->
                updatedCashBook[blockId] = cb.copy(
                    drRows = cb.drRows.map { it.copy(discount = "", cash = "", bank = "") },
                    crRows = cb.crRows.map { it.copy(discount = "", cash = "", bank = "") }
                )
            }

            val updatedPartnersCapital = state.partnersCapitalBlocks.toMutableMap()
            updatedPartnersCapital[blockId]?.let { pc ->
                updatedPartnersCapital[blockId] = pc.copy(
                    drRows = pc.drRows.map { it.copy(partnerA = "", partnerB = "") },
                    crRows = pc.crRows.map { it.copy(partnerA = "", partnerB = "") }
                )
            }

            val updatedPettyCash = state.pettyCashBlocks.toMutableMap()
            updatedPettyCash[blockId]?.let { rows ->
                updatedPettyCash[blockId] = rows.map { it.copy(receipts = "", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = "") }
            }
            
            val updatedWorkingNotes = state.workingNotes.toMutableMap()
            updatedWorkingNotes[blockId]?.let { wn ->
                updatedWorkingNotes[blockId] = wn.copy(content = "")
            }

            state.copy(
                twoSidedBlocks = updatedTwoSided,
                ledgerBlocks = updatedLedger,
                journalBlocks = updatedJournal,
                trialBalanceBlocks = updatedTrialBalance,
                brsBlocks = updatedBrs,
                cashBookBlocks = updatedCashBook,
                partnersCapitalBlocks = updatedPartnersCapital,
                pettyCashBlocks = updatedPettyCash,
                workingNotes = updatedWorkingNotes
            )
        }
    }
"""

if "fun clearBlockValues(" not in content:
    content = content.replace("fun removeBlock(blockId: String) {", new_func + "\n    fun removeBlock(blockId: String) {")

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "w") as f:
    f.write(content)
