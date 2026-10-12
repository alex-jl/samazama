package dev.alexjl.samazama.api

import dev.alexjl.samazama.data.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookmeterProfileTest {

    private val html: String =
        checkNotNull(javaClass.getResourceAsStream("/home_logged_in.html")) {
            "missing test fixture"
        }.reader().use { it.readText() }

    @Test
    fun `parses the logged-in user and their book counts`() {
        assertEquals(
            UserProfile(
                id = 123456,
                name = "本の虫",
                imageUrl = "https://bookmeter.com/images/common/profile_image.png",
                readCount = 1234,
                readingCount = 2,
                stackedCount = 35,
                wishCount = 0
            ),
            parseUserProfile(html)
        )
    }

    @Test
    fun `keeps the user when the book counts are missing`() {
        val countless = html.replace(Regex("<ul class=\"userdata-nav\">.*?</ul>"), "")
        assertNotEquals(html, countless)

        assertEquals(
            UserProfile(
                id = 123456,
                name = "本の虫",
                imageUrl = "https://bookmeter.com/images/common/profile_image.png"
            ),
            parseUserProfile(countless)
        )
    }

    @Test
    fun `makes a site-relative profile picture absolute`() {
        val relative = html.replace(
            "https://bookmeter.com/images/common/profile_image.png&quot;}}",
            "/images/common/profile_image.png&quot;}}"
        )
        assertNotEquals(html, relative)

        assertEquals(
            "https://bookmeter.com/images/common/profile_image.png",
            parseUserProfile(relative)?.imageUrl
        )
    }

    @Test
    fun `falls back to the default profile picture when the user has none`() {
        val pictureless = html.replace(
            "&quot;image&quot;:&quot;https://bookmeter.com/images/common/profile_image.png&quot;",
            "&quot;image&quot;:&quot;&quot;"
        )
        assertNotEquals(html, pictureless)

        assertEquals(
            "https://bookmeter.com/images/common/profile_image.png",
            parseUserProfile(pictureless)?.imageUrl
        )
        assertEquals(
            "https://bookmeter.com/images/common/profile_image.png",
            DEFAULT_PROFILE_IMAGE_URL
        )
    }

    @Test
    fun `finds no user on a page without one`() {
        assertNull(parseUserProfile("<html><head></head><body></body></html>"))
    }

    @Test
    fun `reads the CSRF token from the home page`() {
        assertEquals("dGVzdC10b2tlbg+/abc==", parseCsrfToken(html))
    }
}
