package dev.alexjl.samazama.api

import dev.alexjl.samazama.data.AccountDetails
import dev.alexjl.samazama.data.AccountField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookmeterAccountTest {

    @Test
    fun `lists the filled-in text fields under their labels`() {
        val html = """
            <html><head><title>アカウント設定 - 読書メーター</title></head><body><form>
            <input type="hidden" name="authenticity_token" value="secret" />
            <label for="user_name">ニックネーム</label>
            <input type="text" name="user[name]" id="user_name" value=" 本の虫 " />
            <input type="email" name="user[email_address]" value="sample@bookmeter.com" />
            <input type="text" name="user[url]" id="user_url" value="" />
            <input type="password" name="user[password]" value="hunter2" />
            <label for="user_introduction">自己紹介</label>
            <textarea name="user[introduction]" id="user_introduction">ミステリが好き</textarea>
            </form></body></html>
        """.trimIndent()

        assertEquals(
            AccountDetails(
                pageTitle = "アカウント設定 - 読書メーター",
                fields = listOf(
                    AccountField("ニックネーム", "本の虫"),
                    AccountField("user[email_address]", "sample@bookmeter.com"),
                    AccountField("自己紹介", "ミステリが好き"),
                )
            ),
            parseAccountDetails(html)
        )
    }

    @Test
    fun `reads the CSRF token from the page head`() {
        val html = "<html><head><meta name=\"csrf-param\" content=\"authenticity_token\" />" +
                "<meta name=\"csrf-token\" content=\"abc+/123==\" /></head><body></body></html>"

        assertEquals("abc+/123==", parseCsrfToken(html))
        assertNull(parseCsrfToken("<html><head></head><body></body></html>"))
    }

    @Test
    fun `keeps the page title when there are no fields`() {
        assertEquals(
            AccountDetails(pageTitle = "アカウント"),
            parseAccountDetails("<html><head><title>アカウント</title></head><body></body></html>")
        )
    }
}
