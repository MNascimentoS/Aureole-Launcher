package dev.mnascimentos.aureole.core.data.model

/** Room between the items of an app & folder group (grupo-apps-pastas RF-D3, D-05); [NORMAL] is the old spacing. */
enum class ContainerItemSpacing {
    TIGHT,
    NORMAL,
    WIDE
}

/** Reads a spacing saved by name; unknown or missing is [ContainerItemSpacing.NORMAL]. */
fun containerItemSpacingOf(name: String?): ContainerItemSpacing =
    ContainerItemSpacing.entries.firstOrNull { it.name == name } ?: ContainerItemSpacing.NORMAL
