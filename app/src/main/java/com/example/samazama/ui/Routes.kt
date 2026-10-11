package com.example.samazama.ui

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object RankingsRoute

@Serializable
data object ProfileRoute

@Serializable
data object SearchRoute

@Serializable
data object SettingsRoute

@Serializable
data class BookRoute(val id: Int)

@Serializable
data class AuthorRoute(val name: String)
