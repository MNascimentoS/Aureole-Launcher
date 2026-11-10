package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.composable.SettingsMenuParams
import dev.mnascimentos.aureole.composable.sheets.SettingsBottomSheet
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.StrokeColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val BLUR_LOW_OPACITY = 0.2f
private const val BLUR_MED_OPACITY = 0.5f
private const val BLUR_MAX_OPACITY = 0.9f

@Composable
fun SetBackgroundBottomSheet(
    onSolidColorClick: () -> Unit,
    onCustomImageClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsBottomSheet(
        title = stringResource(R.string.settings_appearance_set_background_title),
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            SettingsMenuItem(
                SettingsMenuParams(
                    title = stringResource(R.string.settings_appearance_solid_color),
                    leadingContent = { AureoleDS.icons.StrokeColor() },
                    onClick = onSolidColorClick
                )
            )

            SettingsMenuItem(
                SettingsMenuParams(
                    title = stringResource(R.string.settings_appearance_custom_image),
                    leadingContent = { AureoleDS.icons.Edit() },
                    onClick = onCustomImageClick
                )
            )
        }
    }
}

@Composable
fun BlurBottomSheet(
    currentOpacity: Float,
    isHazeEnabled: Boolean,
    onOptionSelected: (Float, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        Triple(0.0f, false, stringResource(R.string.settings_appearance_blur_none)),
        Triple(BLUR_LOW_OPACITY, true, stringResource(R.string.settings_appearance_blur_opacity_low)),
        Triple(BLUR_MED_OPACITY, true, stringResource(R.string.settings_appearance_blur_opacity_medium)),
        Triple(BLUR_MAX_OPACITY, true, stringResource(R.string.settings_appearance_blur_max))
    )

    SettingsBottomSheet(
        title = stringResource(R.string.settings_appearance_glass_blur_title),
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            options.forEach { (opacity, enableHaze, label) ->
                val isSelected = if (!enableHaze || opacity == 0f) {
                    !isHazeEnabled || currentOpacity == 0f
                } else {
                    isHazeEnabled && (currentOpacity == opacity)
                }

                BlurOptionRow(
                    label = label,
                    isSelected = isSelected,
                    onSelect = { onOptionSelected(opacity, enableHaze) }
                )
            }
        }
    }
}

@Composable
private fun BlurOptionRow(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val rowBg = if (isSelected) {
        AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }
    val textColor = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.onSurfaceMedium
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(rowBg)
            .clickable(
                role = Role.RadioButton,
                onClick = onSelect
            )
            .padding(vertical = AureoleDS.dimens.small, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = label,
            color = textColor,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
        if (isSelected) {
            AureoleDS.icons.Check(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
    }
}

@AureolePreview
@Composable
private fun SetBackgroundBottomSheetPreview() {
    AureoleLauncherTheme {
        SetBackgroundBottomSheet(
            onSolidColorClick = {},
            onCustomImageClick = {},
            onDismissRequest = {}
        )
    }
}

@AureolePreview
@Composable
private fun BlurBottomSheetPreview() {
    AureoleLauncherTheme {
        BlurBottomSheet(
            currentOpacity = BLUR_MED_OPACITY,
            isHazeEnabled = true,
            onOptionSelected = { _, _ -> },
            onDismissRequest = {}
        )
    }
}
