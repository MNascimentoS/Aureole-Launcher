package dev.mnascimentos.aureole.feature.settings.components.wallpaper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.data.model.WALLPAPER_MAX_BLUR_DP
import dev.mnascimentos.aureole.core.data.model.WALLPAPER_MAX_DIM
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Crosshair
import dev.mnascimentos.aureole.core.designsystem.icons.RotateClockwise
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.EditorSliderSpec
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorFrame
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorHandlers
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorState
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorTab
import dev.mnascimentos.aureole.feature.settings.model.centeredAdjustments
import dev.mnascimentos.aureole.feature.settings.model.editorSliderSteps
import dev.mnascimentos.aureole.feature.settings.model.resetAdjustments
import dev.mnascimentos.aureole.feature.settings.model.rotated
import dev.mnascimentos.aureole.feature.settings.model.wallpaperEditorTabLabel
import dev.mnascimentos.aureole.feature.settings.model.withBlur
import dev.mnascimentos.aureole.feature.settings.model.withDim

/** Same tone as the cards of the settings root (D-07 of the redesign). */
private const val BUTTON_ALPHA = 0.45f

/** Dim moves in whole percents. */
private const val DIM_UNIT = 0.01f

/** Blur moves in whole dp. */
private const val BLUR_UNIT = 1f

/**
 * The editor sheet (RF-05): tabs Frame / Dim / Blur, the controls of the open tab and "Reset", which brings
 * everything back to the classic look. Mirrored for the left thumb, except the sliders, which keep 0 on the left.
 */
@Composable
internal fun WallpaperEditorControls(
    state: WallpaperEditorState,
    frame: WallpaperEditorFrame,
    handlers: WallpaperEditorHandlers,
    modifier: Modifier = Modifier
) {
    val tabs = WallpaperEditorTab.entries
    val tabsLabel = stringResource(R.string.settings_wallpaper_editor_tabs)
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
        AureoleSegmentedControl(
            options = tabs.map { stringResource(wallpaperEditorTabLabel(it)) },
            selectedIndex = tabs.indexOf(state.tab),
            onSelect = { handlers.onChange(state.copy(tab = tabs[it])) },
            modifier = Modifier.semantics { contentDescription = tabsLabel }
        )
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = AureoleDS.dimens.sizeLarge),
            contentAlignment = Alignment.Center
        ) {
            when (state.tab) {
                WallpaperEditorTab.FRAME -> FramePane(
                    onCenter = { handlers.onAnimateTo(state.centeredAdjustments()) },
                    onRotate = { handlers.onChange(state.rotated(frame)) }
                )
                WallpaperEditorTab.DIM -> EditorSlider(
                    spec = dimSlider(state),
                    onValueChange = { handlers.onChange(state.withDim(it)) }
                )
                WallpaperEditorTab.BLUR -> EditorSlider(
                    spec = blurSlider(state),
                    onValueChange = { handlers.onChange(state.withBlur(it)) }
                )
            }
        }
        ResetButton(onClick = { handlers.onAnimateTo(state.resetAdjustments()) })
    }
}

@Composable
@ReadOnlyComposable
private fun dimSlider(state: WallpaperEditorState) = EditorSliderSpec(
    label = stringResource(R.string.settings_wallpaper_editor_tab_dim),
    valueText = stringResource(R.string.settings_wallpaper_editor_dim_value, state.dimPercent),
    value = state.current.dim,
    max = WALLPAPER_MAX_DIM,
    steps = editorSliderSteps(WALLPAPER_MAX_DIM, DIM_UNIT)
)

@Composable
@ReadOnlyComposable
private fun blurSlider(state: WallpaperEditorState) = EditorSliderSpec(
    label = stringResource(R.string.settings_wallpaper_editor_tab_blur),
    valueText = stringResource(R.string.settings_wallpaper_editor_blur_value, state.blurDpValue),
    value = state.current.blurDp,
    max = WALLPAPER_MAX_BLUR_DP,
    steps = editorSliderSteps(WALLPAPER_MAX_BLUR_DP, BLUR_UNIT)
)

/** "Center" and "Rotate 90°" side by side; the first one under the thumb. */
@Composable
private fun FramePane(onCenter: () -> Unit, onRotate: () -> Unit) {
    LeftHandMirror {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            FrameButton(label = stringResource(R.string.settings_wallpaper_editor_center), onClick = onCenter) {
                AureoleDS.icons.Crosshair(
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium),
                    tint = AureoleDS.colors.onSurfaceHigh
                )
            }
            FrameButton(label = stringResource(R.string.settings_wallpaper_editor_rotate), onClick = onRotate) {
                AureoleDS.icons.RotateClockwise(
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium),
                    tint = AureoleDS.colors.onSurfaceHigh
                )
            }
        }
    }
}

@Composable
private fun RowScope.FrameButton(label: String, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .weight(1f)
            .height(AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = BUTTON_ALPHA))
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        AureoleText(
            text = label,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, maxLines = 1)
        )
    }
}

/**
 * The label and the value on one line (mirrored for the left thumb), the slider below. TalkBack reads the label as
 * its name and the value as its state.
 */
@Composable
private fun EditorSlider(spec: EditorSliderSpec, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LeftHandMirror {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AureoleText(
                    text = spec.label,
                    color = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.weight(1f),
                    format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
                )
                AureoleText(
                    text = spec.valueText,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyMedium.copy(textDirection = TextDirection.Ltr),
                        textAlign = TextAlign.End
                    )
                )
            }
        }
        Slider(
            value = spec.value,
            onValueChange = onValueChange,
            valueRange = 0f..spec.max,
            steps = spec.steps,
            colors = SliderDefaults.colors(
                thumbColor = AureoleDS.colors.onSurfaceHigh,
                activeTrackColor = AureoleDS.colors.onSurfaceHigh,
                inactiveTrackColor = AureoleDS.colors.surfaceVariant,
                activeTickColor = AureoleDS.colors.onSurfaceHigh.copy(alpha = 0f),
                inactiveTickColor = AureoleDS.colors.surfaceVariant.copy(alpha = 0f)
            ),
            modifier = Modifier.semantics {
                contentDescription = spec.label
                stateDescription = spec.valueText
            }
        )
    }
}

/** "Reset" at the far side from the thumb's start, like a link. */
@Composable
private fun ResetButton(onClick: () -> Unit) {
    val alignment = if (LocalLeftHanded.current) Alignment.CenterStart else Alignment.CenterEnd
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Box(
            modifier = Modifier
                .heightIn(min = AureoleDS.dimens.minTouchTarget)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = AureoleDS.dimens.small),
            contentAlignment = Alignment.Center
        ) {
            AureoleText(
                text = stringResource(R.string.settings_wallpaper_editor_reset),
                color = AureoleDS.colors.onSurfaceLow,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun WallpaperEditorControlsPreview() {
    AureoleLauncherTheme {
        WallpaperEditorControls(
            state = WallpaperEditorState(initial = WallpaperAdjustments.DEFAULT, tab = WallpaperEditorTab.DIM),
            frame = WallpaperEditorFrame(imageSize = Size.Unspecified, viewSize = Size.Unspecified),
            handlers = WallpaperEditorHandlers(onChange = {}, onAnimateTo = {})
        )
    }
}
