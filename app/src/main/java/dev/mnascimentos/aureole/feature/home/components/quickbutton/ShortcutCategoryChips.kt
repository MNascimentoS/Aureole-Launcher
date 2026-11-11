package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutCategory

/**
 * Pill chips (Aureole, System, Apps) above the search field. The selected one is filled; the row is mirrored for
 * left-handed users so the first chip sits at the thumb side.
 */
@Composable
internal fun ShortcutCategoryChips(
    selected: ShortcutCategory,
    onSelect: (ShortcutCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LeftHandMirror {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShortcutCategory.entries.forEach { category ->
                ShortcutCategoryChip(
                    label = stringResource(category.labelRes),
                    isSelected = category == selected,
                    onClick = { onSelect(category) }
                )
            }
        }
    }
}

@Composable
private fun ShortcutCategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val background = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = AureoleDS.dimens.minTouchTarget, minHeight = AureoleDS.dimens.iconXLarge)
            .clip(CircleShape)
            .background(background)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = label,
            color = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        )
    }
}

@AureolePreview
@Composable
private fun ShortcutCategoryChipsPreview() {
    AureoleLauncherTheme {
        ShortcutCategoryChips(selected = ShortcutCategory.AUREOLE, onSelect = {})
    }
}
