package dev.alexjl.samazama.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookmeterClientTest {

    @Test
    fun `reads the CSRF token from the page head`() {
        val html = "<html><head><meta name=\"csrf-param\" content=\"authenticity_token\" />" +
                "<meta name=\"csrf-token\" content=\"abc+/123==\" /></head><body></body></html>"

        assertEquals("abc+/123==", parseCsrfToken(html))
        assertNull(parseCsrfToken("<html><head></head><body></body></html>"))
    }
}
