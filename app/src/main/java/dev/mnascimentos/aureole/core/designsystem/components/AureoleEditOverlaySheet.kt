package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState

private const val ANIMATION_DURATION_MS = 300

@Composable
internal fun OverlayContentBox(
    stateData: OverlaySheetStateData,
    params: AureoleEditOverlayParams,
    hazeState: HazeState?
) {
    val localHaze = LocalHazeState.current
    val effectiveHazeState = hazeState ?: localHaze

    val hazeModifier = if (effectiveHazeState != null) {
        Modifier.hazeEffect(
            state = effectiveHazeState,
            style = HazeStyle(
                blurRadius = 32.dp,
                tint = HazeTint(Color.Black.copy(alpha = 0.55f))
            )
        ) { blurEnabled = true }
    } else {
        Modifier.Companion
    }
    val backgroundColor = if (effectiveHazeState != null) Color.Transparent else Color.Black

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .then(hazeModifier)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = stateData.triggerDismiss
            )
    ) {
        val maxSheetHeight = maxHeight * params.config.maxSheetHeightFraction
        val sheetMaxExpandedHeight = maxHeight - AureoleDS.dimens.large
        val maxExpansionAllowed = (sheetMaxExpandedHeight - maxSheetHeight).coerceAtLeast(0.dp)
        val bounds = SheetBounds(maxSheetHeight, maxExpansionAllowed)

        val previewHeight = maxHeight - maxSheetHeight

        Box(modifier = Modifier.fillMaxSize()) {
            PreviewSlot(
                previewHeight = previewHeight,
                previewContent = params.previewContent,
                effectiveAnimated = stateData.effectiveAnimated,
                animScope = stateData.animScope
            )

            SheetSurface(
                stateData = stateData,
                params = params,
                bounds = bounds
            )
        }
    }
}

@Composable
private fun BoxScope.PreviewSlot(
    previewHeight: Dp,
    previewContent: (@Composable BoxScope.() -> Unit)?,
    effectiveAnimated: Boolean,
    animScope: AnimatedVisibilityScope
) {
    val previewModifier = if (effectiveAnimated) {
        with(animScope) {
            Modifier.animateEnterExit(
                enter = fadeIn(tween(ANIMATION_DURATION_MS)) +
                    scaleIn(
                        initialScale = 0.75f,
                        animationSpec = tween(ANIMATION_DURATION_MS)
                    ),
                exit = fadeOut(tween(ANIMATION_DURATION_MS)) +
                    scaleOut(
                        targetScale = 0.75f,
                        animationSpec = tween(ANIMATION_DURATION_MS)
                    )
            )
        }
    } else {
        Modifier.Companion
    }

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(previewHeight)
            .then(previewModifier)
            .padding(
                start = AureoleDS.dimens.medium,
                end = AureoleDS.dimens.medium,
                top = AureoleDS.dimens.large,
                bottom = AureoleDS.dimens.medium
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        previewContent?.invoke(this)
    }
}

@Composable
internal fun OverlaySheetBody(
    title: String?,
    onSettingsClick: (() -> Unit)?,
    bottomBarContent: (@Composable ColumnScope.() -> Unit)?,
    sheetContent: (@Composable ColumnScope.() -> Unit)?,
    headerIcon: (@Composable (Modifier) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xxSmall)
    ) {
        if (title != null) {
            OverlaySheetHeader(
                title = title,
                onSettingsClick = onSettingsClick,
                headerIcon = headerIcon
            )
        }

        if (sheetContent != null) {
            Column(
                modifier = Modifier
                    .padding(horizontal = AureoleDS.dimens.small)
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                sheetContent()
            }
        }

        if (bottomBarContent != null) {
            Spacer(
                modifier = Modifier
                    .height(AureoleDS.dimens.small)
                    .padding(horizontal = AureoleDS.dimens.small)
            )
            Column(modifier = Modifier.padding(horizontal = AureoleDS.dimens.small)) {
                bottomBarContent()
            }
        }
    }
}

@Composable
private fun OverlaySheetHeader(
    title: String,
    onSettingsClick: (() -> Unit)?,
    headerIcon: (@Composable (Modifier) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AureoleDS.dimens.medium),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = title,
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleDS.colors.onSurfaceHigh,
            textAlign = TextAlign.Center
        )

        if (onSettingsClick != null || headerIcon != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(AureoleDS.dimens.iconXLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                    .clickable(
                        onClick = { onSettingsClick?.invoke() },
                        role = Role.Button
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (headerIcon != null) {
                    headerIcon(Modifier.size(AureoleDS.dimens.iconSmall))
                } else {
                    AureoleDS.icons.Logo(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                    )
                }
            }
        }
    }
}

@AureolePreview
@Composable
private fun AureoleEditOverlaySheetPreview() {
    AureoleLauncherTheme {
        OverlaySheetBody(
            title = "Preview Overlay Sheet",
            onSettingsClick = {},
            bottomBarContent = null,
            sheetContent = {
                Text(
                    text = "Sheet Body Content",
                    color = AureoleDS.colors.onSurfaceHigh
                )
            }
        )
    }
}

@AureolePreview
@Composable
private fun AureoleEditOverlaySheetWithBottomBarPreview() {
    AureoleLauncherTheme {
        OverlaySheetBody(
            title = "Overlay Sheet with Bottom Bar",
            onSettingsClick = {},
            bottomBarContent = {
                Text(
                    text = "Bottom Bar Content",
                    color = AureoleDS.colors.onSurfaceMedium
                )
            },
            sheetContent = {
                Text(
                    text = "Sheet Body Content",
                    color = AureoleDS.colors.onSurfaceHigh
                )
            }
        )
    }
}
