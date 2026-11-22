package dev.alexjl.samazama.ui

import dev.alexjl.samazama.api.BookShelf
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object RankingsRoute

@Serializable
data object ProfileRoute

@Serializable
data object LoginRoute

@Serializable
data object SearchRoute

@Serializable
data object SettingsRoute

@Serializable
data class BookRoute(val id: Int)

@Serializable
data class AuthorRoute(val name: String)

@Serializable
data class BookShelfRoute(val userId: Int, val shelf: BookShelf)
