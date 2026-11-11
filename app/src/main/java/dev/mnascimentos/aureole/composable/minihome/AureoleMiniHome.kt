package dev.mnascimentos.aureole.composable.minihome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.minihome.model.MiniHomeContent
import dev.mnascimentos.aureole.composable.minihome.model.MiniHomeFavorite
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val MINI_HOME_ASPECT_RATIO = 300f / 168f
private const val MINI_HOME_WIDTH_FRACTION = 0.85f
private const val VARIANT_GLOW_ALPHA = 0.7f
private const val BORDER_ALPHA = 0.3f
private const val DOCK_DIVIDER_ALPHA = 0.08f
private const val DOCK_BORDER_PX = 1f
private const val PHONE_HEX = 0xFF2E8B3A
private const val MESSAGES_HEX = 0xFFF28A0F
private const val CAMERA_HEX = 0xFF1F63C2
private const val BROWSER_HEX = 0xFFE0453A

/**
 * Stateless miniature of the home screen (clock, date, four apps and a four-icon dock) drawn with the tokens of the
 * current `AureoleDS` theme, so wrapping it in another theme previews that theme.
 *
 * @param content Sample content to draw.
 * @param modifier Modifier applied to the root.
 * @param backgroundColor Fill of the mini-home; null keeps the theme background.
 * @param isLeftHanded Puts the apps on the side opposite to the thumb: left for right-handed (icon first), right
 *   for left-handed (label first). The default is the right-handed layout, which the background preview also uses.
 */
@Composable
fun AureoleMiniHome(
    content: MiniHomeContent,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false,
    backgroundColor: Color? = null
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
    val colors = AureoleDS.colors
    Column(
        modifier = modifier
            .fillMaxWidth(MINI_HOME_WIDTH_FRACTION)
            .aspectRatio(MINI_HOME_ASPECT_RATIO)
            .clip(shape)
            .background(backgroundColor ?: colors.background)
            .background(
                Brush.verticalGradient(
                    listOf(colors.surfaceVariant.copy(alpha = VARIANT_GLOW_ALPHA), Color.Transparent)
                )
            )
            .border(AureoleDS.dimens.borderMinimal, colors.outline.copy(alpha = BORDER_ALPHA), shape)
            .padding(top = AureoleDS.dimens.xSmall)
    ) {
        MiniHomeClock(content)
        MiniHomeFavorites(
            favorites = content.favorites,
            isLeftHanded = isLeftHanded,
            modifier = Modifier.weight(1f)
        )
        MiniHomeDock(dockSize = content.dockSize)
    }
}

/** Dock strip: [dockSize] icon tiles evenly spaced under a thin top border. */
@Composable
private fun MiniHomeDock(dockSize: Int, modifier: Modifier = Modifier) {
    val dividerColor = AureoleDS.colors.onSurfaceHigh.copy(alpha = DOCK_DIVIDER_ALPHA)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(dividerColor, Offset.Zero, Offset(size.width, 0f), strokeWidth = DOCK_BORDER_PX)
            }
            .padding(vertical = AureoleDS.dimens.xxSmall),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        repeat(dockSize) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                    .background(AureoleDS.colors.surface)
            )
        }
    }
}

@Composable
private fun MiniHomeClock(content: MiniHomeContent) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AureoleText(
            text = content.time,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        AureoleText(
            text = content.date,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.smallText, textAlign = TextAlign.Center)
        )
    }
}

@Composable
private fun MiniHomeFavorites(
    favorites: List<MiniHomeFavorite>,
    isLeftHanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.large)
            .padding(bottom = AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal, Alignment.Bottom),
        horizontalAlignment = if (isLeftHanded) Alignment.End else Alignment.Start
    ) {
        favorites.forEach { favorite ->
            MiniHomeFavoriteRow(favorite = favorite, iconFirst = !isLeftHanded)
        }
    }
}

@Composable
private fun MiniHomeFavoriteRow(favorite: MiniHomeFavorite, iconFirst: Boolean) {
    val icon = @Composable {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconMedium)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMinimal))
                .background(favorite.color)
        )
    }
    val label = @Composable {
        AureoleText(
            text = favorite.name,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.smallText)
        )
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconFirst) {
            icon()
            label()
        } else {
            label()
            icon()
        }
    }
}

/** Sample content of the mini-home used by the appearance sheets. */
@ReadOnlyComposable
@Composable
fun sampleMiniHomeContent(): MiniHomeContent = MiniHomeContent(
    time = stringResource(R.string.settings_appearance_minihome_time),
    date = stringResource(R.string.settings_appearance_minihome_date),
    favorites = listOf(
        MiniHomeFavorite(stringResource(R.string.settings_appearance_minihome_phone), Color(PHONE_HEX)),
        MiniHomeFavorite(stringResource(R.string.settings_appearance_minihome_messages), Color(MESSAGES_HEX)),
        MiniHomeFavorite(stringResource(R.string.settings_appearance_minihome_camera), Color(CAMERA_HEX)),
        MiniHomeFavorite(stringResource(R.string.settings_appearance_minihome_browser), Color(BROWSER_HEX))
    )
)

@AureolePreview
@Composable
private fun AureoleMiniHomePreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
            AureoleMiniHome(content = sampleMiniHomeContent())
        }
    }
}
