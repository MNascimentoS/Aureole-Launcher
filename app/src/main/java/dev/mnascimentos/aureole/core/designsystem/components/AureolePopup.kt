package dev.mnascimentos.aureole.core.designsystem.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
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
 */
@Composable
fun AureolePopupBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = AureoleDS.dimens.radiusMedium,
    hazeState: HazeState? = null,
    contentPadding: PaddingValues = PaddingValues(AureoleDS.dimens.small),
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
            .padding(contentPadding)
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
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleDS.colors.onSurfaceHigh,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 36.dp),
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = AureoleDS.dimens.xxSmall, end = AureoleDS.dimens.xxSmall)
                .size(AureoleDS.dimens.iconXLarge)
                .clip(CircleShape)
                .clickable(onClick = handleSettingsClick),
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
 * Standard Popup Header with distinct header background bar (surfaceVariant),
 * app/folder icon badge, main title, and top-right Aureole logo icon aligned to TopEnd (4dp margin).
 * Clipped to top corner radius.
 */
@Suppress("LongParameterList", "LongMethod")
@Composable
fun AureolePopupHeader(
    title: String,
    modifier: Modifier = Modifier,
    sessionTitle: String? = null,
    iconVector: @Composable ((Modifier) -> Unit)? = null,
    iconBitmap: ImageBitmap? = null,
    customIcon: (@Composable () -> Unit)? = null,
    showAureoleLogo: Boolean = true,
    onSettingsClick: (() -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null,
) {
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
                    iconVector != null -> iconVector(
                        Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                    else -> AureoleText(
                        text = title.take(1).uppercase(),
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleDS.colors.onSurfaceHigh,
                    )
                }
            }

            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))

            Column(modifier = Modifier.weight(1f)) {
                AureoleText(
                    text = title,
                    style = AureoleDS.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AureoleDS.colors.onSurfaceHigh,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!sessionTitle.isNullOrBlank()) {
                    AureoleText(
                        text = sessionTitle,
                        style = AureoleDS.typography.labelSmall,
                        color = AureoleDS.colors.onSurfaceMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = AureoleDS.dimens.xxSmall, end = AureoleDS.dimens.xxSmall),
        ) {
            if (showAureoleLogo) {
                IconButton(
                    onClick = handleOpenSettings,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                ) {
                    AureoleDS.icons.Logo(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall),
                    )
                }
            } else if (onSettingsClick != null) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                ) {
                    AureoleDS.icons.Settings(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall),
                    )
                }
            } else if (onCloseClick != null) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                ) {
                    AureoleDS.icons.Close(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall),
                    )
                }
            }
        }
    }
}

/**
 * Section Title divider row inside popups (e.g. "Titulo de sessão", "Folder actions", "App actions").
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
            style = AureoleDS.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = AureoleDS.colors.onSurfaceMedium,
        )
    }
}

/**
 * Standard Popup Menu Action Row Item with icon, title, optional badge, or trailing action button.
 */
@Suppress("LongParameterList", "LongMethod")
@Composable
fun AureolePopupMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable ((Modifier) -> Unit)? = null,
    customIcon: (@Composable () -> Unit)? = null,
    badge: String? = null,
    trailingIcon: @Composable ((Modifier) -> Unit)? = null,
    onTrailingClick: (() -> Unit)? = null,
    isDestructive: Boolean = false,
) {
    val textColor = if (isDestructive) MaterialTheme.colorScheme.error else AureoleDS.colors.onSurfaceHigh

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .clickable(onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                customIcon != null -> customIcon()
                icon != null -> icon(Modifier.size(AureoleDS.dimens.iconMedium))
            }

            if (icon != null || customIcon != null) {
                Spacer(modifier = Modifier.width(AureoleDS.dimens.iconMedium))
            }

            AureoleText(
                text = title,
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall),
        ) {
            if (!badge.isNullOrBlank()) {
                AureoleText(
                    text = badge,
                    style = AureoleDS.typography.labelMedium,
                    color = AureoleDS.colors.onSurfaceLow,
                )
            }

            if (trailingIcon != null) {
                IconButton(
                    onClick = { (onTrailingClick ?: onClick).invoke() },
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                ) {
                    trailingIcon(Modifier.size(AureoleDS.dimens.iconSmall))
                }
            }
        }
    }
}

@AureolePreview
@Composable
fun AureolePopupPreview() {
    AureoleLauncherTheme {
        AureolePopupBox(
            modifier = Modifier.width(260.dp),
        ) {
            AureolePopupHeader(
                title = "Folder name",
                sessionTitle = "Folder actions",
                onSettingsClick = {},
                onCloseClick = {},
            )

            AureolePopupMenuSection(title = "Titulo de sessão")

            AureolePopupMenuItem(
                title = "Add folder",
                icon = { modifier -> AureoleDS.icons.Settings(modifier, AureoleDS.colors.onSurfaceHigh) },
                badge = "12",
                onClick = {},
            )

            AureolePopupMenuItem(
                title = "Create stack",
                badge = "36",
                onClick = {},
            )

            AureolePopupMenuItem(
                title = "Remove",
                isDestructive = true,
                onClick = {},
            )
        }
    }
}
