package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.Keep

/**
 * A folder as the old preferences saved it, in JSON, before folders moved to Room. Every field is nullable because
 * Gson fills missing ones with null; [toAppFolder] puts the defaults back (the look fields did not exist then).
 */
@Keep
data class LegacyFolderJson(
    val id: String? = null,
    val panelId: String? = null,
    val name: String? = null,
    val iconPackage: String? = null,
    val icon: String? = null,
    val appPackageNames: List<String>? = null,
    val displayAsGrid: Boolean? = null,
) {
    fun toAppFolder(): AppFolder {
        val defaults = AppFolder(name = "")
        return AppFolder(
            id = id ?: defaults.id,
            panelId = panelId,
            name = name.orEmpty(),
            iconPackage = iconPackage,
            icon = icon,
            appPackageNames = appPackageNames.orEmpty(),
            displayAsGrid = displayAsGrid ?: false
        )
    }
}
