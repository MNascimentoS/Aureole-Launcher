package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

internal data class AppearanceDialogState(
    val colorPickerTarget: QuickButtonColorPickerTarget?,
    val activeModel: QuickButtonModel,
    val showIconPicker: Boolean,
    val actions: HomeScreenActions
)
