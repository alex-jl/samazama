package dev.alexjl.samazama.data

data class AccountDetails(
    val pageTitle: String,
    val fields: List<AccountField> = emptyList()
)

data class AccountField(
    val label: String,
    val value: String
)

val sampleAccountDetails = AccountDetails(
    pageTitle = "アカウント設定 - 読書メーター",
    fields = listOf(
        AccountField("ニックネーム", "本の虫"),
        AccountField("メールアドレス", "sample@bookmeter.com"),
    )
)
