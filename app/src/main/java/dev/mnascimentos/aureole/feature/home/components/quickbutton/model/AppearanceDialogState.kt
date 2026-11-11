package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel

internal data class AppearanceDialogState(
    val colorPickerTarget: QuickButtonColorPickerTarget?,
    val activeModel: QuickButtonModel,
    val showIconPicker: Boolean,
    val onUpdateAppearance: (QuickButtonAppearance) -> Unit,
    val isLeftHanded: Boolean = false
)
