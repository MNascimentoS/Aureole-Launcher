package dev.mnascimentos.aureole.composable.appitem.model

data class AppItemRowConfig(
    val isFavorite: Boolean = false,
    val useActionSheet: Boolean = true,
    val isThemedAppIconsEnabled: Boolean = false,
    val isLeftHandedMode: Boolean = false
)
