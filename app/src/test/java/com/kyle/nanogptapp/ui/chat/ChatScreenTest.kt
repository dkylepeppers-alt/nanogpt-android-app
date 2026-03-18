package com.kyle.nanogptapp.ui.chat

import org.junit.Assert.assertEquals
import org.junit.Test

class ChatScreenTest {
    @Test
    fun chatStatusMessage_withoutApiKey_promptsUserToVisitSettings() {
        assertEquals(
            "No API key configured yet. Use Settings to save one before enabling live requests.",
            chatStatusMessage(hasApiKey = false),
        )
    }

    @Test
    fun chatStatusMessage_withApiKey_reportsReadyState() {
        assertEquals(
            "Ready for local chat input. Live requests stay disabled until networking is wired.",
            chatStatusMessage(hasApiKey = true),
        )
    }
}
