package dev.mnascimentos.aureole.core.designsystem.components

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
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.model.AureolePopupHeaderParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

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
                .then(
                    if (onLogoClick != null) {
                        Modifier.clickable(onClick = onLogoClick, role = Role.Button)
                    } else {
                        Modifier
                    }
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
 * Standard Popup Header displaying a surfaceVariant top background bar, optional app/folder icon badge,
 * main title, optional session subtitle, and top-right action icon (Settings/Logo/Close).
 *
 * @param title The primary title text of the popup header.
 * @param modifier The [Modifier] applied to the header layout. First optional parameter.
 * @param params Additional configuration options provided via [AureolePopupHeaderParams].
 */
@Composable
fun AureolePopupHeader(
    title: String,
    modifier: Modifier = Modifier,
    params: AureolePopupHeaderParams = AureolePopupHeaderParams(),
) {
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xSmall)
                .padding(end = AureoleDS.dimens.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PopupHeaderIconBadge(
                title = title,
                iconBitmap = params.iconBitmap,
                customIcon = params.customIcon
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            PopupHeaderTitleGroup(
                title = title,
                sessionTitle = params.sessionTitle,
                modifier = Modifier.weight(1f)
            )
        }

        PopupHeaderActions(params = params, modifier = Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun PopupHeaderIconBadge(
    title: String,
    iconBitmap: ImageBitmap?,
    customIcon: (@Composable () -> Unit)?
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
}

@Composable
private fun PopupHeaderTitleGroup(
    title: String,
    sessionTitle: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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

@Composable
private fun PopupHeaderActions(
    params: AureolePopupHeaderParams,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = { params.onSettingsClick?.invoke() },
            modifier = Modifier.size(AureoleDS.dimens.iconXLarge),
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall),
            )
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
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
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
