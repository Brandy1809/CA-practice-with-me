import re

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "r") as f:
    content = f.read()

new_func = """    fun generateExportJson(): String {
        val state = _uiState.value
        val json = org.json.JSONObject()
        
        state.currentQuestion?.let { q ->
            val qObj = org.json.JSONObject().apply {
                put("id", q.id)
                put("topic", q.topic)
                put("questionText", q.questionText)
                put("subject", q.subject.name)
                put("chapter", q.chapter)
                put("depth", q.depth.name)
                put("mode", q.mode.name)
                put("modelAnswer", q.modelAnswer)
                put("explanation", q.explanation)
            }
            json.put("question", qObj)
        }
        json.put("userAnswer", state.userAnswerText)
        
        val blocksArr = org.json.JSONArray()
        state.insertedBlocks.forEachIndexed { index, block ->
            val bObj = org.json.JSONObject().apply {
                put("id", block.id)
                put("type", block.type.name)
                put("order", index)
                put("title", block.displayTitle)
                put("isCollapsed", block.isCollapsed)
                
                // Working Note
                state.workingNotes[block.id]?.let { wn ->
                    val wnObj = org.json.JSONObject()
                    wnObj.put("title", wn.title)
                    wnObj.put("content", wn.content)
                    put("workingNote", wnObj)
                }
                
                // Two-Sided Data
                state.twoSidedBlocks[block.id]?.let { ts ->
                    val tsObj = org.json.JSONObject()
                    val drArr = org.json.JSONArray()
                    ts.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("particulars", it.particulars); put("amount", it.amount) }) }
                    val crArr = org.json.JSONArray()
                    ts.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("particulars", it.particulars); put("amount", it.amount) }) }
                    tsObj.put("drRows", drArr)
                    tsObj.put("crRows", crArr)
                    put("twoSidedData", tsObj)
                }
                
                // Ledger Data
                state.ledgerBlocks[block.id]?.let { lg ->
                    val lgObj = org.json.JSONObject()
                    lgObj.put("accountName", lg.accountName)
                    val drArr = org.json.JSONArray()
                    lg.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("jf", it.jf); put("amount", it.amount) }) }
                    val crArr = org.json.JSONArray()
                    lg.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("jf", it.jf); put("amount", it.amount) }) }
                    lgObj.put("drRows", drArr)
                    lgObj.put("crRows", crArr)
                    put("ledgerData", lgObj)
                }

                // Journal Data
                state.journalBlocks[block.id]?.let { rows ->
                    val rArr = org.json.JSONArray()
                    rows.forEach { rArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("lf", it.lf); put("drAmount", it.drAmount); put("crAmount", it.crAmount) }) }
                    put("journalData", rArr)
                }

                // Trial Balance Data
                state.trialBalanceBlocks[block.id]?.let { rows ->
                    val rArr = org.json.JSONArray()
                    rows.forEach { rArr.put(org.json.JSONObject().apply { put("serialNo", it.serialNo); put("headOfAccount", it.headOfAccount); put("lf", it.lf); put("drBalance", it.drBalance); put("crBalance", it.crBalance) }) }
                    put("trialBalanceData", rArr)
                }

                // BRS Data
                state.brsBlocks[block.id]?.let { brs ->
                    val brsObj = org.json.JSONObject()
                    brsObj.put("startingBalance", brs.startingBalance)
                    val rArr = org.json.JSONArray()
                    brs.rows.forEach { rArr.put(org.json.JSONObject().apply { put("particulars", it.particulars); put("isAdd", it.isAdd); put("amount", it.amount) }) }
                    brsObj.put("rows", rArr)
                    put("brsData", brsObj)
                }

                // Cash Book Data
                state.cashBookBlocks[block.id]?.let { cb ->
                    val cbObj = org.json.JSONObject()
                    val drArr = org.json.JSONArray()
                    cb.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("vn", it.vn); put("lf", it.lf); put("discount", it.discount); put("cash", it.cash); put("bank", it.bank) }) }
                    val crArr = org.json.JSONArray()
                    cb.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("vn", it.vn); put("lf", it.lf); put("discount", it.discount); put("cash", it.cash); put("bank", it.bank) }) }
                    cbObj.put("drRows", drArr)
                    cbObj.put("crRows", crArr)
                    put("cashBookData", cbObj)
                }

                // Partners Capital Data
                state.partnersCapitalBlocks[block.id]?.let { pc ->
                    val pcObj = org.json.JSONObject()
                    val drArr = org.json.JSONArray()
                    pc.drRows.forEach { drArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("partnerA", it.partnerA); put("partnerB", it.partnerB); put("partnerC", it.partnerC) }) }
                    val crArr = org.json.JSONArray()
                    pc.crRows.forEach { crArr.put(org.json.JSONObject().apply { put("date", it.date); put("particulars", it.particulars); put("partnerA", it.partnerA); put("partnerB", it.partnerB); put("partnerC", it.partnerC) }) }
                    pcObj.put("drRows", drArr)
                    pcObj.put("crRows", crArr)
                    put("partnersCapitalData", pcObj)
                }

                // Petty Cash Data
                state.pettyCashBlocks[block.id]?.let { rows ->
                    val rArr = org.json.JSONArray()
                    rows.forEach { rArr.put(org.json.JSONObject().apply { put("receipts", it.receipts); put("date", it.date); put("vn", it.vn); put("particulars", it.particulars); put("totalPayments", it.totalPayments); put("postage", it.postage); put("conveyance", it.conveyance); put("stationery", it.stationery); put("misc", it.misc) }) }
                    put("pettyCashData", rArr)
                }
            }
            blocksArr.put(bObj)
        }
        json.put("insertedBlocks", blocksArr)
        
        return json.toString(4)
    }"""

pattern = r"    fun generateExportJson\(\): String \{.*?(?=    fun updateWorkingNote)"
new_content = re.sub(pattern, new_func + "\n", content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/PracticeViewModel.kt", "w") as f:
    f.write(new_content)
