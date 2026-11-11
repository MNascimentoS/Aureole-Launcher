package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.StrokeColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.BackgroundType

/** The two background types as selectable cards side by side; the selected one gets a ring. */
@Composable
internal fun BackgroundTypeCards(
    selected: BackgroundType,
    onSelect: (BackgroundType) -> Unit,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    LeftHandMirror(isLeftHanded = isLeftHanded) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            TypeCard(
                label = stringResource(R.string.settings_appearance_solid_color),
                isSelected = selected == BackgroundType.SOLID_COLOR,
                onClick = { onSelect(BackgroundType.SOLID_COLOR) }
            ) { AureoleDS.icons.StrokeColor(tint = AureoleDS.colors.onSurfaceHigh) }
            TypeCard(
                label = stringResource(R.string.settings_appearance_custom_image),
                isSelected = selected == BackgroundType.CUSTOM_IMAGE,
                onClick = { onSelect(BackgroundType.CUSTOM_IMAGE) }
            ) { AureoleDS.icons.Edit(tint = AureoleDS.colors.onSurfaceHigh) }
        }
    }
}

@Composable
private fun RowScope.TypeCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Column(
        modifier = Modifier
            .weight(1f)
            .height(AureoleDS.dimens.sizeLarge)
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant)
            .border(
                width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.zero,
                color = AureoleDS.colors.onSurfaceHigh,
                shape = shape
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = isSelected }
            .padding(AureoleDS.dimens.xSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        AureoleText(
            text = label,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, textAlign = TextAlign.Center)
        )
    }
}

@AureolePreview
@Composable
private fun BackgroundTypeCardsPreview() {
    AureoleLauncherTheme {
        BackgroundTypeCards(selected = BackgroundType.SOLID_COLOR, onSelect = {})
    }
}
