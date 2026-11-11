package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmOverlay
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.fontNameOf
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.BlurOpacity
import dev.mnascimentos.aureole.feature.settings.model.CardRadiusOptions
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassParams
import dev.mnascimentos.aureole.feature.settings.model.blurOpacityOf

private const val CHIP_INACTIVE_ALPHA = 0.5f

/**
 * "Cards and glass" editor in an edit overlay: corner radius of the cards, glass (blur) level and blur opacity in
 * one place. The sample cards above the sheet and the sheet itself are drawn with the draft, so every tap shows at
 * once; nothing is saved until Apply calls `params.onApply` with the draft. Cancel, swiping down and tapping
 * outside only close.
 */
@Composable
fun CardsGlassBottomSheet(
    params: CardsGlassParams,
    modifier: Modifier = Modifier
) {
    var draft by remember { mutableStateOf(params.initial) }
    val config = AureoleThemeConfig(
        darkTheme = isSystemInDarkTheme(),
        aureoleColors = AureoleDS.colors,
        aureoleFontName = fontNameOf(AureoleDS.typography.fontFamily),
        aureoleIcons = AureoleDS.icons,
        aureoleDimens = AureoleDS.dimens.copy(radiusLarge = draft.radiusDp.dp)
    )
    AureoleLauncherTheme(config = config) {
        AureoleConfirmOverlay(
            params = ConfirmOverlayParams(
                title = stringResource(R.string.settings_cards_glass_title),
                primaryLabel = stringResource(R.string.settings_appearance_apply),
                onPrimary = { params.onApply(draft) },
                onDismissRequest = params.onDismissRequest,
                isLeftHanded = params.isLeftHanded
            ),
            modifier = modifier,
            preview = {
                CardsGlassPreview(
                    draft = draft,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        ) {
            CardsGlassControls(
                draft = draft,
                onDraftChange = { draft = it }
            )
        }
    }
}

@Composable
private fun CardsGlassControls(
    draft: CardsGlassDraft,
    onDraftChange: (CardsGlassDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = GlassLevel.entries
    val opacities = BlurOpacity.entries
    val glassDescription = stringResource(R.string.settings_cards_glass_glass_label)
    val opacityDescription = stringResource(R.string.settings_cards_glass_opacity_label)
    Column(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        SectionLabel(stringResource(R.string.settings_cards_glass_corners_label))
        RadiusChips(selectedDp = draft.radiusDp, onSelect = { onDraftChange(draft.copy(radiusDp = it)) })
        SectionLabel(stringResource(R.string.settings_cards_glass_glass_label))
        AureoleSegmentedControl(
            options = levels.map { stringResource(it.labelRes) },
            selectedIndex = levels.indexOf(draft.glass),
            onSelect = { onDraftChange(draft.copy(glass = levels[it])) },
            modifier = Modifier.semantics { contentDescription = glassDescription }
        )
        SectionLabel(stringResource(R.string.settings_cards_glass_opacity_label))
        AureoleSegmentedControl(
            options = opacities.map { stringResource(it.labelRes) },
            selectedIndex = opacities.indexOf(blurOpacityOf(draft.opacity)),
            onSelect = { onDraftChange(draft.copy(opacity = opacities[it].value)) },
            modifier = Modifier.semantics { contentDescription = opacityDescription }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    SheetSectionLabel(text = text)
}

@Composable
private fun RadiusChips(selectedDp: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        CardRadiusOptions.forEach { radius ->
            RadiusChip(radiusDp = radius, isSelected = radius == selectedDp, onClick = { onSelect(radius) })
        }
    }
}

@Composable
private fun RowScope.RadiusChip(radiusDp: Int, isSelected: Boolean, onClick: () -> Unit) {
    val container = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.surfaceVariant.copy(alpha = CHIP_INACTIVE_ALPHA)
    }
    val content = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceMedium
    val shape = if (radiusDp == 0) RectangleShape else RoundedCornerShape(AureoleDS.dimens.radiusSmall)
    val description = stringResource(R.string.settings_appearance_border_radius_format, radiusDp)
    val label = if (radiusDp == 0) stringResource(R.string.settings_cards_glass_corners_flat) else radiusDp.toString()
    Box(
        modifier = Modifier
            .weight(1f)
            .height(AureoleDS.dimens.minTouchTarget)
            .clip(shape)
            .background(container)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = label,
            color = content,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, maxLines = 1)
        )
    }
}

@AureolePreview
@Composable
private fun CardsGlassBottomSheetPreview() {
    AureoleLauncherTheme {
        CardsGlassBottomSheet(
            params = CardsGlassParams(
                initial = CardsGlassDraft(radiusDp = 16, glass = GlassLevel.MEDIUM, opacity = BlurOpacity.MEDIUM.value),
                onApply = {},
                onDismissRequest = {}
            )
        )
    }
}
