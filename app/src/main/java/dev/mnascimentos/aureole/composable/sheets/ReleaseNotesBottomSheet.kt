package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.actions.AureolePrimaryActionButton
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.composable.sheets.model.initialReleaseVersionName
import dev.mnascimentos.aureole.composable.sheets.model.resolveReleaseVersion
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.data.repository.ReleaseNotesRepository
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val CHIP_ALPHA = 0.5f

/**
 * "What's new": the notes of one version as cards, with the version chips and the "Understood" button in the
 * bottom bar, within reach of the thumb. Opens on the current version; tapping a chip swaps the cards.
 */
@Composable
fun ReleaseNotesBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    allVersions: List<ReleaseNoteVersion> = remember { ReleaseNotesRepository.getReleaseNotes() }
) {
    var selectedName by remember { mutableStateOf(initialReleaseVersionName(allVersions)) }
    val selected = resolveReleaseVersion(allVersions, selectedName) ?: return

    AureoleMenuSheet(
        title = stringResource(R.string.settings_advanced_release_notes_whats_new),
        onDismiss = onDismissRequest,
        modifier = modifier,
        params = MenuSheetParams(
            bottomBar = {
                ReleaseNotesBottomBar(
                    versions = allVersions,
                    selected = selected,
                    onSelect = { selectedName = it.versionName },
                    onUnderstood = onDismissRequest
                )
            }
        )
    ) {
        ReleaseNotesBody(version = selected)
    }
}

@Composable
private fun ReleaseNotesBottomBar(
    versions: List<ReleaseNoteVersion>,
    selected: ReleaseNoteVersion,
    onSelect: (ReleaseNoteVersion) -> Unit,
    onUnderstood: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        if (versions.size > 1) {
            VersionChipsRow(versions = versions, selected = selected, onSelect = onSelect)
        }
        AureolePrimaryActionButton(
            label = stringResource(R.string.core_common_understood),
            onClick = onUnderstood,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun VersionChipsRow(
    versions: List<ReleaseNoteVersion>,
    selected: ReleaseNoteVersion,
    onSelect: (ReleaseNoteVersion) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth().padding(vertical = AureoleDS.dimens.xxSmall),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        items(items = versions, key = { it.versionName }, contentType = { "version_chip" }) { version ->
            VersionChip(
                version = version,
                isSelected = version.versionName == selected.versionName,
                onSelect = { onSelect(version) }
            )
        }
    }
}

@Composable
private fun VersionChip(
    version: ReleaseNoteVersion,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = if (version.isCurrent) {
        stringResource(R.string.settings_advanced_release_notes_version_current, version.versionName)
    } else {
        stringResource(R.string.settings_advanced_release_notes_version_past, version.versionName)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMediumLarge))
            .background(
                if (isSelected) {
                    AureoleDS.colors.onSurfaceHigh
                } else {
                    AureoleDS.colors.surfaceVariant.copy(
                        alpha = CHIP_ALPHA
                    )
                }
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onSelect)
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall)
    ) {
        AureoleText(
            text = label,
            color = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@AureolePreview
@Composable
private fun ReleaseNotesBottomSheetPreview() {
    AureoleLauncherTheme {
        ReleaseNotesBottomSheet(onDismissRequest = {})
    }
}
