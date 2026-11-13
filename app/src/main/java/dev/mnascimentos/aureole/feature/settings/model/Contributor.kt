package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

private const val GITHUB_AISLAN = "https://github.com/thisaislan"
private const val GITHUB_MATEUS = "https://github.com/MNascimentoS"

data class Contributor(
    @StringRes val name: Int,
    @StringRes val role: Int,
    @DrawableRes val avatar: Int,
    val githubUrl: String
)

val Contributors: List<Contributor> = listOf(
    Contributor(
        name = R.string.settings_contributors_aislan_name,
        role = R.string.settings_contributors_aislan_role,
        avatar = R.drawable.contributor_aislan,
        githubUrl = GITHUB_AISLAN
    ),
    Contributor(
        name = R.string.settings_contributors_mateus_name,
        role = R.string.settings_contributors_mateus_role,
        avatar = R.drawable.contributor_mateus,
        githubUrl = GITHUB_MATEUS
    )
)
