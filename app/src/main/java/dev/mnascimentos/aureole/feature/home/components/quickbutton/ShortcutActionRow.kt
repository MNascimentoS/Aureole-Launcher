package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.menu.AureoleRadioRow
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Placeholder
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSearchCandidate

/**
 * A shortcut option: icon tile (the app icon when the candidate has an app), label and the radio marker, the
 * last two at the thumb side. The radio row mirrors itself for left-handed users.
 */
@Composable
internal fun ShortcutActionRow(
    candidate: ShortcutSearchCandidate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleRadioRow(
        label = candidate.label,
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier,
        icon = { ShortcutIconTile(action = candidate.action, app = candidate.app) }
    )
}

@Composable
private fun ShortcutIconTile(action: ShortcutAction, app: AppInfo?) {
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.iconXLarge)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(AureoleDS.colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (app != null) {
            Image(
                bitmap = app.getIconBitmap(),
                contentDescription = null,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge)
            )
        } else {
            val modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            ShortcutActionIcon(action.type, modifier, AureoleDS.colors.onSurfaceMedium)
        }
    }
}

@Composable
private fun ShortcutActionIcon(type: ShortcutType, modifier: Modifier, tint: Color) {
    when (type) {
        ShortcutType.ALL_APPS -> AureoleDS.icons.AllApps(modifier = modifier, tint = tint)
        ShortcutType.ASSISTANT -> AureoleDS.icons.Star(modifier = modifier, tint = tint)
        ShortcutType.SETTINGS -> AureoleDS.icons.Settings(modifier = modifier, tint = tint)
        ShortcutType.NOTIFICATIONS -> AureoleDS.icons.Dots(modifier = modifier, tint = tint)
        ShortcutType.NONE -> AureoleDS.icons.Close(modifier = modifier, tint = tint)
        ShortcutType.OPEN_APP -> AureoleDS.icons.AppSelect(modifier = modifier, tint = tint)
        ShortcutType.SYSTEM_INTENT -> AureoleDS.icons.Placeholder(modifier = modifier, tint = tint)
    }
}

@AureolePreview
@Composable
private fun ShortcutActionRowPreview() {
    AureoleLauncherTheme {
        ShortcutActionRow(
            candidate = ShortcutSearchCandidate(ShortcutAction.AllApps, "All apps"),
            isSelected = true,
            onClick = {}
        )
    }
}
