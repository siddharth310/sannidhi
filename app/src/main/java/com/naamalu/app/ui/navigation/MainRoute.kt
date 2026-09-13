package com.naamalu.app.ui.navigation

object MainRoute {
    const val Home = "home"
    const val Library = "library"
    const val Japa = "japa"
    const val Saadhana = "saadhana"
    const val Settings = "settings"

    const val DeityDetail = "library/deity/{deityId}"
    const val NaamavaliDetail = "library/naamavali/{naamavaliId}"
    const val DayDetail = "saadhana/day/{date}"

    fun deityDetail(deityId: String) = "library/deity/$deityId"
    fun naamavaliDetail(naamavaliId: String) = "library/naamavali/$naamavaliId"
    fun dayDetail(date: String) = "saadhana/day/$date"

    val bottomTabs = setOf(Home, Library, Japa, Saadhana)
}
