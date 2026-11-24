package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.wallpaper.FramedWallpaperImage
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.ImageSwap
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.BackgroundPhotoCard
import java.io.File

/** Opacity of the round "Change photo" button. */
private const val SECONDARY_BUTTON_ALPHA = 0.12f

/**
 * Inside of the "Custom image" card with a photo applied: a thumbnail of the photo with its framing and dim, the
 * label and two icon buttons, "Edit photo" (pencil, filled, the main one) and "Change photo" (RF-02). The names of
 * the buttons are read by TalkBack.
 */
@Composable
internal fun ColumnScope.BackgroundPhotoContent(photo: BackgroundPhotoCard, label: String) {
    BackgroundThumbnail(path = photo.path, adjustments = photo.adjustments)
    TypeLabel(label)
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        PhotoIconButton(
            description = stringResource(R.string.settings_wallpaper_editor_title),
            isPrimary = true,
            onClick = photo.onEdit
        ) { tint -> AureoleDS.icons.Edit(modifier = Modifier.size(AureoleDS.dimens.iconMedium), tint = tint) }
        PhotoIconButton(
            description = stringResource(R.string.settings_wallpaper_change_photo),
            isPrimary = false,
            onClick = photo.onChange
        ) { tint -> AureoleDS.icons.ImageSwap(modifier = Modifier.size(AureoleDS.dimens.iconMedium), tint = tint) }
    }
}

/** The photo as the home shows it (framing and dim), small. */
@Composable
private fun BackgroundThumbnail(path: String, adjustments: WallpaperAdjustments) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.backgroundThumbHeight)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.background)
    ) {
        WallpaperPhotoLayer(path = path, adjustments = adjustments)
    }
}

/** The photo at [path] with its framing and dim, filling its parent (the thumbnail and the preview above the sheet). */
@Composable
internal fun WallpaperPhotoLayer(path: String, adjustments: WallpaperAdjustments, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        if (path.isNotEmpty()) {
            FramedWallpaperImage(file = File(path), adjustments = adjustments, modifier = Modifier.fillMaxSize())
        }
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = adjustments.dim)))
    }
}

/** A 40 dp round icon button with a 48 dp touch area. */
@Composable
private fun PhotoIconButton(
    description: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    icon: @Composable (tint: Color) -> Unit
) {
    val container = if (isPrimary) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.onSurfaceHigh.copy(alpha = SECONDARY_BUTTON_ALPHA)
    }
    val content = if (isPrimary) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceHigh
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.minTouchTarget)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(AureoleDS.dimens.xxLarge).clip(CircleShape).background(container),
            contentAlignment = Alignment.Center
        ) { icon(content) }
    }
}

@AureolePreview
@Composable
private fun BackgroundPhotoContentPreview() {
    AureoleLauncherTheme {
        Column {
            BackgroundPhotoContent(
                photo = BackgroundPhotoCard(
                    path = "",
                    adjustments = WallpaperAdjustments.DEFAULT,
                    onEdit = {},
                    onChange = {}
                ),
                label = stringResource(R.string.settings_appearance_custom_image)
            )
        }
    }
}
