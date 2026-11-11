package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * A 56 dp row with a title, an optional description, an optional icon column at the start and a switch at
 * the end. Mirrored for left-handed users so
 * the switch sits at the thumb side. The whole row toggles.
 */
@Composable
fun AureoleSwitchRow(params: SwitchRowParams, modifier: Modifier = Modifier) {
    LeftHandMirror {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = AureoleDS.dimens.menuRowHeight)
                .toggleable(value = params.checked, role = Role.Switch, onValueChange = params.onCheckedChange)
                .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            params.icon?.let { MenuIconColumn(icon = it) }
            Column(modifier = Modifier.weight(1f)) {
                AureoleText(
                    text = params.title,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyLarge.copy(textDirection = TextDirection.Content)
                    )
                )
                if (params.description != null) {
                    AureoleText(
                        text = params.description,
                        color = AureoleDS.colors.onSurfaceLow,
                        format = AureoleTextFormatParams(
                            style = AureoleDS.typography.bodySmall.copy(textDirection = TextDirection.Content)
                        )
                    )
                }
            }
            // The switch thumb must not flip with the mirror, or an ON switch looks OFF.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Switch(checked = params.checked, onCheckedChange = null)
            }
        }
    }
}

@AureolePreview
@Composable
private fun AureoleSwitchRowPreview() {
    AureoleLauncherTheme {
        Column {
            AureoleSwitchRow(
                SwitchRowParams(
                    title = "Show all apps",
                    checked = true,
                    onCheckedChange = {},
                    description = "Favorites only"
                )
            )
            AureoleSwitchRow(
                SwitchRowParams(
                    title = "Show indicator",
                    checked = false,
                    onCheckedChange = {},
                    icon = { m, c -> AureoleDS.icons.Dots(modifier = m, tint = c) }
                )
            )
        }
    }
}
