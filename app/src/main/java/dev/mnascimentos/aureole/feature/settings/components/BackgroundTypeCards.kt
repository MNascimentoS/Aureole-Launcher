package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Image
import dev.mnascimentos.aureole.core.designsystem.icons.StrokeColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.BackgroundPhotoCard
import dev.mnascimentos.aureole.feature.settings.model.BackgroundType

/**
 * The two background types as selectable cards side by side, both as tall as the taller one; the selected one gets
 * a ring. With a photo applied ([photo]), the "Custom image" card shows it with "Edit photo" and "Change photo"
 * (RF-02); the "Solid color" content stays centered.
 */
@Composable
internal fun BackgroundTypeCards(
    selected: BackgroundType,
    onSelect: (BackgroundType) -> Unit,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false,
    photo: BackgroundPhotoCard? = null
) {
    LeftHandMirror(isLeftHanded = isLeftHanded) {
        Row(
            modifier = modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            TypeCard(
                isSelected = selected == BackgroundType.SOLID_COLOR,
                onClick = { onSelect(BackgroundType.SOLID_COLOR) }
            ) {
                AureoleDS.icons.StrokeColor(tint = AureoleDS.colors.onSurfaceHigh)
                TypeLabel(stringResource(R.string.settings_appearance_solid_color))
            }
            TypeCard(
                isSelected = selected == BackgroundType.CUSTOM_IMAGE,
                onClick = { onSelect(BackgroundType.CUSTOM_IMAGE) }
            ) {
                val label = stringResource(R.string.settings_appearance_custom_image)
                if (photo != null) {
                    BackgroundPhotoContent(photo = photo, label = label)
                } else {
                    AureoleDS.icons.Image(tint = AureoleDS.colors.onSurfaceHigh)
                    TypeLabel(label)
                }
            }
        }
    }
}

@Composable
private fun RowScope.TypeCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .heightIn(min = AureoleDS.dimens.sizeLarge)
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
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall, Alignment.CenterVertically),
        content = content
    )
}

@Composable
internal fun TypeLabel(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceHigh,
        format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, textAlign = TextAlign.Center)
    )
}

@AureolePreview
@Composable
private fun BackgroundTypeCardsPreview() {
    AureoleLauncherTheme {
        BackgroundTypeCards(selected = BackgroundType.SOLID_COLOR, onSelect = {})
    }
}

@AureolePreview
@Composable
private fun BackgroundTypeCardsPhotoPreview() {
    AureoleLauncherTheme {
        BackgroundTypeCards(
            selected = BackgroundType.CUSTOM_IMAGE,
            onSelect = {},
            photo = BackgroundPhotoCard(
                path = "",
                adjustments = WallpaperAdjustments.DEFAULT,
                onEdit = {},
                onChange = {}
            )
        )
    }
}
