package dev.mnascimentos.aureole.feature.home.components.model

import dev.mnascimentos.aureole.core.data.model.AppInfo

data class AppItemRowActions(
    val onToggleFavorite: ((String) -> Unit)? = null,
    val onEditFavoritesClick: (() -> Unit)? = null,
    val onAppInfoClick: ((AppInfo) -> Unit)? = null
)
