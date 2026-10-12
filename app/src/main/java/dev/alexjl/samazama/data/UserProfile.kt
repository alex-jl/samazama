package dev.alexjl.samazama.data

data class UserProfile(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val readCount: Int? = null,
    val readingCount: Int? = null,
    val stackedCount: Int? = null,
    val wishCount: Int? = null
)

val sampleUserProfile = UserProfile(
    id = 123456,
    name = "本の虫",
    imageUrl = "https://bookmeter.com/images/common/profile_image.png",
    readCount = 1234,
    readingCount = 2,
    stackedCount = 35,
    wishCount = 108
)
