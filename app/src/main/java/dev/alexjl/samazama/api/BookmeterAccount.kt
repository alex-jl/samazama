package dev.alexjl.samazama.api

import dev.alexjl.samazama.data.AccountDetails
import dev.alexjl.samazama.data.AccountField
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

const val BOOKMETER_ACCOUNT_URL = "$BOOKMETER_BASE_URL/account"

suspend fun fetchAccountDetails(): AccountDetails =
    parseAccountDetails(fetchBookmeterPage(BOOKMETER_ACCOUNT_URL, requiresLogin = true))

fun parseAccountDetails(html: String): AccountDetails {
    val document = Jsoup.parse(html)
    return AccountDetails(
        pageTitle = document.title(),
        fields = document.select(ACCOUNT_FIELDS).mapNotNull { parseField(document, it) }
    )
}

private fun parseField(document: Document, input: Element): AccountField? {
    val value = input.`val`().trim().takeIf { it.isNotEmpty() } ?: return null
    val id = input.id()
    val label = document.takeIf { id.isNotEmpty() }
        ?.selectFirst("label[for=\"$id\"]")?.text()?.takeIf { it.isNotEmpty() }
        ?: input.attr("name").takeIf { it.isNotEmpty() }
        ?: return null
    return AccountField(label = label, value = value)
}

private const val ACCOUNT_FIELDS =
    "input[type=text], input[type=email], input[type=url], input[type=tel], textarea"
