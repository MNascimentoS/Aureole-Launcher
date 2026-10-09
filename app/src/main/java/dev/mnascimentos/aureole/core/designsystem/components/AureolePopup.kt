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
import dev.mnascimentos.aureole.core.designsystem.icons.*
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
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
    cornerRadius: Dp = AureoleTheme.dimens.cornerRadius,
    hazeState: HazeState? = null,
    contentPadding: PaddingValues = PaddingValues(14.dp),
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
                    topStart = AureoleTheme.dimens.cornerRadius,
                    topEnd = AureoleTheme.dimens.cornerRadius,
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
                .padding(top = 4.dp, end = 4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = handleSettingsClick),
            contentAlignment = Alignment.Center,
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(18.dp),
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
                    topStart = AureoleTheme.dimens.cornerRadius,
                    topEnd = AureoleTheme.dimens.cornerRadius,
                )
            )
            .background(AureoleDS.colors.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AureoleDS.colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    customIcon != null -> customIcon()
                    iconBitmap != null -> Image(
                        bitmap = iconBitmap,
                        contentDescription = title,
                        modifier = Modifier.size(22.dp),
                    )
                    iconVector != null -> iconVector(
                        Modifier.size(20.dp)
                    )
                    else -> AureoleText(
                        text = title.take(1).uppercase(),
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleDS.colors.onSurfaceHigh,
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

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
                .padding(top = 4.dp, end = 4.dp),
        ) {
            if (showAureoleLogo) {
                IconButton(
                    onClick = handleOpenSettings,
                    modifier = Modifier.size(26.dp),
                ) {
                    AureoleDS.icons.Logo(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(18.dp),
                    )
                }
            } else if (onSettingsClick != null) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(26.dp),
                ) {
                    AureoleDS.icons.Settings(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(18.dp),
                    )
                }
            } else if (onCloseClick != null) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(26.dp),
                ) {
                    AureoleDS.icons.Close(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(18.dp),
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
            .padding(top = 8.dp, bottom = 4.dp, start = 4.dp, end = 4.dp),
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
    val iconTint = if (isDestructive) MaterialTheme.colorScheme.error else AureoleDS.colors.onSurfaceMedium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                customIcon != null -> customIcon()
                icon != null -> icon(Modifier.size(20.dp))
            }

            if (icon != null || customIcon != null) {
                Spacer(modifier = Modifier.width(20.dp))
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
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                    modifier = Modifier.size(28.dp),
                ) {
                    trailingIcon(Modifier.size(16.dp))
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
