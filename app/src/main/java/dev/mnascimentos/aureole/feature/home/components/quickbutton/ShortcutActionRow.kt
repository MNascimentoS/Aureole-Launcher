package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.menu.AureoleRadioRow
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.icons.Image
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Plus
import dev.mnascimentos.aureole.core.designsystem.icons.Press
import dev.mnascimentos.aureole.core.designsystem.icons.RotateClockwise
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.icons.Users
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
            AureoleAppIcon(app = app, size = AureoleDS.dimens.iconLarge)
        } else {
            val modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            ShortcutActionIcon(action, AureoleDS.colors.onSurfaceMedium, modifier)
        }
    }
}

@Composable
private fun ShortcutActionIcon(action: ShortcutAction, tint: Color, modifier: Modifier = Modifier) {
    when (action.type) {
        ShortcutType.ALL_APPS -> AureoleDS.icons.AllApps(modifier = modifier, tint = tint)
        ShortcutType.ASSISTANT -> AureoleDS.icons.Star(modifier = modifier, tint = tint)
        ShortcutType.SETTINGS -> AureoleDS.icons.Settings(modifier = modifier, tint = tint)
        ShortcutType.NOTIFICATIONS -> AureoleDS.icons.Dots(modifier = modifier, tint = tint)
        ShortcutType.NONE -> AureoleDS.icons.Close(modifier = modifier, tint = tint)
        ShortcutType.OPEN_APP -> AureoleDS.icons.AppSelect(modifier = modifier, tint = tint)
        ShortcutType.SYSTEM_INTENT -> SystemShortcutIcon(action.target, tint, modifier)
    }
}

/** The DS icon of a system shortcut with no app to take the icon from: one per target, never the placeholder. */
@Composable
private fun SystemShortcutIcon(target: String, tint: Color, modifier: Modifier) {
    val icons = AureoleDS.icons
    when (target) {
        "Browser", "Search" -> icons.Search(modifier = modifier, tint = tint)
        "Calendar" -> icons.Calendar(modifier = modifier, tint = tint)
        "Camera" -> icons.Image(modifier = modifier, tint = tint)
        "Clock" -> icons.RotateClockwise(modifier = modifier, tint = tint)
        "Contacts" -> icons.Users(modifier = modifier, tint = tint)
        "Dialer" -> icons.Press(modifier = modifier, tint = tint)
        "Email", "Messages" -> icons.Text(modifier = modifier, tint = tint)
        "Files" -> icons.Folder(modifier = modifier, tint = tint)
        "Music Player" -> icons.Star(modifier = modifier, tint = tint)
        "Device Settings" -> icons.Info(modifier = modifier, tint = tint)
        "Calculator" -> icons.Plus(modifier = modifier, tint = tint)
        else -> icons.Settings(modifier = modifier, tint = tint)
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
