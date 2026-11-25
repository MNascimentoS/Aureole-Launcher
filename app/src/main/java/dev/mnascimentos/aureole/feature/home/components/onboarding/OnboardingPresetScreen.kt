package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ChevronLeft
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.contentLines
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.lineRes
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.nameRes
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.thumbnailItems
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.LayoutPresetPlan
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingPresetMetrics
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetContentLine

/**
 * "Pick a starting point" (RF-F1): one selectable card per preset (mini home, name, one line, what it brings)
 * and a fixed "Use <name>" at the bottom. The back arrow (and the system back) returns to the welcome.
 */
@Composable
fun OnboardingPresetScreen(
    onboarding: OnboardingUiState,
    actions: OnboardingActions,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AureoleDS.colors.background.copy(alpha = OnboardingPresetMetrics.BACKDROP_ALPHA))
            // Blocks touches from reaching the home below.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = AureoleDS.dimens.large)
        ) {
            PresetHeader(onBack = actions.onClosePresets)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
            ) {
                LayoutPreset.entries.forEach { preset ->
                    PresetCard(
                        preset = preset,
                        plan = onboarding.presetPlans.firstOrNull { it.preset == preset },
                        isSelected = preset == onboarding.selectedPreset,
                        isLeftHanded = isLeftHanded,
                        onSelect = { actions.onSelectPreset(preset) }
                    )
                }
            }
            OnboardingPrimaryButton(
                text = stringResource(
                    R.string.home_onboarding_preset_use,
                    stringResource(onboarding.selectedPreset.nameRes)
                ),
                onClick = { actions.onApplyPreset(onboarding.selectedPreset) },
                modifier = Modifier.padding(vertical = AureoleDS.dimens.medium)
            )
        }
    }
}

@Composable
private fun PresetHeader(onBack: () -> Unit) {
    Column {
        val backDescription = stringResource(R.string.home_onboarding_preset_back)
        Box(
            modifier = Modifier
                .padding(top = AureoleDS.dimens.small)
                .size(AureoleDS.dimens.minTouchTarget)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onBack)
                .semantics { contentDescription = backDescription },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.ChevronLeft(
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                tint = AureoleDS.colors.onSurfaceHigh
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        AureoleText(
            text = stringResource(R.string.home_onboarding_preset_title),
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.semantics { heading() },
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        AureoleText(
            text = stringResource(R.string.home_onboarding_preset_subtitle),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
    }
}

@Composable
private fun PresetCard(
    preset: LayoutPreset,
    plan: LayoutPresetPlan?,
    isSelected: Boolean,
    isLeftHanded: Boolean,
    onSelect: () -> Unit
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
    val borderColor = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceLow
    val borderWidth = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AureoleDS.colors.surface.copy(alpha = OnboardingPresetMetrics.CARD_SURFACE_ALPHA))
            .border(borderWidth, borderColor, shape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onSelect)
            .padding(AureoleDS.dimens.medium),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnboardingPresetThumbnail(items = preset.thumbnailItems(plan, isLeftHanded))
        Column(modifier = Modifier.weight(1f)) {
            AureoleText(
                text = stringResource(preset.nameRes),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
            AureoleText(
                text = stringResource(preset.lineRes),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
            )
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
            preset.contentLines.forEach { line ->
                AureoleText(
                    text = presetLineText(line),
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun presetLineText(line: PresetContentLine): String = when (line) {
    is PresetContentLine.Text -> stringResource(line.res)
    is PresetContentLine.Count -> pluralStringResource(line.res, line.count, line.count)
}

@AureolePreview
@Composable
private fun OnboardingPresetScreenPreview() {
    AureoleLauncherTheme {
        OnboardingPresetScreen(
            onboarding = OnboardingUiState(isChoosingPreset = true, selectedPreset = LayoutPreset.PRODUCTIVE),
            actions = OnboardingActions()
        )
    }
}
