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
