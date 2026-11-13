package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.GlassBaseBlurRadius
import dev.mnascimentos.aureole.core.designsystem.utils.blurRadius
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.isHazeEnabled

private const val PREVIEW_WIDTH_FRACTION = 0.85f
private const val PREVIEW_ASPECT_RATIO = 2.1f
private const val DISC_FRACTION = 0.7f
private const val DISC_ALPHA = 0.8f
private const val BORDER_ALPHA = 0.3f
private const val TILE_COLUMNS = 2
private const val GREEN_HEX = 0xFF2E8B3A
private const val ORANGE_HEX = 0xFFF28A0F
private const val BLUE_HEX = 0xFF1F63C2
private const val LIGHT_BLUE_HEX = 0xFF1D8FE0
private val AppTileColors = listOf(Color(GREEN_HEX), Color(ORANGE_HEX), Color(BLUE_HEX), Color(LIGHT_BLUE_HEX))

/**
 * Sample of the home over a wallpaper with two cards (apps and clock) drawn with the glass settings in [draft]:
 * the corner radius comes from the local `AureoleDS` theme (the sheet overrides it with the draft), the blur
 * from the draft's glass level and the card opacity from its opacity. The preview is a home, so it never
 * mirrors for left-handed users.
 */
@Composable
internal fun CardsGlassPreview(
    draft: CardsGlassDraft,
    modifier: Modifier = Modifier
) {
    val hazeState = rememberHazeState()
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
    Box(
        modifier = modifier
            .fillMaxWidth(PREVIEW_WIDTH_FRACTION)
            .aspectRatio(PREVIEW_ASPECT_RATIO)
            .clip(shape)
    ) {
        SampleWallpaper(modifier = Modifier.fillMaxSize().hazeSource(hazeState))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxSize().padding(AureoleDS.dimens.medium),
                horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
            ) {
                GlassCard(draft = draft, hazeState = hazeState, modifier = Modifier.weight(1f)) { AppTiles() }
                GlassCard(draft = draft, hazeState = hazeState, modifier = Modifier.weight(1f)) { ClockTexts() }
            }
        }
    }
}

@Composable
private fun SampleWallpaper(modifier: Modifier = Modifier) {
    val colors = AureoleDS.colors
    Box(
        modifier = modifier.background(
            Brush.linearGradient(listOf(colors.surfaceVariant, colors.background, colors.surfaceVariant))
        ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = AureoleDS.dimens.large)
                .fillMaxHeight(DISC_FRACTION)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(colors.outline.copy(alpha = DISC_ALPHA))
        )
    }
}

/** Card with the draft's corner radius, blur and opacity; with the blur off it is a solid surface. */
@Composable
private fun GlassCard(
    draft: CardsGlassDraft,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
    val surface = AureoleDS.colors.surface
    val glass = if (draft.isHazeEnabled()) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = draft.glass.blurRadius(GlassBaseBlurRadius),
                tint = HazeTint(surface.copy(alpha = draft.opacity))
            )
        ) { blurEnabled = true }
    } else {
        Modifier.background(surface)
    }
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .then(glass)
            .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline.copy(alpha = BORDER_ALPHA), shape)
            .padding(AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun AppTiles() {
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        AppTileColors.chunked(TILE_COLUMNS).forEach { rowColors ->
            Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                rowColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(AureoleDS.dimens.iconLargePlus)
                            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockTexts() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AureoleText(
            text = stringResource(R.string.settings_appearance_minihome_time),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        AureoleText(
            text = stringResource(R.string.settings_cards_glass_preview_date),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.smallText, textAlign = TextAlign.Center)
        )
    }
}

@AureolePreview
@Composable
private fun CardsGlassPreviewPreview() {
    AureoleLauncherTheme {
        CardsGlassPreview(draft = CardsGlassDraft(radiusDp = 16, glass = GlassLevel.MEDIUM, opacity = 0.5f))
    }
}
