package com.smaranam.app.domain.model

data class UserProfile(
    val userId: String?,
    val phone: String?,
    val isLoggedIn: Boolean,
    val ishtaDevataId: String?,
    val defaultScript: Script,
    val prefersDarkTheme: Boolean,
    val isOnboardingComplete: Boolean,
    val showFirstRunNudge: Boolean,
) {
    val ishtaDevataName: String?
        get() = ishtaDevataId?.let { id -> SeedDeities.findById(id)?.name }

    companion object {
        val Guest = UserProfile(
            userId = null,
            phone = null,
            isLoggedIn = false,
            ishtaDevataId = null,
            defaultScript = Script.TELUGU,
            prefersDarkTheme = false,
            isOnboardingComplete = false,
            showFirstRunNudge = false,
        )
    }
}

object SeedDeities {
    val ganesha = Deity(
        id = "ganesha",
        name = "Ganesha",
        nameTe = "గణేశ",
        nameSa = "गणेश",
    )

    val lakshmi = Deity(
        id = "lakshmi",
        name = "Lakshmi",
        nameTe = "లక్ష్మీ",
        nameSa = "लक्ष्मी",
    )

    val venkateswara = Deity(
        id = "venkateswara",
        name = "Venkateswara",
        nameTe = "వెంకటేశ్వర",
        nameSa = "वेंकटेश्वर",
    )

    val availableInOnboarding = listOf(ganesha)

    fun findById(id: String): Deity? {
        return listOf(ganesha, lakshmi, venkateswara).find { it.id == id }
    }
}
