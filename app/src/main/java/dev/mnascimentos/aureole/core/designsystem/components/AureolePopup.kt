package dev.mnascimentos.aureole.core.designsystem.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.SettingsActivity

private const val HAZE_ALPHA = 0.85f
private const val OPAQUE_ALPHA = 1f

/**
 * Standard Popup Container for Aureole Launcher popups, matching Figma specifications.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param cornerRadius The corner radius of the popup.
 * @param hazeState The haze state for the background blur effect.
 * @param content The content of the popup.
 */
@Composable
fun AureolePopupBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = AureoleDS.dimens.radiusMedium,
    hazeState: HazeState? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val surfaceColor = AureoleDS.colors.surface

    val hazeModifier = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(surfaceColor.copy(alpha = HAZE_ALPHA)),
            ),
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val bgAlpha = if (hazeState != null) HAZE_ALPHA else OPAQUE_ALPHA

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .then(hazeModifier)
            .background(surfaceColor.copy(alpha = bgAlpha))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

/**
 * Header Banner matching design:
 * Filled top banner with height (48dp), centered title, and Aureole logo icon
 * aligned to top right with 4dp margin. Clipped to top corner radius.
 * Clicking the Aureole logo (or header banner) navigates to Aureole Settings screen.
 *
 * @param title The title of the banner.
 * @param modifier The modifier to be applied to the layout.
 * @param onLogoClick Executes when the user clicks the logo.
 */
@Composable
fun AureoleHeaderBanner(
    title: String,
    modifier: Modifier = Modifier,
    onLogoClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val handleSettingsClick = {
        if (onLogoClick != null) {
            onLogoClick()
        } else {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(
                RoundedCornerShape(
                    topStart = AureoleDS.dimens.radiusMedium,
                    topEnd = AureoleDS.dimens.radiusMedium,
                )
            )
            .background(AureoleDS.colors.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = AureoleDS.dimens.iconXLarge),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = AureoleDS.dimens.xxSmall, end = AureoleDS.dimens.xxSmall)
                .size(AureoleDS.dimens.iconXLarge)
                .clip(CircleShape)
                .clickable(
                    onClick = handleSettingsClick,
                    role = Role.Button
                ),
            contentAlignment = Alignment.Center,
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall),
            )
        }
    }
}

/**
 * Configuration parameters for [AureolePopupHeader].
 *
 * @param sessionTitle The optional subtitle or session name displayed under the main title.
 * @param iconBitmap Optional bitmap image to be rendered as the header icon badge.
 * @param showAureoleLogo Whether the top-right Aureole logo button should be shown.
 * @param onSettingsClick Callback invoked when the settings icon is clicked.
 * @param onCloseClick Callback invoked when the close icon is clicked.
 * @param customIcon Optional custom `@Composable` slot for rendering a custom icon badge.
 */
class AureolePopupHeaderParams(
    val sessionTitle: String? = null,
    val iconBitmap: ImageBitmap? = null,
    val showAureoleLogo: Boolean = true,
    val onSettingsClick: (() -> Unit)? = null,
    val onCloseClick: (() -> Unit)? = null,
    val customIcon: (@Composable () -> Unit)? = null
)

/**
 * Standard Popup Header displaying a surfaceVariant top background bar, optional app/folder icon badge,
 * main title, optional session subtitle, and top-right action icon (Settings/Logo/Close).
 *
 * @param title The primary title text of the popup header.
 * @param modifier The [Modifier] applied to the header layout. First optional parameter.
 * @param params Additional configuration options provided via [AureolePopupHeaderParams].
 */
@Suppress("LongMethod") // AureolePopupHeader provides structured header with logo, title, and slots
@Composable
fun AureolePopupHeader(
    title: String,
    modifier: Modifier = Modifier,
    params: AureolePopupHeaderParams = AureolePopupHeaderParams(),
) {
    val sessionTitle = params.sessionTitle
    val iconBitmap = params.iconBitmap
    val showAureoleLogo = params.showAureoleLogo
    val onSettingsClick = params.onSettingsClick
    val onCloseClick = params.onCloseClick
    val customIcon = params.customIcon
    val context = LocalContext.current
    val handleOpenSettings = {
        if (onSettingsClick != null) {
            onSettingsClick()
        } else {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = AureoleDS.dimens.radiusMedium,
                    topEnd = AureoleDS.dimens.radiusMedium,
                )
            )
            .background(AureoleDS.colors.surfaceVariant)
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = AureoleDS.dimens.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconXLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                    .background(AureoleDS.colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    customIcon != null -> customIcon()
                    iconBitmap != null -> Image(
                        bitmap = iconBitmap,
                        contentDescription = title,
                        modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                    )
                    else -> AureoleText(
                        text = title.take(1).uppercase(),
                        color = AureoleDS.colors.onSurfaceHigh,
                        format = AureoleTextFormatParams(
                            style = AureoleDS.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))

            Column(modifier = Modifier.weight(1f)) {
                AureoleText(
                    text = title,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                )

                if (!sessionTitle.isNullOrBlank()) {
                    AureoleText(
                        text = sessionTitle,
                        color = AureoleDS.colors.onSurfaceMedium,
                        format = AureoleTextFormatParams(
                            style = AureoleDS.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            if (showAureoleLogo) {
                IconButton(
                    onClick = handleOpenSettings,
                    modifier = Modifier.size(AureoleDS.dimens.iconXLarge),
                ) {
                    AureoleDS.icons.Logo(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall),
                    )
                }
            } else if (onSettingsClick != null) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(AureoleDS.dimens.iconXLarge),
                ) {
                    AureoleDS.icons.Settings(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium),
                    )
                }
            } else if (onCloseClick != null) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(AureoleDS.dimens.iconXLarge),
                ) {
                    AureoleDS.icons.Close(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium),
                    )
                }
            }
        }
    }
}

/**
 * Section Title divider row inside popups (e.g. "Titulo de sessão", "Folder actions", "App actions").
 *
 * @param title The title of the section.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun AureolePopupMenuSection(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = AureoleDS.dimens.xSmall,
                bottom = AureoleDS.dimens.xxSmall,
                start = AureoleDS.dimens.xxSmall,
                end = AureoleDS.dimens.xxSmall
            ),
    ) {
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, fontWeight = FontWeight.SemiBold)
        )
    }
}

@AureolePreview
@Composable
private fun AureolePopupBoxPreview() {
    AureoleLauncherTheme {
        AureolePopupBox(
            modifier = Modifier.width(260.dp),
        ) {
            AureolePopupHeader(
                title = "Folder Name",
                params = AureolePopupHeaderParams(
                    sessionTitle = "Session Subtitle",
                    onSettingsClick = {},
                )
            )
            AureolePopupMenuSection(title = "Section Header")
            AureoleText(
                text = "Popup Body Content",
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.padding(AureoleDS.dimens.small),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    }
}
