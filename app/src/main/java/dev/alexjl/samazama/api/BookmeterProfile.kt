package dev.alexjl.samazama.api

import dev.alexjl.samazama.data.UserProfile
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

const val DEFAULT_PROFILE_IMAGE_URL = "$BOOKMETER_BASE_URL/images/common/profile_image.png"

suspend fun fetchUserProfile(): UserProfile =
    parseUserProfile(fetchBookmeterPage(BOOKMETER_HOME_URL, requiresLogin = true))
        ?: throw IOException("GET $BOOKMETER_HOME_URL returned a page without a current user")

fun parseUserProfile(html: String): UserProfile? {
    val document = Jsoup.parse(html)
    val user = document.selectFirst("meta[name=current_user]")?.attr("content")
        ?.let(::parseCurrentUser) ?: return null
    val id = user.optInt("id").takeIf { it > 0 } ?: return null
    val image = user.optString("image").ifBlank { DEFAULT_PROFILE_IMAGE_URL }
    return UserProfile(
        id = id,
        name = user.optString("name"),
        imageUrl = if (image.startsWith("/")) BOOKMETER_BASE_URL + image else image,
        readCount = document.bookCount("read"),
        readingCount = document.bookCount("reading"),
        stackedCount = document.bookCount("stacked"),
        wishCount = document.bookCount("wish")
    )
}

/** JSON such as `{"user":{"id":1,"path":"/users/1","name":"…","image":"https://…"}}`. */
private fun parseCurrentUser(json: String): JSONObject? =
    try {
        JSONObject(json).optJSONObject("user")
    } catch (e: JSONException) {
        null
    }

private fun Document.bookCount(list: String): Int? =
    selectFirst(".userdata-nav a[href$=/books/$list] .userdata-nav__count")
        ?.ownText()?.replace(",", "")?.trim()?.toIntOrNull()
