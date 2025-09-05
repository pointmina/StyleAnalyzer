package com.hanto.styleanalyzer.domain.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class SwipeActionSerializationTest {
    @Test
    fun swipeAction_serializesAndDeserializes() {
        val original = SwipeAction(
            id = "action1",
            itemId = "item1",
            action = SwipeType.LIKE,
            sessionId = "session1",
            timestamp = 123456789L
        )
        val gson = Gson()
        val json = gson.toJson(original)
        val restored = gson.fromJson(json, SwipeAction::class.java)
        assertEquals(original, restored)
    }
}
