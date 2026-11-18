package dev.mnascimentos.aureole.feature.home.components.containers.model

/** What the container sheet content can ask of its screen: switch page, or leave for Customize. */
data class EditContainerSheetActions(
    val onModeChange: (EditContainerScreenMode) -> Unit,
    val onCustomizeHome: () -> Unit
)
