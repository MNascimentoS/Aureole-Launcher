package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.model.AureoleDialogConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val DIALOG_WIDTH_FRACTION = 0.92f

/**
 * A dialog component that follows the Aureole Design System.
 *
 * @param onDismissRequest Executes when the user tries to dismiss the dialog.
 * @param modifier The modifier to be applied to the layout.
 * @param config Configuration parameters provided via [AureoleDialogConfig].
 * @param icon Optional leading icon composable for the header title.
 * @param content The content of the dialog.
 */
@Composable
fun AureoleDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    config: AureoleDialogConfig = AureoleDialogConfig(),
    icon: (@Composable (Modifier) -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val dialogContent = @Composable {
        Surface(
            modifier = modifier
                .fillMaxWidth(DIALOG_WIDTH_FRACTION)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge)),
            shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge),
            color = AureoleDS.colors.surface,
            contentColor = AureoleDS.colors.onSurfaceHigh
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (config.title != null) {
                    AureoleDialogHeader(
                        title = config.title,
                        modifier = Modifier,
                        icon = icon,
                        onLogoClick = config.onLogoClick
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AureoleDS.dimens.large)
                ) {
                    content()
                }
            }
        }
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            dialogContent()
        }
    } else {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            dialogContent()
        }
    }
}

/**
 * Overload for [AureoleDialog] accepting a simple title string.
 */
@Composable
fun AureoleDialog(
    onDismissRequest: () -> Unit,
    title: String?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AureoleDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        config = AureoleDialogConfig(title = title),
        content = content
    )
}

/**
 * A header component for the Aureole dialog.
 *
 * @param title The title of the dialog.
 * @param modifier The modifier to be applied to the layout.
 * @param icon The icon to be displayed next to the title.
 * @param onLogoClick Optional callback triggered when the logo is clicked.
 */
@Composable
fun AureoleDialogHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: @Composable ((Modifier) -> Unit)? = null,
    onLogoClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(AureoleDS.colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = AureoleDS.dimens.xLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon(Modifier.size(AureoleDS.dimens.iconMedium))
                Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            }
            AureoleText(
                text = title,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = AureoleDS.dimens.xSmall)
                .size(AureoleDS.dimens.xLarge)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .then(
                    if (onLogoClick != null) {
                        Modifier.clickable(role = Role.Button, onClick = onLogoClick)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun AureoleDialogDefaultPreview() {
    AureoleLauncherTheme {
        AureoleDialog(
            onDismissRequest = {},
            title = "Sample Dialog Title",
        ) {
            AureoleText(
                text = "This is sample content inside AureoleDialog.",
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium),
            )
        }
    }
}

@AureolePreview
@Composable
private fun AureoleDialogWithIconAndLogoPreview() {
    AureoleLauncherTheme {
        AureoleDialog(
            onDismissRequest = {},
            config = AureoleDialogConfig(
                title = "Dialog With Icon",
                onLogoClick = {},
            ),
            icon = { m -> AureoleDS.icons.Smile(m) },
        ) {
            AureoleText(
                text = "Dialog content with leading icon and logo callback.",
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium),
            )
        }
    }
}

@AureolePreview
@Composable
private fun AureoleDialogHeaderSlotPreview() {
    AureoleLauncherTheme {
        AureoleDialogHeader(
            title = "Header Composition Title",
            icon = { m -> AureoleDS.icons.Smile(m) },
            onLogoClick = {},
        )
    }
}
